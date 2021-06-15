package eu.software4you.ulib.impl.configuration.yaml;

import eu.software4you.common.Nameable;
import eu.software4you.common.collection.Pair;
import eu.software4you.configuration.yaml.YamlSub;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.nodes.*;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

class YamlDocument implements YamlSub, Nameable {
    private static final String PATH_SEPARATOR = ".";
    // deserialized data
    final Map<String, Object> data = new LinkedHashMap<>();
    // key nodes do contain all nodes from this level
    final Map<String, Node> keyNodes = new LinkedHashMap<>();
    // yaml-subs
    final Map<String, YamlDocument> children = new LinkedHashMap<>();
    private final YamlSerializer serializer;
    private final YamlDocument root;
    private final YamlDocument parent;
    private final String key;
    // node that this sub represents
    Node node;
    private boolean throwIfConversionFails;

    // constructor for root
    YamlDocument(YamlSerializer serializer) {
        this.serializer = serializer;
        this.parent = this;
        this.root = this;
        this.key = "";
    }

    // constructor for sub
    YamlDocument(YamlDocument parent, String key, Node node) {
        this.root = parent.getRoot();
        this.parent = parent;
        this.serializer = root.serializer;
        this.key = key;
        this.node = node;
    }

    @Override
    public @Nullable String getName() {
        return key;
    }

    public @NotNull YamlDocument getRoot() {
        return root;
    }

    public <T> T get(@NotNull String path) {
        return get(path, null);
    }

    public <T> T get(@NotNull String path, T def) {
        Validate.notNull(path, "Path may not be null");

        return resolve(path)
                .map(pair -> pair.getFirst().data.get(pair.getSecond()))
                .map(data -> {
                    try {
                        return (T) data;
                    } catch (ClassCastException e) {
                        if (root.throwIfConversionFails)
                            throw new IllegalArgumentException("Cannot convert " + path + " to requested type", e);
                        return def;
                    }
                }).orElse(def);
    }

    @Override
    public @NotNull Collection<String> getKeys(boolean deep) {
        Set<String> keys = new LinkedHashSet<>(data.keySet());

        if (deep && !children.isEmpty()) {
            String prefix = key == null || key.isEmpty() ? "" : key + PATH_SEPARATOR;
            children.values().forEach(doc -> doc.getKeys(true).forEach(key ->
                    keys.add(String.format("%s%s%s%s", prefix, doc.key, PATH_SEPARATOR, key))));
        }

        return keys;
    }

    @Override
    public @NotNull Map<String, Object> getValues(boolean deep) {
        Map<String, Object> values = new LinkedHashMap<>(data);

        if (deep && !children.isEmpty()) {
            String prefix = key == null || key.isEmpty() ? "" : key + PATH_SEPARATOR;
            children.values().forEach(doc -> doc.getValues(true).forEach((key, value) ->
                    values.put(String.format("%s%s%s%s", prefix, doc.key, PATH_SEPARATOR, key), value)));
        }

        return values;
    }

    public void set(@NotNull String fullPath, Object value) {

        String restPath = fullPath;
        YamlDocument doc = this;
        int i;
        while ((i = restPath.indexOf(PATH_SEPARATOR)) >= 0) {
            String key = restPath.substring(0, i);
            restPath = restPath.substring(i + PATH_SEPARATOR.length());

            // create new sub
            if (!doc.children.containsKey(key)) {
                // add new sub node to root node

                Node node = restPath.isEmpty() ? null : new MappingNode(Tag.MAP, new ArrayList<>(), DumperOptions.FlowStyle.AUTO);
                YamlDocument sub = new YamlDocument(doc, key, node);

                Node keyNode = doc.addNode(key, node); // add new node to current
                doc.children.put(key, sub);

                // sub overwrites other data
                doc.keyNodes.put(key, keyNode);
                doc.data.remove(key);
            }

            doc = doc.children.get(key);
        }

        final String key = restPath;

        if (value == null) {
            doc.children.remove(key);
            doc.keyNodes.remove(key);
            doc.data.remove(key);
            doc.delNode(key);
            return;
        }

        Node dataNode = doc.serializer.represent(value);
        Node keyNode;

        if (key.isEmpty()) {
            doc.replaceNode(keyNode = dataNode);
            doc.children.clear();
            doc.keyNodes.clear();
            doc.data.clear();
        } else if (keyNodes.containsKey(key)) {
            keyNode = doc.replaceNode(key, dataNode);
        } else {
            keyNode = doc.addNode(key, dataNode);
        }
        doc.children.remove(key); // remove any subs with that name

        doc.keyNodes.put(key, keyNode);
        doc.data.put(key, value);

    }

    public List<String> getComments(@NotNull String path) {
        return resolveCommentNode(path)
                .map(node -> node.getBlockComments().stream()
                        .map(CommentLine::getValue)
                        .collect(Collectors.toList()))
                .orElse(null);
    }

    public void setComments(@NotNull String path, String... lines) {
        Node node = resolveCommentNode(path).orElseThrow(() -> new IllegalArgumentException("Path is not set"));

        List<CommentLine> comments = Arrays.stream(lines)
                .map(line -> new CommentLine(null, null, line, CommentType.BLOCK))
                .collect(Collectors.toList());
        node.setBlockComments(comments);
    }

    @Override
    public String toString() {
        return keyNodes.toString();
    }

    @Override
    public boolean isRoot() {
        return root == this;
    }

    @Override
    public void setConversionPolicy(boolean throwing) {
        root.throwIfConversionFails = throwing;
    }

    @Override
    public boolean isSet(@NotNull String path) {
        return resolve(path).map(pair -> {
            YamlDocument doc = pair.getFirst();
            String key = pair.getSecond();
            return doc.data.containsKey(key) || doc.children.containsKey(key);
        }).orElse(false);
    }

    @Override
    public @Nullable YamlDocument getSub(@NotNull String path) {
        return resolve(path).map(pair -> pair.getFirst().children.get(pair.getSecond())).orElse(null);
    }

    @Override
    public @NotNull Collection<YamlSub> getSubs() {
        return new LinkedHashSet<>(children.values());
    }

    @Override
    public boolean isSub(@NotNull String path) {
        return resolve(path).map(pair -> pair.getFirst().children.containsKey(pair.getSecond())).orElse(false);
    }

    @Override
    public @NotNull Node asNode() {
        return node;
    }

    /* helpers */

    void clear() {
        children.clear();
        keyNodes.clear();
        data.clear();
    }

    @Override
    public void reset() {
        clear();
        Node newNode = new MappingNode(Tag.MAP, new ArrayList<>(), DumperOptions.FlowStyle.AUTO);
        if (isRoot()) {
            node = newNode;
        } else {
            replaceNode(newNode);
        }
    }

    @Override
    public void load(Reader reader) throws IOException {
        serializer.deserialize(reader, this);
    }

    @Override
    public void save(Writer writer) throws IOException {
        serializer.serialize(this, writer);
    }

    private Optional<Pair<YamlDocument, String>> resolve(final String fullPath) {

        String restPath = fullPath;
        YamlDocument doc = this;
        int i;
        while ((i = restPath.indexOf(PATH_SEPARATOR)) >= 0) {
            String subPath = restPath.substring(0, i);
            restPath = restPath.substring(i + PATH_SEPARATOR.length());

            if ((doc = doc.children.get(subPath)) == null) {
                System.out.println(subPath + " not found");
                return Optional.empty();
            }
        }

        return Optional.of(new Pair<>(doc, restPath));
    }

    private Optional<Node> resolveCommentNode(String fullPath) {
        return resolve(fullPath).map(pair -> pair.getFirst().keyNodes.get(pair.getSecond()));
    }

    private Node addNode(String key, Node node) {
        MappingNode root = selfRoot();
        List<NodeTuple> tuples = new ArrayList<>(root.getValue());

        Node keyNode = new ScalarNode(Tag.STR, key, null, null, DumperOptions.ScalarStyle.PLAIN);
        tuples.add(new NodeTuple(keyNode, node));

        root.setValue(tuples);

        return keyNode;
    }

    private void delNode(String key) {
        MappingNode root = selfRoot();
        List<NodeTuple> tuples = new ArrayList<>(root.getValue());
        tuples.removeIf(tuple -> ((ScalarNode) tuple.getKeyNode()).getValue().equals(key));
        root.setValue(tuples);
    }

    // replaces the current node with a new one
    void replaceNode(Node newNode) {
        if (!isRoot()) {
            // update parent node
            parent.replaceNode(key, newNode);
            parent.keyNodes.put(this.key, newNode);
        }

        // update this node
        this.node = newNode;
    }

    private Node replaceNode(String key, Node newNode) {
        MappingNode root = selfRoot();

        List<CommentLine> comments = null; /* = null to make compiler happy */
        List<NodeTuple> tuples = new ArrayList<>(root.getValue());
        int i = -1;
        for (int j = 0; j < tuples.size(); j++) {
            ScalarNode keyNode = (ScalarNode) tuples.get(j).getKeyNode();
            if (keyNode.getValue().equals(key)) {
                i = j;
                comments = keyNode.getBlockComments();
                break;
            }
        }
        if (i < 0)
            throw new IllegalStateException("Could not find " + key + " in parent");

        Node keyNode = new ScalarNode(Tag.STR, this.key, null, null, DumperOptions.ScalarStyle.PLAIN);
        keyNode.setBlockComments(comments);
        tuples.set(i, new NodeTuple(keyNode, newNode));
        root.setValue(tuples);
        return keyNode;
    }

    private MappingNode selfRoot() {
        if (node instanceof MappingNode) {
            return (MappingNode) node;
        }
        throw new IllegalStateException("Sub cannot hold keyed values.");
    }
}

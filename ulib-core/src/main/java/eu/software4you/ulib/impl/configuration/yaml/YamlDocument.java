package eu.software4you.ulib.impl.configuration.yaml;

import eu.software4you.common.Keyable;
import eu.software4you.common.collection.Pair;
import eu.software4you.configuration.yaml.YamlSub;
import lombok.val;
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

class YamlDocument implements YamlSub, Keyable<String> {
    private static final String PATH_SEPARATOR = ".";
    // key -> ( key-node, data/sub )
    final Map<String, Pair<Node, Object>> children = new LinkedHashMap<>();

    private final YamlSerializer serializer;
    private final YamlDocument root;
    private final YamlDocument parent;
    private final String key;
    // node that this sub represents
    // (value node)
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
    YamlDocument(YamlDocument parent, String key, Node valueNode) {
        this.root = parent.getRoot();
        this.parent = parent;
        this.serializer = root.serializer;
        this.key = key;
        this.node = valueNode;
    }

    @Override
    public @NotNull String getKey() {
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

        return resolveChild(path)
                .map(child -> {
                    Object value = child.getSecond();
                    try {
                        return (T) value;
                    } catch (ClassCastException e) {
                        if (root.throwIfConversionFails)
                            throw new IllegalArgumentException("Cannot convert " + path + " to requested type", e);
                        return def;
                    }
                }).orElse(def);
    }

    @Override
    public @NotNull Collection<String> getKeys(boolean deep) {
        Set<String> keys = new LinkedHashSet<>();

        children.forEach((key, child) -> {

            // do not add a sub as key, only it's values
            if (deep && child.getSecond() instanceof YamlDocument) {
                String prefix = this.key == null || this.key.isEmpty() ? "" : this.key + PATH_SEPARATOR;
                YamlDocument doc = (YamlDocument) child.getSecond();

                doc.getKeys(true).stream()
                        .map(k -> String.format("%s%s%s%s", prefix, doc.key, PATH_SEPARATOR, k))
                        .forEach(keys::add);
            } else {
                keys.add(key);
            }

        });

        return keys;
    }

    @Override
    public @NotNull Map<String, Object> getValues(boolean deep) {
        Map<String, Object> values = new LinkedHashMap<>();

        children.forEach((key, child) -> {

            Object value = child.getSecond();

            // do not add a sub as key, only it's values
            if (deep && value instanceof YamlDocument) {
                String prefix = this.key == null || this.key.isEmpty() ? "" : this.key + PATH_SEPARATOR;
                YamlDocument doc = (YamlDocument) value;

                doc.getValues(true).forEach((k, v) -> {
                    values.put(String.format("%s%s%s%s", prefix, doc.key, PATH_SEPARATOR, k), v);
                });
            } else {
                values.put(key, value);
            }

        });

        return values;
    }

    public void set(@NotNull String fullPath, Object value) {

        String restPath = fullPath;
        YamlDocument doc = this;
        int i;
        while ((i = restPath.indexOf(PATH_SEPARATOR)) >= 0) {
            String key = restPath.substring(0, i);
            restPath = restPath.substring(i + PATH_SEPARATOR.length());

            if (doc.children.containsKey(key) && doc.children.get(key).getSecond() instanceof YamlDocument) {
                doc = (YamlDocument) doc.children.get(key).getSecond();
                continue;
            }

            if (value == null) {
                // path does not exist, no need to remove
                return;
            }

            // create new sub
            Node keyNode, valueNode = restPath.isEmpty() ? null : new MappingNode(Tag.MAP, new ArrayList<>(), DumperOptions.FlowStyle.AUTO);

            if (doc.children.containsKey(key)) {
                // replace old node
                keyNode = doc.replaceNode(key, valueNode);
            } else {
                // add new node to current
                keyNode = doc.addNode(key, valueNode);
            }

            YamlDocument sub = new YamlDocument(doc, key, valueNode);
            doc.children.put(key, new Pair<>(keyNode, sub));
            doc = sub;
        }

        final String key = restPath;

        if (value == null) {
            // remove node
            doc.children.remove(key);
            doc.delNode(key);
            return;
        }

        Node keyNode, valueNode = doc.serializer.represent(value);

        if (key.isEmpty()) {
            doc.replaceNode(keyNode = valueNode);
            doc.children.clear();
        } else if (doc.children.containsKey(key)) {
            keyNode = doc.replaceNode(key, valueNode);
        } else {
            keyNode = doc.addNode(key, valueNode);
        }

        // overwrite potential old values
        doc.children.put(key, new Pair<>(keyNode, value));
    }

    public List<String> getComments(@NotNull String fullPath) {
        return resolveKeyNode(fullPath)
                .map(node -> node.getBlockComments().stream()
                        .map(CommentLine::getValue)
                        .collect(Collectors.toList()))
                .orElse(null);
    }

    public void setComments(@NotNull String fullPath, String... lines) {
        Node node = resolveKeyNode(fullPath)
                .orElseThrow(() -> new IllegalArgumentException("Path " + fullPath + " is not set"));

        List<CommentLine> comments = Arrays.stream(lines)
                .map(line -> new CommentLine(null, null, line, CommentType.BLOCK))
                .collect(Collectors.toList());
        node.setBlockComments(comments);
    }

    @Override
    public String toString() {
        return getValues(true).toString();
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
        return resolveChild(path).isPresent();
    }

    @Override
    public @Nullable YamlDocument getSub(@NotNull String path) {
        return (YamlDocument) resolveChild(path)
                .map(Pair::getSecond)
                .filter(value -> value instanceof YamlDocument)
                .orElse(null);
    }

    @Override
    public @NotNull Collection<YamlSub> getSubs() {
        return children.values().stream()
                .map(Pair::getSecond)
                .filter(value -> value instanceof YamlDocument)
                .map(value -> (YamlDocument) value)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public boolean isSub(@NotNull String path) {
        return resolveChild(path)
                .map(pair -> pair.getSecond() instanceof YamlDocument)
                .orElse(false);
    }

    @Override
    public @NotNull Node asNode() {
        return node;
    }

    @Override
    public void reset() {
        children.clear();
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

    /* helpers */

    // resolves the fullPath into a valid sub and a key
    // note that the key is not guaranteed to exist in the sub
    private Optional<Pair<YamlDocument, String>> resolve(final String fullPath) {

        String restPath = fullPath;
        YamlDocument doc = this;
        int i;
        while ((i = restPath.indexOf(PATH_SEPARATOR)) >= 0) {
            String subPath = restPath.substring(0, i);
            restPath = restPath.substring(i + PATH_SEPARATOR.length());

            val pair = doc.children.get(subPath);
            if (pair == null || !(pair.getSecond() instanceof YamlDocument)) {
                return Optional.empty();
            }

            doc = (YamlDocument) pair.getSecond();
        }

        return Optional.of(new Pair<>(doc, restPath));
    }

    // returns pair: (keyNode, child)
    private Optional<Pair<Node, Object>> resolveChild(final String fullPath) {
        return resolve(fullPath)
                // pair: (doc, key)
                .map(pair -> pair.getFirst().children.get(pair.getSecond()));
    }

    private Optional<Node> resolveKeyNode(String fullPath) {
        return resolveChild(fullPath)
                // pair: (keyNode, child)
                .map(Pair::getFirst);
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
            val child = parent.children.get(key);
            if (child.getSecond() != this) { // value of child should be this instance
                throw new IllegalStateException("Parent has stored another value than acceptable");
            }
            child.setFirst(newNode);
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

        Node keyNode = new ScalarNode(Tag.STR, key, null, null, DumperOptions.ScalarStyle.PLAIN);
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

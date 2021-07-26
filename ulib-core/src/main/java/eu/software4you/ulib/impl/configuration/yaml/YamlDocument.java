package eu.software4you.ulib.impl.configuration.yaml;

import eu.software4you.common.Keyable;
import eu.software4you.common.collection.Pair;
import eu.software4you.configuration.ConversionPolicy;
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

public class YamlDocument implements YamlSub, Keyable<String> {
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
    @NotNull
    protected ConversionPolicy conversionPolicy = ConversionPolicy.RETURN_DEFAULT;

    // constructor for empty root
    protected YamlDocument(YamlSerializer serializer) {
        this.serializer = serializer;
        this.parent = this;
        this.root = this;
        this.key = "";
        this.node = new MappingNode(Tag.MAP, new ArrayList<>(), DumperOptions.FlowStyle.AUTO);
    }

    // constructor for deserialized root
    protected YamlDocument(YamlSerializer serializer, Reader reader) throws IOException {
        this.serializer = serializer;
        this.parent = this;
        this.root = this;
        this.key = "";
        load(reader);
    }

    // constructor for sub
    protected YamlDocument(YamlDocument parent, String key, Node valueNode) {
        this.root = parent.getRoot();
        this.parent = parent;
        this.serializer = root.serializer;
        this.key = key;
        this.node = valueNode;
    }

    // constructs a new sub
    // to be overridden by subclasses
    protected YamlDocument constructChild(String key, Node valueNode) {
        return new YamlDocument(this, key, valueNode);
    }

    @Override
    public @NotNull String getKey() {
        return key;
    }

    public @NotNull YamlDocument getRoot() {
        return root;
    }

    public <T> T get(@NotNull String path, T def) {
        Validate.notNull(path, "Path may not be null");

        return resolveChild(path)
                .map(Pair::getSecond)
                .map(child -> {
                    try {
                        return (T) child;
                    } catch (ClassCastException e) {
                        switch (root.conversionPolicy) {
                            case THROW_EXCEPTION:
                                throw new IllegalArgumentException("Cannot convert " + path + " to requested type", e);
                            case RETURN_DEFAULT:
                                return def;
                        }
                        throw new IllegalStateException(); // make compiler happy
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
        Pair<YamlDocument, String> r;
        if (value != null) {
            r = resolveFull(fullPath);
        } else {
            var op = resolve(fullPath);
            if (!op.isPresent())
                return;
            r = op.get();
        }
        r.getFirst().placeNewValue(r.getSecond(), value);
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
    public boolean isRoot() {
        return root == this;
    }

    @Override
    public void setConversionPolicy(@NotNull ConversionPolicy policy) {
        root.conversionPolicy = policy;
    }

    @Override
    public boolean isSet(@NotNull String path) {
        return resolveChild(path)
                .map(Pair::getSecond)
                .map(o -> !(o instanceof YamlDocument))
                .orElse(false);
    }

    @Override
    public boolean contains(@NotNull String path) {
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
    public @NotNull YamlDocument createSub(@NotNull String fullPath) {
        var r = resolveFull(fullPath);
        return r.getFirst().placeNewSub(r.getSecond());
    }

    @Override
    public @NotNull Collection<? extends YamlSub> getSubs() {
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

    @Override
    public String toString() {
        return getValues(true).toString();
    }

    /* helpers */

    // attempts to resolve the full path into a valid sub and a key
    // if `createSub` is true, resolution success is guaranteed
    // note that the key is not guaranteed to exist in the sub
    // returns pair: (sub, key)
    private Optional<Pair<YamlDocument, String>> resolve(final String fullPath, boolean createSub) {
        String restPath = fullPath;
        YamlDocument doc = this;
        int i;
        while ((i = restPath.indexOf(PATH_SEPARATOR)) >= 0) {
            String key = restPath.substring(0, i);
            restPath = restPath.substring(i + PATH_SEPARATOR.length());

            if (doc.children.containsKey(key) && doc.children.get(key).getSecond() instanceof YamlDocument) {
                doc = (YamlDocument) doc.children.get(key).getSecond();
                // sub does exist
                // continue with that
                continue;
            }

            // sub does not exist
            if (!createSub) {
                // not allowed to create a new sub or overwrite existing value with one
                // indicate failure
                return Optional.empty();
            }

            // create/overwrite
            doc = doc.placeNewSub(key);
        }

        // successfully resolved
        return Optional.of(new Pair<>(doc, restPath));
    }

    // attempts to resolve the full path into a valid sub and a key
    // resolution success is not guaranteed
    // note that the key is not guaranteed to exist in the sub
    // returns pair: (sub, key)
    private Optional<Pair<YamlDocument, String>> resolve(final String fullPath) {
        return resolve(fullPath, false);
    }

    // resolves the full path into a valid sub and key
    // creates new subs if necessary
    private Pair<YamlDocument, String> resolveFull(final String fullPath) {
        return resolve(fullPath, true)
                .orElseThrow(() -> new IllegalStateException("Resolution failure"));
    }

    // returns pair: (key node, child)
    Optional<Pair<Node, Object>> resolveChild(final String fullPath) {
        return resolve(fullPath)
                // pair: (doc, key)
                .map(pair -> pair.getFirst().children.get(pair.getSecond()));
    }

    // returns key-node
    private Optional<Node> resolveKeyNode(String fullPath) {
        return resolveChild(fullPath)
                // pair: (keyNode, child)
                .map(Pair::getFirst);
    }

    // adds a keyed node to the current node
    // assumes the current node is able to hold other keyed nodes
    private Node addNode(String key, Node node) {
        MappingNode root = selfRoot();
        List<NodeTuple> tuples = new ArrayList<>(root.getValue());

        Node keyNode = new ScalarNode(Tag.STR, key, null, null, DumperOptions.ScalarStyle.PLAIN);
        tuples.add(new NodeTuple(keyNode, node));

        root.setValue(tuples);

        return keyNode;
    }

    // deletes a keyed node from the current node
    // assumes the current node is able to hold other keyed nodes
    private void delNode(String key) {
        MappingNode root = selfRoot();
        List<NodeTuple> tuples = new ArrayList<>(root.getValue());
        tuples.removeIf(tuple -> ((ScalarNode) tuple.getKeyNode()).getValue().equals(key));
        root.setValue(tuples);
    }

    // places a new sub at the specified key
    // overwrites any other associated value
    // returns the new sub
    private YamlDocument placeNewSub(String key) {
        // create new sub
        Node keyNode, valueNode = key.isEmpty() ? null : new MappingNode(Tag.MAP, new ArrayList<>(), DumperOptions.FlowStyle.AUTO);

        if (children.containsKey(key)) {
            // replace old node
            keyNode = replaceNode(key, valueNode);
        } else {
            // add new node to current
            keyNode = addNode(key, valueNode);
        }

        YamlDocument sub = constructChild(key, valueNode);
        children.put(key, new Pair<>(keyNode, sub));
        return sub;
    }

    // places a new value at the specified key
    // overwrites any other associated value
    private void placeNewValue(String key, Object value) {
        if (value == null) {
            // remove node
            children.remove(key);
            delNode(key);
            return;
        }

        Node keyNode;
        Node valueNode = serializer.represent(value);

        if (key.isEmpty()) {
            // overwrite current node
            replaceNode(keyNode = valueNode);
            children.clear();
        } else if (children.containsKey(key)) {
            // replace already existing node
            keyNode = replaceNode(key, valueNode);
        } else {
            // add node
            keyNode = addNode(key, valueNode);
        }

        // overwrite potential old values
        children.put(key, new Pair<>(keyNode, value));
    }

    // replaces the current node with a new one
    void replaceNode(Node newNode) {
        if (!isRoot()) {
            // update parent node

            var child = parent.children.get(key);
            if (child.getSecond() != this) { // value of child should be this instance
                throw new IllegalStateException("Parent has stored another value than acceptable");
            }
            child.setFirst(newNode);

            parent.replaceNode(key, newNode);
        }

        // update this node
        this.node = newNode;
    }

    // replaces a specific keyed node within the current node with another node
    // assumes a node with that specific key already exists
    // assumes the current node is able to hold other keyed nodes
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

    // fetches the current node as mapping node (that is able to hold keyed values)
    // throws an exception if current node cannot hold keyed values
    private MappingNode selfRoot() {
        if (node instanceof MappingNode) {
            return (MappingNode) node;
        }
        throw new IllegalStateException("Sub cannot hold keyed values.");
    }
}

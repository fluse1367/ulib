package eu.software4you.ulib.core.impl.configuration.yaml;

import eu.software4you.ulib.core.configuration.YamlConfiguration;
import eu.software4you.ulib.core.impl.configuration.ConfigurationBase;
import eu.software4you.ulib.core.util.Expect;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.nodes.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class YamlDocument extends ConfigurationBase<YamlDocument> implements YamlConfiguration {

    final Map<String, Node> childNodes = new HashMap<>();
    private final YamlSerializer serializer;
    // node that this sub represents
    // (value node)
    Node node;

    // constructor for empty root
    protected YamlDocument(YamlSerializer serializer) {
        super(); // init as root
        this.serializer = serializer;
        this.node = new MappingNode(Tag.MAP, new ArrayList<>(), DumperOptions.FlowStyle.AUTO);
    }

    // constructor for deserialized root
    protected YamlDocument(YamlSerializer serializer, Reader reader) throws IOException {
        super(); // init as root
        this.serializer = serializer;
        reinit(reader).rethrow(IOException.class);
    }

    // constructor for sub
    protected YamlDocument(YamlDocument parent, String key, Node valueNode) {
        super(parent.getRoot(), parent, key); // init as child
        this.serializer = getRoot().serializer;
        this.node = valueNode;
    }

    // constructs a new sub
    // to be overridden by subclasses
    protected YamlDocument constructChild(String key, Node valueNode) {
        return new YamlDocument(this, key, valueNode);
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
    public void purge() {
        clear();
        Node newNode = new MappingNode(Tag.MAP, new ArrayList<>(), DumperOptions.FlowStyle.AUTO);
        if (isRoot()) {
            node = newNode;
        } else {
            replaceNode(newNode);
        }
    }

    // serializer access

    void clear() {
        children.clear();
        childNodes.clear();
    }

    void put(String key, Object val) {
        children.put(key, val);
    }

    @Override
    public Expect<Void, IOException> reinit(Reader reader) {
        return Expect.compute(() -> serializer.deserialize(reader, this));
    }

    @Override
    public Expect<Void, IOException> dump(Writer writer) {
        return Expect.compute(() -> serializer.serialize(this, writer));
    }

    @Override
    public String toString() {
        return getValues(true).toString();
    }

    // returns key-node
    private Optional<Node> resolveKeyNode(String fullPath) {
        return resolve(fullPath)
                // pair: (sub, key)
                .map(pair -> pair.getFirst().childNodes.get(pair.getSecond()));
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

    protected YamlDocument constructSub(String key) {
        // create new sub
        Node keyNode, valueNode = key.isEmpty() ? null : new MappingNode(Tag.MAP, new ArrayList<>(), DumperOptions.FlowStyle.AUTO);

        if (children.containsKey(key)) {
            // replace old node
            keyNode = replaceNode(key, valueNode);
        } else {
            // add new node to current
            keyNode = addNode(key, valueNode);
        }

        // put node
        childNodes.put(key, keyNode);

        return constructChild(key, valueNode);
    }

    protected void placedNewValue(String key, Object value) {
        if (value == null) {
            // remove node
            childNodes.remove(key);
            delNode(key);
            return;
        }

        Node keyNode;
        Node valueNode = serializer.represent(value);

        if (key.isEmpty()) {
            // overwrite current node
            replaceNode(keyNode = valueNode);
            clear();
        } else if (children.containsKey(key)) {
            // replace already existing node
            keyNode = replaceNode(key, valueNode);
        } else {
            // add node
            keyNode = addNode(key, valueNode);
        }

        // overwrite potential old node
        childNodes.put(key, keyNode);
    }

    // replaces the current node with a new one
    void replaceNode(Node newNode) {
        if (!isRoot()) {
            // update reference to this node in parent node
            getParent().replaceNode(getKey(), newNode);
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

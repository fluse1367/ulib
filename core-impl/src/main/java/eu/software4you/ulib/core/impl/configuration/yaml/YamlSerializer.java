package eu.software4you.ulib.core.impl.configuration.yaml;

import eu.software4you.ulib.core.api.common.collection.Pair;
import eu.software4you.ulib.core.api.configuration.yaml.YamlSub;
import eu.software4you.ulib.core.api.io.IOUtil;
import eu.software4you.ulib.core.impl.configuration.SerializationAdapters;
import lombok.Getter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.nodes.AnchorNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class YamlSerializer {
    /* singleton */
    @Getter
    private static final YamlSerializer instance = new YamlSerializer();

    @Getter
    private final SerializationAdapters adapters;
    private final Yaml yaml;
    private final YamlConstructor constructor;

    private YamlSerializer() {
        LoaderOptions loaderConfig = new LoaderOptions();

        DumperOptions dumperConfig = new DumperOptions();
        dumperConfig.setIndent(2);


        var representer = new YamlRepresenter(dumperConfig);

        this.adapters = new SerializationAdapters(representer);
        this.constructor = new YamlConstructor(loaderConfig);

        this.yaml = new Yaml(constructor, representer,
                dumperConfig, loaderConfig);
    }

    public ExtYamlDocument createNew() {
        return new ExtYamlDocument(this);
    }

    public YamlDocument deserialize(Reader reader) throws IOException {
        return new ExtYamlDocument(this, reader);
    }

    void deserialize(Reader reader, YamlDocument doc) throws IOException {
        var content = new String(IOUtil.read(reader)); // copy contents

        Node root = yaml.compose(new StringReader(content));

        // clear doc
        doc.children.clear();
        // replace node
        doc.replaceNode(extractAnchor(root));

        if (root instanceof MappingNode) {
            graph(doc, (MappingNode) root, "\n" + content, 0);
        } else {
            doc.children.put("", new Pair<>(doc.node, read(doc.node)));
        }
    }

    void serialize(YamlSub sub, Writer writer) throws IOException {
        if (!(sub instanceof YamlDocument))
            throw new IllegalArgumentException("Serialization of " + sub.getClass().getName() + " not supported");

        StringWriter dumper = new StringWriter();

        Node root = ((YamlDocument) sub).node;

        if (root != null) {
            yaml.serialize(root, dumper);
        } else {
            dumper.write("");
            dumper.flush();
        }

        // replace
        String output = dumper.toString()
                .replaceAll("(^|\\n)\\s*#($|\\n)", "$1$2") // replace empty comments with empty lines
                .replaceAll("((?:^|\\n)\\s*)#", "$1# ") // add space before comment
                ;

        IOUtil.write(new StringReader(output), writer);
    }

    Node represent(Object data) {
        return yaml.represent(data);
    }

    private YamlDocument graph(YamlDocument parent, MappingNode root, final String content, final int start) {
        root = extractAnchor(root);

        AtomicInteger ai = new AtomicInteger(start);
        root.getValue().forEach(nt -> {
            ScalarNode keyNode = (ScalarNode) nt.getKeyNode();
            String key = keyNode.getValue();
            Node node = extractAnchor(nt.getValueNode());

            int s = ai.getAndSet(node.getEndMark().getIndex());
            s = content.indexOf("\n", s) + 1;
            Mark m = keyNode.getStartMark();
            int e = m.getIndex() - m.getColumn();
            keyNode.setBlockComments(comment(content, s, e));

            Object value;
            if (node instanceof MappingNode mNode) {
                if (!constructor.isSerialized(mNode) || (value = constructor.getSerializationConstruct().construct(mNode)) == null) {
                    value = graph(parent.constructChild(key, node), mNode,
                            content, keyNode.getEndMark().getIndex());
                }
                // get last child to set `ai` to correct position
                ai.set(getLastChild(mNode).getEndMark().getIndex());
            } else {
                value = read(node);
            }
            parent.children.put(key, new Pair<>(keyNode, value));
        });

        return parent;
    }

    private Node getLastChild(MappingNode root) {
        var li = root.getValue();
        var last = li.get(li.size() - 1);
        var node = last.getValueNode();

        if (node instanceof MappingNode)
            return getLastChild((MappingNode) node);
        return node;
    }

    private List<CommentLine> comment(String content, int s, int e) {
        if (s >= e)
            return Collections.emptyList();
        String comment = content.substring(s, e);

        List<String> lines = getLines(comment);

        return lines.stream()
                .map(line -> line.trim().startsWith("# ") ? line.substring(line.indexOf("# ") + 2) : line)
                .map(line -> new CommentLine(null, null, line, CommentType.BLOCK))
                .collect(Collectors.toList());
    }

    private List<String> getLines(String s) {
        List<String> lines = new ArrayList<>();
        int i;
        while ((i = s.indexOf("\n")) != -1) {
            lines.add(s.substring(0, i));
            s = s.substring(i + 1);
        }
        lines.add(s); // add what's left
        return lines;
    }

    private <T extends Node> T extractAnchor(T node) {
        if (node instanceof AnchorNode)
            return (T) extractAnchor(((AnchorNode) node).getRealNode());
        return node;
    }

    private Object read(Node node) {
        return constructor.construct(node);
    }

}

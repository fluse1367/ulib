package eu.software4you.ulib.core.impl.configuration.yaml;

import eu.software4you.ulib.core.configuration.YamlConfiguration;
import eu.software4you.ulib.core.impl.configuration.SerializationAdapters;
import eu.software4you.ulib.core.io.IOUtil;
import lombok.Getter;
import org.yaml.snakeyaml.*;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.nodes.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class YamlSerializer {
    /* singleton */
    @Getter
    private static final YamlSerializer instance = new YamlSerializer();

    private final Yaml yaml;
    private final YamlConstructor constructor;

    private YamlSerializer() {
        LoaderOptions loaderConfig = new LoaderOptions();

        DumperOptions dumperConfig = new DumperOptions();
        dumperConfig.setIndent(2);
        dumperConfig.setProcessComments(true);

        var representer = new YamlRepresenter(dumperConfig);
        SerializationAdapters.getInstance().addHook(representer);

        this.constructor = new YamlConstructor(loaderConfig);

        this.yaml = new Yaml(constructor, representer,
                dumperConfig, loaderConfig);
    }

    public YamlDocument createNew() {
        return new YamlDocument(this);
    }

    public YamlDocument deserialize(Reader reader) throws IOException {
        return new YamlDocument(this, reader);
    }

    void deserialize(Reader reader, YamlDocument doc) throws IOException {
        var content = new String(IOUtil.read(reader).orElseRethrow(IOException.class)); // copy contents

        Node root = extractAnchor(yaml.compose(new StringReader(content)));
        if (root == null) {
            doc.clear();
            return;
        }

        // clear doc
        doc._clear();
        // replace node
        doc.replaceNode(extractAnchor(root));

        if (root instanceof MappingNode) {
            graph(doc, (MappingNode) root, "\n" + content, 0);
        } else {
            var val = read(doc.node);
            doc.put("", val);
            doc.childNodes.put("", doc.node);
        }
    }

    void serialize(YamlConfiguration sub, Writer writer) throws IOException {
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

        IOUtil.write(new StringReader(output), writer).rethrow(IOException.class);
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

            if (node instanceof CollectionNode<?> collNode) {
                // get last child to set `ai` to correct position
                ai.set(getLastChild(collNode).getEndMark().getIndex());
            }
            parent.put(key, process(parent, keyNode, node, content));
            parent.childNodes.put(key, keyNode);
        });

        return parent;
    }

    private Object process(final YamlDocument parent, final ScalarNode keyNode, final Node node, final String content) {
        if (!(node instanceof CollectionNode<?> cn))
            return read(node);

        if (cn instanceof MappingNode mn) {
            Object val;
            if (!constructor.isSerialized(mn) || (val = constructor.getSerializationConstruct().construct(mn)) == null) {
                return graph(parent.constructChild(keyNode.getValue(), mn), mn,
                        content, keyNode.getEndMark().getIndex());
            }
            return val;
        }

        if (!(cn instanceof SequenceNode sn))
            throw new IllegalStateException();

        return sn.getValue().stream()
                .map(n -> process(parent, keyNode, n, content))
                .toList();
    }

    private Node getLastChild(CollectionNode<?> root) {
        var li = root.getValue();
        if (li.isEmpty()) {
            return root;
        }
        var elem = li.get(li.size() - 1);

        Node node;
        if (elem instanceof NodeTuple tuple) {
            node = tuple.getValueNode();
        } else if (elem instanceof Node n) {
            node = n;
        } else {
            throw new IllegalStateException();
        }

        if (node instanceof CollectionNode<?> cn)
            return getLastChild(cn);
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

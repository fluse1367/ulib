package eu.software4you.ulib.impl.configuration.yaml;

import eu.software4you.configuration.yaml.YamlSub;
import eu.software4you.utils.IOUtil;
import lombok.val;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.nodes.*;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class YamlSerializer {
    /* singleton */
    private static final YamlSerializer instance = new YamlSerializer();

    private final Yaml yaml;
    private final YamlConstructor constructor;

    private YamlSerializer() {
        LoaderOptions loaderConfig = new LoaderOptions();

        DumperOptions dumperConfig = new DumperOptions();
        dumperConfig.setIndent(2);

        constructor = new YamlConstructor(loaderConfig);
        yaml = new Yaml(constructor, new Representer(), dumperConfig, loaderConfig);
    }

    public static YamlSerializer getInstance() {
        return instance;
    }

    public YamlDocument createNew() {
        val doc = new YamlDocument(this);
        doc.node = new MappingNode(Tag.MAP, new ArrayList<>(), DumperOptions.FlowStyle.AUTO);
        return doc;
    }

    public YamlDocument deserialize(Reader reader) throws IOException {
        YamlDocument doc = new YamlDocument(this);
        doc.load(reader);
        return doc;
    }

    void deserialize(Reader reader, YamlDocument doc) throws IOException {
        val content = new String(IOUtil.read(reader)); // copy contents

        Node root = yaml.compose(new StringReader(content));

        // clear doc
        doc.clear();

        // replace root
        doc.replaceNode(extractAnchor(root));

        if (root instanceof MappingNode) {
            graph(doc, (MappingNode) root, "\n" + content, 0);
        } else {
            doc.keyNodes.put("", doc.node);
            doc.data.put("", read(doc.node));
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

            if (node instanceof MappingNode) {
                MappingNode mNode = (MappingNode) node;
                parent.children.put(key, graph(new YamlDocument(parent, key, node), mNode,
                        content, keyNode.getEndMark().getIndex()));

                // get last child to set `ai` to correct position
                ai.set(getLastChild(mNode).getEndMark().getIndex());
            } else {
                parent.data.put(key, read(node));
            }
            parent.keyNodes.put(key, keyNode);
        });

        return parent;
    }

    private Node getLastChild(MappingNode root) {
        val li = root.getValue();
        val last = li.get(li.size() - 1);
        val node = last.getValueNode();

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

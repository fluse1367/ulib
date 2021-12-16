package eu.software4you.ulib.loader.install;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Objects;
import java.util.regex.Pattern;

import static eu.software4you.ulib.loader.install.Util.write;

@RequiredArgsConstructor
final class DependencyDownloader {
    static final Pattern PATTERN = Pattern.compile("([a-zA-Z-0-9._]+):([a-zA-Z-0-9._]+):([a-zA-Z-0-9._]+)\\b", Pattern.MULTILINE);
    private final String url;

    DependencyDownloader() {
        this("https://repo1.maven.org/maven2/");
    }

    @SneakyThrows
    public File download(String coords, File dir) {
        if (!Objects.requireNonNull(coords).matches(PATTERN.pattern()))
            throw new IllegalArgumentException("Malformed coords: " + coords);

        var matcher = PATTERN.matcher(coords);
        if (!matcher.find())
            throw new IllegalStateException();


        String group = matcher.group(1);
        String name = matcher.group(2);
        String version = matcher.group(3);

        String request = String.format("%s/%s/%s/%s-%s.jar",
                group.replace(".", "/"), name, version, name, version);

        File dest = new File(dir, request);

        if (!dest.exists()) {
            dest.getParentFile().mkdirs();

            write(new URL(url + request).openStream(), new FileOutputStream(dest));
        }

        return dest;
    }

}

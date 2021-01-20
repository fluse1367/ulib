package eu.software4you.aether;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.eclipse.aether.repository.RemoteRepository;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Repository {
    public static final Repository MAVEN_CENTRAL = Repository.notPooled("central", "https://repo1.maven.org/maven2/");
    public static final Repository JITPACK = Repository.notPooled("jitpack", "https://jitpack.io");
    public static final Repository JCENTER = Repository.notPooled("jcenter", "https://jcenter.bintray.com");
    public static final Repository SONATYPE = Repository.notPooled("sonatype", "https://oss.sonatype.org/content/repositories/releases");

    private static final Map<String, Repository> repositories = new HashMap<>();
    @Getter(AccessLevel.PACKAGE)
    private final RemoteRepository repository;

    public static Repository of(String id, String url) {
        Repository repo;
        if (repositories.containsKey(id) && (repo = repositories.get(id)).repository.getUrl().equals(url)) {
            return repo;
        }
        repositories.put(id, repo = new Repository(new RemoteRepository.Builder(id, "default", url).build()));
        return repo;
    }

    private static Repository notPooled(String id, String url) {
        return new Repository(new RemoteRepository.Builder(id, "default", url).build()) {
            @Override
            public boolean unpool() {
                throw new UnsupportedOperationException();
            }
        };
    }

    String getUrl() {
        return repository.getUrl();
    }

    @Deprecated
    public boolean unpool() {
        return repositories.remove(repository.getId(), this);
    }
}

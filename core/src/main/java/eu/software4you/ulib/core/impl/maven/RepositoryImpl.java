package eu.software4you.ulib.core.impl.maven;

import eu.software4you.ulib.core.dependencies.Repository;
import lombok.Getter;
import org.eclipse.aether.repository.RemoteRepository;

import java.util.HashMap;
import java.util.Map;

@Getter
public final class RepositoryImpl implements Repository {

    private static final Map<String, RepositoryImpl> cache = new HashMap<>();

    static {
        // cache default (common) repositories
        cache("central", "https://repo1.maven.org/maven2/");
        cache("jitpack", "https://jitpack.io");
        cache("jcenter", "https://jcenter.bintray.com");
        cache("sonatype", "https://oss.sonatype.org/content/repositories/releases");
    }

    private static void cache(String id, String url) {
        cache.put(id, new RepositoryImpl(id, url));
    }

    public static Repository of(String id, String url) {
        if (!cache.containsKey(id))
            cache(id, url);
        return cache.get(id);
    }

    public static Repository of(String id) {
        return cache.get(id);
    }

    static RepositoryImpl convertFrom(Repository repo) {
        if (repo instanceof RepositoryImpl impl)
            return impl;

        return (RepositoryImpl) of(repo.getId(), repo.getUrl());
    }


    private final String id;
    private final String url;
    private final RemoteRepository repository;

    private RepositoryImpl(String id, String url) {
        this.id = id;
        this.url = url;
        this.repository = new RemoteRepository.Builder(id, "default", url).build();
    }
}

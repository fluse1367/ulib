package eu.software4you.ulib.impl.dependencies;

import eu.software4you.dependencies.Repositories;
import eu.software4you.dependencies.Repository;
import eu.software4you.ulib.inject.Impl;
import lombok.Getter;
import org.eclipse.aether.repository.RemoteRepository;

import java.util.HashMap;
import java.util.Map;

@Impl(value = Repositories.class, priority = Integer.MAX_VALUE - 2)
final class RepositoriesImpl extends Repositories {
    private final Map<String, Repo> repos = new HashMap<>();

    private RepositoriesImpl() {
        // cache default (common) repositories
        cache("central", "https://repo1.maven.org/maven2/");
        cache("jitpack", "https://jitpack.io");
        cache("jcenter", "https://jcenter.bintray.com");
        cache("sonatype", "https://oss.sonatype.org/content/repositories/releases");
    }

    private void cache(String id, String url) {
        repos.put(id, new Repo(id, url));
    }

    @Override
    public Repository of0(String id, String url) {
        if (!repos.containsKey(id))
            cache(id, url);
        return repos.get(id);
    }

    @Override
    public Repository of0(String id) {
        return repos.get(id);
    }

    @Getter
    protected static class Repo implements Repository {
        private final String id;
        private final String url;
        private final RemoteRepository repository;

        public Repo(String id, String url) {
            this.id = id;
            this.url = url;
            this.repository = new RemoteRepository.Builder(id, "default", url).build();
        }
    }
}

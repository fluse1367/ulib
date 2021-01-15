package eu.software4you.aether;

import eu.software4you.ulib.ULib;
import eu.software4you.utils.ClassPathHacker;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.graph.visitor.PreorderNodeListGenerator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MavenRepository {
    public static final Repository MAVEN_CENTRAL = Repository.notPooled("central", "https://repo1.maven.org/maven2/");
    public static final Repository JITPACK = Repository.notPooled("jitpack", "https://jitpack.io");
    public static final Repository JCENTER = Repository.notPooled("jcenter", "https://jcenter.bintray.com");
    public static final Repository SONATYPE = Repository.notPooled("sonatype", "https://oss.sonatype.org/content/repositories/releases");

    private static final RepositorySystem LOCAL_M2_REPOSITORY_SYSTEM = _newRepositorySystem();
    private static final RepositorySystemSession LOCAL_M2_REPOSITORY_SESSION = _newSession(LOCAL_M2_REPOSITORY_SYSTEM);

    @Deprecated
    private static RepositorySystem _newRepositorySystem() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);

        return locator.getService(RepositorySystem.class);
    }

    @Deprecated
    private static RepositorySystemSession _newSession(RepositorySystem system) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

        LocalRepository localRepo = new LocalRepository(ULib.getInstance().getLibsM2Dir());
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));

        return session;
    }

    public static void requireLibrary(String coords, String testClass) {
        requireLibrary(coords, testClass, MAVEN_CENTRAL);
    }

    public static void requireLibrary(String coords, String testClass, Consumer<File> loader) {
        requireLibrary(coords, testClass, MAVEN_CENTRAL, loader);
    }

    public static void requireLibrary(String coords, String testClass, Repository repository) {
        requireLibrary(coords, testClass, repository, ClassPathHacker::addFile);
    }

    @SneakyThrows
    public static void requireLibrary(String coords, String testClass, Repository repository, Consumer<File> loader) {
        ULib.getInstance().getLogger().fine(String.format("Soft-Requiring %s from repo %s", coords, repository.getUrl()));
        UnsafeLibraries.classTest(testClass, coords, () -> requireLibrary(coords, repository, loader));
    }

    public static void requireLibrary(String coords) {
        requireLibrary(coords, MAVEN_CENTRAL);
    }

    public static void requireLibrary(String coords, Consumer<File> loader) {
        requireLibrary(coords, MAVEN_CENTRAL, loader);
    }

    public static void requireLibrary(String coords, Repository repository) {
        requireLibrary(coords, repository, ClassPathHacker::addFile);
    }

    @SneakyThrows
    public static void requireLibrary(String coords, Repository repository, Consumer<File> loader) {
        ULib.getInstance().getLogger().fine(String.format("Requiring %s from repo %s", coords, repository.getUrl()));

        Dependency dependency =
                new Dependency(new DefaultArtifact(coords), "compile");

        RepositorySystem repoSystem = LOCAL_M2_REPOSITORY_SYSTEM;

        RepositorySystemSession session = LOCAL_M2_REPOSITORY_SESSION;

        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot(dependency);

        collectRequest.addRepository(repository.repository);

        DependencyNode node = repoSystem.collectDependencies(session, collectRequest).getRoot();

        DependencyRequest dependencyRequest = new DependencyRequest();
        dependencyRequest.setRoot(node);

        repoSystem.resolveDependencies(session, dependencyRequest);

        PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
        node.accept(nlg);

        for (Artifact resolvedArtifact : nlg.getArtifacts(false)) {
            loader.accept(resolvedArtifact.getFile());
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Repository {
        private static final Map<String, Repository> repositories = new HashMap<>();
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

        private String getUrl() {
            return repository.getUrl();
        }

        @Deprecated
        public boolean unpool() {
            return repositories.remove(repository.getId(), this);
        }
    }
}

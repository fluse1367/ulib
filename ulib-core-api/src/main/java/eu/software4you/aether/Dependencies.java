package eu.software4you.aether;

import eu.software4you.ulib.ULib;
import eu.software4you.utils.JarLoader;
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
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.graph.visitor.PreorderNodeListGenerator;

import java.io.File;
import java.util.function.Consumer;

import static eu.software4you.aether.Repository.*;

public class Dependencies {
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

    public static void depend(String coords, String testClass) {
        depend(coords, testClass, MAVEN_CENTRAL);
    }

    public static void depend(String coords, String testClass, Consumer<File> loader) {
        depend(coords, testClass, MAVEN_CENTRAL, loader);
    }

    public static void depend(String coords, String testClass, Repository repository) {
        depend(coords, testClass, repository, JarLoader::load);
    }

    @SneakyThrows
    public static void depend(String coords, String testClass, Repository repository, Consumer<File> loader) {
        ULib.getInstance().getLogger().fine(String.format("Depending on %s from repo %s", coords, repository.getUrl()));
        UnsafeDependencies.classTest(testClass, coords, () -> depend(coords, repository, loader));
    }

    public static void depend(String coords) {
        depend(coords, MAVEN_CENTRAL);
    }

    public static void depend(String coords, Consumer<File> loader) {
        depend(coords, MAVEN_CENTRAL, loader);
    }

    public static void depend(String coords, Repository repository) {
        depend(coords, repository, JarLoader::load);
    }

    @SneakyThrows
    public static void depend(String coords, Repository repository, Consumer<File> loader) {
        ULib.getInstance().getLogger().fine(String.format("Depending on %s from repo %s", coords, repository.getUrl()));

        Dependency dependency =
                new Dependency(new DefaultArtifact(coords), "compile");

        RepositorySystem repoSystem = LOCAL_M2_REPOSITORY_SYSTEM;

        RepositorySystemSession session = LOCAL_M2_REPOSITORY_SESSION;

        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot(dependency);

        collectRequest.addRepository(repository.getRepository());

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
}

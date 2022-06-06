package eu.software4you.ulib.core.impl.maven;

import eu.software4you.ulib.core.dependencies.Repository;
import eu.software4you.ulib.core.impl.Internal;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.resolution.*;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

public class MavenController {
    private static final RepositorySystem REPOSITORY;
    private static final DefaultRepositorySystemSession SESSION;

    static {
        // create thingy that downloads artifacts
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);

        // create actual repository
        REPOSITORY = locator.getService(RepositorySystem.class);
        SESSION = MavenRepositorySystemUtils.newSession();

        // get sure artifacts are valid
        SESSION.setChecksumPolicy(RepositoryPolicy.CHECKSUM_POLICY_FAIL);


        LocalRepository lr = new LocalRepository(Internal.getLocalMavenDir());
        SESSION.setLocalRepositoryManager(REPOSITORY.newLocalRepositoryManager(SESSION, lr));

        // lock session
        SESSION.setReadOnly();
    }

    public static Stream<Path> require(String coords, Collection<Repository> repos) throws Exception {
        Dependency dependency = new Dependency(new DefaultArtifact(coords), "compile");


        // build dependency graph
        CollectResult graph = REPOSITORY.collectDependencies(SESSION,
                new CollectRequest(dependency, repos.stream()
                        .map(RepositoryImpl::convertFrom)
                        .map(RepositoryImpl::getRepository)
                        .toList()));

        // resolve dependencies
        DependencyResult res = REPOSITORY.resolveDependencies(SESSION, new DependencyRequest(graph.getRoot(), null));

        return res.getArtifactResults().stream()
                .map(ArtifactResult::getArtifact)
                .filter(Objects::nonNull) // filter out unresolved
                .map(Artifact::getFile)
                .map(File::toPath);
    }
}

package eu.software4you.ulib.core.impl.dependencies;

import eu.software4you.ulib.core.ULib;
import eu.software4you.ulib.core.agent.Agent;
import eu.software4you.ulib.core.api.dependencies.Dependencies;
import eu.software4you.ulib.core.api.dependencies.DependencyLoader;
import eu.software4you.ulib.core.api.dependencies.Repositories;
import eu.software4you.ulib.core.api.dependencies.Repository;
import eu.software4you.ulib.core.impl.dependencies.RepositoriesImpl.Repo;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transfer.AbstractTransferListener;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transport.http.HttpTransporterFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;

public final class DependenciesImpl extends Dependencies {

    static {
        if (Agent.available()) {
            UnsafeUtil.dependUnsafe("{{maven.http-core}}");
            UnsafeUtil.dependUnsafe("{{maven.http-client}}");
            UnsafeUtil.dependUnsafe("{{maven.commons-logging}}");
        }
    }

    private final RepositorySystem repository;
    private final DefaultRepositorySystemSession session;
    private boolean fallback;

    public DependenciesImpl() {
        resetFallbackPolicy0();

        // create thingy that downloads artifacts
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);

        // create actual repository
        this.repository = locator.getService(RepositorySystem.class);
        this.session = MavenRepositorySystemUtils.newSession();

        // get sure artifacts are valid
        this.session.setChecksumPolicy(RepositoryPolicy.CHECKSUM_POLICY_FAIL);


        LocalRepository lr = new LocalRepository(ULib.get().getLibrariesDir());
        this.session.setLocalRepositoryManager(repository.newLocalRepositoryManager(session, lr));

        // some logging
        this.session.setTransferListener(new AbstractTransferListener() {
            @Override
            public void transferStarted(TransferEvent e) {
                ULib.logger().finer(() -> String.format("[Dependencies] Downloading %s", e.getResource().getResourceName()));
            }

            @Override
            public void transferFailed(TransferEvent e) {
                ULib.logger().finer(() -> String.format("[Dependencies] Download of %s failed.", e.getResource().getResourceName()));
            }
        });

        // lock session
        this.session.setReadOnly();
    }

    @Override
    protected void setFallbackPolicy0(boolean fallback) {
        this.fallback = fallback;
    }

    @Override
    protected void resetFallbackPolicy0() {
        this.fallback = true;
    }

    @Override
    protected void depend0(String coords, Repository repository, ClassLoader cl) {
        depend0(coords, repository, f -> DependencyLoader.load(f, cl, this.fallback));
    }

    @Override
    protected void depend0(String coords, Repository r, Consumer<File> loader) {
        Repo repo = (Repo) r;

        ULib.logger().fine(() -> String.format("Depending on %s from repo %s", coords, repo.getUrl()));

        Dependency dependency = new Dependency(new DefaultArtifact(coords), "compile");


        // build dependency graph
        CollectResult graph;
        try {
            var repos = new ArrayList<>(Collections.singletonList(repo.getRepository()));
            if (repo != Repositories.mavenCentral())
                repos.add(((Repo) Repositories.mavenCentral()).getRepository());

            graph = repository.collectDependencies(session,
                    new CollectRequest(dependency, repos));
        } catch (DependencyCollectionException e) {
            ULib.logger().log(Level.SEVERE, e, () -> String.format("Could not build dependency graph for %s", coords));
            return;
        }

        // resolve dependencies
        DependencyResult res;
        try {
            res = repository.resolveDependencies(session, new DependencyRequest(graph.getRoot(), null));
        } catch (DependencyResolutionException e) {
            ULib.logger().log(Level.SEVERE, e, () -> String.format("Could not resolve dependency %s", coords));
            return;
        }

        // apply loader
        res.getArtifactResults().stream()
                .map(ArtifactResult::getArtifact)
                .filter(Objects::nonNull) // filter out unresolved
                .map(Artifact::getFile)
                .forEach(loader);
    }

    @Override
    protected void depend0(String coords, String testClass, Repository repository, ClassLoader source) {
        if (!available(testClass, source))
            depend0(coords, repository, source);
    }

    @Override
    protected void depend0(String coords, String testClass, ClassLoader testLoader, Repository repository, Consumer<File> loader) {
        if (!available(testClass, testLoader))
            depend0(coords, repository, loader);
    }

    private boolean available(String className, ClassLoader cl) {
        try {
            ULib.logger().finer(() -> String.format("Testing for class %s in %s", className, cl));
            Class.forName(className, false, cl);
        } catch (ClassNotFoundException e) {
            ULib.logger().finer(() -> String.format("Class %s NOT found in %s", className, cl));
            return false;
        }
        ULib.logger().finer(() -> String.format("Class %s found in %s", className, cl));
        return true;
    }
}

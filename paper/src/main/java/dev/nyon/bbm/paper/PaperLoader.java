package dev.nyon.bbm.paper;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings({ "UnstableApiUsage", "unused" })
public final class PaperLoader implements PluginLoader {
    @Override
    public void classloader(@NotNull PluginClasspathBuilder builder) {
        final MavenLibraryResolver resolver = new MavenLibraryResolver();
        resolver.addRepository(new RemoteRepository.Builder(
            "nyonReleases",
            "default",
            "https://repo.nyon.dev/releases"
        ).build());
        resolver.addRepository(new RemoteRepository.Builder(
            "central",
            "default",
            MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR
        ).build());

        List.of(
            "dev.nyon:konfig:3.0.1",
            "org.jetbrains.kotlin:kotlin-stdlib:2.3.10",
            "org.jetbrains.kotlinx:kotlinx-serialization-core:1.10.0",
            "org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0"
        ).forEach(coordinate -> resolver.addDependency(new Dependency(
            new DefaultArtifact(coordinate),
            null
        )));
        builder.addLibrary(resolver);
    }
}

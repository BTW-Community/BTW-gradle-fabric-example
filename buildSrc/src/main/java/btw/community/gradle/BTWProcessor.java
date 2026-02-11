package btw.community.gradle;

import net.fabricmc.loom.api.mappings.layered.MappingsNamespace;
import net.fabricmc.loom.api.processor.MinecraftJarProcessor;
import net.fabricmc.loom.api.processor.ProcessorContext;
import net.fabricmc.loom.api.processor.SpecContext;
import net.fabricmc.loom.util.FileSystemUtil;
import net.fabricmc.loom.util.LazyCloseable;
import net.fabricmc.tinyremapper.OutputConsumerPath;
import net.fabricmc.tinyremapper.TinyRemapper;
import org.jspecify.annotations.Nullable;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class BTWProcessor implements MinecraftJarProcessor<BTWProcessor.Spec> {
    private final BTWGradleExtensionImpl extension;

    @Inject
    public BTWProcessor(BTWGradleExtensionImpl extension) {
        this.extension = extension;
    }

    @Override
    public @Nullable Spec buildSpec(SpecContext specContext) {
        BTWJarProvider provider = extension.getJarProvider();
        return provider.isPresent() ? new Spec(provider) : null;
    }

    @Override
    public void processJar(Path path, Spec spec, ProcessorContext processorContext) throws IOException {
        var outputDir = path.resolveSibling(spec.provider.btwPath.getFileName().toString().replace(".jar", "-mapped"));

        // Empty the outputDir directory if it's not empty
        if (Files.exists(outputDir) && Files.isDirectory(outputDir)) {
            try (var stream = Files.walk(outputDir)) {
                stream.sorted((p1, p2) -> -p1.compareTo(p2))
                        .forEach(p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
            }
        }

        // Remap BTW intermediary jar to named
        try (LazyCloseable<TinyRemapper> tinyRemapper = processorContext.createRemapper(processorContext.getProductionNamespace(), MappingsNamespace.NAMED)) {
            var remapper = tinyRemapper.get();
            remapper.readInputsAsync(spec.provider.btwPath);
            var outputConsumer = new OutputConsumerPath.Builder(outputDir).build();
            remapper.apply(outputConsumer);
            outputConsumer.addNonClassFiles(spec.provider.btwPath);

            remapper.finish();
            outputConsumer.close();
        }

        overwrite(path, outputDir);

        var javadocPath = path.resolveSibling(path.getFileName().toString().replace(".jar", "-javadoc.jar"));
        Files.copy(spec.provider.javadocPath, javadocPath, StandardCopyOption.REPLACE_EXISTING);
    }

    private void overwrite(Path mcPath, Path btwPath) throws IOException {
        try (FileSystemUtil.Delegate fs = FileSystemUtil.getJarFileSystem(mcPath)) {
            try (var stream = Files.walk(btwPath)) {
                stream.sorted(Path::compareTo)
                        .forEach(p -> {
                            var rel = btwPath.relativize(p);
                            var target = fs.getPath("/" + rel);

                            if (Files.isDirectory(p)) {
                                if (!Files.exists(target)) {
                                    try {
                                        Files.createDirectories(target);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            } else {
                                try {
                                    Files.copy(p, target, StandardCopyOption.REPLACE_EXISTING);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
            }
        }
    }

    @Override
    public String getName() {
        return "btw:jar";
    }

    public static class Spec implements MinecraftJarProcessor.Spec {
        final BTWJarProvider provider;
        private Integer hashcode;

        public Spec(BTWJarProvider provider) {
            this.provider = provider;
        }

        @Override
        public int hashCode() {
            if (hashcode == null) {
                hashcode = provider.hashCode();
            }

            return hashcode;
        }
    }
}

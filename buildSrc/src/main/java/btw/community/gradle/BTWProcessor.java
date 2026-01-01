package btw.community.gradle;

import net.fabricmc.loom.api.mappings.layered.MappingsNamespace;
import net.fabricmc.loom.api.processor.MinecraftJarProcessor;
import net.fabricmc.loom.api.processor.ProcessorContext;
import net.fabricmc.loom.api.processor.SpecContext;
import net.fabricmc.loom.util.LazyCloseable;
import net.fabricmc.tinyremapper.TinyRemapper;
import org.jspecify.annotations.Nullable;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;

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
//        Files.delete(path);
//        processorContext.getJarConfiguration()
//
//        try (LazyCloseable<TinyRemapper> tinyRemapper = processorContext.createRemapper(processorContext.getProductionNamespace(), MappingsNamespace.NAMED)) {
//            var remapper = tinyRemapper.get();
//            spec.provider.btwPath
//        }
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

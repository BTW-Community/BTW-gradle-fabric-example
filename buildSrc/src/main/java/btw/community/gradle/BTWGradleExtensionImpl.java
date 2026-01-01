package btw.community.gradle;

import net.fabricmc.loom.LoomGradleExtension;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;

public class BTWGradleExtensionImpl implements BTWGradleExtensionAPI {
    protected final Property<String> version;
    private final Property<BTWJarProvider> jarProvider;

    public BTWGradleExtensionImpl(Project project) {
        this.version = project.getObjects().property(String.class);

        var loom = LoomGradleExtension.get(project);

        jarProvider = project.getObjects().property(BTWJarProvider.class);
        jarProvider.convention(project.provider(() -> {
            var provider = new BTWJarProvider(project, loom);
            provider.provide();

            return provider;
        }));

        project.getConfigurations().register(Constants.BTW_CONFIGURATION);
        project.getConfigurations().register(Constants.BTW_JAVADOC_CONFIGURATION);

        project.getDependencies().addProvider(Constants.BTW_CONFIGURATION, this.version.map(v -> "maven.modrinth:btwce:" + v + ":intermediary"));
        project.getDependencies().addProvider(Constants.BTW_JAVADOC_CONFIGURATION, this.version.map(v -> "maven.modrinth:btwce:" + v + ":javadoc"));

        loom.addMinecraftJarProcessor(BTWProcessor.class, this);
    }

    @Override
    public Property<String> getVersion() {
        return version;
    }

    public BTWJarProvider getJarProvider() {
        return jarProvider.get();
    }
}

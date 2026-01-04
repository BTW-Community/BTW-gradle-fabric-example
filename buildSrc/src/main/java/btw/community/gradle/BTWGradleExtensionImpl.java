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

        project.getDependencies().add(Constants.BASE_IMPLEMENTATION_CONFIGURATION, "org.apache.logging.log4j:log4j-core:2.19.0");
        project.getDependencies().add(Constants.BASE_IMPLEMENTATION_CONFIGURATION, "org.apache.logging.log4j:log4j-api:2.19.0");
        project.getDependencies().add(Constants.BASE_IMPLEMENTATION_CONFIGURATION, "it.unimi.dsi:fastutil:8.5.12");
        project.getDependencies().add(Constants.BASE_IMPLEMENTATION_CONFIGURATION, "com.typesafe:config:1.4.5");

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

package btw.community.gradle;

import net.fabricmc.loom.LoomGradleExtension;
import net.fabricmc.loom.configuration.DependencyInfo;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;

import java.nio.file.Path;

public class BTWJarProvider {
    final Project project;
    final LoomGradleExtension loom;

    Path btwPath;
    Path javadocPath;

    public BTWJarProvider(Project project, LoomGradleExtension loom) {
        this.project = project;
        this.loom = loom;
    }

    @Override
    public int hashCode() {
        return (2 << getPathHashCode(btwPath)) + (4 << getPathHashCode(javadocPath));
    }

    public void provide() {
        Configuration mainConfiguration = project.getConfigurations().getByName(Constants.BTW_CONFIGURATION);

        if (!mainConfiguration.getDependencies().isEmpty()) {
            var mainDependency = DependencyInfo.create(project, Constants.BTW_CONFIGURATION);
            var mainJar = mainDependency.resolveFile();

            mainJar.ifPresent(file -> btwPath = file.toPath());
        }

        Configuration javadocConfiguration = project.getConfigurations().getByName(Constants.BTW_JAVADOC_CONFIGURATION);

        if (!javadocConfiguration.getDependencies().isEmpty()) {
            var javadocDependency = DependencyInfo.create(project, Constants.BTW_JAVADOC_CONFIGURATION);
            var javadocJar = javadocDependency.resolveFile();

            javadocJar.ifPresent(file -> javadocPath = file.toPath());
        }
    }

    public boolean isPresent() {
        return btwPath != null && javadocPath != null;
    }

    private int getPathHashCode(Path path) {
        return path != null ? path.hashCode() : 0;
    }
}

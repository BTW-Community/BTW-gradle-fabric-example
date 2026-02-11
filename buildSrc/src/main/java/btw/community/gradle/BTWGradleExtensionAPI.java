package btw.community.gradle;

import org.gradle.api.Project;
import org.gradle.api.provider.Property;

public interface BTWGradleExtensionAPI {
    static BTWGradleExtensionAPI get(Project project) {
        return project.getExtensions().findByType(BTWGradleExtensionAPI.class);
    }

    Property<String> getVersion();
}

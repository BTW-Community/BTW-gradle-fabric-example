package btw.community.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginAware;

public class BTWGradlePlugin implements Plugin<PluginAware> {
    @Override
    public void apply(PluginAware target) {
        if (target instanceof Project project) {
            project.getExtensions().create(BTWGradleExtensionAPI.class, "btw", BTWGradleExtensionImpl.class, project);
        }
    }
}

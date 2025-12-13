package btw.community.example;

import api.AddonHandler;
import api.BTWAddon;

public class ExampleAddon extends BTWAddon {
    private static ExampleAddon instance;

    public ExampleAddon() {
        super();
    }

    @Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
    }
}
package org.icetank;

import com.zenith.plugin.api.Plugin;
import com.zenith.plugin.api.PluginAPI;
import com.zenith.plugin.api.ZenithProxyPlugin;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.icetank.command.BaritoneMineAssistCommand;
import org.icetank.module.BaritoneMineAssist;

@Plugin(
    id = BuildConstants.PLUGIN_ID,
    version = BuildConstants.VERSION,
    description = "ZenithProxy Baritone Mine Assist",
    url = "https://github.com/IceTank/ZenithBaritoneMineAssist",
    authors = {"icetank"},
    mcVersions = {BuildConstants.MC_VERSION} // to indicate any MC version: @Plugin(mcVersions = "*")
)
public class BaritoneMineAssistPlugin implements ZenithProxyPlugin {
    // public static for simple access from modules and commands
    // or alternatively, you could pass these around in constructors
    public static BaritoneMineAssistConfig PLUGIN_CONFIG;
    public static ComponentLogger LOG;

    @Override
    public void onLoad(PluginAPI pluginAPI) {
        LOG = pluginAPI.getLogger();
        LOG.info("Baritone Mine Assist Plugin loading...");
        // initialize any configurations before modules or commands might need to read them
        PLUGIN_CONFIG = pluginAPI.registerConfig(BuildConstants.PLUGIN_ID, BaritoneMineAssistConfig.class);
        pluginAPI.registerModule(new BaritoneMineAssist());
        pluginAPI.registerCommand(new BaritoneMineAssistCommand());
        LOG.info("Baritone Mine Assist Plugin loaded \\o/");
    }
}

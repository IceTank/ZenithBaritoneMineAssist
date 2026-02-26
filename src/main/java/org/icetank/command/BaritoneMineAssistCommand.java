package org.icetank.command;


import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.zenith.command.api.Command;
import com.zenith.command.api.CommandContext;
import com.zenith.command.api.CommandUsage;
import org.icetank.module.BaritoneMineAssist;

import static com.zenith.command.brigadier.ToggleArgumentType.getToggle;
import static com.zenith.command.brigadier.ToggleArgumentType.toggle;
import static org.icetank.BaritoneMineAssistPlugin.PLUGIN_CONFIG;
import static com.zenith.Globals.MODULE;

/*
 * @author IceTank
 * @since 26.02.2026
 */
public class BaritoneMineAssistCommand extends Command {
    @Override
    public CommandUsage commandUsage() {
        return CommandUsage.builder()
                .name("baritoneMineAssist")
                .description("Commands for Baritone Mine Assist")
                .usageLines(
                        "baritoneMineAssist on/off - Enable or disable Baritone Mine Assist"
                )
                .build();
    }

    @Override
    public LiteralArgumentBuilder<CommandContext> register() {
        return command("baritoneMineAssist")
                .then(argument("toggle", toggle()).executes(c -> {
                    PLUGIN_CONFIG.enabled = getToggle(c, "toggle");
                    MODULE.get(BaritoneMineAssist.class).syncEnabledFromConfig();
                    c.getSource().getEmbed().title("Baritone Mine Assist")
                            .addField("Enabled", PLUGIN_CONFIG.enabled ? "On" : "Off");
                    return OK;
                }));
    }
}

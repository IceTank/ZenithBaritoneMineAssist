package org.icetank.module;


import com.github.rfresh2.EventConsumer;
import com.zenith.Proxy;
import com.zenith.event.client.ClientBotTick;
import com.zenith.feature.pathfinder.calc.IPath;
import com.zenith.feature.pathfinder.movement.Movement;
import com.zenith.feature.player.World;
import com.zenith.mc.block.BlockPos;
import com.zenith.mc.block.BlockRegistry;
import com.zenith.mc.item.ItemRegistry;
import com.zenith.module.api.Module;
import org.geysermc.mcprotocollib.protocol.data.game.entity.object.Direction;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Hand;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.PlayerAction;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.player.ServerboundPlayerActionPacket;

import java.util.*;

import static com.github.rfresh2.EventConsumer.of;
import static com.zenith.Globals.*;
import static com.zenith.Globals.BOT;
import static org.icetank.BaritoneMineAssistPlugin.PLUGIN_CONFIG;

/*
 * @author IceTank
 * @since 26.02.2026
 */
public class BaritoneMineAssist extends Module {
    List<BlockPos> lastSendBreaks = new LinkedList<>();
    @Override
    public boolean enabledSetting() {
        return PLUGIN_CONFIG.enabled;
    }

    @Override
    public List<EventConsumer<?>> registerEvents() {
        return List.of(of(ClientBotTick.class, this::onClientBotTick));
    }

    private void onClientBotTick(ClientBotTick clientBotTick) {
        Optional<IPath> path = BARITONE.getPathingBehavior().getPath();
        if (path.isEmpty()) return;

        if (BOT.getInteractions().isDestroying() || !BOT.isOnGround()) return;

        var nextBlock = getNextBlockToBreak(path.get());
        if (nextBlock == null) return;

        lastSendBreaks.add(nextBlock);
        Proxy.getInstance().getClient().sendAsync(new ServerboundPlayerActionPacket(PlayerAction.START_DESTROY_BLOCK, nextBlock.x(), nextBlock.y(), nextBlock.z(), Direction.DOWN, 0));
        while (lastSendBreaks.size() > 50) {
            lastSendBreaks.removeFirst();
        }
    }

    private BlockPos getNextBlockToBreak(IPath path) {
        var botPos = BOT.blockPosition();
        var interaction = BOT.getInteractions();
        return path.movements().stream().filter(move -> move instanceof Movement)
                .map(move -> (Movement) move)
                .flatMap(move -> move.toBreak().stream())
                .filter(p -> botPos.distance(p) < 5)
                .filter(p -> p.y() > botPos.y() - 1) // only break blocks on block below or baritone gets confused
                .filter(p -> !interaction.isDestroying(p.x(), p.y(), p.z()))
                .filter(p -> !lastSendBreaks.contains(p))
                .filter(p -> interaction.blockBreakSpeed(World.getBlock(p)) >= (double) 1.0F)
                .min(Comparator.comparingDouble(p -> p.distance(botPos))).orElse(null);
    }
}

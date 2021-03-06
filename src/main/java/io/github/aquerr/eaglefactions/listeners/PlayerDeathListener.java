package io.github.aquerr.eaglefactions.listeners;

import io.github.aquerr.eaglefactions.PluginInfo;
import io.github.aquerr.eaglefactions.logic.AttackLogic;
import io.github.aquerr.eaglefactions.logic.FactionLogic;
import io.github.aquerr.eaglefactions.logic.MainLogic;
import io.github.aquerr.eaglefactions.services.PlayerService;
import io.github.aquerr.eaglefactions.services.PowerService;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class PlayerDeathListener
{
    @Listener
    public void onPlayerDeath(DestructEntityEvent.Death event)
    {
        if(event.getTargetEntity() instanceof Player)
        {
            Player player = (Player)event.getTargetEntity();

            PowerService.decreasePower(player.getUniqueId());

            player.sendMessage(Text.of(PluginInfo.PluginPrefix, "Your power has been decreased by ", TextColors.GOLD, String.valueOf(MainLogic.getPowerDecrement()) + "\n",
                    TextColors.GRAY, "Current power: ", String.valueOf(PowerService.getPlayerPower(player.getUniqueId())) + "/" + String.valueOf(PowerService.getPlayerMaxPower(player.getUniqueId()))));

            PlayerService.setPlayerChunkPosition(player.getUniqueId(), null);

            if (MainLogic.shouldBlockHomeAfterDeathInOwnFaction())
            {
                if (FactionLogic.getFactionNameByChunk(player.getWorld().getUniqueId(), player.getLocation().getChunkPosition()).equals(FactionLogic.getFactionName(player.getUniqueId())))
                {
                    AttackLogic.blockHome(player.getUniqueId());
                }
            }

            return;
        }
        else return;
    }
}

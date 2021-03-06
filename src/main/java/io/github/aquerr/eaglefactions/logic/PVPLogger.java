package io.github.aquerr.eaglefactions.logic;

import io.github.aquerr.eaglefactions.EagleFactions;
import io.github.aquerr.eaglefactions.PluginInfo;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PVPLogger
{
    private static List<UUID> AttackedPlayers = new ArrayList<>();
    private static boolean _isActive;
    private static int _blockTime;

    public static void setupPVPLogger()
    {
        _isActive = MainLogic.isPVPLoggerActive();
        _blockTime = MainLogic.getPVPLoggerTime();
    }

    public static boolean isActive()
    {
        return _isActive;
    }

    public static int getBlockTime()
    {
        return _blockTime;
    }

    public static boolean addOrUpdatePlayer(Player player)
    {
        //Update player's time if it already in a list.

        Optional<Task> optionalTask = Sponge.getScheduler().getScheduledTasks().stream().filter(x->x.getName().equals("EagleFactions - PVPLogger for " + player.getUniqueId().toString())).findFirst();

        if (optionalTask.isPresent())
        {
            optionalTask.get().cancel();

            return addOrUpdatePlayer(player);
        }
        else
        {
            AttackedPlayers.add(player.getUniqueId());
            player.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.RED, "PVPLogger has turned on! You will die if you disconnect in " + getBlockTime() + "s!"));

            Task.Builder allowLogging = Sponge.getScheduler().createTaskBuilder();

            allowLogging.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    AttackedPlayers.remove(player.getUniqueId());
                    player.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.GREEN, "PVPLogger has turned off for you! You can now disconnect safely."));
                }
            }).delay(getBlockTime(), TimeUnit.SECONDS).submit(EagleFactions.getEagleFactions());

            return true;
        }
    }

    public static boolean isPlayerBlocked(Player player)
    {
        if (AttackedPlayers.contains(player.getUniqueId())) return true;

        return false;
    }

    public static void removePlayer(Player player)
    {
        Optional<Task> optionalTask = Sponge.getScheduler().getScheduledTasks().stream().filter(x->x.getName().equals("EagleFactions - PVPLogger for " + player.getUniqueId().toString())).findFirst();

        if (optionalTask.isPresent()) optionalTask.get().cancel();

        AttackedPlayers.remove(player.getUniqueId());
    }
}

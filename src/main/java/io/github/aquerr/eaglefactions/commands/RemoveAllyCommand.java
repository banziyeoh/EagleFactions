package io.github.aquerr.eaglefactions.commands;

import io.github.aquerr.eaglefactions.EagleFactions;
import io.github.aquerr.eaglefactions.PluginInfo;
import io.github.aquerr.eaglefactions.logic.FactionLogic;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

/**
 * Created by Aquerr on 2017-08-04.
 */
public class RemoveAllyCommand implements CommandExecutor
{
    @Override
    public CommandResult execute(CommandSource source, CommandContext context) throws CommandException
    {
        Optional<String> optionalFactionName = context.<String>getOne("faction name");

        if (optionalFactionName.isPresent())
        {
            if(source instanceof Player)
            {
                Player player = (Player)source;
                String playerFactionName = FactionLogic.getFactionName(player.getUniqueId());

                String rawFactionName = optionalFactionName.get();
                String removedFaction = FactionLogic.getRealFactionName(rawFactionName);

                if (removedFaction == null)
                {
                    player.sendMessage(Text.of(PluginInfo.ErrorPrefix, TextColors.RED, "There is no faction called ", TextColors.GOLD, rawFactionName + "!"));
                    return CommandResult.success();
                }
                else
                {
                    if(playerFactionName != null)
                    {
                        if(EagleFactions.AdminList.contains(player.getUniqueId()))
                        {
                            if(!FactionLogic.getAlliances(playerFactionName).contains(removedFaction))
                            {
                                FactionLogic.removeAlly(playerFactionName, removedFaction);
                                player.sendMessage(Text.of(PluginInfo.PluginPrefix,TextColors.GREEN, "You disbanded your alliance with ", TextColors.GOLD, removedFaction, TextColors.GREEN, "!"));
                            }
                            else
                            {
                                source.sendMessage(Text.of(PluginInfo.ErrorPrefix, TextColors.RED, "Your faction is not in the alliance with ", TextColors.GOLD, removedFaction + "!"));
                            }

                            return CommandResult.success();
                        }

                        if(FactionLogic.getLeader(playerFactionName).equals(player.getUniqueId().toString()) || FactionLogic.getOfficers(playerFactionName).contains(player.getUniqueId().toString()))
                        {
                            if(FactionLogic.getAlliances(playerFactionName).contains(removedFaction))
                            {
                                FactionLogic.removeAlly(playerFactionName, removedFaction);
                                player.sendMessage(Text.of(PluginInfo.PluginPrefix,TextColors.GREEN, "You disbanded your alliance with ", TextColors.GOLD, removedFaction, TextColors.GREEN, "!"));
                                return CommandResult.success();
                            }
                            else
                            {
                                source.sendMessage(Text.of(PluginInfo.ErrorPrefix, TextColors.RED, "Your faction is not in the alliance with ", TextColors.GOLD, removedFaction + "!"));
                            }
                        }
                        else
                        {
                            source.sendMessage(Text.of(PluginInfo.ErrorPrefix, TextColors.RED, "You must be the faction leader or officer to do this!"));
                        }
                    }
                    else
                    {
                        source.sendMessage(Text.of(PluginInfo.ErrorPrefix, TextColors.RED, "You must be in a faction in order to use this command!"));
                    }
                }
            }
            else
            {
                source.sendMessage (Text.of (PluginInfo.ErrorPrefix, TextColors.RED, "Only in-game players can use this command!"));
            }
        }
        else
        {
            source.sendMessage(Text.of(PluginInfo.ErrorPrefix, TextColors.RED, "Wrong command arguments!"));
            source.sendMessage(Text.of(TextColors.RED, "Usage: /f ally remove <faction name>"));
        }

        return CommandResult.success();
    }
}

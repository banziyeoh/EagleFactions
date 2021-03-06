package io.github.aquerr.eaglefactions.commands;

import io.github.aquerr.eaglefactions.EagleFactions;
import io.github.aquerr.eaglefactions.PluginInfo;
import io.github.aquerr.eaglefactions.entities.Faction;
import io.github.aquerr.eaglefactions.entities.Invite;
import io.github.aquerr.eaglefactions.logic.FactionLogic;
import io.github.aquerr.eaglefactions.logic.MainLogic;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class JoinCommand implements CommandExecutor
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
                String rawFactionName = optionalFactionName.get();

                if(FactionLogic.getFactionName(player.getUniqueId()) == null)
                {
                    String factionName = FactionLogic.getRealFactionName(rawFactionName);
                    if (factionName == null)
                    {
                        player.sendMessage(Text.of(PluginInfo.ErrorPrefix, TextColors.RED, "There is no faction called ", TextColors.GOLD, rawFactionName + "!"));
                        return CommandResult.success();
                    }
                    else
                    {
                        //If player has admin mode then force join.
                        if(EagleFactions.AdminList.contains(player.getUniqueId()))
                        {
                            FactionLogic.joinFaction(player.getUniqueId(), factionName);
                            source.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.GREEN, "Successfully joined faction ", TextColors.GOLD, factionName));

                            return CommandResult.success();
                        }

                        for (Invite invite: EagleFactions.InviteList)
                        {
                            if(invite.getPlayerUUID().equals(player.getUniqueId()) && invite.getFactionName().equals(factionName))
                            {
                                try
                                {
                                    if(MainLogic.isPlayerLimit())
                                    {
                                        int playerCount = 0;
                                        Faction faction = FactionLogic.getFaction(factionName);
                                        playerCount += faction.Leader.equals("") ? 0 : 1;
                                        playerCount += faction.Officers.isEmpty() ? 0 : faction.Officers.size();
                                        playerCount += faction.Members.isEmpty() ? 0 : faction.Members.size();

                                        if(playerCount >= MainLogic.getPlayerLimit())
                                        {
                                            player.sendMessage(Text.of(PluginInfo.ErrorPrefix, TextColors.RED, "You can't join this faction because it reached its player limit!"));
                                            return CommandResult.success();
                                        }
                                    }

                                    //TODO: Create a listener which will notify all players in faction that someone has joined.
                                    FactionLogic.joinFaction(player.getUniqueId(), factionName);

                                    EagleFactions.InviteList.remove(new Invite(factionName, player.getUniqueId()));

                                    source.sendMessage(Text.of(PluginInfo.PluginPrefix, TextColors.GREEN, "Successfully joined faction ", TextColors.GOLD, factionName));
                                    return CommandResult.success();
                                }
                                catch (Exception exception)
                                {
                                    exception.printStackTrace();
                                }
                            }
                        }
                        source.sendMessage(Text.of(PluginInfo.ErrorPrefix, TextColors.RED, "You haven't been invited to this faction."));
                    }
                }
                else
                {
                    source.sendMessage(Text.of(PluginInfo.ErrorPrefix, TextColors.RED, "You already are in a faction."));
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
            source.sendMessage(Text.of(TextColors.RED, "Usage: /f join <faction name>"));
        }

        return CommandResult.success();
    }
}

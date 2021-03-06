package me.totalfreedom.tfguilds.command;

import me.totalfreedom.tfguilds.Common;
import me.totalfreedom.tfguilds.guild.Guild;
import me.totalfreedom.tfguilds.util.GUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class TagSubcommand extends Common implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof ConsoleCommandSender)
        {
            sender.sendMessage(NO_PERMS);
            return true;
        }

        if (args.length < 2)
        {
            sender.sendMessage(tl(PREFIX + "Proper usage: /g tag <set <tag> | clear [guild]>>"));
            return true;
        }

        Player player = (Player)sender;
        Guild guild = Guild.getGuild(player);
        if (guild == null)
        {
            sender.sendMessage(NG);
            return true;
        }

        if (!guild.getOwner().equals(player.getUniqueId()))
        {
            sender.sendMessage(ChatColor.RED + "You can't modify your guild's tag!");
            return true;
        }

        if (args.length >= 3)
        {
            if (args[1].equalsIgnoreCase("set"))
            {
                String tag = StringUtils.join(args, " ", 2, args.length);

                tag = tag.replace("%tag%", guild.getName());

                if (tag.length() > 30)
                {
                    player.sendMessage(ChatColor.RED + "Your guild tag must not be over 30 characters.");
                    return true;
                }

                for (String blacklisted : GUtil.BLACKLISTED_NAMES_AND_TAGS)
                {
                    if (tag.equalsIgnoreCase(blacklisted))
                    {
                        if (!plugin.bridge.isAdmin(player))
                        {
                            player.sendMessage(ChatColor.RED + "You may not use that name.");
                            return true;
                        }
                    }
                }

                guild.setTag(tag);
                guild.save();
                sender.sendMessage(tl("%p%Your guild tag has been changed to be \"" + GUtil.colorize(tag).replace("%rank%", "Guild Owner") + "%p%\"."));
                return true;
            }
        }

        if (args[1].equalsIgnoreCase("clear"))
        {
            if (args.length >= 3)
            {
                if (!plugin.bridge.isAdmin(sender))
                {
                    sender.sendMessage(NO_PERMS);
                    return true;
                }

                Guild g = Guild.getGuild(GUtil.flatten(StringUtils.join(args, " ", 2, args.length)));
                if (g == null)
                {
                    sender.sendMessage(ChatColor.RED + "That guild doesn't exist!");
                    return true;
                }

                g.setTag(null);
                g.save();
                sender.sendMessage(tl("%p%Cleared guild tag for " + g.getName() + "."));
                return true;
            }

            guild.setTag(null);
            guild.save();
            sender.sendMessage(tl("%p%Your guild tag has been cleared."));
        }
        return true;
    }
}
package com.gonagutor.minions.commands;

import java.util.Set;

import com.gonagutor.minions.Minions;
import com.gonagutor.minions.configs.MinionData;
import com.gonagutor.minions.managers.MinionManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class MinionsCommand implements CommandExecutor {
    private MinionManager minionManager;
    public MinionsCommand (MinionManager mm) {
        this.minionManager = mm;
    }

    private boolean sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + "==================== " + Minions.getPrefix() + "====================");
        sender.sendMessage(ChatColor.GOLD + "/minions get " + ChatColor.GRAY + " - Get an specific minion");
        sender.sendMessage(ChatColor.GOLD + "/minions list " + ChatColor.GRAY + " - Get available minions");
        sender.sendMessage(ChatColor.GOLD + "/minions help " + ChatColor.GRAY + " - Shows this");
        return (true);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1)
            return sendHelpMessage(sender);
        switch (args[0].toLowerCase()) {
            case "get":
				if (sender instanceof Player) {
                    try {
                        MinionData mData = ((MinionData)minionManager.getMinionList().toArray()[Integer.parseInt(args[1])]);
                        ((Player) sender).getInventory().addItem(mData.toSkull());
                        ((Player) sender).sendMessage(Minions.getPrefix() + ChatColor.GREEN + "You have received a " + mData.getItemName());
                    } catch (Exception e) {
                        ((Player) sender).sendMessage(Minions.getPrefix() + ChatColor.RED + "The minion number " + args[1] + " does not exist");
                    }
                } else
					sender.sendMessage("No puedes hacer eso desde la consola");
                return (true);

            case "list":
                //TODO: Add available pages counter
                Set<MinionData> minionList = minionManager.getMinionList();
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.GREEN + "==========" + ChatColor.GRAY + " Available Minions " + ChatColor.GREEN + "==========");
                    for (int i = 0; i < minionList.size() && i < 10; i++) {
                        sender.sendMessage(ChatColor.GOLD + "" + i + " - " + ChatColor.RESET + ((MinionData) minionList.toArray()[i]).getItemName());
                    }
                    return true;
                }
                sender.sendMessage(ChatColor.GREEN + "==========" + ChatColor.GRAY + " Available Minions " + ChatColor.GREEN + "==========");
                for (int i = (Integer.parseInt(args[1]) - 1) * 10; i < minionList.size() && i / 10 < 10; i++) {
                    sender.sendMessage(ChatColor.GOLD + "" + i + " - " + ChatColor.RESET + ((MinionData) minionList.toArray()[i]).getItemName());
                }
                return true;

            case "help":
                return sendHelpMessage(sender);

            default:
                return sendHelpMessage(sender);
        }
    }
}

package com.gonagutor.minions.commands;

import java.util.Set;
import java.util.ArrayList;
import java.util.List;

import com.gonagutor.minions.Minions;
import com.gonagutor.minions.configs.MinionData;
import com.gonagutor.minions.managers.MinionManager;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class MinionsCommand implements TabExecutor {
    private MinionManager minionManager;

    public MinionsCommand(MinionManager mm) {
        this.minionManager = mm;
    }

    private boolean sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + "==================== " + Minions.getPrefix() + "====================");
        sender.sendMessage(ChatColor.GOLD + "/minions give " + ChatColor.GRAY + " - Give an specific minion");
        sender.sendMessage(ChatColor.GOLD + "/minions list " + ChatColor.GRAY + " - Get available minions");
        sender.sendMessage(ChatColor.GOLD + "/minions help " + ChatColor.GRAY + " - Shows this");
        return (true);
    }

    public static void givePlayerMinion(MinionData minion, Player giveTo) {
        giveTo.getInventory().addItem(minion.toSkull());
        giveTo.sendMessage(Minions.getPrefix() + ChatColor.GREEN + "You have received a " + minion.getItemName());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> ret = new ArrayList<>();
        if (args.length == 1) {
            ret.add("list");
            ret.add("give");
            ret.add("help");
            return ret;
        }
        if (args.length == 2) {
            for (int i = 0; i < minionManager.getMinionList().size(); i++)
                ret.add("" + i);
            return ret;
        }
        if (args.length == 3) {
            for (Player p : Bukkit.getOnlinePlayers())
                ret.add(p.getName());
            return ret;
        }
        return ret;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1)
            return sendHelpMessage(sender);
        // TODO: Add a minion remover based on the type to clean the server
        switch (args[0].toLowerCase()) {
        case "give":
            if (sender instanceof Player) {
                try {
                    MinionData mData = (MinionData) minionManager.getMinionList().toArray()[Integer.parseInt(args[1])];
                    if (args.length < 3) {
                        givePlayerMinion(mData, (Player) sender);
                        return true;
                    }
                    Player sendTo = Bukkit.getPlayerExact(args[2]);
                    if (sendTo != null) {
                        givePlayerMinion(mData, sendTo);
                        ((Player) sender)
                                .sendMessage(Minions.getPrefix() + args[2] + " received " + mData.getItemName());
                        return true;
                    }
                    ((Player) sender).sendMessage(
                            Minions.getPrefix() + ChatColor.RED + "Could not find player named " + args[2]);
                } catch (Exception e) {
                    e.printStackTrace();
                    ((Player) sender).sendMessage(
                            Minions.getPrefix() + ChatColor.RED + "The minion number " + args[1] + " does not exist");
                }
            } else
                sender.sendMessage(Minions.getPrefix() + "This command can only be used by a player.");
            return (true);

        case "list":
            Set<MinionData> minionList = minionManager.getMinionList();
            int maxPages = (int) Math.ceil(((float) minionList.size()) / 10);
            if (args.length < 2) {
                sender.sendMessage(ChatColor.GREEN + "==========" + ChatColor.GRAY + " Available Minions (1/" + maxPages
                        + ")" + ChatColor.GREEN + " ==========");
                for (int i = 0; i < minionList.size() && i < 10; i++) {
                    sender.sendMessage(ChatColor.GOLD + "" + i + " - " + ChatColor.RESET
                            + ((MinionData) minionList.toArray()[i]).getItemName());
                }
                return true;
            }
            try {
                int pageNumber = Integer.parseInt(args[1]);
                if (pageNumber > maxPages || pageNumber <= 0) {
                    throw new Exception("Illegal Number");
                }

                sender.sendMessage(ChatColor.GREEN + "==========" + ChatColor.GRAY + " Available Minion (" + pageNumber
                        + "/" + maxPages + ")" + ChatColor.GREEN + " ==========");
                for (int i = (pageNumber - 1) * 10; i < minionList.size()
                        && (int) Math.ceil(((float) i) / 10) < 10; i++) {
                    sender.sendMessage(ChatColor.GOLD + "" + i + " - " + ChatColor.RESET
                            + ((MinionData) minionList.toArray()[i]).getItemName());
                }

                return true;
            } catch (Exception e) {
                sender.sendMessage(
                        Minions.getPrefix() + ChatColor.RED + "That page does not exist. Available pages: " + maxPages);
                return true;
            }

        case "help":
            return sendHelpMessage(sender);

        default:
            return sendHelpMessage(sender);
        }
    }
}

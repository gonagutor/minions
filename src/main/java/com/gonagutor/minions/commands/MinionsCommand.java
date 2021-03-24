package com.gonagutor.minions.commands;

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
        sender.sendMessage(ChatColor.GOLD + "/minions get " + ChatColor.GRAY + " - Iniciar la partida");
        sender.sendMessage(ChatColor.GOLD + "/minions stopall " + ChatColor.GRAY + " - Terminar la partida");
        sender.sendMessage(ChatColor.GOLD + "/minions help " + ChatColor.GRAY + " - Muestra esto");
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

            case "help":
                return sendHelpMessage(sender);

            default:
                return sendHelpMessage(sender);
        }
    }
}

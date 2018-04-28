package ca.jamiesinn.trailgui.commands;

import ca.jamiesinn.trailgui.TrailGUI;
import ca.jamiesinn.trailgui.Util;
import ca.jamiesinn.trailgui.trails.Trail;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandTrail implements CommandExecutor, TabCompleter {

    private TrailGUI trailGUI;

    public CommandTrail(TrailGUI trailGUI) {
        this.trailGUI = trailGUI;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        List<String> trailList = new ArrayList<>(TrailGUI.trailTypes.keySet());
        trailList.add("ClearAll");
        if (args.length == 1) {
            Collections.sort(trailList);
            return trailList;
        }
        return null;
    }

    private void showList(Player player) {
        player.sendMessage(TrailGUI.prefix + ChatColor.GREEN + "Available trails:");
        String list = ChatColor.RED + "Trails: " + ChatColor.GREEN;
        for (Trail type : TrailGUI.trailTypes.values()) {
            if (type.canUse(player)) {
                list += type.getName() + ", ";
            }
        }
        list = list.substring(0, list.length() - 2);
        player.sendMessage(list);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        List<Trail> trails = new ArrayList<>(TrailGUI.trailTypes.values());
        if (!(sender instanceof Player)) {
            if (args.length == 2 && args[0].equalsIgnoreCase("clearall")) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                Util.clearTrails(player);
            } else
                sender.sendMessage("Syntax: /trail clearall <player>");
            return true;
        }
        Player player = (Player) sender;
        for (String string : TrailGUI.disabledWorlds) {
            string = string.replace("[", "");
            string = string.replace("]", "");
            if (string.equals(player.getWorld().getName())) {
                player.sendMessage(TrailGUI.prefix + ChatColor.GREEN + "You cannot use this command in this world.");
                return true;
            }
        }

        if (args.length == 0) {
            player.sendMessage(TrailGUI.prefix + ChatColor.GREEN + "Available commands:");
            player.sendMessage(ChatColor.GREEN + "/trail <TrailName>");
            player.sendMessage(ChatColor.GREEN + "/trail <TrailName> <PlayerName>");
            showList(player);
            return true;
        }


        for (Trail trail : trails) {
            if (trail.onCommand(player, args)) {
                return true;
            }
        }

        if (args[0].equalsIgnoreCase("clearall")) {
            if (!player.hasPermission("trailgui.commands.clearall") && !player.hasPermission("trailgui.*")) {
                player.sendMessage(TrailGUI.getPlugin().getConfig().getString("Commands.denyPermissionMessage").replaceAll("&", "\u00A7"));
                return false;
            }
            if (args.length == 1) {
                Util.clearTrails(player);
                player.sendMessage(this.trailGUI.getConfig().getString("ClearAll.message").replaceAll("&", "\u00A7").replaceAll("%TrailName%", "ClearAll"));
                return true;
            }
            if (!player.hasPermission("trailgui.commands.clearall.other") && !player.hasPermission("trailgui.*")) {
                player.sendMessage(TrailGUI.getPlugin().getConfig().getString("Commands.denyPermissionMessage").replaceAll("&", "\u00A7"));
                return true;
            }
            Player target = Bukkit.getServer().getPlayerExact(args[1]);
            if (target == null) {
                player.sendMessage(this.trailGUI.getConfig().getString("Commands.noTargetMessage").replaceAll("&", "\u00A7").replaceAll("%Target%", args[1]));
                return true;
            }
            if (player.getName().equals(args[1])) {
                player.sendMessage(this.trailGUI.getConfig().getString("Commands.targetSelfMessage").replaceAll("&", "\u00A7").replaceAll("%TrailName%", "ClearAll"));
                return true;
            }
            Util.clearTrails(target);
            target.sendMessage(this.trailGUI.getConfig().getString("ClearAll.targetMessage").replaceAll("&", "\u00A7").replaceAll("%Sender%", player.getName()));
            player.sendMessage(this.trailGUI.getConfig().getString("ClearAll.senderMessage").replaceAll("&", "\u00A7").replaceAll("%Target%", args[1]));
            return true;
        } else {
            player.sendMessage(TrailGUI.prefix + ChatColor.DARK_RED + "That's not a valid trail.");
            showList(player);
            return true;
        }
    }
}

package io.github.galaipa.sbb;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;


public class SignListener implements Listener {
    public SuperBuildBattle plugin;

    public SignListener(SuperBuildBattle instance) {
        plugin = instance;
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        Player p = e.getPlayer();
        if (e.getLine(0).equalsIgnoreCase("[BuildBattle]")) {
            if (p.hasPermission("bb.admin")) {
                e.setLine(0, ChatColor.BLACK + "[" + ChatColor.DARK_GREEN + "BuildBattle" + ChatColor.BLACK + "]");
                if (e.getLine(2).equalsIgnoreCase("Join")) {
                    e.setLine(2, ChatColor.DARK_RED + plugin.getTr("28"));
                } else if (e.getLine(2).equalsIgnoreCase("Leave")) {
                    e.setLine(2, ChatColor.DARK_RED + plugin.getTr("38"));
                    p.sendMessage(ChatColor.RED + plugin.getTr("3"));
                }
                p.sendMessage(ChatColor.GREEN + "[BuildBattle]" + ChatColor.GREEN + "Sign created");
                ;
            }
        }
    }

    @EventHandler
    public void SignClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getClickedBlock().getState() instanceof Sign) {
                Sign sign = (Sign) e.getClickedBlock().getState();
                if (ChatColor.stripColor(sign.getLine(0)).equalsIgnoreCase("[BuildBattle]")) {
                    if (sign.getLine(2).equals(ChatColor.DARK_RED + plugin.getTr("28"))) {
                        ArenaManager.getManager().addPlayer(p, p.getLocation());
                    } else if (sign.getLine(2).equals(ChatColor.DARK_RED + plugin.getTr("38"))) {
                        ArenaManager.getManager().removePlayer(p, true);
                    }
                }
            }
        }
    }

}

package io.github.galaipa.sbb;

import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

import static io.github.galaipa.sbb.SuperBuildBattle.getTr;


public class PlayerPointsOptional {
    public static SuperBuildBattle plugina;
    private static PlayerPoints playerPoints;

    public PlayerPointsOptional(SuperBuildBattle instance) {
        plugina = instance;
    }

    public static void hookPlayerPoints(Plugin plugin) {
        playerPoints = PlayerPoints.class.cast(plugin);
    }

    public static void givePlayerPointsRewards(Player p, int points) {
        playerPoints.getAPI().give(p.getUniqueId(), points);
        p.sendMessage(ChatColor.GREEN + "[BuildBattle]" + ChatColor.GREEN + getTr("34") + " " + points + " " + getTr("24"));
    }

    public static void givePlayerPointsRewards(ArrayList<Player> pList, int points) {
        for (Player p : pList) {
            playerPoints.getAPI().give(p.getUniqueId(), points);
            p.sendMessage(ChatColor.GREEN + "[BuildBattle]" + ChatColor.GREEN + getTr("34") + " " + points + " " + getTr("24"));
        }
    }

    public PlayerPoints getPlayerPoints() {
        return playerPoints;
    }
}
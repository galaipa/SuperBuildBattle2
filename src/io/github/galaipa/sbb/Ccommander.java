package io.github.galaipa.sbb;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by matte on 08/02/2016.
 */
public class Ccommander {
    public static boolean resetArena(JavaPlugin plugin, int arena_number){
        try{
            com.commander.nolag.Main cc = getCC(plugin);
            if(cc != null && com.commander.nolag.api.Utils.isSaveFileValid("bb" + "_" + arena_number)){
                cc.getAsynchBuilder().waitForReset("bb" + "_" + arena_number);
                return true;
            }
        }catch (Throwable ignored){}
        return false;
    }

    public static com.commander.nolag.Main getCC(Plugin instance) {
        if (instance == null) return null;
        Plugin plugin = instance.getServer().getPluginManager().getPlugin("ccommander");
        if (plugin == null || !(plugin instanceof com.commander.nolag.Main)) {
            return null;
        }
        return (com.commander.nolag.Main) plugin;
    }
}

package io.github.galaipa.sbb;

import com.connorlinfoot.titleapi.TitleAPI;
import static io.github.galaipa.sbb.AdminGui.item;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static io.github.galaipa.sbb.SuperBuildBattle.getTr;
import org.bukkit.Material;

public class ArenaManager {
    public static ArenaManager am = new ArenaManager();
    public static boolean debug;
    public static boolean PlayerPoints, Vault, Command, WorldGuarda,Sounds;
    public static int  timeBeforeStart = 45;
    //Location lobby;
    List<Arena> arenas = new ArrayList<>();
    SuperBuildBattle plugin = SuperBuildBattle.getInstance();
    private Location globalLobby;


    public static ArenaManager getManager() {
        return am;
    }

    //TITLES
    public static void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
        TitleAPI.sendTitle(player, fadeIn, stay, fadeOut, title, subtitle);
    }

    public static void debug(String msg) {
        if (debug) {
            System.out.println(msg);
        }
    }

    public Arena getArena(int i) {
        for (Arena a : arenas) {
            if (a.getID() == i) {
                return a;
            }
        }
        return null;
    }

    public void addPlayer(Player p, Location l) {
        if (getArena(p) != null) {
            //TODO translate in your language
            p.sendMessage(ChatColor.YELLOW + "[Build Battle] You are already playing!");
            return;
        }
        // p.sendMessage(Integer.toString(arenas.size()));
        for (Arena a : arenas) {
            // p.sendMessage(a.inGame.toString());
            //  p.sendMessage(Integer.toString(a.players.size()));
            if (!a.inGame) {
                if (a.players.size() != a.maxPlayers) {
                    a.Broadcast(ChatColor.YELLOW + "[Build Battle] " + ChatColor.GREEN + p.getName() + " " + getTr("10"));
                    ArenaPlayer j = new ArenaPlayer(p, a.getID(), l);
                    a.getPlayers().add(j);
                    p.teleport(a.getLobby());
                    p.getInventory().setItem(8, item(Material.BED, 0, 1, ChatColor.RED + getTr("38")));
                    //  p.sendMessage(ChatColor.YELLOW + "[Build Battle] " +ChatColor.GREEN +"You joined arena " + a.getID() + "/" + arenas.size());
                    p.sendMessage(ChatColor.YELLOW + "[Build Battle] " + ChatColor.GREEN + getTr("39").replace("{1}", j.getID() + "/" + a.maxPlayers).replace("{2}", a.getID() + "/" + arenas.size()));
                    if (a.players.size() == a.minPlayers) {
                        a.Broadcast(ChatColor.YELLOW + "[Build Battle] " + ChatColor.GREEN + "Countdown started!");
                        System.out.println("Minimum players = " + a.players.size() + " " + a.minPlayers);
                        a.minimunReached();
                    }
                    return;
                }
            }
        }
        p.sendMessage(ChatColor.YELLOW + "[Build Battle] " + ChatColor.RED + "All arenas are full or inGame");
    }

    public Location getGlobalLobby(){
        return globalLobby;
    }

    public void removePlayer(final Player p, boolean bo) {
        if (getArena(p) != null) {
            Arena arena = getArena(p);
            arena.removePlayer(p, bo);
        } else {
            p.sendMessage(ChatColor.GREEN + "[Build Battle] " + ChatColor.RED + getTr("5"));
        }
    }

    public Arena getArena(Player p) {
        for (Arena a : arenas) {
            for (ArenaPlayer j : a.players) {
                if (j.getPlayer().equals(p)) {
                    return a;
                }
            }
        }
        return null;
    }

    public void loadGlobalLobby(){
        try{
            if(plugin.getConfig().contains("GlobalLobby")){
                String world = plugin.getConfig().getString("GlobalLobby.world");
                double x = plugin.getConfig().getDouble("GlobalLobby.x");
                double y = plugin.getConfig().getDouble("GlobalLobby.y");
                double z = plugin.getConfig().getDouble("GlobalLobby.z");
                this.globalLobby = new Location(Bukkit.getWorld(world), x, y, z);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveGlobalLobby(Location location){
        try{
            FileConfiguration conf = plugin.getConfig();
            conf.set("GlobalLobby.world", location.getWorld().getName());
            conf.set("GlobalLobby.x", location.getX());
            conf.set("GlobalLobby.y", location.getY());
            conf.set("GlobalLobby.z", location.getZ());
            plugin.saveConfig();
            this.globalLobby = location;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void loadArenas() {
        loadGlobalLobby();

        arenas.clear();
        int id = 1;
        do {
            if (plugin.getConfig().contains("Arenas." + id)) {
                loadArena(id);
                debug("Loaded arena " + id);
                id++;
            } else {
                id = -1;
            }
        } while (id > 0);
        debug("Arena load finished");
        //loadLobby(); 
    }

    public void createArena(int id, int minPlayers, int maxPlayers, int time, int votingTime, Location lobby, ArrayList<Cuboid> cuboids) {
        plugin.getConfig().set("Arenas." + Integer.toString(id) + ".Time", time);
        debug("Time " + Integer.toString(time));
        plugin.getConfig().set("Arenas." + Integer.toString(id) + ".VotingTime", votingTime);
        debug("VotingTime " + Integer.toString(votingTime));
        plugin.getConfig().set("Arenas." + Integer.toString(id) + ".MinPlayers", minPlayers);
        debug("MinPlayers " + Integer.toString(minPlayers));
        plugin.getConfig().set("Arenas." + Integer.toString(id) + ".MaxPlayers", maxPlayers);
        debug("MaxPlayers " + Integer.toString(maxPlayers));
        plugin.getConfig().set("Arenas." + Integer.toString(id) + ".Spawn.World", lobby.getWorld().getName());
        plugin.getConfig().set("Arenas." + Integer.toString(id) + ".Spawn.X", lobby.getX());
        plugin.getConfig().set("Arenas." + Integer.toString(id) + ".Spawn.Y", lobby.getY());
        plugin.getConfig().set("Arenas." + Integer.toString(id) + ".Spawn.Z", lobby.getZ());
        debug("Spawn:" + lobby.getWorld() + lobby.getX() + lobby.getY() + lobby.getZ());
        int zenbat = 1;
        for (Cuboid c : cuboids) {
            Location l1 = c.getLowerNE();
            Location l2 = c.getUpperSW();
            plugin.getConfig().set("Arenas." + Integer.toString(id) + ".Region." + zenbat + ".World", l1.getWorld().getName());
            plugin.getConfig().set("Arenas." + Integer.toString(id) + ".Region." + zenbat + ".Min.x", l1.getX());
            plugin.getConfig().set("Arenas." + Integer.toString(id) + ".Region." + zenbat + ".Min.y", l1.getY());
            plugin.getConfig().set("Arenas." + Integer.toString(id) + ".Region." + zenbat + ".Min.z", l1.getZ());

            plugin.getConfig().set("Arenas." + Integer.toString(id) + ".Region." + zenbat + ".Max.x", l2.getX());
            plugin.getConfig().set("Arenas." + Integer.toString(id) + ".Region." + zenbat + ".Max.y", l2.getY());
            plugin.getConfig().set("Arenas." + Integer.toString(id) + ".Region." + zenbat + ".Max.z", l2.getZ());
            debug("Cuboid:" + zenbat);
            zenbat++;
        }
        plugin.saveConfig();
        debug("Config saved");
        plugin.reloadConfig();
        debug("Config reloaded");
        loadArenas();
    }

    public Arena loadArena(int id) {
        int maxPlayers = plugin.getConfig().getInt("Arenas." + Integer.toString(id) + ".MaxPlayers");
        int minPlayers = plugin.getConfig().getInt("Arenas." + Integer.toString(id) + ".MinPlayers");
        int time = plugin.getConfig().getInt("Arenas." + Integer.toString(id) + ".Time");
        int votingTime = plugin.getConfig().getInt("Arenas." + Integer.toString(id) + ".VotingTime");
        debug("Load:" + maxPlayers + "," + minPlayers + "," + time + "," + votingTime);
        String w = plugin.getConfig().getString("Arenas." + Integer.toString(id) + ".Spawn.World");
        Double x = plugin.getConfig().getDouble("Arenas." + Integer.toString(id) + ".Spawn.X");
        Double y = plugin.getConfig().getDouble("Arenas." + Integer.toString(id) + ".Spawn.Y");
        Double z = plugin.getConfig().getDouble("Arenas." + Integer.toString(id) + ".Spawn.Z");
        Location lobbya = new Location(plugin.getServer().getWorld(w), x, y, z);
        debug("Load spawn: " + lobbya.toString());
        Cuboid[] cuboidList = new Cuboid[maxPlayers];
        for (int i = 1; i <= maxPlayers; i++) {
            String w1 = plugin.getConfig().getString("Arenas." + Integer.toString(id) + ".Region." + Integer.toString(i) + ".World");
            Double x1 = plugin.getConfig().getDouble("Arenas." + Integer.toString(id) + ".Region." + Integer.toString(i) + ".Min.x");
            Double y1 = plugin.getConfig().getDouble("Arenas." + Integer.toString(id) + ".Region." + Integer.toString(i) + ".Min.y");
            Double z1 = plugin.getConfig().getDouble("Arenas." + Integer.toString(id) + ".Region." + Integer.toString(i) + ".Min.z");
            Double x2 = plugin.getConfig().getDouble("Arenas." + Integer.toString(id) + ".Region." + Integer.toString(i) + ".Max.x");
            Double y2 = plugin.getConfig().getDouble("Arenas." + Integer.toString(id) + ".Region." + Integer.toString(i) + ".Max.y");
            Double z2 = plugin.getConfig().getDouble("Arenas." + Integer.toString(id) + ".Region." + Integer.toString(i) + ".Max.z");
            debug("Load points");
            debug(w1);
            debug(plugin.getServer().getWorlds().toString());
            debug(Bukkit.getWorlds().toString());
            Location l1 = new Location(plugin.getServer().getWorld(w1), x1, y1, z1);
            debug(l1.toString());
            Location l2 = new Location(plugin.getServer().getWorld(w1), x2, y2, z2);
            debug(l2.toString());
            Cuboid c = new Cuboid(l1, l2);
            debug("Cuboid: " + c.toString());
            cuboidList[i - 1] = c;
            debug("Loaded cuboid: " + i);
            if (WorldGuarda) {
                WorldGuardOptional.WGregion(plugin.getServer().getWorld(w1), x1, y1, z1, x2, y2, z2, i, id);
                debug("WG: " + i);
            }
        }
        Arena a = new Arena(id, minPlayers, maxPlayers, time, votingTime, lobbya, cuboidList);
        debug("New arena");
        arenas.add(a);
        return a;
    }

    public void createLobby(Location l) {
        plugin.getConfig().set("Lobby.World", l.getWorld().getName());
        plugin.getConfig().set("Lobby.X", l.getX());
        plugin.getConfig().set("Lobby.Y", l.getY());
        plugin.getConfig().set("Lobby.Z", l.getZ());
        plugin.saveConfig();
        loadLobby();
    }

    public void loadLobby() {
        if (plugin.getConfig().getString("Lobby.World") != null) {
            String w = plugin.getConfig().getString("Lobby.World");
            Double x = plugin.getConfig().getDouble("Lobby.X");
            Double y = plugin.getConfig().getDouble("Lobby.Y");
            Double z = plugin.getConfig().getDouble("Lobby.Z");
            // lobby = new Location(Bukkit.getWorld(w), x, y, z);
        }
        //debug("Lobby loaded: " + lobby.toString());
    }

    public String getRandomTheme() {
        List<String> themes = plugin.getConfig().getStringList("Themes");
        if(themes.isEmpty()){
            return "Missing.Contact Admin";
        }else{
            Random random = new Random();
            return themes.get(random.nextInt(themes.size()));
        }
    }

    //REWARDS
    public void giveVaultRewards(Player p, int points) {
        EconomyResponse r = plugin.econ.depositPlayer(p, points);
        if (r.transactionSuccess()) {
            p.sendMessage(ChatColor.GREEN + "[Build Battle] " + ChatColor.GREEN + getTr("34") + " " + points + " " + "$");
        }
    }

    public void Rewards(ArenaPlayer t, String s) {
        Player p = t.getPlayer();
        if (Vault) {
            giveVaultRewards(p.getPlayer(), plugin.getConfig().getInt("Rewards.Vault." + s));
        }
        if (Command) {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), (plugin.getConfig().getString("Rewards.Command." + s)).replace("$player$", p.getName()));
        }
        if (PlayerPoints) {
            PlayerPointsOptional.givePlayerPointsRewards(p.getPlayer(), plugin.getConfig().getInt("Rewards.PlayerPoints." + s));
        }
    }

    public void Rewards(Player p, String s) {
        if (Vault) {
            giveVaultRewards(p.getPlayer(), plugin.getConfig().getInt("Rewards.Vault." + s));
        }
        if (Command) {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), (plugin.getConfig().getString("Rewards.Command." + s)).replace("$player$", p.getName()));
        }
        if (PlayerPoints) {
            PlayerPointsOptional.givePlayerPointsRewards(p.getPlayer(), plugin.getConfig().getInt("Rewards.PlayerPoints." + s));
        }
    }
}

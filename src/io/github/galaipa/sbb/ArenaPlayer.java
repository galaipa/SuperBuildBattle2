package io.github.galaipa.sbb;

import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class ArenaPlayer {
    private final int id;
    public Player player;
    ItemStack[] armor;
    ItemStack[] inv;
    private int arenaID;
    private int point;
    private Cuboid cuboid;
    private Location startLoc;
    private World world;
    private int type;
    private byte data;
    private int exp;
    private long time;
   private WeatherType weather;
    private GameMode gamemode;

    public ArenaPlayer(Player p2, int arenaID, Location l) {
        player = p2;
        point = 0;
        id = ArenaManager.getManager().getArena(arenaID).getPlayers().size() + 1;
        this.arenaID = arenaID;
        inv = player.getInventory().getContents();
        armor = player.getInventory().getArmorContents();
        player.getInventory().setArmorContents(null);
        player.getInventory().clear();
        startLoc = l;
        exp = player.getLevel();
        time = 6000;
        weather = WeatherType.CLEAR;
        gamemode = player.getGameMode();
    }

    public int getArenaID() {
        return arenaID;
    }

    public int getID() {
        return id;
    }

    public void addRegion(Cuboid c) {
        //  cuboid = new Cuboid(l1,l2);
        cuboid = c;
        //  world = l1.getWorld();
        world = c.getWorld();
        for (Block block : cuboid.getFace(Cuboid.CuboidDirection.Down)) {
            data = block.getData();
            type = block.getTypeId();
            break;
        }
    }

    public void resetArenas() {
        for (Block block : cuboid) {
            block.setType(Material.AIR);
        }
        for (Chunk chunk : cuboid.getChunks()) {
            for (Entity e : chunk.getEntities()) {
                if (e instanceof Player) {
                    return;
                } else if (cuboid.contains(e.getLocation())) {
                    e.remove();
                }
            }
        }
        setGround(type, data);
    }

    public void setGround(int type, byte data) {
        for (Block block : cuboid.getFace(Cuboid.CuboidDirection.Down)) {
            block.setTypeIdAndData(type, data, true);
        }
    }

    public Player getPlayer() {
        return player;
    }

    public World getWorld() {
        return world;
    }

    public Cuboid getCuboid() {
        return cuboid;
    }

    public int getPoint() {
        return point;
    }

    public void addPoint(int p) {
        point = point + p;
    }

    public Location getSpawnPoint() {
        Location spawnpoint = cuboid.getCenter();
        return spawnpoint.getWorld().getHighestBlockAt(spawnpoint).getLocation();
    }

    public Location getPreSpawn() {
        return startLoc;
    }

    public String getPlayerString() {
        return player.getName();
    }

    public void returnInv() {
        player.getInventory().setContents(inv);
        player.getInventory().setArmorContents(armor);
        player.setLevel(exp);
        player.setGameMode(gamemode);
    }

    public void returnInv(Player p1) {
        p1.getInventory().setContents(inv);
        p1.getInventory().setArmorContents(armor);
        p1.setLevel(exp);
        p1.setGameMode(gamemode);
    }

    public Boolean checkPlayer(Player p) {
        return player.equals(p);
    }

    public long getTime() {
        return time;
    }

    public void setTime(long t) {
        player.setPlayerTime(t, false);
        time = t;
    }

  public WeatherType getWeather() {
        if (weather == null) {
            return WeatherType.CLEAR;
        }
        return weather;
    }

    public void setWeather(WeatherType t) {
        player.setPlayerWeather(t);
        weather = t;
    }

}
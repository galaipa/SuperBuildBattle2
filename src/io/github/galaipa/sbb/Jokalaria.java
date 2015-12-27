package io.github.galaipa.sbb;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class Jokalaria {
    public Player Jokalaria;
    ItemStack[] armor;
    ItemStack[] inv;
    private int id;
    private Arena arena;
    private Player p;
    private int point;
    private Cuboid cuboid;
    private Location spawnpoint, startLoc;
    private World world;
    private int type;
    private byte data;
    private int exp;
    public Jokalaria(Player p2, int arenaID, Location l) {
        p = p2;
        point = 0;
        arena = ArenaManager.getManager().getArena(arenaID);
        id = arena.getPlayers().size() + 1;
        inv = p.getInventory().getContents();
        armor = p.getInventory().getArmorContents();
        p.getInventory().setArmorContents(null);
        p.getInventory().clear();
        startLoc = l;
        exp = p.getLevel();
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
        setGround(type, data);
    }

    public void setGround(int type, byte data) {
        for (Block block : cuboid.getFace(Cuboid.CuboidDirection.Down)) {
            block.setTypeIdAndData(type, data, true);
        }
    }

    public Player getPlayer() {
        return p;
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
        Location v = cuboid.getCenter();
        spawnpoint = v;
        return spawnpoint;
    }

    public Location getPreSpawn() {
        return startLoc;
    }

    public void addPlayer(Player p) {
        Jokalaria = p;
    }

    public void removePlayer(Player p) {
        Jokalaria = null;
    }

    public String getPlayerString() {
        return p.getName();
    }

    public void returnInv() {
        p.getInventory().setContents(inv);
        p.getInventory().setArmorContents(armor);
        p.setLevel(exp);
    }

    public Boolean checkPlayer(Player p) {
        Player pa = Jokalaria;
        if (p == pa) {
            return true;
        } else {
            return false;
        }
    }
}


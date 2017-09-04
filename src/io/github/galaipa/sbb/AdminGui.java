package io.github.galaipa.sbb;

import static io.github.galaipa.sbb.AdminGui.item;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import static io.github.galaipa.sbb.ArenaManager.getManager;
import static io.github.galaipa.sbb.InGameGui.myInventory;
import static io.github.galaipa.sbb.SuperBuildBattle.getTr;



public class AdminGui implements Listener {
    
    private SuperBuildBattle instance;
    public AdminGui(SuperBuildBattle instance){
        this.instance = instance;
    }
    private static ArrayList<Player> admins = new ArrayList<>();
     
    private int timeBuild;
    private int timeVote;
    private int playersMin;
    private int playersMax;
    private Location spawnpoint;
    private final Map<Integer, Location> location1 = new HashMap<>();
    private final Map<Integer, Location> location2 = new HashMap<>();
    private ArrayList<Cuboid> cuboids = new ArrayList<>();
    private static int arenaId;
    

    @EventHandler
    public void onInventoryClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            if(admins.contains(event.getPlayer())){
                event.setCancelled(true);
                Player p = event.getPlayer();
                String izena =getHand(p).getItemMeta().getDisplayName();
                izena = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', izena));
                if(izena.startsWith("Arena")){
                    arenaId = getHand(p).getAmount();
                    guiArena(p);
                }
                switch(izena){
                    case "New Arena":
                        eNewArena(p);
                        break;
                    case "Close Menu":
                        eCloseMenu(p);
                        break;
                    case "Set building time (Minutes)":
                        eSetBuildTime(p);
                        break;
                    case "Set voting time (Seconds)":
                        eSetVoteTime(p);
                        break;
                    case "Set minimum players":
                        eSetMinPlayers(p);
                        break;
                    case "Set maximum players":
                        eSetMaxPlayers(p);
                        break;
                    case "Clear":
                        eClear(p);
                        break;
                    case "Next step":
                        eNextStep(p);
                        break;
                    case "Abort":
                        eCloseMenu(p);
                        break;
                    case "Next arena":
                        eNextCuboid(p);
                        break;
                    case "Set lobby spawnpoint":
                        eLobby(p);
                        break;     
                    case "Force START game":
                        eForceStart(p);
                        break;
                    case "Force STOP game":
                        eForceStop(p);
                        break;
                    case "Close":
                        eCloseMenu(p);
                }
            }else if(getManager().getArena(event.getPlayer()) != null){
                ItemStack i = getHand(event.getPlayer());
                if(i.getType() == Material.BED){
                    boolean cancel = eLeave(event.getPlayer());
                    event.setCancelled(cancel);
                }else if (i.getType() == Material.ENCHANTED_BOOK){
                    boolean cancel = eMenu(event.getPlayer());
                    event.setCancelled(cancel);
                }
                
            }
        }
        
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(admins.contains(event.getPlayer())){
            Player p = event.getPlayer();
            if(getHand(p).hasItemMeta() && getHand(p).getItemMeta().hasDisplayName()){
                String izena = getHand(p).getItemMeta().getDisplayName();
                izena = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', izena));
                switch(izena){
                    case "Point A":
                        ePointA(p,event.getBlock().getLocation());
                        break;
                    case "Point B":
                        ePointB(p,event.getBlock().getLocation());
                        break;
                }
                event.setCancelled(true);
            }
        }
    }
    public static void guiArenas(Player p){
        PlayerInventory inv = p.getInventory();
        inv.clear();
        for (Arena a : ArenaManager.getManager().arenas) {
            inv.addItem(item(Material.STAINED_CLAY, 5, a.getID(), ChatColor.GREEN + "Arena " + a.getID()));
        }
        inv.addItem(item(Material.STAINED_CLAY, 4, 1, ChatColor.GREEN + "New Arena"));
        inv.addItem(item(Material.STAINED_CLAY, 1, 1, ChatColor.RED + "Close Menu"));
        p.updateInventory();
    }
    public void guiNewArena(Player p){
        defaultSetup();
        PlayerInventory inv = p.getInventory();
        inv.clear();
        inv.addItem(item(Material.STAINED_CLAY, 3, 1, ChatColor.GREEN + "Set building time (Minutes)"));
        inv.addItem(item(Material.STAINED_CLAY, 3, 1, ChatColor.GREEN + "Set voting time (Seconds)"));
        inv.addItem(item(Material.STAINED_CLAY, 3, 1, ChatColor.GREEN + "Set minimum players"));
        inv.addItem(item(Material.STAINED_CLAY, 3, 1, ChatColor.GREEN + "Set maximum players"));
        inv.addItem(item(Material.STAINED_CLAY, 3, 1, ChatColor.GREEN + "Set lobby spawnpoint"));
        inv.addItem(item(Material.STAINED_CLAY, 4, 1, ChatColor.GREEN + "Clear"));
        inv.addItem(item(Material.STAINED_CLAY, 13, 1, ChatColor.GREEN + "Next step"));
        inv.addItem(item(Material.STAINED_CLAY, 1, 1, ChatColor.RED + "Abort"));
        p.updateInventory();
    }
    public void guiCuboid(Player p, int id){
        PlayerInventory inv = p.getInventory();
        inv.clear();
        inv.addItem(item(Material.STAINED_CLAY, 10, id, ChatColor.GREEN + "Point A"));
        inv.addItem(item(Material.STAINED_CLAY, 11, id, ChatColor.GREEN + "Point B"));
        inv.addItem(item(Material.STAINED_CLAY, 5, id, ChatColor.GREEN + "Next arena"));
        p.updateInventory();
    }
    public void guiArena(Player p){
        PlayerInventory inv = p.getInventory();
        inv.clear();
    //    inv.addItem(item(Material.STAINED_CLAY, 3, 1, ChatColor.GREEN + "Setup arena"));
        inv.addItem(item(Material.STAINED_CLAY, 5, 1, ChatColor.GREEN + "Force START game"));
        inv.addItem(item(Material.STAINED_CLAY, 14, 1, ChatColor.GREEN + "Force STOP game"));
        inv.addItem(item(Material.STAINED_CLAY, 1, 1, ChatColor.GREEN + "Close"));
        p.updateInventory();
    }
    public void eNewArena(Player p){
        int id = ArenaManager.getManager().arenas.size();
        id++;
        arenaId = id;
        guiNewArena(p);
        p.sendMessage(ChatColor.YELLOW + "[Build Battle] " + ChatColor.GREEN + "You have entered the BuildBattle setup:");
        p.sendMessage(ChatColor.YELLOW + "[Build Battle] " + ChatColor.GREEN + "Use the setup inventory to set all the game parametres. Use it with out opening the inventory");
        p.sendMessage("");
    }
    public void eCloseMenu(Player p){ // also eAbort
        admins.remove(p);
        p.getInventory().clear();
    }
    public void eSetBuildTime(Player p){
        timeBuild++;
        addOne(p);
    }
    public void eSetVoteTime(Player p){
        timeVote++;
        addOne(p);
    }
    public void eSetMinPlayers(Player p){
        playersMin++;
        addOne(p);
    }
    public void eSetMaxPlayers(Player p){
        playersMax++;
        addOne(p);
    }
    public void eClear(Player p){
        guiNewArena(p);
    }
    public void eLobby(Player p){
        spawnpoint = p.getLocation();
        p.sendMessage(ChatColor.YELLOW + "[Build Battle] " + ChatColor.GREEN + "Lobby set\n\n");
    }
    public void eNextStep(Player p){
        if(spawnpoint == null){
            p.sendMessage(ChatColor.YELLOW + "[Build Battle] " + ChatColor.RED + "SpawnPoint missing\n\n");
        }else{
            guiCuboid(p,1);
        }
    }
    public void eNextCuboid(Player p){
        int id = getHand(p).getAmount();
        if (location1.get(id) == null || location2.get(id) == null) {
            p.sendMessage(ChatColor.YELLOW + "[Build Battle] " + ChatColor.RED + "Points missing\n\n");
        } else {
            cuboids.add(new Cuboid(location1.get(id), location2.get(id)));
            if (id != playersMax) {
                guiCuboid(p,id+1);
            } else {
                ArenaManager.getManager().createArena(arenaId, playersMin, playersMax, timeBuild, timeVote, spawnpoint, cuboids);
                p.getInventory().clear();
                defaultSetup();
                p.sendMessage(ChatColor.YELLOW + "[Build Battle] " + ChatColor.GREEN + "You finished setting up the game. Now you can start having fun.");
            }
        }
    }
    public void ePointA(Player p,Location loc){
        int id = getHand(p).getAmount();
        location1.put(id, loc);
        p.sendMessage(ChatColor.YELLOW + "[Build Battle] " + ChatColor.GREEN + "Point A set");
    }
    public void ePointB(Player p,Location loc){
        int id = getHand(p).getAmount();
        location2.put(id, loc);
        p.sendMessage(ChatColor.YELLOW + "[Build Battle] " + ChatColor.GREEN + "Point B set");
    }
    public void eForceStart(Player p){
        Arena a = ArenaManager.getManager().getArena(arenaId);
        if (a.players.isEmpty()) {
            p.sendMessage(ChatColor.YELLOW + "[Build Battle] " + ChatColor.RED + "Not enough players");
            return;
        }
        p.sendMessage(ChatColor.YELLOW + "[Build Battle] " + ChatColor.GREEN + "You forced the game to start");
        a.forceStart();
        eCloseMenu(p);
    }
    public void eForceStop(Player p){
        Arena a = ArenaManager.getManager().getArena(arenaId);
        a.reset();
        p.sendMessage(ChatColor.YELLOW + "[Build Battle] " + ChatColor.GREEN + "You forced the game to stop");
        eCloseMenu(p);
    }
    public boolean eLeave(Player p){
        ItemStack i = getHand(p);
        if (i.hasItemMeta() && i.getItemMeta().hasDisplayName()) {
            if (i.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + getTr("38"))) {
                ArenaManager.getManager().removePlayer(p, true);
                p.sendMessage(ChatColor.RED + getTr("3"));
                return true;
            }
        }
        return false;
    }
    public boolean eMenu(Player p){
        ItemStack i = getHand(p);
        if (i.hasItemMeta() && i.getItemMeta().hasDisplayName()) {
            if (i.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.BLUE + "Menu")) {
                p.openInventory(myInventory);
                return true;
            }
        }
        return false;
    }

   
    
    public void defaultSetup(){
        timeBuild = 1;
        timeVote = 1;
        spawnpoint = null;
        playersMin = 1;
        playersMax = 1;
        cuboids.clear();
        location1.clear();
        location2.clear();
    }
    
    public static ItemStack item(Material material, int id, int amount, String name) {
        ItemStack b = new ItemStack(material, amount, (short) id);
        ItemMeta metaB = b.getItemMeta();
        metaB.setDisplayName(name);
        b.setItemMeta(metaB);
        return b;
    }
    public void addOne(Player p){
        ItemStack i = getHand(p);
        p.getInventory().remove(i);
        i.setAmount(i.getAmount() + 1);
        p.getInventory().addItem(i);
        p.updateInventory();
    }
    
    public static void newAdmin(Player p) {
        admins.add(p);
        guiArenas(p);
    }
    
    private ItemStack getHand(Player p){
        ItemStack item;
        if(SuperBuildBattle.version.startsWith("1.8")){
            item = p.getItemInHand();
        }else{
           item = p.getInventory().getItemInMainHand();
        }
        return item;
    }    
}



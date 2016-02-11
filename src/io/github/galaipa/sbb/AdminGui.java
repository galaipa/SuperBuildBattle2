package io.github.galaipa.sbb;


import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.galaipa.sbb.ArenaManager.getManager;
import static io.github.galaipa.sbb.InGameGui.myInventory;

public class AdminGui implements Listener {
    public static int arenaId;
    //public final Map<Integer, Location> location1 = new HashMap<>();
    //public final Map<Integer, Location> location2 = new HashMap<>();
    ArrayList<Cuboid> cuboids = new ArrayList<>();
    private int time = 1;
    private int timeVote = 1;
    private int players1 = 1;
    private int players2 = 1;
    private Location l;
    private boolean region = false;
    private boolean setup = false;
    private SuperBuildBattle instance;

    public AdminGui(SuperBuildBattle instance){
        this.instance = instance;
    }

    public static ItemStack item(Material material, int id, int amount, String name) {
        ItemStack b = new ItemStack(material, amount, (short) id);
        ItemMeta metaB = b.getItemMeta();
        metaB.setDisplayName(name);
        b.setItemMeta(metaB);
        return b;
    }

    public static ItemStack item(Material material, int id, int amount, String name, String lore) {
        ItemStack b = new ItemStack(material, amount, (short) id);
        ItemMeta metaB = b.getItemMeta();
        metaB.setDisplayName(name);
        List<String> lorea = new ArrayList<>();
        lorea.add(lore);
        metaB.setLore(lorea);
        b.setItemMeta(metaB);
        return b;
    }

    public static void arenaGui(Player p) {
        Inventory inv = p.getInventory();
        inv.clear();
        for (Arena a : ArenaManager.getManager().arenas) {
            inv.addItem(item(Material.STAINED_CLAY, 5, a.getID(), ChatColor.GREEN + "Arena " + a.getID()));
        }
        inv.addItem(item(Material.STAINED_CLAY, 4, 1, ChatColor.GREEN + "New Arena"));
        inv.addItem(item(Material.STAINED_CLAY, 1, 1, ChatColor.RED + "Close Menu"));
        p.updateInventory();
    }

    public static void adminGui(Player p) {
        Inventory inv = p.getInventory();
        inv.clear();
        inv.addItem(item(Material.STAINED_CLAY, 3, 1, ChatColor.GREEN + "Setup arena"));
        inv.addItem(item(Material.STAINED_CLAY, 5, 1, ChatColor.GREEN + "Force START game"));
        inv.addItem(item(Material.STAINED_CLAY, 14, 1, ChatColor.GREEN + "Force STOP game"));
        inv.addItem(item(Material.STAINED_CLAY, 1, 1, ChatColor.GREEN + "Close"));
        p.updateInventory();
    }

    public static void SetupInventory(Player p) {
        Inventory inv = p.getInventory();
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

    public void SetupInventory2(Player p, int id) {
        region = true;
        setup = true;
        Inventory inv = p.getInventory();
        inv.clear();
        p.sendMessage(ChatColor.GREEN + "[Build Battle] " + ChatColor.RED + " Select a WorldEdit region with //pos1 and //pos2 or //wand, then right click Next Arena to setup next arena region!");
        //inv.addItem(item(Material.STAINED_CLAY, 10, id, ChatColor.GREEN + "Point A"));
        //inv.addItem(item(Material.STAINED_CLAY, 11, id, ChatColor.GREEN + "Point B"));
        inv.addItem(item(Material.STAINED_CLAY, 5, id, ChatColor.GREEN + "Next arena"));
        p.updateInventory();
    }

    @EventHandler
    public void onInventoryClick2(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player p = event.getPlayer();
            if (p.getItemInHand().getType() == Material.STAINED_CLAY && p.getItemInHand().hasItemMeta() && p.getItemInHand().getItemMeta().hasDisplayName()) {
                String izena = p.getItemInHand().getItemMeta().getDisplayName();
                ItemStack i = p.getItemInHand();
                Inventory inve = p.getInventory();
                if (setup && event.getPlayer().hasPermission("bb.admin")) {
                    if (izena.equalsIgnoreCase(ChatColor.GREEN + "Set building time (Minutes)")) {
                        event.setCancelled(true);
                        time = i.getAmount() + 1;
                        inve.remove(i);
                        i.setAmount(i.getAmount() + 1);
                        inve.addItem(i);
                        p.updateInventory();
                    } else if (izena.equalsIgnoreCase(ChatColor.GREEN + "Set voting time (Seconds)")) {
                        event.setCancelled(true);
                        timeVote = i.getAmount() + 5;
                        inve.remove(i);
                        i.setAmount(timeVote);
                        inve.addItem(i);
                        p.updateInventory();
                    } else if (izena.equalsIgnoreCase(ChatColor.GREEN + "Set minimum players")) {
                        event.setCancelled(true);
                        players1 = i.getAmount() + 1;
                        inve.remove(i);
                        i.setAmount(i.getAmount() + 1);
                        inve.addItem(i);
                        p.updateInventory();
                    } else if (izena.equalsIgnoreCase(ChatColor.GREEN + "Set maximum players")) {
                        event.setCancelled(true);
                        players2 = i.getAmount() + 1;
                        inve.remove(i);
                        i.setAmount(i.getAmount() + 1);
                        inve.addItem(i);
                        p.updateInventory();
                    } else if (izena.equalsIgnoreCase(ChatColor.GREEN + "Clear")) {
                        event.setCancelled(true);
                        time = 1;
                        players1 = 1;
                        players2 = 1;
                        timeVote = 1;
                        p.getInventory().clear();
                        SetupInventory(p);
                    } else if (izena.equalsIgnoreCase(ChatColor.GREEN + "Set lobby spawnpoint")) {
                        event.setCancelled(true);
                        l = p.getLocation();
                        p.sendMessage(ChatColor.YELLOW + "[Build Battle] " + ChatColor.GREEN + "Lobby set to: " + p.getLocation());
                    } else if (izena.equalsIgnoreCase(ChatColor.GREEN + "Next step")) {
                        event.setCancelled(true);
                        if (l == null) {
                            p.sendMessage(ChatColor.YELLOW + "[Build Battle] " + ChatColor.RED + "SpawnPoint missing");
                            return;
                        }
                        SetupInventory2(p, 1);
                    } else if (izena.equalsIgnoreCase(ChatColor.RED + "Abort")) {
                        event.setCancelled(true);
                        ArenaManager.admin = false;
                        setup = false;
                        time = 1;
                        players1 = 1;
                        players2 = 1;
                        timeVote = 1;
                        p.getInventory().clear();
                        p.sendMessage(ChatColor.YELLOW + "[Build Battle] " + ChatColor.RED + "New arena aborted");
                    }else if (izena.equalsIgnoreCase(ChatColor.GREEN + "Next arena")) {
                        int id = p.getItemInHand().getAmount();
                        Selection sel = instance.getWorldEdit().getSelection(p);
                        if(sel == null || sel.getMaximumPoint() == null || sel.getMinimumPoint() == null){
                            p.sendMessage(ChatColor.YELLOW + "[Build Battle] " + ChatColor.RED + "Select a worldguard region first!!");
                            return;
                        }
                        cuboids.add(new Cuboid(sel.getMinimumPoint(), sel.getMaximumPoint()));
                        if (id != players2) {
                            SetupInventory2(p, id + 1);
                        } else {
                            ArenaManager.getManager().createArena(arenaId, players1, players2, time, timeVote, l, cuboids);
                            p.sendMessage(ChatColor.YELLOW + "[Build Battle] " + ChatColor.GREEN + "You finished setting up the game. Now you can start having fun.");
                            p.getInventory().clear();
                            setup = false;
                            region = false;
                        }
                    }
                } else if (ArenaManager.admin && event.getPlayer().hasPermission("bb.admin")) {
                    if (izena.equalsIgnoreCase(ChatColor.GREEN + "Setup arena")) {
                        p.getInventory().clear();
                        SetupInventory(p);
                        setup = true;
                        p.sendMessage(ChatColor.YELLOW + "[Build Battle] " + ChatColor.GREEN + "You have entered the BuildBattle setup:");
                        p.sendMessage(ChatColor.YELLOW + "[Build Battle] " + ChatColor.GREEN + "Use the setup inventory to set all the game parametres. Use it with out opening the inventory");
                    } else if (izena.equalsIgnoreCase(ChatColor.GREEN + "Force START game")) {
                        Arena a = ArenaManager.getManager().getArena(arenaId);
                        if (a.players.isEmpty()) {
                            p.sendMessage(ChatColor.YELLOW + "[Build Battle] " + ChatColor.RED + "Not enough players");
                            return;
                        }
                        p.sendMessage(ChatColor.YELLOW + "[Build Battle] " + ChatColor.GREEN + "You forced the game to start");
                        a.forceStart();
                        setup = false;
                        //admin = false;
                    } else if (izena.equalsIgnoreCase(ChatColor.GREEN + "Force STOP game")) {
                        p.sendMessage(ChatColor.YELLOW + "[Build Battle] " + ChatColor.GREEN + "You forced the game to stop");
                        Arena a = ArenaManager.getManager().getArena(arenaId);
                        a.reset();
                        setup = false;
                    } else if (izena.equalsIgnoreCase(ChatColor.GREEN + "Close")) {
                        setup = false;
                        ArenaManager.admin = false;
                        p.getInventory().clear();
                        // plugin.returnInventory(p);
                    } else if (izena.equalsIgnoreCase(ChatColor.GREEN + "New Arena")) {
                        int id = ArenaManager.getManager().arenas.size();
                        id++;
                        p.getInventory().clear();
                        SetupInventory(p);
                        setup = true;
                        p.sendMessage(ChatColor.YELLOW + "[Build Battle] " + ChatColor.GREEN + "You have entered the BuildBattle setup:");
                        p.sendMessage(ChatColor.YELLOW + "[Build Battle] " + ChatColor.GREEN + "Use the setup inventory to set all the game parametres. Use it with out opening the inventory");
                        arenaId = id;
                    } else if (izena.startsWith(ChatColor.GREEN + "Arena")) {
                        int id = i.getAmount();
                        arenaId = id;
                        AdminGui.adminGui(p);
                    } else if (izena.equalsIgnoreCase(ChatColor.RED + "Close Menu")) {
                        ArenaManager.admin = false;
                        p.getInventory().clear();
                    }
                }
            } else if (event.getPlayer().getItemInHand().getType() == Material.ENCHANTED_BOOK) {
                if (getManager().getArena(event.getPlayer()) != null) {
                    Arena a = getManager().getArena(event.getPlayer());
                    if (a.inGame) {
                        if (p.getItemInHand().hasItemMeta()) {
                            if (p.getItemInHand().getItemMeta().hasDisplayName()) {
                                if (p.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.BLUE + "Menu")) {
                                    event.getPlayer().openInventory(myInventory);
                                    event.setCancelled(true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}



package io.github.galaipa.sbb;

import com.darkblade12.particleeffect.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Map;

import static io.github.galaipa.sbb.AdminGui.item;
import static io.github.galaipa.sbb.ArenaManager.getManager;
import org.bukkit.WeatherType;


public class InGameGui implements Listener {
    public static Inventory myInventory;
    public static Inventory skullsInventory;
    public static Inventory weatherInventory;
    public static Inventory timeInventory;
    public static Inventory partInventory;
    public static Map<String, String> skulls = new HashMap<>();

    public static void userGui() {
        myInventory = Bukkit.createInventory(null, 9, "InGame Menu");
     //   myInventory.setItem(1, item(Material.BARRIER, 0, 1, ChatColor.GREEN + "Particles", "Reset your plot"));
        myInventory.setItem(2, item(Material.BARRIER, 0, 1, ChatColor.RED + "Clear arena", "Reset your plot"));
        myInventory.setItem(3, item(Material.WOOD_PLATE, 0, 1, ChatColor.GREEN + "Set ground", "Drag the block you want to put as floor"));
        myInventory.setItem(4,item(Material.SULPHUR,0,1,ChatColor.GREEN + "Weather", "Set the Weather in your plot"));
        myInventory.setItem(5,item(Material.WATCH,0,1,ChatColor.GREEN + "Time", "Set the Time in your plot"));
        myInventory.setItem(6, item(Material.SKULL_ITEM, 3, 1, ChatColor.GREEN + "Skulls", "Decorate your plot with cool skulls"));
        skullGui();
        weatherGui();
        timeGui();
        partGui();
    }

    public static void giveUserGui(Player p) {
        p.getInventory().setItem(8, item(Material.ENCHANTED_BOOK, 1, 1, ChatColor.BLUE + "Menu"));
        p.updateInventory();

    }
    public static void weatherGui() {
        weatherInventory  = Bukkit.createInventory(null, 9, "Weather");
        weatherInventory.setItem(2, item(Material.WATER_BUCKET,0,1,ChatColor.BLUE + "Rain"));
        weatherInventory.setItem(6, item(Material.LAVA_BUCKET,0,1,ChatColor.RED + "Sun"));
    }
    public static void timeGui() {
        timeInventory  = Bukkit.createInventory(null, 9, "Time");
        timeInventory.setItem(2, item(Material.STAINED_CLAY,8,1,ChatColor.BLUE + "3:00"));
        timeInventory.setItem(3, item(Material.STAINED_CLAY,4,1,ChatColor.BLUE + "6:00"));
        timeInventory.setItem(4, item(Material.STAINED_CLAY,6,1,ChatColor.BLUE + "12:00"));
        timeInventory.setItem(5, item(Material.STAINED_CLAY,1,1,ChatColor.BLUE + "18:00"));
        timeInventory.setItem(6, item(Material.STAINED_CLAY,9,1,ChatColor.BLUE + "24:00"));
        
    }
    public static void partGui(){
       partInventory = Bukkit.createInventory(null, 9, "Particles");
       partInventory.addItem(item(Material.TNT,8,1,ChatColor.GREEN + "TNT"));
    }
    public static void skullGui() {
        skullsInventory = Bukkit.createInventory(null, 54, "Skulls");
        String s = "Blaze,CaveSpider,Chicken,Cow,Enderman,Ghast,Golem,LavaSlime,MushroomCow,Ocelot,Pig,PigZombie,Sheep,Slime,Spider,Squid,Villager,Cactus,Cake,Chest,Melon,OakLog,Pumpkin,TNT,ArrowUp,ArrowLeft,ArrowRight,Question,Exclamation,";
        for (String n : s.split(",")) {
            skulls.put("MHF_" + n, n);
        }
        skulls.put("rsfx", "Leaves");
        skulls.put("Robbydeezle", "Rocks");
        skulls.put("C418", "Music");
        skulls.put("scemm", "Dispenser");
        skulls.put("Panda4994", "Sticky piston");
        skulls.put("JL2579", "Piston");
        skulls.put("akaBruce", "Diamond ore");
        skulls.put("annayirb", "Redstone ore");
        skulls.put("Tereneckla", "Emerald ore");
        skulls.put("pomi44", "Sponge");
        skulls.put("bubbadawg01", "Quartz block");
        skulls.put("Bendablob", "Hay");
        skulls.put("teachdaire", "Gold block");
        skulls.put("metalhedd", "Iron Block");
        skulls.put("loiwiol", "Obsidian");
        skulls.put("rugofluk", "Sand");
        skulls.put("ZachWarnerHD", "Popcorn");
        skulls.put("ChoclateMuffin", "Muffin");
        skulls.put("food", "Hamburger");
        skulls.put("CoderPuppy", "Monitor");
        skulls.put("sysfailure", "TV");
        skulls.put("uioz", "Radio");
        skulls.put("Edna_I", "Ender eye");
        skulls.put("KylexDavis", "Apple");
        skulls.put("Chuzard", "Pokeball");
        for (Map.Entry<String, String> entry : skulls.entrySet()) {
            String owner = entry.getKey();
            String name = entry.getValue();
            ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1);
            skull.setDurability((short) 3);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setDisplayName(name);
            meta.setOwner(owner);
            skull.setItemMeta(meta);
            skullsInventory.addItem(skull);
        }
    }

    @EventHandler
    public void userGuiUse(InventoryClickEvent event) {
        if (getManager().getArena((Player) event.getWhoClicked()) != null) {
            Arena a = getManager().getArena((Player) event.getWhoClicked());
            if (a.inGame) {
                Inventory inventory = event.getInventory();
                if (inventory.getName().equals(myInventory.getName()) && event.getCurrentItem() != null) {
                    ItemStack clicked = event.getCurrentItem();
                    Player player = (Player) event.getWhoClicked();
                    ArenaPlayer t = a.getArenaPlayer(player);
                    String izena;
                    if (clicked.hasItemMeta() && clicked.getItemMeta().hasDisplayName()) {
                        izena = clicked.getItemMeta().getDisplayName();
                    } else {
                        return;
                    }
                    if (izena.equalsIgnoreCase(ChatColor.RED + "Clear arena")) {
                        event.setCancelled(true);
                        t.resetArenas();
                        player.closeInventory();
                    } else if (izena.equalsIgnoreCase(ChatColor.GREEN + "Set ground")) {
                        event.setCancelled(true);
                        ItemStack i = event.getCursor();
                        player.closeInventory();
                        if (i.getType().isBlock()) {
                            t.setGround(i.getTypeId(), i.getData().getData());
                        } else if (i.getType() == Material.LAVA_BUCKET) {
                            for (Block block : t.getCuboid().getFace(Cuboid.CuboidDirection.Down)) {
                                block.setType(Material.LAVA);
                            }
                        } else if (i.getType() == Material.WATER_BUCKET) {
                            for (Block block : t.getCuboid().getFace(Cuboid.CuboidDirection.Down)) {
                                block.setType(Material.WATER);
                            }
                        }

                    }else if (izena.equalsIgnoreCase(ChatColor.GREEN + "Weather")) {
                        event.setCancelled(true);
                        player.openInventory(weatherInventory);
                    }else if (izena.equalsIgnoreCase(ChatColor.GREEN + "Time")) {
                        event.setCancelled(true);
                        player.openInventory(timeInventory);
                    }else if (izena.equalsIgnoreCase(ChatColor.GREEN + "Particles")) {
                        event.setCancelled(true);
                        player.openInventory(partInventory);
                    }
                        
                    /*else if (izena.equalsIgnoreCase(ChatColor.GREEN + "Banner")) {
                    event.setCancelled(true);
                    if(ArenaManager.BannerMakerEnabled){
                        BannerMakerOptional.getBannerMaker().getAPI().openBannerMakerGUI(player);
                    }else{
                        player.sendMessage(ChatColor.GREEN +"[Build Battle] " + ChatColor.RED + "BannerMaker plugin is needed to use this feature");
                    }
                }*/ else if (izena.equalsIgnoreCase(ChatColor.GREEN + "Skulls")) {
                        event.setCancelled(true);
                        player.openInventory(skullsInventory);
                    }
                }if (inventory.getName().equalsIgnoreCase(weatherInventory.getName()) && event.getCurrentItem() != null) {
                    event.setCancelled(true);
                    ItemStack clicked = event.getCurrentItem();
                    Player player = (Player) event.getWhoClicked();
                    ArenaPlayer t = a.getArenaPlayer(player);
                    String izena;
                    if (clicked.hasItemMeta() && clicked.getItemMeta().hasDisplayName()) {
                        izena = clicked.getItemMeta().getDisplayName();
                    } else {
                        return;
                    }
                    if (izena.equalsIgnoreCase(ChatColor.BLUE + "Rain")) {
                        player.setPlayerWeather(WeatherType.DOWNFALL); 
                        player.closeInventory();
                    }else if (izena.equalsIgnoreCase(ChatColor.RED + "Sun")) {
                        player.setPlayerWeather(WeatherType.CLEAR); 
                        player.closeInventory();
                    }
                }if (inventory.getName().equalsIgnoreCase(timeInventory.getName()) && event.getCurrentItem() != null) {
                    event.setCancelled(true);
                    ItemStack clicked = event.getCurrentItem();
                    Player player = (Player) event.getWhoClicked();
                    ArenaPlayer t = a.getArenaPlayer(player);
                    String izena;
                    if (clicked.hasItemMeta() && clicked.getItemMeta().hasDisplayName()) {
                        izena = clicked.getItemMeta().getDisplayName();
                    } else {
                        return;
                    }
                    if (izena.equalsIgnoreCase(ChatColor.BLUE + "6:00")) {
                        t.setTime(0);
                        player.closeInventory();
                    }else if (izena.equalsIgnoreCase(ChatColor.BLUE + "12:00")) {
                        t.setTime(6000);
                        player.closeInventory();  
                    }else if (izena.equalsIgnoreCase(ChatColor.BLUE + "18:00")) {
                        t.setTime(1200);
                        player.closeInventory();  
                    }else if (izena.equalsIgnoreCase(ChatColor.BLUE + "24:00")) {
                        t.setTime(18000);
                        player.closeInventory();  
                    }else if (izena.equalsIgnoreCase(ChatColor.BLUE + "3:00")) {
                        t.setTime(21000);
                        player.closeInventory();  
                    }
                }if (inventory.getName().equals(skullsInventory.getName()) && event.getCurrentItem() != null) {
                    event.setCancelled(true);
                    Player player = (Player) event.getWhoClicked();
                    ItemStack i = event.getCurrentItem();
                    player.getInventory().addItem(i);
                    player.closeInventory();
                }if (inventory.getName().equals(partInventory.getName()) && event.getCurrentItem() != null) {
                    ItemStack clicked = event.getCurrentItem();
                    Player p = (Player) event.getWhoClicked();
                    ArenaPlayer t = a.getArenaPlayer(p);
                    String izena;
                    if (clicked.hasItemMeta() && clicked.getItemMeta().hasDisplayName()) {
                        izena = clicked.getItemMeta().getDisplayName();
                    } else {
                        return;
                    }
                    if (izena.equalsIgnoreCase(ChatColor.GREEN + "TNT")) {
                        ParticleEffect.EXPLOSION_NORMAL.display(10, 10, 10, 10, 20, p.getLocation(), p);
                    }
                }
            }
        }
    }


}


package io.github.galaipa.sbb;

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


public class InGameGui implements Listener {
    public static Inventory myInventory;
    public static Inventory skullsInventory;
    public static Map<String, String> skulls = new HashMap<>();

    public static void userGui() {
        myInventory = Bukkit.createInventory(null, 9, "InGame Menu");
        myInventory.setItem(1, item(Material.BARRIER, 0, 1, ChatColor.RED + "Clear arena", "Reset your plot"));
        myInventory.setItem(3, item(Material.WOOD_PLATE, 0, 1, ChatColor.GREEN + "Set ground", "Drag the block you want to put as floor"));
        //     myInventory.setItem(7,item(Material.BANNER,0,1,ChatColor.GREEN + "Banner"));
        myInventory.setItem(5, item(Material.SKULL_ITEM, 3, 1, ChatColor.GREEN + "Skulls", "Decorate your plot with cool skulls"));
        skullGui();
    }

    public static void giveUserGui(Player p) {
        p.getInventory().setItem(8, item(Material.ENCHANTED_BOOK, 1, 1, ChatColor.BLUE + "Menu"));
        p.updateInventory();

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

                    }/*else if (izena.equalsIgnoreCase(ChatColor.GREEN + "Banner")) {
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
                }
                if (inventory.getName().equals(skullsInventory.getName()) && event.getCurrentItem() != null) {
                    event.setCancelled(true);
                    Player player = (Player) event.getWhoClicked();
                    ItemStack i = event.getCurrentItem();
                    player.getInventory().addItem(i);
                    player.closeInventory();
                }
            }
        }
    }


}


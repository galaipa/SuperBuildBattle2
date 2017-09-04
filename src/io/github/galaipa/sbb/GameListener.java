package io.github.galaipa.sbb;

import static io.github.galaipa.sbb.ArenaManager.am;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static io.github.galaipa.sbb.ArenaManager.debug;
import static io.github.galaipa.sbb.ArenaManager.getManager;
import static io.github.galaipa.sbb.SuperBuildBattle.getTr;


public class GameListener implements Listener {
    public static Map<String, ArenaPlayer> Offline = new HashMap<>();
    SuperBuildBattle plugin = SuperBuildBattle.getInstance();

    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryClick(PlayerInteractEvent event) {
        Arena a;
        Player p = event.getPlayer();
        if ((a = getManager().getArena(p)) != null) {
            if (a.voting) {
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    event.setCancelled(true);
                    if (a.currentVotedPlayer.getPlayer() == p) {
                        p.sendMessage(ChatColor.RED + getTr("26"));
                    } else if (p.getItemInHand().getType() == Material.STAINED_CLAY) {
                        String izena = p.getItemInHand().getItemMeta().getDisplayName();
                        if (izena.equalsIgnoreCase(ChatColor.RED + getTr("35"))) {
                            vote(p, 0, a);
                            p.sendMessage(ChatColor.GREEN + getTr("40") + ": " + izena);
                        } else if (izena.equalsIgnoreCase(ChatColor.RED + getTr("33"))) {
                            vote(p, 1, a);
                            p.sendMessage(ChatColor.GREEN + getTr("40") + ": " + izena);
                        } else if (izena.equalsIgnoreCase(ChatColor.RED + getTr("32"))) {
                            vote(p, 2, a);
                            p.sendMessage(ChatColor.GREEN + getTr("40") + ": " + izena);
                        } else if (izena.equalsIgnoreCase(ChatColor.GREEN + getTr("31"))) {
                            vote(p, 3, a);
                            p.sendMessage(ChatColor.GREEN + getTr("40") + ": " + izena);
                        } else if (izena.equalsIgnoreCase(ChatColor.GREEN + getTr("30"))) {
                            vote(p, 4, a);
                            p.sendMessage(ChatColor.GREEN + getTr("40") + ": " + izena);
                        } else if (izena.equalsIgnoreCase(ChatColor.GREEN + getTr("36"))) {
                            vote(p, 5, a);
                            p.sendMessage(ChatColor.GREEN + getTr("40") + ": " + izena);
                        }
                    }
                }
            } else {
                if (event.getClickedBlock() != null) {
                    if (event.getClickedBlock().getType() == Material.ENDER_CHEST) {
                        event.setCancelled(true);
                    }
                }
            }
        }

    }

    public void vote(Player p, int i, Arena a) {
        if (a.botoa.containsKey(p)) {
            a.botoa.remove(p);
            a.botoa.put(p, i);
        } else {
            a.botoa.put(p, i);
        }
    }

    @EventHandler
    public void PlayerCommand(PlayerCommandPreprocessEvent event) {
            Player p = event.getPlayer();
        if (getManager().getArena(p) != null) {
            ArrayList<String> list = (ArrayList<String>) plugin.getConfig().getStringList("CmdWhitelist");
            for (String s : list) {
                if (event.getMessage().toLowerCase().startsWith("/" + s)) {
                    return;
                }
            }
            event.setCancelled(true);
            p.sendMessage(ChatColor.GREEN + "[BuildBattle]" + ChatColor.RED + "You can't do that!");
        }
    }

@EventHandler
    public void CuboidProtection(BlockBreakEvent event) {
        if (getManager().getArena(event.getPlayer()) != null) {
            Arena a = getManager().getArena(event.getPlayer());
            if (a.inGame) {
              
                if (!a.getArenaPlayer(event.getPlayer()).getCuboid().contains(event.getBlock()) || a.voting) {
                    event.setCancelled(true);
                }
            }
        }else{
            for(Arena a : am.arenas){
                for(Cuboid c : a.cuboid){
                   if(c.contains(event.getBlock())){
                       event.setCancelled(true);
                   }
                }
            }
        }
    }
@EventHandler
    public void CuboidProtection2(BlockPlaceEvent event) {
        if (getManager().getArena(event.getPlayer()) != null) {
            Arena a = getManager().getArena(event.getPlayer());
            if (a.inGame) {
                Cuboid c2 = a.getArenaPlayer(event.getPlayer()).getCuboid().shift(Cuboid.CuboidDirection.Down, 2);
                if (!c2.contains(event.getBlock()) || a.voting) {
                    event.setCancelled(true);
                }
            }
        }else{
            for(Arena a : am.arenas){
                for(Cuboid c : a.cuboid){
                   Cuboid c2 = c.shift(Cuboid.CuboidDirection.Down, 2);
                   if(c2.contains(event.getBlock())){
                       event.setCancelled(true);
                   }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Arena a;
        if ((a = getManager().getArena(event.getPlayer())) != null) {
            if (a.inGame) {
                Location l = event.getBlockClicked().getLocation();
                l.setY(l.getY() + 1);
                if (!a.getArenaPlayer(event.getPlayer()).getCuboid().contains(l)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void creativeBlackList(InventoryCreativeEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (getManager().getArena(player) != null) {
            Arena a = getManager().getArena(player);
            if (a.inGame) {
                ArrayList<String> list = (ArrayList<String>) plugin.getConfig().getStringList("ItemBlackList");
                Material m = e.getCursor().getType();
                if (list.contains(m.toString())) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.GREEN + "[Build Battle] " + ChatColor.RED + "Blacklisted item");
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        Arena a;
        if ((a = getManager().getArena(p)) != null) {
            ArenaPlayer j = a.getArenaPlayer(p);
            ArrayList<String> list = (ArrayList<String>) plugin.getConfig().getStringList("OfflinePlayers");
            list.add(p.getName());
            plugin.getConfig().set("OfflinePlayers", list);
            plugin.saveConfig();
            if (a.inGame) {
                j.resetArenas();
               /* if (ArenaManager.WorldGuarda) {
                    WorldGuardOptional.WGregionRM(a.getArenaPlayer(e.getPlayer()).getID(), a.getID());
                }*/
            }else{
                a.reassignID();
            }
            Offline.put(p.getName(), j);
            a.players.remove(j);
            //Taldea ezabatu
            // ArenaManager.getManager().removePlayer(e.getPlayer(), true);
            if (a.players.isEmpty()) {
                a.reset();
            }
        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        ArrayList<String> list = (ArrayList<String>) plugin.getConfig().getStringList("OfflinePlayers");
        if (list.contains(p.getName())) {
            list.remove(p.getName());
            plugin.getConfig().set("OfflinePlayers", list);
            plugin.saveConfig();
            debug("Player detected: " + p.getName());
            if (Offline.containsKey(p.getName())) {
                debug("Player data found");
                ArenaPlayer j = Offline.get(p.getName());
                p.teleport(j.getPreSpawn());
                p.setGameMode(GameMode.SURVIVAL);
                j.returnInv(p);
            } else {
                debug("Player data not found");
                p.setGameMode(GameMode.SURVIVAL);
                p.getInventory().clear();
                p.getInventory().setArmorContents(null);
                //  p.teleport(ArenaManager.getManager().lobby);
            }
            // e.getPlayer().setGameMode(GameMode.SURVIVAL);
            // e.getPlayer().teleport(ArenaManager.getManager().lobby);
            // plugin.returnInventory(e.getPlayer());
        }

    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Arena a = null;
        if (getManager().getArena(e.getPlayer()) != null) {
            a = getManager().getArena(e.getPlayer());
            if (a.inGame && !a.voting) {
                if (!a.getArenaPlayer(e.getPlayer()).getCuboid().contains(e.getPlayer().getLocation())) {
                    e.getPlayer().teleport(a.getArenaPlayer(e.getPlayer()).getSpawnPoint());
                }
            } else if (a.inGame) {
                if (a.contains(e.getPlayer())) {
                    if (!a.currentVotedPlayer.getCuboid().contains(e.getPlayer().getLocation())) {
                        e.getPlayer().teleport(a.currentVotedPlayer.getSpawnPoint());
                    }
                }
            }
        }
    }
}


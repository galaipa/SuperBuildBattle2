
package io.github.galaipa.sbb;

import static io.github.galaipa.sbb.ArenaManager.getManager;
import static io.github.galaipa.sbb.SuperBuildBattle.getTr;
import java.util.ArrayList;
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
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class GameListener implements Listener {
    SuperBuildBattle plugin = SuperBuildBattle.getInstance();
          @EventHandler(priority = EventPriority.LOW)
          public void onInventoryClick(PlayerInteractEvent event){
              if(getManager().getArena(event.getPlayer()) != null){
                  Arena a = getManager().getArena(event.getPlayer());
              if(a.voting == true){
                 if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK ){
                        Player p = event.getPlayer();
                        event.setCancelled(true);
                       /* for(Player b : a.botoa){
                            if(b == p){
                                p.sendMessage(ChatColor.RED + getTr("25"));
                                return;
                            }
                        }*/
                        if(a.jabea.getPlayer() == p){
                            p.sendMessage(ChatColor.RED + getTr("26"));
                        }
                        else if(p.getItemInHand().getType() == Material.STAINED_CLAY){
                              String izena = p.getItemInHand().getItemMeta().getDisplayName();
                                if (izena.equalsIgnoreCase(ChatColor.RED + getTr("35"))){
                                    gehituBotoa(p,0,a);
                                    p.sendMessage(ChatColor.GREEN +  getTr("39") + ": " + izena );
                                    }
                                else if (izena.equalsIgnoreCase(ChatColor.RED + getTr("33"))){
                                    gehituBotoa(p,1,a);
                                    p.sendMessage(ChatColor.GREEN +  getTr("39") + ": " + izena );
                                    }
                                else if (izena.equalsIgnoreCase(ChatColor.RED + getTr("32"))){
                                    gehituBotoa(p,2,a);
                                    p.sendMessage(ChatColor.GREEN +  getTr("39") + ": " + izena );
                                    }
                                else if (izena.equalsIgnoreCase(ChatColor.GREEN + getTr("31"))){
                                    gehituBotoa(p,3,a);
                                    p.sendMessage(ChatColor.GREEN +  getTr("39") + ": " + izena );
                                    }
                                else if (izena.equalsIgnoreCase(ChatColor.GREEN + getTr("30"))){
                                    gehituBotoa(p,4,a);
                                    p.sendMessage(ChatColor.GREEN +  getTr("39") + ": " + izena );
                                    }
                                else if (izena.equalsIgnoreCase(ChatColor.GREEN + getTr("36"))){
                                    gehituBotoa(p,5,a);
                                    p.sendMessage(ChatColor.GREEN +  getTr("30") + ": " + izena );
                                    }
                        }
              }
          }} 
              else{
                  
              }
         
          }
          public void gehituBotoa(Player p, int i,Arena a){
              if(a.botoa.containsKey(p)){
                  a.botoa.remove(p);
                  a.botoa.put(p, i);
              }else{
                  a.botoa.put(p, i);
              }
          }
              @EventHandler
              public void PlayerCommand(PlayerCommandPreprocessEvent event) {
              if(getManager().getArena(event.getPlayer()) != null){
                  Arena a = getManager().getArena(event.getPlayer());
                  if(a.inGame == true){
                      Player p = event.getPlayer();
                              ArrayList<String> list = (ArrayList<String>) plugin.getConfig().getStringList("CmdWhitelist");
                              for(String s : list){
                                  if(event.getMessage().toLowerCase().startsWith("/" +s)){
                                   return;   
                                  }
                              }
                               event.setCancelled(true);
                               p.sendMessage(ChatColor.GREEN +"[BuildBattle]" + ChatColor.RED + "You can't use command during the game");
                              }
                          
                      }
                  }
              @EventHandler
              public void CuboidProtection(BlockBreakEvent event) {
              if(getManager().getArena(event.getPlayer()) != null){
                  Arena a = getManager().getArena(event.getPlayer());
                  if (a.inGame){
                          if(!a.getJolakaria(event.getPlayer()).getCuboid().contains(event.getBlock()) || a.voting){
                              event.setCancelled(true);
                          }
                      }
                  }
              }
              @EventHandler
              public void CuboidProtection2(BlockPlaceEvent event) {
              if(getManager().getArena(event.getPlayer()) != null){
                  Arena a = getManager().getArena(event.getPlayer());
                  if (a.inGame){
                          if(!a.getJolakaria(event.getPlayer()).getCuboid().contains(event.getBlock())){
                              event.setCancelled(true);
                          }
                      }
                  }
              }

            @EventHandler
            public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
                if(getManager().getArena(event.getPlayer()) != null){
                  Arena a = getManager().getArena(event.getPlayer());
                  if (a.inGame){
                          Location l = event.getBlockClicked().getLocation();
                          l.setY(l.getY()+1);
                          if(!a.getJolakaria(event.getPlayer()).getCuboid().contains(l)){
                              event.setCancelled(true);
                          }
                      }
                  }
            }
       @EventHandler
       public void creativeBlackList(InventoryCreativeEvent e){
           Player player = (Player) e.getWhoClicked();
        if(getManager().getArena(player) != null){
                  Arena a = getManager().getArena(player);
            if(a.inGame){
                ArrayList<String> list = (ArrayList<String>) plugin.getConfig().getStringList("ItemBlackList");
                Material m = e.getCursor().getType();
                if(list.contains(m.toString())){
                    player.closeInventory();
                    player.sendMessage(ChatColor.GREEN +"[Build Battle] " + ChatColor.RED + "Blacklisted item");
                    e.setCancelled(true);
                }
            }    
            }
          }
      @EventHandler
      public void onLeave(PlayerQuitEvent e){
            if(getManager().getArena(e.getPlayer()) != null){
                  Arena a = getManager().getArena(e.getPlayer());
                    ArrayList<String> list = (ArrayList<String>) plugin.getConfig().getStringList("OfflinePlayers");
                    list.add(e.getPlayer().getName());
                    plugin.getConfig().set("OfflinePlayers",list);
                    plugin.saveConfig();
                    if(a.inGame){
                        a.getJolakaria(e.getPlayer()).resetArenas();
                        if(ArenaManager.WorldGuarda == true){ WorldGuardOptional.WGregionRM(a.getJolakaria(e.getPlayer()).getID(),a.getID());}
                    }
                    //Taldea ezabatu
                    ArenaManager.getManager().removePlayer(e.getPlayer());
                    if(a.players.isEmpty()){
                        a.reset();
                    }
                }
        
      }
      @EventHandler
      public void onJoin(PlayerJoinEvent e){
             ArrayList<String> list = (ArrayList<String>) plugin.getConfig().getStringList("OfflinePlayers");
             if(list.contains(e.getPlayer().getName())){
                 list.remove(e.getPlayer().getName());
                 plugin.getConfig().set("OfflinePlayers",list);
                 plugin.saveConfig();
                 e.getPlayer().setGameMode(GameMode.SURVIVAL);
                 e.getPlayer().teleport(ArenaManager.getManager().lobby);
                // plugin.returnInventory(e.getPlayer());
             }

      }
      @EventHandler
     public void onMove(PlayerMoveEvent e) {
         Arena a = null;
        if(getManager().getArena(e.getPlayer()) != null){
            a = getManager().getArena(e.getPlayer());
            if(a.inGame && !a.voting){
                    if(a.getJolakaria(e.getPlayer()).getCuboid().contains(e.getPlayer().getLocation())){
                    }else{
                    e.getPlayer().teleport(a.getJolakaria(e.getPlayer()).getSpawnPoint());
                }
            }else if(a.inGame && a.voting){
                if(a.players.contains(e.getPlayer())){
                    if(a.jabea.getCuboid().contains(e.getPlayer().getLocation())){
                    }else{
                    e.getPlayer().teleport(a.jabea.getSpawnPoint());
                }
            }
        }
     }
     }
}


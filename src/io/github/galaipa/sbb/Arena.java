package io.github.galaipa.sbb;

import me.hfox.spigboard.Spigboard;
import me.hfox.spigboard.SpigboardEntry;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static io.github.galaipa.sbb.SuperBuildBattle.getTr;


public class Arena {
    public int id;
    public int maxPlayers, minPlayers, time, votingtime;
    List<Jokalaria> players = new ArrayList<>();
    HashMap<Player, Integer> botoa = new HashMap<>();
    Location lobby;
    Cuboid[] cuboid;
    Boolean inGame = false, voting = false;
    String theme;
    Spigboard SpigBoard;
    String timer = null;
    Jokalaria jabea;
    SuperBuildBattle plugin = SuperBuildBattle.getInstance();

    public Arena(int id, int minPlayers, int maxPlayers, int time, int votingTime, Location lobby, Cuboid[] cuboid) {
        this.id = id;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.time = time;
        this.votingtime = votingTime;
        this.lobby = lobby;
        this.cuboid = cuboid;
    }

    public int getID() {
        return this.id;
    }

    public List<Jokalaria> getPlayers() {
        return this.players;
    }

    public Jokalaria getJolakaria(Player p) {
        for (Jokalaria j : players) {
            if (j.getPlayer() == p) {
                return j;
            }
        }
        return null;
    }

    public void assignArenas() {
        for (Jokalaria j2 : players) {
            j2.addRegion(cuboid[j2.getID() - 1]);
        }
    }

    public void Broadcast(String msg) {
        for (Jokalaria j : players) {
            j.getPlayer().sendMessage(msg);
        }
    }

    public void sendTitleAll(Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
        for (Jokalaria j : players) {
            ArenaManager.sendTitle(j.getPlayer(), fadeIn, stay, fadeOut, title, subtitle);
        }
    }

    public void start() {
        assignArenas();
        //  Broadcast("AssignArenas OK");
        theme = ArenaManager.getManager().getRandomTheme();
        Broadcast(ChatColor.GREEN + getTr("13"));
        new BukkitRunnable() {
            int countdown = 10;

            @Override
            public void run() {
                for (Jokalaria j : getPlayers()) {
                    Player p = j.getPlayer();
                    p.setLevel(countdown);
                    //   p.sendMessage(ChatColor.GREEN + " " + countdown);
                    p.getWorld().playSound(p.getLocation(), Sound.NOTE_STICKS, 10, 1);
                    ArenaManager.sendTitle(p, 20, 40, 20, Integer.toString(countdown), "");
                }
                countdown--;
                if (countdown < 0) {
                    Broadcast(ChatColor.GREEN + "-----------------------------------------------");
                    Broadcast(ChatColor.BOLD.toString());
                    Broadcast(ChatColor.WHITE + "                         �lSuper Build Battle");
                    Broadcast(ChatColor.GREEN + "       " + getTr("15") + " " + time + " " + getTr("16"));
                    Broadcast(ChatColor.GREEN + "         " + getTr("17") + ": " + ChatColor.YELLOW + theme);
                    Broadcast(ChatColor.BOLD.toString());
                    Broadcast(ChatColor.GREEN + "-----------------------------------------------");
                    sendTitleAll(20, 40, 20, ChatColor.GREEN + theme, getTr("14"));
                    for (Jokalaria j : getPlayers()) {
                        Player p = j.getPlayer();
                        p.teleport(j.getSpawnPoint());
                        p.setGameMode(GameMode.CREATIVE);
                        p.getWorld().playSound(p.getLocation(), Sound.NOTE_PLING, 10, 1);
                        InGameGui.giveUserGui(p);
                        InGameGui.userGui();
                        if (plugin.getConfig().getBoolean("StartCommand.Enabled")) {
                            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), (plugin.getConfig().getString("StartCommand.Command")).replace("$player$", p.getName()));
                        }
                    }
                    inGame = true;
                    cancel();
                    Building();
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public void Building() {
        SpigBoard = new Spigboard(ChatColor.DARK_GREEN.BOLD + "BuildBattle");
        SpigBoard.add("theme", ChatColor.GREEN + getTr("17") + ": " + ChatColor.YELLOW + theme, 4);
        for (Jokalaria j : getPlayers()) {
            SpigBoard.add(j.getPlayer());
        }
        new BukkitRunnable() {
            int seconds = 0;
            int minutes = time;

            @Override
            public void run() {
                if (minutes == 10 && seconds == 2 || minutes == 5 && seconds == 2 || minutes == 4 && seconds == 2 || minutes == 3 && seconds == 2 || minutes == 2 && seconds == 2 || minutes == 1 && seconds == 2) {
                    sendTitleAll(20, 40, 20, Integer.toString(minutes), getTr("29"));
                }
                if (minutes == 0 && seconds == 0) {
                    cancel();
                    voting = true;
                    voting();
                } else if (seconds == 0) {
                    seconds = 60;
                    minutes = minutes - 1;
                } else {
                    seconds = seconds - 1;
                }

                if (seconds < 10 && seconds >= 0) {
                    seconds = 0 + seconds;
                    String timer2 = ChatColor.GREEN + getTr("18") + ": " + ChatColor.YELLOW + minutes + ":" + "0" + seconds;
                    SpigboardEntry score = SpigBoard.getEntry("timer");
                    if (score != null) {
                        score.update(timer2);
                    } else {
                        SpigBoard.add("timer", timer2, 2);
                    }
                } else {
                    String timer2 = ChatColor.GREEN + getTr("18") + ": " + ChatColor.YELLOW + minutes + ":" + seconds;
                    SpigboardEntry score = SpigBoard.getEntry("timer");
                    if (score != null) {
                        score.update(timer2);
                    } else {
                        SpigBoard.add("timer", timer2, 2);
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public void voting() {
        SpigBoard.remove(SpigBoard.getEntry("timer"));
        for (Jokalaria j : getPlayers()) {
            Player p = j.getPlayer();
            Inventory inv = p.getInventory();
            inv.clear();
            inv.addItem(AdminGui.item(Material.STAINED_GLASS_PANE, 14, 1, ChatColor.RED + getTr("37")));
            inv.addItem(AdminGui.item(Material.STAINED_CLAY, 14, 1, ChatColor.RED + getTr("35")));
            inv.addItem(AdminGui.item(Material.STAINED_CLAY, 1, 1, ChatColor.RED + getTr("33")));
            inv.addItem(AdminGui.item(Material.STAINED_CLAY, 6, 1, ChatColor.RED + getTr("32")));
            inv.addItem(AdminGui.item(Material.STAINED_GLASS_PANE, 0, 1, getTr("37")));
            inv.addItem(AdminGui.item(Material.STAINED_CLAY, 4, 1, ChatColor.GREEN + getTr("31")));
            inv.addItem(AdminGui.item(Material.STAINED_CLAY, 5, 1, ChatColor.GREEN + getTr("30")));
            inv.addItem(AdminGui.item(Material.STAINED_CLAY, 13, 1, ChatColor.GREEN + getTr("36")));
            inv.addItem(AdminGui.item(Material.STAINED_GLASS_PANE, 13, 1, ChatColor.GREEN + getTr("37")));
            p.updateInventory();
        }
        new BukkitRunnable() {
            int zenbat = 0;
            String timer = "";
            String reset = "";
            String taldekideak = "";

            @Override
            public void run() {
                for (Jokalaria j2 : players) {
                    if (botoa.containsKey(j2.getPlayer())) {
                        if (getPlayer(zenbat) != null) {
                            getPlayer(zenbat).addPoint(botoa.get(j2.getPlayer()));
                        }
                    }
                }
                botoa.clear();

                if (zenbat >= players.size()) {
                    cancel();
                    winner();
                } else {
                    jabea = getPlayer(zenbat + 1);

                    for (Jokalaria j : getPlayers()) {
                        Player p = j.getPlayer();
                        p.teleport(jabea.getSpawnPoint());
                        sendTitleAll(20, 40, 20, jabea.getPlayerString(), "");
                        p.getWorld().playSound(p.getLocation(), Sound.NOTE_PLING, 10, 1);
                    }
                    String taldeakideak = ChatColor.GREEN + getTr("19") + ": " + ChatColor.YELLOW + jabea.getPlayerString();
                    SpigboardEntry score = SpigBoard.getEntry("taldeakideak2");
                    if (score != null) {
                        score.update(taldeakideak);
                    } else {
                        SpigBoard.add("taldeakideak2", taldeakideak, 2);
                    }
                    if (zenbat == 0) {
                        Broadcast(ChatColor.GREEN + "-----------------------------------------------");
                        Broadcast(ChatColor.BOLD.toString());
                        Broadcast(ChatColor.WHITE + "                         �lVoting");
                        Broadcast(ChatColor.GREEN + "        " + getTr("21"));
                        Broadcast(ChatColor.BOLD.toString());
                        Broadcast(ChatColor.GREEN + "-----------------------------------------------");
                        Broadcast(ChatColor.YELLOW + getTr("19") + ": " + jabea.getPlayerString());

                    } else {
                        Broadcast(ChatColor.YELLOW + getTr("19") + ": " + jabea.getPlayerString());
                    }
                    zenbat++;
                }

            }
        }.runTaskTimer(plugin, 0, 20 * votingtime);
    }

    public void winner() {
        Jokalaria taldeIrabazlea = null;
        Jokalaria talde2 = null;
        Jokalaria talde3 = null;
        List<Winners> users = new ArrayList<>();
        for (Jokalaria t : players) {
            users.add(new Winners(t, t.getPoint()));
        }
        Collections.sort(users);
        for (Winners n : users) {
            if (taldeIrabazlea == null) {
                taldeIrabazlea = n.getName();
            } else if (talde2 == null) {
                talde2 = n.getName();
            } else if (talde3 == null) {
                talde3 = n.getName();
            }
        }
        final Jokalaria t = taldeIrabazlea;
        Broadcast(ChatColor.GREEN + "------------------------------------------------");
        Broadcast(ChatColor.BOLD.toString());
        Broadcast(ChatColor.WHITE + "                         �lSuper Build Battle");
        Broadcast(ChatColor.GREEN + "       " + getTr("20"));
        Broadcast(ChatColor.YELLOW + "1�: " + ChatColor.GREEN + taldeIrabazlea.getPlayerString() + "(" + taldeIrabazlea.getPoint() + " " + getTr("24") + ")");
        if (players.size() > 1) {
            Broadcast(ChatColor.YELLOW + "2�: " + ChatColor.GREEN + talde2.getPlayerString() + "(" + talde2.getPoint() + " " + getTr("24") + ")");
        }
        if (players.size() > 2) {
            Broadcast(ChatColor.YELLOW + "3�: " + ChatColor.GREEN + talde3.getPlayerString() + "(" + talde3.getPoint() + " " + getTr("24") + ")");
        }
        Broadcast(ChatColor.BOLD.toString());
        Broadcast(ChatColor.GREEN + "------------------------------------------------");
        ArenaManager.getManager().Rewards(taldeIrabazlea, "Winner");
        if (players.size() > 1) {
            ArenaManager.getManager().Rewards(talde2, "Second");
        }
        if (players.size() > 2) {
            ArenaManager.getManager().Rewards(talde3, "Third");
        }
        for (Jokalaria j : players) {
            Player p = j.getPlayer();
            p.teleport(taldeIrabazlea.getSpawnPoint());
            if (p != taldeIrabazlea.getPlayer() && p != talde2.getPlayer() && p != talde3.getPlayer()) {
                ArenaManager.getManager().Rewards(p, "Rest");
            }
        }
        new BukkitRunnable() {
            int zenbat = 0;

            @Override
            public void run() {
                Firework f = (Firework) t.getWorld().spawn(t.getCuboid().getCenter(), Firework.class);
                FireworkMeta fm = f.getFireworkMeta();
                fm.addEffect(FireworkEffect.builder().flicker(false).trail(true).with(FireworkEffect.Type.STAR).withColor(Color.GREEN).withFade(Color.BLUE).build());
                fm.setPower(3);
                f.setFireworkMeta(fm);
                zenbat++;
                if (zenbat == 10) {
                    cancel();
                    reset();
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public void reset() {
        Iterator<Jokalaria> it = players.iterator();
        while (it.hasNext()) {
            Jokalaria j = it.next();
            ArenaManager.getManager().removePlayer(j.getPlayer(), false);
        }
        players.clear();
        SpigBoard = null;
        inGame = false;
        time = 0;
        voting = false;
        jabea = null;
        players.clear();
        botoa.clear();

    }

    public void minimunReached() {
        new BukkitRunnable() {
            int a = 0;

            @Override
            public void run() {
                if (maxPlayers == minPlayers) {
                    cancel();
                    start();
                } else if (a == 10) {
                    cancel();
                    start();
                } else {
                    a++;
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public Jokalaria getPlayer(int id) {
        for (Jokalaria j : players) {
            if (j.getID() == id) {
                return j;
            }
        }
        return null;
    }

}

package io.github.galaipa.sbb;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.io.Reader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CharSequenceReader;

import static io.github.galaipa.sbb.ArenaManager.debug;



public class SuperBuildBattle extends JavaPlugin {
    //LANGUAGES:
    public static String translation;
    public static YamlConfiguration yaml;
    public Economy econ = null;

    public static SuperBuildBattle getInstance() {
        return JavaPlugin.getPlugin(SuperBuildBattle.class);
    }

    public static String getTr(String path) {
        if (yaml.getString(path) == null) {
            return "Message missing in the lang file. Contact Admin (N." + path + ")";
        } else {
            return ChatColor.translateAlternateColorCodes('&', yaml.getString(path));
        }
    }

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }



    public WorldEditPlugin getWorldEdit(){
        return  (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
    }

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        getConfig().options().copyHeader(true);
        saveConfig();

        getServer().getPluginManager().registerEvents(new SignListener(this), this);
        getServer().getPluginManager().registerEvents(new GameListener(), this);
        getServer().getPluginManager().registerEvents(new AdminGui(this), this);
        getServer().getPluginManager().registerEvents(new InGameGui(), this);
        loadTranslations();
        if (Bukkit.getPluginManager().getPlugin("PlayerPoints") != null && getConfig().getBoolean("Rewards.PlayerPoints.Enabled")) {
            PlayerPointsOptional.hookPlayerPoints(Bukkit.getPluginManager().getPlugin("PlayerPoints"));
            ArenaManager.PlayerPoints = true;
        } else {
            ArenaManager.PlayerPoints = false;
        }
        ArenaManager.Sounds = getConfig().getBoolean("Sounds");
        if ((getConfig().getBoolean("Rewards.Vault.Enabled"))) {
            if (!setupEconomy()) {
                ArenaManager.Vault = false;
                System.out.print("[SBB] Economy rewards disabled, due there is not Vault installed or a economy plugin is missing");
                return;
            } else {
                ArenaManager.Vault = true;
                debug("Vault: true");
            }
        } else {
            ArenaManager.Vault = false;
            debug("Vault: false");
        }
        if (getServer().getPluginManager().getPlugin("PlayerPoints") != null && getConfig().getBoolean("Rewards.PlayerPoints.Enabled")) {
            PlayerPointsOptional.hookPlayerPoints(getServer().getPluginManager().getPlugin("PlayerPoints"));
            ArenaManager.PlayerPoints = true;
            debug("PlayerPoints: true");
        } else {
            ArenaManager.PlayerPoints = false;
            debug("PlayerPoints: false");
        }
        ArenaManager.timeBeforeStart = getConfig().getInt("TimeBeforeStart", ArenaManager.timeBeforeStart);
        ArenaManager.Command = getConfig().getBoolean("Rewards.Command.Enabled");
        ArenaManager.debug = getConfig().getBoolean("Debug");
        ArenaManager.WorldGuarda = getServer().getPluginManager().getPlugin("WorldGuard") != null;
        ArenaManager.getManager().loadArenas();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("[Super Build Battle]" + "Commands can only be run by players");
            return true;
        }
        Player p = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("buildbattle")) {
            if (!p.hasPermission("bb.user")) {
                sender.sendMessage(ChatColor.GREEN + "[Build Battle] " + ChatColor.GREEN + "Super Build Battle by Galapia, moloco, Carra and community");
            } else if (args.length < 1) {
                ArenaManager.sendTitle(p, 20, 40, 20, "&2Super Build Battle", "by Galapia, moloco, Carra and community");
                sender.sendMessage(ChatColor.GREEN + "[Build Battle] " + ChatColor.RED + "Super Build Battle by Galapia, moloco, Carra and community");
            } else if (args[0].equalsIgnoreCase("join")) {
                ArenaManager.getManager().addPlayer(p, p.getLocation());
            } else if (args[0].equalsIgnoreCase("leave")) {
                ArenaManager.getManager().removePlayer(p, true);
                return true;
            } else if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(ChatColor.GREEN + "[Build Battle] " + ChatColor.BLUE + "User commands:");
                sender.sendMessage(ChatColor.YELLOW + "/bb join");
                sender.sendMessage(ChatColor.YELLOW + "/bb leave");
                sender.sendMessage(ChatColor.GREEN + "[Build Battle] " + ChatColor.YELLOW + "Admin commands:");
                sender.sendMessage(ChatColor.YELLOW + "/bbadmin version");
                sender.sendMessage(ChatColor.YELLOW + "/bbadmin (To enter setup)");
                sender.sendMessage(ChatColor.YELLOW + "/bbadmin addtopic-removetopic-topiclist");
            } else {
                sender.sendMessage(ChatColor.GREEN + "[Build Battle] " + ChatColor.RED + "Unknown command");
            }

        } else if (cmd.getName().equalsIgnoreCase("buildbattleadmin")) {
            if (!p.hasPermission("bb.admin")) {
                sender.sendMessage(ChatColor.GREEN + "[Build Battle] " + ChatColor.RED + "You don't have permission");
                return true;
            } else if (args.length == 0) {
                AdminGui.newAdmin(p);
                p.sendMessage(ChatColor.YELLOW + "[Build Battle] " + ChatColor.GREEN + "Use the items in your hand to setup SuperBuildBattle\n\n");
            } else if (args[0].equalsIgnoreCase("start")) {
                if (args.length < 2) {
                    p.sendMessage(ChatColor.YELLOW + "[Build Battle] " + ChatColor.RED + "You must specify the arena number");
                } else {
                    p.sendMessage(ChatColor.YELLOW + "[Build Battle] " + ChatColor.GREEN + "You forced the game to start");
                    ArenaManager.getManager().getArena(Integer.parseInt(args[1])).forceStart();
                }
            } else if (args[0].equalsIgnoreCase("stop")) {
                if (args.length < 2) {
                    p.sendMessage(ChatColor.YELLOW + "[Build Battle] " + ChatColor.RED + "You must specify the arena number");
                } else {
                    p.sendMessage(ChatColor.YELLOW + "[Build Battle] " + ChatColor.GREEN + "Use the /bbadmin gui to stop the game");
                    //reset();
                }
            } else if (args[0].equalsIgnoreCase("version")) {
                PluginDescriptionFile pdfFile = this.getDescription();
                String version1 = pdfFile.getVersion();
                p.sendMessage(ChatColor.YELLOW + "[Build Battle] " + ChatColor.GREEN + "Version: " + version1);
                return true;
            } else if (args[0].equalsIgnoreCase("addtopic")) {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.GREEN + "/bbadmin addtopic [topic]");
                    return true;
                }
                String gaia = args[1];
                List<String> themes = getConfig().getStringList("Themes");
                themes.add(gaia);
                getConfig().set("Themes", themes);
                saveConfig();
                sender.sendMessage(ChatColor.GREEN + "Added a new topic: " + gaia);
                return true;
            } else if (args[0].equalsIgnoreCase("removetopic")) {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.GREEN + "/bbadmin removetopic [topic] ");
                    return true;
                }
                List<String> themes = getConfig().getStringList("Themes");
                String gaia = args[1];
                themes.remove(gaia);
                getConfig().set("Gaiak", themes);
                saveConfig();
                sender.sendMessage(ChatColor.GREEN + "Removed topic: " + gaia);
                return true;
            } else if (args[0].equalsIgnoreCase("topiclist")) {
                List<String> themes = getConfig().getStringList("Themes");
                sender.sendMessage(ChatColor.GREEN + "Topics: ");
                String s = "";
                for (String g : themes) {
                    s = s + g + ", ";
                }
                sender.sendMessage(s);
                return true;
            } else if (args[0].equalsIgnoreCase("topic")) {
                sender.sendMessage(ChatColor.GREEN + "/bbadmin addtopic-removetopic-topiclist");
                return true;
            } else if (args[0].equalsIgnoreCase("cmdwhitelist")) {
                ArrayList<String> list = (ArrayList<String>) getConfig().getStringList("CmdWhitelist");
                if (args[1].equalsIgnoreCase("add") && args.length >= 2) {
                    list.add(args[2]);
                    sender.sendMessage(ChatColor.GREEN + "Command added to whitelist");
                } else if (args[1].equalsIgnoreCase("remove") && args.length >= 2) {
                    list.remove(args[2]);
                    sender.sendMessage(ChatColor.GREEN + "Command removed from whitelist");
                } else if (args[1].equalsIgnoreCase("list")) {
                    String a = "CmdWhitelist: ";
                    for (String s : list) {
                        a = a + ", " + s;
                    }
                    p.sendMessage(a);
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid command");
                    return true;
                }
                getConfig().set("CmdWhitelist", list);
                saveConfig();
                return true;
            } else if (args[0].equalsIgnoreCase("itemblacklist")) {
                ArrayList<String> list = (ArrayList<String>) getConfig().getStringList("ItemBlackList");
                if (args[1].equalsIgnoreCase("add")) {
                    list.add(p.getItemInHand().getType().toString());
                    sender.sendMessage(ChatColor.GREEN + "Item inhand added to blacklist");
                } else if (args[1].equalsIgnoreCase("remove")) {
                    list.remove(p.getItemInHand().getType().toString());
                    sender.sendMessage(ChatColor.GREEN + "Item inhand removed from blacklist");
                } else if (args[1].equalsIgnoreCase("list")) {
                    String a = "ItemBlackList: ";
                    for (String s : list) {
                        a = a + ", " + s;
                    }
                    p.sendMessage(a);
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid command");
                    return true;
                }
                getConfig().set("ItemBlackList", list);
                saveConfig();
                return true;
            } else if(args[0].equalsIgnoreCase("saveGlobalLobby")){
                ArenaManager.getManager().saveGlobalLobby(((Player) sender).getLocation());
                sender.sendMessage("Fatto");
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid command");
                return true;
            }
        }
        return true;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private void loadTranslations() {
        copyTranslation("custom");
        translation = getConfig().getString("Language");
        File languageDir = new File(getDataFolder(), "lang");
        File languageFile;
        if (languageDir.exists() && (languageFile = new File(languageDir, translation + ".yml")).exists()) {
            yaml = YamlConfiguration.loadConfiguration(languageFile);
        } else {
            InputStream defaultStream = getResource(translation + ".yml");
            if (defaultStream != null) {
                Reader r;
		try {
                    r = this.getReaderFromStream(defaultStream);
                    yaml =  YamlConfiguration.loadConfiguration(r);
	            r.close();
		} catch (IOException e) {
                    e.printStackTrace();
				}
            } else {
                System.out.println("[SuperBuildBattle] " + translation + ".yml" + " missing! Add it in " + languageDir.getPath());
                Bukkit.getPluginManager().disablePlugin(this);
            }
        }
    }

    private void copyTranslation(String trans) {
        File file = new File(getDataFolder().getAbsolutePath() + File.separator + "lang" + File.separator + trans + ".yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            copy(getResource(trans + ".yml"), file);
        }
    }

    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     public Reader getReaderFromStream(InputStream initialStream) 
              throws IOException {

                byte[] buffer = IOUtils.toByteArray(initialStream);
                Reader targetReader = new CharSequenceReader(new String(buffer));
                return targetReader;
            }

}

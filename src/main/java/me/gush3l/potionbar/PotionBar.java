package me.gush3l.potionbar;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class PotionBar extends JavaPlugin {

    private Economy econ;

    private static PotionBar instance;

    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerItemConsume(), this);
        this.saveDefaultConfig();
        instance = this;
        Config.setup();
        Config.get().options().copyDefaults(true);
        Config.save();
        if (!setupEconomy()) {
            this.getLogger().severe("[PotionBar] Plugin disabled due to no Vault found!");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public Economy getEconomy() {
        return econ;
    }

    public boolean onCommand(CommandSender sender , Command cmd, String label, String[] args) {
        FileConfiguration config = this.getConfig();
        if (sender instanceof Player && !sender.hasPermission("potionbar.admin")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',config.getString("noperm")));
            return true;
        }
        String itemname;
        String chance;
        String price;
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")){
            this.reloadConfig();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',config.getString("reload")));
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("help")){
            for (String help : config.getStringList("help")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', help));
            }
            return true;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("debug")) {
            String arg = args[1];
            String path = "items." + arg;
            if (config.isSet(path)
                    && config.isSet(path + ".name")
                    && config.isSet(path + ".color")
                    && config.isSet(path + ".chance")
                    && config.isSet(path + ".price")
                    && config.isSet(path + ".lore")
                    && config.isSet(path + ".win")
                    && config.isSet(path + ".lose")) {
                sender.sendMessage("Potion is valid!");
                return true;
            }
            sender.sendMessage("The items marked with false are incorrectly defined in the config:");
            sender.sendMessage(config.isSet(path + ".name") + " - name");
            sender.sendMessage(config.isSet(path + ".color") + " - color");
            sender.sendMessage(config.isSet(path + ".chance") + " - chance");
            sender.sendMessage(config.isSet(path + ".price") + " - price");
            sender.sendMessage(config.isSet(path + ".lore") + " - lore");
            sender.sendMessage(config.isSet(path + ".win") + " - win");
            sender.sendMessage(config.isSet(path + ".lose") + " - lose");
            return true;

        }
        if (args.length == 3 && args[0].equalsIgnoreCase("give") && config.isSet("items." + args[1])) {
            Player target = Bukkit.getPlayerExact(args[2]);
            Inventory inventory = target.getInventory();
            ItemStack potion = new ItemStack(Material.POTION);
            PotionMeta meta = (PotionMeta) potion.getItemMeta();
            Color potioncolor = Color.WHITE;
            String arg = args[1];
            String path = "items." + arg;
            List<String> lore = config.getStringList(path + ".lore");
            if (inventory.firstEmpty() == -1) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("nospace")));
                return true;
            }
            itemname = ChatColor.translateAlternateColorCodes('&', config.getString(path+".name"));
                switch (config.getString(path+".color")) {
                    case "AQUA":
                        potioncolor = Color.AQUA;
                        break;
                    case "BLACK":
                        potioncolor = Color.BLACK;
                        break;
                    case "BLUE":
                        potioncolor = Color.BLUE;
                        break;
                    case "FUCHSIA":
                        potioncolor = Color.FUCHSIA;
                        break;
                    case "GRAY":
                        potioncolor = Color.GRAY;
                        break;
                    case "GREEN":
                        potioncolor = Color.GREEN;
                        break;
                    case "LIME":
                        potioncolor = Color.LIME;
                        break;
                    case "MAROON":
                        potioncolor = Color.MAROON;
                        break;
                    case "NAVY":
                        potioncolor = Color.NAVY;
                        break;
                    case "OLIVE":
                        potioncolor = Color.OLIVE;
                        break;
                    case "ORANGE":
                        potioncolor = Color.ORANGE;
                        break;
                    case "PURPLE":
                        potioncolor = Color.PURPLE;
                        break;
                    case "RED":
                        potioncolor = Color.RED;
                        break;
                    case "SILVER":
                        potioncolor = Color.SILVER;
                        break;
                    case "TEAL":
                        potioncolor = Color.TEAL;
                        break;
                    case "YELLOW":
                        potioncolor = Color.YELLOW;
                        break;
                    case "WHITE":
                        potioncolor = Color.WHITE;
                        break;
                }
                chance = config.getString(path+".chance");
                price = config.getString(path+".price");
            for (int i = 0; i < lore.size(); ++i) {
                lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)).replace("%Chance%", chance));
                lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)).replace("%Prize%", String.valueOf(Integer.parseInt(price) * 2)));
                lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)).replace("%Price%", price));
            }
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            meta.setColor(potioncolor);
            meta.setDisplayName(itemname);
            potion.setItemMeta(meta);
            NBTItem nbti = new NBTItem(potion);
            nbti.setInteger("ChanceNBT",Integer.valueOf(chance));
            nbti.setInteger("PriceNBT",Integer.valueOf(price));
            nbti.setString("PotionID",args[1]);
            inventory.addItem(nbti.getItem());
            return true;
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("notcmd")));
        return true;
    }

    public static PotionBar getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {

    }
}

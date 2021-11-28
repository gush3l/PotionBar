package me.gush3l.potionbar;

import de.tr7zw.nbtapi.NBTItem;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class PlayerItemConsume implements Listener {

    public static PotionBar plugin;

    private PotionBar pb = PotionBar.getInstance();

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        FileConfiguration config = PotionBar.getInstance().getConfig();
        Economy economy = PotionBar.getInstance().getEconomy();
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        NBTItem nbti = new NBTItem(item);
        int price;
        int chance;
        if (item.getType() == Material.POTION && nbti.hasKey("PriceNBT") && nbti.hasKey("ChanceNBT")) {
            price = nbti.getInteger("PriceNBT");
            chance = nbti.getInteger("ChanceNBT");
            String potionid = nbti.getString("PotionID");
            String path = "items." + potionid;
            List<String> wincommands = config.getStringList(path + ".commands.win");
            List<String> losecommands = config.getStringList(path + ".commands.lose");
            List<String> wineffects = config.getStringList(path + ".effects.win");
            List<String> loseeffects = config.getStringList(path + ".effects.lose");
            List<String> winmessages = config.getStringList(path + ".messages.win");
            List<String> losemessages = config.getStringList(path + ".messages.lose");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',config.getString("drankpot")
                    .replace("%Chance%", String.valueOf(chance))
                    .replace("%PotionName%",config.getString(path+".name"))
                    .replace("%Prize%", String.valueOf(price * 2))
                    .replace("%Price%", String.valueOf(price))));
            if (Math.random() <= Double.parseDouble(String.valueOf(chance))/100){
                economy.depositPlayer(player,price*2);
                for (String wincommand : wincommands) {
                    String command = wincommand.replace("%Player%", player.getName())
                            .replace("%Chance%", String.valueOf(chance))
                            .replace("%PotionName%", config.getString(path + ".name"))
                            .replace("%Prize%", String.valueOf(price * 2))
                            .replace("%Price%", String.valueOf(price));
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
                for (String wineffect : wineffects) {
                    int ticks = Integer.parseInt(wineffect.substring(wineffect.lastIndexOf(";") + 1))*20;
                    int power = Integer.parseInt(StringUtils.substringBetween(wineffect, ";", ";"));
                    PotionEffectType potioneffect = PotionEffectType.getByName(wineffect.split(";")[0]);
                    player.addPotionEffect(new PotionEffect(potioneffect, ticks, power));
                }
                for (String winmessage : winmessages) {
                    String message = winmessage.replace("%Player%", player.getName())
                            .replace("%Chance%", String.valueOf(chance))
                            .replace("%PotionName%", config.getString(path + ".name"))
                            .replace("%Prize%", String.valueOf(price * 2))
                            .replace("%Price%", String.valueOf(price));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',message));
                }
            }
            else{
                for (String losecommand : losecommands) {
                    String command = losecommand.replace("%Player%", player.getName())
                            .replace("%Chance%", String.valueOf(chance))
                            .replace("%PotionName%", config.getString(path + ".name"))
                            .replace("%Prize%", String.valueOf(price * 2))
                            .replace("%Price%", String.valueOf(price));
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
                for (String loseeffect : loseeffects) {
                    int ticks = Integer.parseInt(loseeffect.substring(loseeffect.lastIndexOf(";") + 1))*20;
                    int power = Integer.parseInt(StringUtils.substringBetween(loseeffect, ";", ";"));
                    PotionEffectType potioneffect = PotionEffectType.getByName(loseeffect.split(";")[0]);
                    player.addPotionEffect(new PotionEffect(potioneffect, ticks, power));
                }
                for (String losemessage : losemessages) {
                    String message = losemessage.replace("%Player%", player.getName())
                            .replace("%Chance%", String.valueOf(chance))
                            .replace("%PotionName%", config.getString(path + ".name"))
                            .replace("%Prize%", String.valueOf(price * 2))
                            .replace("%Price%", String.valueOf(price));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',message));
                }
            }

        }
    }

}

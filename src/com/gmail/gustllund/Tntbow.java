/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.gmail.gustllund;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

public class Tntbow extends JavaPlugin implements Listener {
	private final NamespacedKey key = new NamespacedKey(this, "recipe");
	private final Permission shootPermission = new Permission("tntbow.shoot");
    private ItemStack tntbowItem;
    private boolean ammoOn = this.getConfig().getBoolean("ammo", true);
    public void onEnable() {
    	getCommand("tntbow").setExecutor(this);
        this.getServer().getPluginManager().registerEvents(this, this);
        FileConfiguration config = this.getConfig();
        config.options().copyDefaults(true);
        this.saveConfig();
        tntbowItem = new ItemStack(Material.BOW, 1);
        tntbowItem.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10);
        ItemMeta meta = tntbowItem.getItemMeta();
        meta.setDisplayName("Tntbow");
        tntbowItem.setItemMeta(meta);
        ShapedRecipe recipe = new ShapedRecipe(key, tntbowItem);
        recipe.shape(new String[]{"TTT", "TBT", "TTT"});
        recipe.setIngredient('T', Material.TNT);
        recipe.setIngredient('B', Material.BOW);
        this.getServer().addRecipe(recipe);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (!(sender instanceof Player)) {
    		sender.sendMessage(ChatColor.RED + "This command can only be used as a player!");
    		return true;
    	}
    	Player player = (Player)sender;
    	int fail = player.getInventory().addItem(tntbowItem).size();
    	if (fail == 0) {
    		player.sendMessage(ChatColor.GREEN + "You have been given a tntbow!");
    	} else {
    		player.sendMessage(ChatColor.RED + "Your inventory is full!");
    	}
    	return true;
    }
    @EventHandler
    public void bow(EntityShootBowEvent e) {
        if (e.getEntityType() != EntityType.PLAYER) return;
        Player player = (Player)e.getEntity();
        if (e.getBow().getEnchantmentLevel(Enchantment.ARROW_INFINITE) != 10) return;
        if (!player.hasPermission(shootPermission)) {
        	player.sendMessage(ChatColor.RED + "You do not have permission to use a tntbow.");
        	return;
        }
        if (ammoOn && player.getGameMode() != GameMode.CREATIVE) {
            if (!player.getInventory().contains(Material.TNT)) {
            	player.sendMessage(ChatColor.RED + "You don't have enough ammo!");
            	return;
            }
            ItemStack tnt = new ItemStack(Material.TNT);
            if (!player.getInventory().contains(tnt)) {
            	for (ItemStack is : player.getInventory()) {
                	if (is.getType() != Material.TNT) continue;
                	tnt = is.clone();
                	tnt.setAmount(1);
                	break;
                }
            }
            if (player.getInventory().removeItem(tnt).size() > 0) {
            	getLogger().severe("Failed to remove TNT from inventory :(");
            	return;
            }
        }
        Entity en = player.getWorld().spawnEntity(e.getProjectile().getLocation(), EntityType.PRIMED_TNT);
        en.setVelocity(e.getProjectile().getVelocity());
        e.getProjectile().remove();
    }
}


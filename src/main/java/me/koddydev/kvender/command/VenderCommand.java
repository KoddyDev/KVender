package me.koddydev.kvender.command;

import me.koddydev.kvender.KVender;
import me.koddydev.kvender.util.Settings;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class VenderCommand implements CommandExecutor {

    private KVender plugin = (KVender) KVender.plugin;
    private Economy econ = KVender.econ;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("§cERRO → Você não é um player.");
            return true;
        }
        Player p = (Player) sender;

        if(args.length == 0) {
            if(!checkInventoryIsEmpty(p)) {
                sendActionbar(p, "§cVocê não possue items para vender.");
                return true;
            }
            int items = 0;
            AtomicReference<Double> moneyGanhar = new AtomicReference<>((double) 0);
            for(String key : plugin.getConfig().getConfigurationSection("Items").getKeys(false)) {
                for (ItemStack i : p.getInventory().getContents()) {
                    if (i == null) continue;
                    int Type = Integer.valueOf(plugin.getConfig().getString("Items."+key+".Item").split(":")[0]);
                    int id = Integer.valueOf(plugin.getConfig().getString("Items."+key+".Item").split(":")[1]);
                    Double Price = plugin.getConfig().getDouble("Items."+key+".PrecoDrop");
                    int amount = i.getAmount();

                    if(i.getType().getId() == Type && i.getData().getData() == id) {
                        items += amount;
                            moneyGanhar.set(moneyGanhar.get() + (Price * amount));
                            p.getInventory().remove(i);
                    }
                }
            }
            if(items == 0) {
                sendActionbar(p, "§cVocê não possue nenhum item para vender.");
                return true;
            }
            if(items > 1 && moneyGanhar.get() > 1) {
                sendActionbar(p, "§aVocê vendeu §f" + items + " §aitems e ganhou num total de §2$§a" + moneyGanhar.get());
                econ.depositPlayer(p, moneyGanhar.get());
            }
            return true;
        } else {
            String arg0 = args[0];
            if(arg0.equalsIgnoreCase("menu")) {
                Inventory inventory = Bukkit.createInventory(null, 27, plugin.getConfig().getString("Menu.Titulo").replace("&", "§"));

                ItemStack autovender = new ItemStack(Material.NAME_TAG);
                ItemMeta auto_meta = autovender.getItemMeta();

                auto_meta.setDisplayName(plugin.getConfig().getString("Menu.Items.AutoVender.Titulo").replace("&", "§"));
                ArrayList<String> lore_auto = new ArrayList<String>();

                plugin.getConfig().getStringList("Menu.Items.AutoVender.Descricao").forEach(f -> lore_auto.add(f.replace("&", "§").replace("%status%", (Settings.autoVenda.contains(p.getDisplayName()) ? "§aAtivado" : "§cDesativado"))));


                auto_meta.setLore(lore_auto);
                autovender.setItemMeta(auto_meta);

                inventory.setItem(11, autovender);

                ItemStack shiftvender = new ItemStack(Material.LEVER);
                ItemMeta shift_meta = autovender.getItemMeta();

                shift_meta.setDisplayName(plugin.getConfig().getString("Menu.Items.Shift.Titulo").replace("&", "§"));
                ArrayList<String> lore_shift = new ArrayList<String>();

                plugin.getConfig().getStringList("Menu.Items.Shift.Descricao").forEach(f -> lore_shift.add(f.replace("&", "§").replace("%status%", (Settings.vendaShift.contains(p.getDisplayName()) ? "§aAtivado" : "§cDesativado"))));

                shift_meta.setLore(lore_shift);
                shiftvender.setItemMeta(shift_meta);

                inventory.setItem(13, shiftvender);

                ItemStack vender = new ItemStack(Material.PAPER);
                ItemMeta vender_meta = autovender.getItemMeta();

                vender_meta.setDisplayName(plugin.getConfig().getString("Menu.Items.Vender.Titulo").replace("&", "§"));
                ArrayList<String> lore_vender = new ArrayList<String>();
                plugin.getConfig().getStringList("Menu.Items.Vender.Descricao").forEach(f -> lore_vender.add(f.replace("&", "§")));


                vender_meta.setLore(lore_vender);
                vender.setItemMeta(vender_meta);

                inventory.setItem(15, vender);

                p.openInventory(inventory);
            }
        }
        return false;
    }

    public static void sendActionbar(Player player, String message) {
        KVender.sendActionBar(player, message);
    }

    private boolean checkInventoryIsEmpty(Player player) {
        PlayerInventory inv = player.getInventory();
        for (ItemStack i : inv.getContents()) {
            if (i != null && !(i.getType() == Material.AIR)) {
                return true;
            }
        }
        return false;
    }
}

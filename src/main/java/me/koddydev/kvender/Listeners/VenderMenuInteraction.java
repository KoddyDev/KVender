package me.koddydev.kvender.Listeners;

import me.koddydev.kvender.KVender;
import me.koddydev.kvender.util.Settings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

import static me.koddydev.kvender.KVender.*;

public class VenderMenuInteraction implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equalsIgnoreCase("§aMenu de Vendas")) {
            e.setCancelled(true);
            ItemStack item = e.getCurrentItem();
            Player p = (Player) e.getWhoClicked();
            if (item != null && item.hasItemMeta()) {
                if (item.getItemMeta().getDisplayName().equalsIgnoreCase(plugin.getConfig().getString("Menu.Items.AutoVender.Titulo").replace("&", "§"))) {
                    if(!p.hasPermission("vendas.autovender")) {
                        p.sendMessage("§cERRO → Você não possue permissão para usar o auto vender!");
                    } else {

                        if (Settings.autoVenda.contains(p.getDisplayName())) {
                            KVender.sendActionBar(p, "§cVocê desativou o modo de venda automática!");
                            Settings.autoVenda.remove(p.getName());
                            p.closeInventory();
                            Bukkit.dispatchCommand(p, "vender menu");
                        } else {
                            KVender.sendActionBar(p, "§aVocê ativou o modo de venda automática!");
                            Settings.autoVenda.add(p.getName());
                            p.closeInventory();
                            Bukkit.dispatchCommand(p, "vender menu");
                            double delayDouble = plugin.getConfig().getDouble("Delays.AutoVender");
                            if (delayDouble < 0.5) {
                                delayDouble = 2.5;
                            }
                            long timeLong = (long) (1000 * delayDouble);

                            Timer timer = new Timer(true);
                            timer.scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    if (Settings.autoVenda.contains(p.getName())) {
                                        int items = 0;
                                        AtomicReference<Double> moneyGanhar = new AtomicReference<>((double) 0);
                                        for (String key : plugin.getConfig().getConfigurationSection("Items").getKeys(false)) {
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
                                        if (items > 1 && moneyGanhar.get() > 1) {
                                            sendActionBar(p, "§aVocê vendeu §f" + items + " §aitems e ganhou num total de §2$§a" + moneyGanhar.get());
                                            econ.depositPlayer(p, moneyGanhar.get());
                                        }
                                    } else {
                                        cancel();
                                    }
                                }
                            }, timeLong, timeLong);
                        }
                    }

                } else if (item.getItemMeta().getDisplayName().equalsIgnoreCase(plugin.getConfig().getString("Menu.Items.Shift.Titulo").replace("&", "§")))
                    if(p.hasPermission("vendas.shift")) {
                        if (Settings.vendaShift.contains(p.getDisplayName())) {
                            KVender.sendActionBar(p, "§cVocê desativou o modo de venda por shift!");
                            Settings.vendaShift.remove(p.getName());
                            p.closeInventory();
                            Bukkit.dispatchCommand(p, "vender menu");
                        } else {
                            KVender.sendActionBar(p, "§aVocê ativou o modo de venda por shift!");
                            Settings.vendaShift.add(p.getName());
                            p.closeInventory();
                            Bukkit.dispatchCommand(p, "vender menu");
                            return;
                        }
                    } else {
                        p.sendMessage("§cERRO → Você não possue permissão para usar o vender por shift!");
                    }
                else if (item.getItemMeta().getDisplayName().equalsIgnoreCase(plugin.getConfig().getString("Menu.Items.Vender.Titulo").replace("&", "§")))
                    Bukkit.dispatchCommand(p, "vender");
                return;
            }
        }
    }
}

package me.koddydev.kvender.Listeners;

import me.koddydev.kvender.KVender;
import me.koddydev.kvender.util.Settings;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.ArrayList;

public class PlayerShiftListener implements Listener {

    private static final ArrayList<String> delay = new ArrayList<>();
    private KVender plugin = (KVender) KVender.plugin;
    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent e) {

        if (Settings.vendaShift.contains(e.getPlayer().getName()) && e.isSneaking()) {

            double delayDouble = plugin.getConfig().getDouble("Delays.Shift");
            if(Double.isNaN(delayDouble)) delayDouble = Double.parseDouble("1");
            if (delayDouble <= 0) {
                Bukkit.dispatchCommand(e.getPlayer(), "vender");
            } else {
                long timeLong = (long) (20 * delayDouble);
                if (!delay.contains(e.getPlayer().getName())) {
                    Bukkit.dispatchCommand(e.getPlayer(), "vender");
                    delay.add(e.getPlayer().getName());
                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> delay.remove(e.getPlayer().getName()), timeLong);
                }
            }
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Settings.vendaShift.remove(e.getPlayer().getName());
        Settings.autoVenda.remove(e.getPlayer().getName());
    }

}

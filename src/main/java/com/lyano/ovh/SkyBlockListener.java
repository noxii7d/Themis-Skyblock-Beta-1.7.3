package com.lyano.ovh;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.PluginManager;

public class SkyBlockListener {
    private ThemisSkyBlock plugin;

    public SkyBlockListener(ThemisSkyBlock plugin) {
        this.plugin = plugin;
    }

    public void registerEvents() {
        PluginManager pm = plugin.getServer().getPluginManager();
        pm.registerEvent(Type.BLOCK_BREAK, new CustomBlockListener(), Priority.Normal, plugin);
        pm.registerEvent(Type.BLOCK_PLACE, new CustomBlockListener(), Priority.Normal, plugin);
        pm.registerEvent(Type.ENTITY_DAMAGE, new CustomEntityListener(), Priority.Normal, plugin);
        pm.registerEvent(Type.PLAYER_INTERACT, new CustomPlayerListener(), Priority.Normal, plugin);
    }

    private class CustomBlockListener extends BlockListener {
        @Override
        public void onBlockBreak(BlockBreakEvent event) {
            Player p = event.getPlayer();
            Island island = plugin.getIslandManager().getIslandAt(event.getBlock().getLocation());
            if (island != null && !island.isMemberOrOwner(p.getName())) {
                p.sendMessage("[ThemisMC] You cannot break blocks on this island! Only members or the owner can edit it.");
                event.setCancelled(true);
            }
        }
        @Override
        public void onBlockPlace(BlockPlaceEvent event) {
            Player p = event.getPlayer();
            Island island = plugin.getIslandManager().getIslandAt(event.getBlock().getLocation());
            if (island != null && !island.isMemberOrOwner(p.getName())) {
                p.sendMessage("[ThemisMC] You cannot place blocks on this island! Only members or the owner can edit it.");
                event.setCancelled(true);
            }
        }
    }

    private class CustomEntityListener extends EntityListener {
        @Override
        public void onEntityDamage(org.bukkit.event.entity.EntityDamageEvent event) {
            if (event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event;
                if (subEvent.getDamager() instanceof Player && subEvent.getEntity() instanceof Player) {
                    Player attacker = (Player) subEvent.getDamager();
                    Location loc = subEvent.getEntity().getLocation();
                    Island island = plugin.getIslandManager().getIslandAt(loc);
                    if (island != null) {
                        attacker.sendMessage("[ThemisMC] PvP is strictly disabled on SkyBlock islands!");
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    private class CustomPlayerListener extends PlayerListener {
        @Override
        public void onPlayerInteract(PlayerInteractEvent event) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Player p = event.getPlayer();
                Island island = plugin.getIslandManager().getIslandAt(event.getClickedBlock().getLocation());
                if (island != null && !island.isMemberOrOwner(p.getName())) {
                    p.sendMessage("[ThemisMC] You cannot open chests or interact with blocks here!");
                    event.setCancelled(true);
                }
            }
        }
    }
}
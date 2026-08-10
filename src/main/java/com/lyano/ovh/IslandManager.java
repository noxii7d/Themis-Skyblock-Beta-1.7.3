package com.lyano.ovh;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class IslandManager {
    private final ThemisSkyBlock plugin;
    private final Map<String, Island> islands = new HashMap<>();
    private final Random random = new Random();

    public IslandManager(ThemisSkyBlock plugin) {
        this.plugin = plugin;
        loadIslands();
    }

    public Island getIsland(String playerName) {
        for (Island island : islands.values()) {
            if (island.getOwnerName().equalsIgnoreCase(playerName) || island.isMember(playerName)) {
                return island;
            }
        }
        return null;
    }

    public Island getIslandAt(Location loc) {
        for (Island island : islands.values()) {
            if (Math.abs(loc.getBlockX() - island.getX()) <= 50 && Math.abs(loc.getBlockZ() - island.getZ()) <= 50) {
                return island;
            }
        }
        return null;
    }

    public void createIsland(Player player) {
        World world = plugin.getSkyBlockWorld();
        
        int x = random.nextInt(1000000) - 500000;
        int z = random.nextInt(1000000) - 500000;
        int y = 64;

        Island island = new Island(player.getName(), x, y, z);
        islands.put(player.getName(), island);
        saveIslands();

        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        for (int cx = -1; cx <= 1; cx++) {
            for (int cz = -1; cz <= 1; cz++) {
                world.getChunkAt(chunkX + cx, chunkZ + cz).load(true);
            }
        }

        buildStartingIsland(world, x, y, z);

        player.sendMessage("[ThemisMC] Building your island... get ready!");

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    Location loc = new Location(world, x + 0.5, y + 3.0, z + 0.5);
                    player.teleport(loc);
                    player.setVelocity(new Vector(0, 0, 0));
                    player.setFallDistance(0.0F);
                    player.sendMessage("[ThemisMC] Welcome to your new island!");
                }
            }
        }, 10L);
    }

    private void buildStartingIsland(World world, int x, int y, int z) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = 0; dy >= -2; dy--) {
                    Block block = world.getBlockAt(x + dx, y + dy, z + dz);
                    if (dy == 0 && dx == 0 && dz == 0) {
                        block.setType(Material.BEDROCK);
                    } else if (dy == 0) {
                        block.setType(Material.GRASS);
                    } else {
                        block.setType(Material.DIRT);
                    }
                }
            }
        }

        world.getBlockAt(x, y + 1, z).setType(Material.LOG);
        world.getBlockAt(x, y + 2, z).setType(Material.LOG);
        world.getBlockAt(x, y + 3, z).setType(Material.LOG);
        
        for (int lx = -1; lx <= 1; lx++) {
            for (int lz = -1; lz <= 1; lz++) {
                for (int ly = 3; ly <= 4; ly++) {
                    Block leafBlock = world.getBlockAt(x + lx, y + ly, z + lz);
                    if (leafBlock.getType() == Material.AIR) {
                        leafBlock.setType(Material.LEAVES);
                    }
                }
            }
        }

        int[][] satelliteOffsets = {
            { 8, 0, 0 },
            { -8, 0, 0 },
            { 0, 0, 8 },
            { 0, 0, -8 },
            { 7, 0, 7 }
        };

        for (int i = 0; i < satelliteOffsets.length; i++) {
            int[] offset = satelliteOffsets[i];
            int sx = x + offset[0];
            int sy = y;
            int sz = z + offset[2];

            for (int sdx = -1; sdx <= 1; sdx++) {
                for (int sdz = -1; sdz <= 1; sdz++) {
                    for (int sdy = 0; sdy >= -2; sdy--) {
                        Block subBlock = world.getBlockAt(sx + sdx, sy + sdy, sz + sdz);
                        if (sdy == 0) {
                            subBlock.setType(Material.GRASS);
                        } else {
                            subBlock.setType(Material.DIRT);
                        }
                    }
                }
            }

            Block chestBlock = world.getBlockAt(sx, sy + 1, sz);
            chestBlock.setType(Material.CHEST);
            
            if (chestBlock.getState() instanceof Chest) {
                Chest chest = (Chest) chestBlock.getState();
                fillChestLoot(chest, i + 1);
            }
        }
    }

    private void fillChestLoot(Chest chest, int chestNumber) {
        org.bukkit.inventory.Inventory inv = chest.getInventory();
        
        switch (chestNumber) {
            case 1:
                inv.addItem(new ItemStack(Material.LAVA_BUCKET, 1));
                inv.addItem(new ItemStack(Material.ICE, 1));
                inv.addItem(new ItemStack(Material.BUCKET, 1));
                break;
            case 2:
                inv.addItem(new ItemStack(Material.SAPLING, 1));
                inv.addItem(new ItemStack(Material.SEEDS, 2));
                inv.addItem(new ItemStack(Material.SUGAR_CANE, 1));
                break;
            case 3:
                inv.addItem(new ItemStack(Material.BREAD, 2));
                inv.addItem(new ItemStack(Material.APPLE, 1));
                break;
            case 4:
                inv.addItem(new ItemStack(Material.CACTUS, 1));
                inv.addItem(new ItemStack(Material.YELLOW_FLOWER, 1));
                break;
            case 5:
                inv.addItem(new ItemStack(Material.STRING, 2));
                inv.addItem(new ItemStack(Material.BONE, 1));
                break;
            default:
                inv.addItem(new ItemStack(Material.BREAD, 1));
                break;
        }
    }

    public void saveIslands() {
        System.out.println("[ThemisMC] Saved island database.");
    }

    public void loadIslands() {
    }
}
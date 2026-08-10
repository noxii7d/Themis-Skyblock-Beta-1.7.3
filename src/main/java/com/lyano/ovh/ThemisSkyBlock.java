package com.lyano.ovh;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.Vector;

public class ThemisSkyBlock extends JavaPlugin {
    private IslandManager islandManager;

    @Override
    public void onEnable() {
        islandManager = new IslandManager(this);
        SkyBlockListener listener = new SkyBlockListener(this);
        listener.registerEvents();
        System.out.println("[ThemisMC] ThemisSkyBlock has been enabled!");
    }
    
    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new VoidGenerator();
    }

    @Override
    public void onDisable() {
        if (islandManager != null) {
            islandManager.saveIslands();
        }
        System.out.println("[ThemisMC] ThemisSkyBlock has been disabled.");
    }

    public IslandManager getIslandManager() { return islandManager; }

    public World getSkyBlockWorld() {
        World world = Bukkit.getWorld("skyblock_world");
        if (world == null) {
            world = getServer().createWorld("skyblock_world", World.Environment.NORMAL, new VoidGenerator());
        }
        return world;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("[ThemisMC] Only players can execute ThemisSkyBlock commands!");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            player.sendMessage("--- ThemisSkyBlock Commands ---");
            player.sendMessage("/sb create - Create island");
            player.sendMessage("/sb home - Teleport to your island");
            player.sendMessage("/sb visit <player> - Visit someone else's island");
            player.sendMessage("/sb add <player> - Add a friend to your island");
            player.sendMessage("/sb remove <player> - Remove a friend");
            player.sendMessage("/sb challenge - Complete Cobble Challenge!");
            return true;
        }
        String sub = args[0].toLowerCase();
        if (sub.equals("create")) {
            if (islandManager.getIsland(player.getName()) != null) {
                player.sendMessage("[ThemisMC] You already have a island! Use /sb home .");
                return true;
            }
            getSkyBlockWorld();
            islandManager.createIsland(player);
            return true;
        }
        if (sub.equals("home")) {
            Island island = islandManager.getIsland(player.getName());
            if (island == null) {
                player.sendMessage("[ThemisMC] You don't have an island yet! Use /sb create");
                return true;
            }
            World world = getSkyBlockWorld();
            world.getChunkAt(new Location(world, island.getX(), island.getY(), island.getZ())).load(true);
            Location loc = new Location(world, island.getX() + 0.5, island.getY() + 3.0, island.getZ() + 0.5);
            player.teleport(loc);
            player.setFallDistance(0.0F);
            player.setVelocity(new Vector(0, 0, 0));
            player.sendMessage("[ThemisMC] Teleported safely to your island!");
            return true;
        }
        if (sub.equals("visit")) {
            if (args.length < 2) {
                player.sendMessage("Usage: /sb visit <player>");
                return true;
            }
            Island island = islandManager.getIsland(args[1]);
            if (island == null) {
                player.sendMessage("[ThemisMC] That player doesn't have a island or doesn't exist!");
                return true;
            }
            World world = getSkyBlockWorld();
            world.getChunkAt(new Location(world, island.getX(), island.getY(), island.getZ())).load(true);
            Location loc = new Location(world, island.getX() + 0.5, island.getY() + 3.0, island.getZ() + 0.5);
            player.teleport(loc);
            player.setFallDistance(0.0F);
            player.setVelocity(new Vector(0, 0, 0));
            player.sendMessage("[ThemisMC] Now visiting " + island.getOwnerName() + "'s island safely!");
            return true;
        }
        if (sub.equals("add")) {
            if (args.length < 2) {
                player.sendMessage("Usage: /sb add <player>");
                return true;
            }
            Island island = islandManager.getIsland(player.getName());
            if (island == null) {
                player.sendMessage("[ThemisMC] You must own an island to add members!");
                return true;
            }
            island.addMember(args[1]);
            islandManager.saveIslands();
            player.sendMessage("[ThemisMC] Added " + args[1] + " to your island! :3");
            return true;
        }
        if (sub.equals("remove")) {
            if (args.length < 2) {
                player.sendMessage("Usage: /sb remove <player>");
                return true;
            }
            Island island = islandManager.getIsland(player.getName());
            if (island == null) {
                player.sendMessage("[ThemisMC] You must own an island to remove members!");
                return true;
            }
            island.removeMember(args[1]);
            islandManager.saveIslands();
            player.sendMessage("[ThemisMC] Removed " + args[1] + " from your island");
            return true;
        }
        if (sub.equals("challenge")) {
            if (player.getInventory().contains(Material.COBBLESTONE, 64)) {
                player.getInventory().removeItem(new ItemStack(Material.COBBLESTONE, 64));
                player.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 5));
                player.sendMessage("[ThemisMC] [Challenge Complete] Turned 64 Cobblestone into 5 Iron Ingots!");
            } else {
                player.sendMessage("[ThemisMC] Bring 64 Cobblestone in your inventory to earn 5 Iron Ingots!");
            }
            return true;
        }
        return true;
    }
}
package com.lyano.ovh;

import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import java.util.Random;

public class VoidGenerator extends ChunkGenerator {
    
    @Override
    public byte[] generate(World world, Random random, int cx, int cz) {
        return new byte[32768];
    }

    @Override
    public boolean canSpawn(World world, int x, int z) {
        return true;
    }

    public int xyzToByte(int x, int y, int z) {
        return (x & 0xF) << 11 | (z & 0xF) << 7 | y;
    }
}
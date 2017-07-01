package com.lordstephen77.dreamblocks.fillers;

import com.lordstephen77.dreamblocks.*;

import java.util.Random;

/**
 * Created by Александр on 01.07.2017.
 */
public class WaterFiller extends WorldFiller {
    public WaterFiller(World world, Random random, TileStore tileStore) {
        super(world, random, tileStore);
    }

    @Override
    public void fill() {
        int median = (int) (.5 * world.height);
        for (int x = 0; x < world.width; x++) {
            int depth = getWaterDepthAt(x);
            if (depth == 0){
                continue;
            }
            int amount = (int)(1 + random.nextDouble() * 2);
            fillDown(x, median, depth, Constants.TileID.WATER);
            fillDown(x, median+depth, amount, Constants.TileID.SAND);
            int dx = amount;
            while(dx >= 0) {
                fillSandDown(x+dx, median, depth+amount);
                fillSandDown(x-dx, median, depth+amount);
                dx--;
            }
        }
    }

    private int getWaterDepthAt(int x){
        int median = (int) (.5 * world.height);
        int y = median;
        Constants.TileID tileID = world.tileIdAt(x, y);
        while(y < world.height && tileID == Constants.TileID.AIR){
            y++;
            tileID = world.tileIdAt(x,y);
        }
        return y - median;
    }

    private void fillDown(int x, int y, int amount, Constants.TileID tileID){
        for (int i = y; i < y+amount; i++){
            world.tiles[x][i] = tileStore.make(tileID);
        }
    }

    private void fillSandDown(int x, int y, int amount){
        for (int i = y; i < y+amount; i++){
            boolean isWater = world.tileIdAt(x, i) == Constants.TileID.WATER;
            boolean isAir = world.tileIdAt(x, i) == Constants.TileID.AIR;
            if (!isWater && !isAir) {
                world.changeTile(x, i, tileStore.make(Constants.TileID.SAND));
            }
        }
    }
}

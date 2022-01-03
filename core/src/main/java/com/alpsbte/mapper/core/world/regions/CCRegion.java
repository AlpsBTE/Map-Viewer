package com.alpsbte.mapper.core.world.regions;

import java.io.*;
import java.util.BitSet;

/**
 * Cubic Chunks Region File: x.z.2dr and
 * {@see <a>https://github.com/chunky-dev/chunky/blob/master/chunky/src/java/se/llbit/chunky/world/region/ImposterCubicRegion.java</a>}
 */
public class CCRegion extends AbstractRegion {

    private static final int GZIP_TYPE = 1;
    private static final int DEFLATE_TYPE = 2;

    private static final int REGION_SIZE = 16;
    private static final int HEADER_SIZE = 8192;
    private static final int OFFSET_COUNT = (HEADER_SIZE / 2) / 8;

    private static final int DIAMETER_IN_CUBIC_REGIONS = 2;
    private static final int CUBES_COUNT = REGION_SIZE * 3;
    private static final int LOC_BITS = 4;

    BitSet cubes = new BitSet(CUBES_COUNT);

    protected CCRegion(File regionFile) {
        super(regionFile);
    }

    @Override
    protected void readRegion(File regionFile) {
        // To read the NBT data, we need to access it by using the RandomAccessFile class
        try (RandomAccessFile region = new RandomAccessFile(regionFile, "r")) {
            if (region.length() < 16384) return; // Check if header exists

            offsets = new int[OFFSET_COUNT];

            for (int x = 0; x < REGION_SIZE; x++) {
                for (int y = 0; y < REGION_SIZE; ++y) {
                    for (int z = 0; z < REGION_SIZE; ++z) {
                        int loc = region.readInt();
                        int index = getChunkOffset(x, y, z);
                        cubes.set(index, loc != 0);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public DataInputStream readChunkData(int chunkX, int chunkZ, File regionFile) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected int getChunkOffset(int chunkX, int chunkZ) {
        return getChunkOffset(chunkX, 0, chunkZ);
    }

    private int getChunkOffset(int chunkX, int chunkY, int chunkZ) {
        return (chunkX << LOC_BITS * 2) | (chunkY << LOC_BITS) | chunkZ;
    }

    @Override
    protected boolean outOfBounds(int chunkX, int chunkZ) {
        return chunkX < 0 || chunkZ < 0 || chunkX >= REGION_SIZE || chunkZ >= REGION_SIZE;
    }
}

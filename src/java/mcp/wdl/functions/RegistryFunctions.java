/*
 * This file is part of World Downloader: A mod to make backups of your multiplayer worlds.
 * https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/2520465-world-downloader-mod-create-backups-of-your-builds
 *
 * Copyright (c) 2014 nairol, cubic72
 * Copyright (c) 2018 Pokechu22, julialy
 *
 * This project is licensed under the MMPLv2.  The full text of the MMPL can be
 * found in LICENSE.md, or online at https://github.com/iopleke/MMPLv2/blob/master/LICENSE.md
 * For information about this the MMPLv2, see https://stopmodreposts.org/
 *
 * Do not redistribute (in modified or unmodified form) without prior permission.
 */
package mcp.wdl.functions;

import net.minecraft.block.Block;
import net.minecraft.world.biome.BiomeGenBase;

/**
 * Contains functions that interact with registries.
 *
 * This version is used in versions before 1.13.1, including 1.13.0.
 */
public final class RegistryFunctions {
	private RegistryFunctions() { throw new AssertionError(); }

	/**
	 * Gets the numeric ID for the given block.
	 * @return A numeric ID, the meaning and value of which is unspecified.
	 */
	public static final int getBlockId(Block block) {
		return Block.getIdFromBlock(block);
	}
	
	/**
	 * Gets the numeric ID for the given biome.
	 * @return A numeric ID, the meaning and value of which is unspecified.
	 */
	public static final int getBiomeId(BiomeGenBase biome) {
		return biome.biomeID;
	}
}

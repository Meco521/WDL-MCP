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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.storage.MapData;


/**
 * Functions related to maps (the item).
 *
 * In 1.12.2 and earlier, loadMapData takes an int and the dimension field is a
 * byte.
 */
public final class MapFunctions {
	private MapFunctions() { throw new AssertionError(); }
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Gets the map data associated with the given packet.
	 *
	 * @param world The client world.
	 * @param mapPacket The packet.
	 * @return The map data, or null if the underlying function returns null.
	 */
	public static MapData getMapData(World world, S34PacketMaps mapPacket) {
		return ItemMap.loadMapData(mapPacket.getMapId(), world);
	}

	/**
	 * Gets the ID of the map associated with the given item stack.
	 * Assumes that the item is a map in the first place.
	 *
	 * @param stack The map item stack.
	 * @return The map ID
	 */
	public static int getMapID(ItemStack stack) {
		// Map ID is based on its damage value, yay!
		// See ItemMap.getMapData
		return stack.getMetadata();
	}

	/**
	 * Returns true if the map has a null dimension.  This can happen in 1.13.1 and later.
	 */
	public static boolean isMapDimensionNull(MapData map) {
		return false; // A primitive byte can't be null
	}

	private static boolean useForgeMethod = false;
	
	/**
	 * Sets the map's dimension to the given dimension.  In some versions,
	 * the {@link MapData#dimension} field is a byte, while in other ones it is
	 * a WorldProvider (which might start out null).
	 */
	public static void setMapDimension(MapData map, WorldProvider dim) {
		if (!useForgeMethod) {
			try {
				setMapDimensionVanilla(map, dim.getDimensionId());
			} catch (NoSuchFieldError e) {
				// Forge changes the type of this field from a byte to an int: https://git.io/fpReX
				// While this change is nice, it does make things messy and we need to set the right field
				// Right now, just use reflection and the SRG name of the field, since that'd be what
				// the field is named at runtime (and forge won't be present in the dev environment).
				// (See soccerguy's comment on issue 106)
				LOGGER.info("[WDL] Failed to set map dimension using vanilla field; switching to forge field...", e);
				try {
					setMapDimensionForge(map, dim.getDimensionId());
					useForgeMethod = true;
					LOGGER.info("[WDL] The forge field worked; it will be used for future attempts at setting the dimension.");
				} catch (Exception e2) {
					LOGGER.fatal("[WDL] Failed to set map dimension using both vanilla and forge fields", e2);
					RuntimeException ex = new RuntimeException("Failed to set map dimension", e2);
					ex.addSuppressed(e);
					throw ex;
				}
			}
		} else {
			// The forge version worked once before; use that in the future.
			try {
				setMapDimensionForge(map, dim.getDimensionId());
			} catch (Exception ex) {
				LOGGER.fatal("[WDL] Failed to set map dimension using both forge field, but it worked before?", ex);
				throw new RuntimeException("Failed to set map dimension with forge", ex);
			}
		}
	}

	/**
	 * Uses the vanilla field to set the map dimension.
	 * @throws NoSuchFieldError when forge is installed
	 */
	private static void setMapDimensionVanilla(MapData map, int dim) throws NoSuchFieldError {
		map.dimension = (byte)dim;
	}

	/**
	 * Sets the forge field for the map dimension. This only works in a SRG name
	 * environment with forge installed.
	 */
	private static void setMapDimensionForge(MapData map, int dim) throws Exception {
		MapData.class.getField("field_76200_c").setInt(map, dim);
	}
}

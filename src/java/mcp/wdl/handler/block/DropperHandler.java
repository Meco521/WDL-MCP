/*
 * This file is part of World Downloader: A mod to make backups of your multiplayer worlds.
 * https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/2520465-world-downloader-mod-create-backups-of-your-builds
 *
 * Copyright (c) 2014 nairol, cubic72
 * Copyright (c) 2017-2018 Pokechu22, julialy
 *
 * This project is licensed under the MMPLv2.  The full text of the MMPL can be
 * found in LICENSE.md, or online at https://github.com/iopleke/MMPLv2/blob/master/LICENSE.md
 * For information about this the MMPLv2, see https://stopmodreposts.org/
 *
 * Do not redistribute (in modified or unmodified form) without prior permission.
 */
package mcp.wdl.handler.block;

import java.util.function.BiConsumer;

import mcp.wdl.handler.HandlerException;
import mcp.wdl.reflection.ReflectionUtils;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.IBlockAccess;

public class DropperHandler extends BlockHandler<TileEntityDropper, ContainerDispenser> {
	public DropperHandler() {
		super(TileEntityDropper.class, ContainerDispenser.class, "container.dropper");
	}

	@Override
	public IChatComponent handle(BlockPos clickedPos, ContainerDispenser container,
			TileEntityDropper blockEntity, IBlockAccess world,
			BiConsumer<BlockPos, TileEntityDropper> saveMethod) throws HandlerException {
		IInventory dropperInventory = ReflectionUtils.findAndGetPrivateField(
				container, IInventory.class);
		String title = getCustomDisplayName(dropperInventory);
		saveContainerItems(container, blockEntity, 0);
		saveMethod.accept(clickedPos, blockEntity);
		if (title != null) {
			blockEntity.setCustomName(title);
		}
		return new ChatComponentTranslation("wdl.messages.onGuiClosedInfo.savedTileEntity.dropper");
	}
}

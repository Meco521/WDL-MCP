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


import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

/**
 * Contains functions related to gamerules. This version of the class is used
 * between Minecraft 1.8 and Minecraft 1.12.2.
 */


public final class ChatFunctions {
	private ChatFunctions() { throw new AssertionError(); }

	/**
	 * Creates a link style for the given URL: blue, underlined, and with the right
	 * click event.
	 *
	 * @param url The URL to open.
	 * @return A new style
	 */
	public static ChatStyle createLinkFormatting(String url) {
		// Forwards-compatibility with 1.14
		return new ChatStyle()
				.setColor(EnumChatFormatting.BLUE)
				.setUnderlined(true)
				.setChatClickEvent(new ClickEvent(Action.OPEN_URL, url));
	}
}

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
package mcp.wdl.functions;

import static org.lwjgl.opengl.GL11.*;

import java.net.URI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.world.World;

/**
 * Versioned functions related to GUIs.
 */
public final class GuiFunctions {
	private GuiFunctions() { throw new AssertionError(); }

	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Creates a new instance of {@link EntityPlayerSP}.
	 *
	 * @param minecraft The minecraft instance
	 * @param world The world
	 * @param nhpc The connection
	 * @param base The original player to copy other data from
	 */
	public static EntityPlayerSP makePlayer(Minecraft minecraft, World world, NetHandlerPlayClient nhpc, EntityPlayerSP base) {
		return new EntityPlayerSP(minecraft, world, nhpc,
				base.getStatFileWriter());
	}

	/**
	 * Draws a dark background, similar to {@link GuiScreen#drawBackground(int)} but darker.
	 * Same appearance as the background in lists.
	 *
	 * @param top Where to start drawing (usually, 0)
	 * @param left Where to start drawing (usually, 0)
	 * @param bottom Where to stop drawing (usually, height).
	 * @param right Where to stop drawing (usually, width)
	 */
	public static void drawDarkBackground(int top, int left, int bottom, int right) {
		GlStateManager.disableLighting();
		GlStateManager.disableFog();

		Tessellator t = Tessellator.getInstance();
		WorldRenderer b = t.getWorldRenderer();

		// Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.BACKGROUND_LOCATION);
		Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.optionsBackground);
		// GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		float textureSize = 32.0F;
		b.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		b.pos(0, bottom, 0).tex(0 / textureSize,
				bottom / textureSize).color(32, 32, 32, 255).endVertex();
		b.pos(right, bottom, 0).tex(right / textureSize,
				bottom / textureSize).color(32, 32, 32, 255).endVertex();
		b.pos(right, top, 0).tex(right / textureSize,
				top / textureSize).color(32, 32, 32, 255).endVertex();
		b.pos(left, top, 0).tex(left / textureSize,
				top / textureSize).color(32, 32, 32, 255).endVertex();
		t.draw();
	}

	/**
	 * Draws the top and bottom borders found on gui lists (but no background).
	 * <br/>
	 * Based off of
	 * {@link net.minecraft.client.gui.GuiSlot#overlayBackground(int, int, int, int)}.
	 *
	 * Note that there is an additional 4-pixel padding on the margins for the gradient.
	 *
	 * @param topMargin Amount of space to give for the upper box.
	 * @param bottomMargin Amount of space to give for the lower box.
	 * @param top Where to start drawing (usually, 0)
	 * @param left Where to start drawing (usually, 0)
	 * @param bottom Where to stop drawing (usually, height).
	 * @param right Where to stop drawing (usually, width)
	 */
	public static void drawBorder(int topMargin, int bottomMargin, int top, int left, int bottom, int right) {
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		// GlStateManager.disableDepthTest();
		GlStateManager.disableDepth();
		byte padding = 4;

		// Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.BACKGROUND_LOCATION);
		Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.optionsBackground);
		// GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		float textureSize = 32.0F;

		Tessellator t = Tessellator.getInstance();
		WorldRenderer b = t.getWorldRenderer();

		//Box code is GuiSlot.overlayBackground
		//Upper box
		int upperBoxEnd = top + topMargin;

		// GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		b.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		b.pos(left, upperBoxEnd, 0.0D).tex(0.0D, upperBoxEnd
				/ textureSize).color(64, 64, 64, 255).endVertex();
		b.pos(right, upperBoxEnd, 0.0D).tex(right / textureSize,
				upperBoxEnd / textureSize).color(64, 64, 64, 255).endVertex();
		b.pos(right, top, 0.0D).tex(right / textureSize, top / textureSize)
		.color(64, 64, 64, 255).endVertex();
		b.pos(left, top, 0.0D).tex(0.0D, top / textureSize)
		.color(64, 64, 64, 255).endVertex();
		t.draw();

		// Lower box
		int lowerBoxStart = bottom - bottomMargin;

		// GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		b.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		b.pos(left, bottom, 0.0D).tex(0.0D, bottom / textureSize)
		.color(64, 64, 64, 255).endVertex();
		b.pos(right, bottom, 0.0D).tex(right / textureSize, bottom
				/ textureSize).color(64, 64, 64, 255).endVertex();
		b.pos(right, lowerBoxStart, 0.0D)
		.tex(right / textureSize, lowerBoxStart / textureSize)
		.color(64, 64, 64, 255).endVertex();
		b.pos(left, lowerBoxStart, 0.0D).tex(0.0D, lowerBoxStart
				/ textureSize).color(64, 64, 64, 255).endVertex();
		t.draw();

		//Gradients
		// GlStateManager.enableBlend();
		// GlStateManager.blendFuncSeparate(GL_SRC_ALPHA,
		// 		GL_ONE_MINUS_SRC_ALPHA, 0, 1);
		// GlStateManager.disableAlphaTest();
		// GlStateManager.shadeModel(GL_SMOOTH);
		// GlStateManager.disableTexture();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL_SRC_ALPHA,
				GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableAlpha();
		GlStateManager.shadeModel(GL_SMOOTH);
		GlStateManager.disableTexture2D();
		b.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		b.pos(left, upperBoxEnd + padding, 0.0D).tex(0.0D, 1.0D)
		.color(0, 0, 0, 0).endVertex();
		b.pos(right, upperBoxEnd + padding, 0.0D).tex(1.0D, 1.0D)
		.color(0, 0, 0, 0).endVertex();
		b.pos(right, upperBoxEnd, 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255)
		.endVertex();
		b.pos(left, upperBoxEnd, 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255)
		.endVertex();
		t.draw();
		b.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		b.pos(left, lowerBoxStart, 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255)
		.endVertex();
		b.pos(right, lowerBoxStart, 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255)
		.endVertex();
		b.pos(right, lowerBoxStart - padding, 0.0D).tex(1.0D, 0.0D)
		.color(0, 0, 0, 0).endVertex();
		b.pos(left, lowerBoxStart - padding, 0.0D).tex(0.0D, 0.0D)
		.color(0, 0, 0, 0).endVertex();
		t.draw();

		// GlStateManager.enableTexture();
		// GlStateManager.shadeModel(GL_FLAT);
		// GlStateManager.enableAlphaTest();
		// GlStateManager.disableBlend();
		GlStateManager.enableTexture2D();
		GlStateManager.shadeModel(GL_FLAT);
		GlStateManager.enableAlpha();
		GlStateManager.disableBlend();
	}

	/**
	 * Copies the given text into the system clipboard.
	 * @param text The text to copy
	 */
	public static void setClipboardString(String text) {
		GuiScreen.setClipboardString(text);
	}

	/**
	 * Opens a link using the default browser.
	 * @param url The URL to open.
	 * @see GuiScreen#openWebLink()
	 */
	public static void openLink(String url) {
		try {
			Class<?> desktopClass = Class.forName("java.awt.Desktop");
			Object desktop = desktopClass.getMethod("getDesktop").invoke(
					null);
			desktopClass.getMethod("browse", URI.class).invoke(desktop,
					new URI(url));
		} catch (Throwable e) {
			LOGGER.error("Couldn't open link to " + url, e);
		}
	}
}

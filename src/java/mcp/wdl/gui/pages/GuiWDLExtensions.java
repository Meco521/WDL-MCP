/*
 * This file is part of World Downloader: A mod to make backups of your multiplayer worlds.
 * https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/2520465-world-downloader-mod-create-backups-of-your-builds
 *
 * Copyright (c) 2014 nairol, cubic72
 * Copyright (c) 2017-2019 Pokechu22, julialy
 *
 * This project is licensed under the MMPLv2.  The full text of the MMPL can be
 * found in LICENSE.md, or online at https://github.com/iopleke/MMPLv2/blob/master/LICENSE.md
 * For information about this the MMPLv2, see https://stopmodreposts.org/
 *
 * Do not redistribute (in modified or unmodified form) without prior permission.
 */
package mcp.wdl.gui.pages;

import mcp.wdl.api.IWDLModWithGui;
import mcp.wdl.api.WDLApi;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
// import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.resources.I18n;
// import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumChatFormatting;
import mcp.wdl.gui.widget.WDLButton;
import mcp.wdl.gui.widget.ButtonDisplayGui;
import mcp.wdl.gui.widget.GuiList;
import mcp.wdl.gui.widget.WDLScreen;
import mcp.wdl.gui.widget.TextList;

/**
 * GUI showing the currently enabled mods, and their information.
 *
 * It's composed of two halves, one that lists enabled extensions that can
 * be clicked, and the other that shows the details on the selected extension.
 * The two halves can be dragged up and down (which is why the logic is so
 * complex here; {@link GuiListExtended} was not designed for that).
 *
 * @author Pokechu22
 */
public class GuiWDLExtensions extends WDLScreen {
	/**
	 * Top of the bottom list.
	 */
	private int bottomLocation;

	/**
	 * Height of the bottom area.
	 */
	private static final int TOP_HEIGHT = 23;
	/**
	 * Height of the middle section.
	 *
	 * Equal to <code>{@link FontRenderer#FONT_HEIGHT} + 10</code>.
	 */
	private static final int MIDDLE_HEIGHT = 19;
	/**
	 * Height of the top area.
	 */
	private static final int BOTTOM_HEIGHT = 32;

	private class ModList extends GuiList<ModList.ModEntry> {
		public ModList() {
			super(GuiWDLExtensions.this.mc, GuiWDLExtensions.this.width,
					bottomLocation, TOP_HEIGHT, bottomLocation, 22);
			// this.renderSelection = true;
			this.showSelectionBox = true;
		}

		private ModEntry selectedEntry;

		private class ModEntry extends GuiListEntry<ModEntry> {
			public final WDLApi.ModInfo<?> mod;
			/**
			 * Constant information about the extension (name & version)
			 */
			private final String modDescription;
			/**
			 * The {@link #modDescription}, formated depending on whether
			 * the mod is enabled.
			 */
			private String label;

			public ModEntry(WDLApi.ModInfo<?> mod) {
				this.mod = mod;
				String name = mod.getDisplayName();
				this.modDescription = I18n.format("wdl.gui.extensions.modVersion",
						name, mod.version);

				if (!mod.isEnabled()) {
					this.label = "" + EnumChatFormatting.GRAY
							+ EnumChatFormatting.ITALIC + modDescription;
				} else {
					this.label = modDescription;
				}

				if (mod.mod instanceof IWDLModWithGui) {
					IWDLModWithGui guiMod = (IWDLModWithGui) mod.mod;
					String buttonName = (guiMod).getButtonName();
					if (buttonName == null || buttonName.isEmpty()) {
						buttonName = I18n.format("wdl.gui.extensions.defaultSettingsButtonText");
					}

					this.addButton(new WDLButton(0, 0, 80, 20, guiMod.getButtonName()) {
						public @Override void performAction() {
							if (mod.mod instanceof IWDLModWithGui) {
								((IWDLModWithGui) mod.mod).openGui(GuiWDLExtensions.this);
							}
						}
					}, (GuiWDLExtensions.this.width / 2) - 180, -1);
				}

				this.addButton(new WDLButton(0, 0, 80, 20,
						I18n.format("wdl.gui.extensions."
								+ (mod.isEnabled() ? "enabled" : "disabled"))) {
					public @Override void performAction() {
						mod.toggleEnabled();

						this.setMessage(I18n.format("wdl.gui.extensions."
								+ (mod.isEnabled() ? "enabled" : "disabled")));

						if (!mod.isEnabled()) {
							label = "" + EnumChatFormatting.GRAY
									+ EnumChatFormatting.ITALIC + modDescription;
						} else {
							label = modDescription;
						}
					}
				}, (GuiWDLExtensions.this.width / 2) - 92, -1);
			}

			@Override
			public boolean mouseDown(int mouseX, int mouseY, int mouseButton) {
				if (super.mouseDown(mouseX, mouseY, mouseButton)) {
					return true;
				}

				// A click, but not on a button
				if (selectedEntry != this) {
					selectedEntry = this;

					// mc.getSoundHandler().play(SimpleSound.master(
					// 		SoundEvents.UI_BUTTON_CLICK, 1.0F));

					updateDetailsList(mod);

					return true;
				}

				return false;
			}

			@Override
			public void drawEntry(int x, int y, int width, int height, int mouseX, int mouseY) {
				super.drawEntry(x, y, width, height, mouseX, mouseY);

				int centerY = y + height / 2 - fontRendererObj.FONT_HEIGHT / 2;
				drawString(fontRendererObj, label, x, centerY, 0xFFFFFF);
			}

			@Override
			public boolean isSelected() {
				return selectedEntry == this;
			}
		}

		{
			WDLApi.getWDLMods().values().stream()
					.map(ModEntry::new)
					.forEach(this.getEntries()::add);
		}

		@Override
		public void render(int mouseX, int mouseY, float partialTicks) {
			this.height = this.bottom = bottomLocation;

			super.drawScreen(mouseX, mouseY, partialTicks);
		}

		@Override
		public int getEntryWidth() {
			return GuiWDLExtensions.this.width - 20;
		}

		@Override
		public int getScrollBarX() {
			return GuiWDLExtensions.this.width - 10;
		}
	}

	private class ModDetailList extends TextList {
		public ModDetailList() {
			super(GuiWDLExtensions.this.mc, GuiWDLExtensions.this.width,
					GuiWDLExtensions.this.height - bottomLocation,
					MIDDLE_HEIGHT, BOTTOM_HEIGHT);
		}
		
		@Override
		public void render(int mouseX, int mouseY, float partialTicks) {
			this.setY(bottomLocation);
			this.height = GuiWDLExtensions.this.height - bottomLocation;
			this.bottom = this.height - 32;

			super.drawScreen(mouseX, mouseY, partialTicks);

			drawCenteredString(fontRendererObj,
					I18n.format("wdl.gui.extensions.detailsCaption"),
					GuiWDLExtensions.this.width / 2, bottomLocation + 5, 0xFFFFFF);
		}
	}

	private void updateDetailsList(WDLApi.ModInfo<?> selectedMod) {
		detailsList.clearLines();

		if (selectedMod != null) {
			String info = selectedMod.getInfo();

			detailsList.addLine(info);
		}
	}

	/**
	 * Gui to display after this is closed.
	 */
	private final GuiScreen parent;
	/**
	 * Details on the selected mod.
	 */
	private ModDetailList detailsList;

	public GuiWDLExtensions(GuiScreen parent) {
		super("wdl.gui.extensions.title");
		this.parent = parent;
	}

	@Override
	public void initGui() {
		bottomLocation = height - 100;
		dragging = false;

		this.addList(new ModList());
		this.detailsList = this.addList(new ModDetailList());

		this.addButton(new ButtonDisplayGui(width / 2 - 100, height - 29,
				200, 20, this.parent));
	}

	/**
	 * Whether the center section is being dragged.
	 */
	private boolean dragging = false;
	private int dragOffset;

	@Override
	public void mouseDown(int mouseX, int mouseY) {
		if (mouseY > bottomLocation && mouseY < bottomLocation + MIDDLE_HEIGHT) {
			dragging = true;
			dragOffset = mouseY - bottomLocation;
			// return true?
		}
	}

	@Override
	public void mouseUp(int mouseX, int mouseY) {
		dragging = false;
	}

	@Override
	public void mouseDragged(int mouseX, int mouseY) {
		if (dragging) {
			bottomLocation = mouseY - dragOffset;
		}

		//Clamp bottomLocation.
		if (bottomLocation < TOP_HEIGHT + 8) {
			bottomLocation = TOP_HEIGHT + 8;
		}
		if (bottomLocation > height - BOTTOM_HEIGHT - 8) {
			bottomLocation = height - BOTTOM_HEIGHT - 8;
		}
	}

        @Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();

		//Clamp bottomLocation.
		if (bottomLocation < TOP_HEIGHT + 33) {
			bottomLocation = TOP_HEIGHT + 33;
		}
		if (bottomLocation > height - MIDDLE_HEIGHT - BOTTOM_HEIGHT - 33) {
			bottomLocation = height - MIDDLE_HEIGHT - BOTTOM_HEIGHT - 33;
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}

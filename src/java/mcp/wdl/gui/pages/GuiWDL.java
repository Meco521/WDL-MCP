/*
 * This file is part of World Downloader: A mod to make backups of your multiplayer worlds.
 * https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/2520465-world-downloader-mod-create-backups-of-your-builds
 *
 * Copyright (c) 2014 nairol, cubic72
 * Copyright (c) 2017-2020 Pokechu22, julialy
 *
 * This project is licensed under the MMPLv2.  The full text of the MMPL can be
 * found in LICENSE.md, or online at https://github.com/iopleke/MMPLv2/blob/master/LICENSE.md
 * For information about this the MMPLv2, see https://stopmodreposts.org/
 *
 * Do not redistribute (in modified or unmodified form) without prior permission.
 */
package mcp.wdl.gui.pages;

import java.util.List;
import java.util.function.BiFunction;

import mcp.wdl.WDL;
import mcp.wdl.config.IConfiguration;
import mcp.wdl.config.settings.MiscSettings;
import mcp.wdl.update.WDLUpdateChecker;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentTranslation;
import mcp.wdl.gui.widget.ButtonDisplayGui;
import mcp.wdl.gui.widget.GuiList;
import mcp.wdl.gui.widget.WDLButton;
import mcp.wdl.gui.widget.WDLScreen;
import mcp.wdl.gui.widget.WDLTextField;

public class GuiWDL extends WDLScreen {
	/**
	 * Tooltip to display on the given frame.
	 */
	private String displayedTooltip = null;

	private class GuiWDLButtonList extends GuiList<GuiWDLButtonList.ButtonEntry> {
		public GuiWDLButtonList() {
			super(GuiWDL.this.mc, GuiWDL.this.width, GuiWDL.this.height, 39,
					GuiWDL.this.height - 32, 20);
		}

		private class ButtonEntry extends GuiListEntry<ButtonEntry> {
			private final WDLButton button;

			private final String tooltip;

			/**
			 * Constructor.
			 *
			 * @param key
			 *                   The I18n key, which will have the base for this GUI
			 *                   prepended.
			 * @param openFunc
			 *                   Supplier that constructs a GuiScreen to open based off
			 *                   of this screen (the one to open when that screen is
			 *                   closed) and the WDL instance
			 * @param needsPerms
			 *                   Whether the player needs download permission to use
			 *                   this button.
			 */
			public ButtonEntry(String key, BiFunction<GuiScreen, WDL, GuiScreen> openFunc, boolean needsPerms) {
				this.button = this.addButton(new ButtonDisplayGui(0, 0, 200, 20,
						I18n.format("wdl.gui.wdl." + key + ".name"),
						() -> openFunc.apply(GuiWDL.this, GuiWDL.this.wdl)), -100, 0);
				if (needsPerms) {
					button.setEnabled(true);
				}

				this.tooltip = I18n.format("wdl.gui.wdl." + key + ".description");
			}

			@Override
			public void drawEntry(int x, int y, int width, int height, int mouseX, int mouseY) {
				super.drawEntry(x, y, width, height, mouseX, mouseY);
				if (button.isMouseOver()) {
					displayedTooltip = tooltip;
				}
			}
		}

		{
			List<ButtonEntry> entries = getEntries();

			entries.add(new ButtonEntry("worldOverrides", GuiWDLWorld::new, true));
			entries.add(new ButtonEntry("generatorOverrides", GuiWDLGenerator::new, true));
			entries.add(new ButtonEntry("playerOverrides", GuiWDLPlayer::new, true));
			entries.add(new ButtonEntry("entityOptions", GuiWDLEntities::new, true));
			entries.add(new ButtonEntry("gameruleOptions", GuiWDLGameRules::new, true));
			entries.add(new ButtonEntry("backupOptions", GuiWDLBackup::new, true));
			entries.add(new ButtonEntry("messageOptions", GuiWDLMessages::new, false));
			entries.add(new ButtonEntry("savedChunks", GuiSavedChunks::new, true));
			entries.add(new ButtonEntry("about", GuiWDLAbout::new, false));
			if (WDLUpdateChecker.hasNewVersion()) {
				// Put at start
				entries.add(0, new ButtonEntry("updates.hasNew", GuiWDLUpdates::new, false));
			} else {
				entries.add(new ButtonEntry("updates", GuiWDLUpdates::new, false));
			}
		}
	}


	private final GuiScreen parent;
	private final WDL wdl;
	private final IConfiguration config;

	private WDLTextField worldname;
	private boolean isServer;

	public GuiWDL( GuiScreen parent,  WDL wdl) {
		super(new ChatComponentTranslation("wdl.gui.wdl.title", WDL.baseFolderName));
		this.parent = parent;
		this.wdl = wdl;
		this.config = WDL.serverProps;
		this.isServer = getIsServer();
	}

	public boolean getIsServer() {
		if (wdl == null)
			return false;

		try {
			return (wdl != null && wdl.minecraft.getCurrentServerData() != null);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@Override
	public void initGui() {
		// Done button
		this.addButton(new ButtonDisplayGui(this.width / 2 - 100, this.height - 29,
				200, 20, parent));
			
		if (isServer)
			initGuiServer();
	}

	public void initGuiServer() {
		this.worldname = this.addTextField(new WDLTextField(this.fontRendererObj,
				this.width / 2 - 155, 19, 150, 18, new ChatComponentTranslation("wdl.gui.wdl.worldname")));
		this.worldname.setText(this.config.getValue(MiscSettings.SERVER_NAME));

		this.addList(new GuiWDLButtonList());
	}

	@Override
	public void onGuiClosed() {
		if (this.worldname != null) {
			// Check to see if the server name matches the default, and clear the
			// setting if so, such that changing the name of the server will be
			// reflected in it.
			if (this.worldname.getText().equals(MiscSettings.SERVER_NAME.getDefault(this.config))) {
				this.config.clearValue(MiscSettings.SERVER_NAME);
			} else {
				this.config.setValue(MiscSettings.SERVER_NAME, this.worldname.getText());
			}
		}
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();

		displayedTooltip = null;

		super.drawScreen(mouseX, mouseY, partialTicks);

		if (this.worldname != null)
			drawScreenServer();
		else 
			drawScreenElse();
	}

	public void drawScreenServer() {
		String name = I18n.format("wdl.gui.wdl.worldname");
		this.drawString(this.fontRendererObj, name, this.worldname.xPosition
				- this.fontRendererObj.getStringWidth(name + " "), 26, 0xFFFFFF);

		Utils.drawGuiInfoBox(displayedTooltip, width, height, 48);
	}

	public void drawScreenElse() {
		String[] strings = {
				"Looks like you're trying to access the WorldDownloader config",
				"outside of a server.",
				"Please use this config only when connected to a server."
		};
		for (int i = 0; i < strings.length; i++) {
			String text = strings[i];
			this.drawCenteredString(
					this.fontRendererObj,
					text,
					this.width / 2,
					((this.height / 2) - 40 + (15 * i)),
					0xFFFFFF);
		}
	}
}

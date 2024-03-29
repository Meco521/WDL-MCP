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

import mcp.wdl.WDL;
import mcp.wdl.config.IConfiguration;
import mcp.wdl.config.settings.PlayerSettings;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentTranslation;
import mcp.wdl.gui.widget.ButtonDisplayGui;
import mcp.wdl.gui.widget.GuiNumericTextField;
import mcp.wdl.gui.widget.SettingButton;
import mcp.wdl.gui.widget.WDLButton;
import mcp.wdl.gui.widget.WDLScreen;

public class GuiWDLPlayer extends WDLScreen {
	private final GuiScreen parent;
	private final WDL wdl;
	private final IConfiguration config;
	private SettingButton healthBtn;
	private SettingButton hungerBtn;
	private SettingButton playerPosBtn;
	private WDLButton pickPosBtn;
	private boolean showPosFields = false;
	private GuiNumericTextField posX;
	private GuiNumericTextField posY;
	private GuiNumericTextField posZ;
	private int posTextY;

	public GuiWDLPlayer(GuiScreen parent, WDL wdl) {
		super(new ChatComponentTranslation("wdl.gui.player.title", WDL.baseFolderName));
		this.parent = parent;
		this.wdl = wdl;
		this.config = wdl.worldProps;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@Override
	public void initGui() {
		int y = this.height / 4 - 15;
		this.healthBtn = this.addButton(new SettingButton(
				PlayerSettings.HEALTH, this.config, this.width / 2 - 100, y));
		y += 22;
		this.hungerBtn = this.addButton(new SettingButton(
				PlayerSettings.HUNGER, this.config, this.width / 2 - 100, y));
		y += 22;
		this.playerPosBtn = this.addButton(new SettingButton(
				PlayerSettings.PLAYER_POSITION, this.config, this.width / 2 - 100, y) {
			public @Override void performAction() {
				super.performAction();
				upadatePlayerPosVisibility();
			}
		});
		y += 22;
		this.posTextY = y + 4;
		this.posX = this.addTextField(new GuiNumericTextField(this.fontRendererObj,
				this.width / 2 - 87, y, 50, 16,
				new ChatComponentTranslation("wdl.gui.player.position.coord", "X")));
		this.posY = this.addTextField(new GuiNumericTextField(this.fontRendererObj,
				this.width / 2 - 19, y, 50, 16,
				new ChatComponentTranslation("wdl.gui.player.position.coord", "Y")));
		this.posZ = this.addTextField(new GuiNumericTextField(this.fontRendererObj,
				this.width / 2 + 48, y, 50, 16,
				new ChatComponentTranslation("wdl.gui.player.position.coord", "Z")));
		this.posX.setValue(config.getValue(PlayerSettings.PLAYER_X));
		this.posY.setValue(config.getValue(PlayerSettings.PLAYER_Y));
		this.posZ.setValue(config.getValue(PlayerSettings.PLAYER_Z));
		this.posX.setMaxStringLength(7);
		this.posY.setMaxStringLength(7);
		this.posZ.setMaxStringLength(7);
		y += 18;
		this.pickPosBtn = this.addButton(new WDLButton(
				this.width / 2 - 0, y, 100, 20,
				I18n.format("wdl.gui.player.setPositionToCurrentPosition")) {
			public @Override void performAction() {
				setPlayerPosToPlayerPosition();
			}
		});

		upadatePlayerPosVisibility();

		this.addButton(new ButtonDisplayGui(this.width / 2 - 100, this.height - 29,
				200, 20, this.parent));
	}

	@Override
	public void onGuiClosed() {
		if (this.showPosFields) {
			this.config.setValue(PlayerSettings.PLAYER_X, posX.getValue());
			this.config.setValue(PlayerSettings.PLAYER_Y, posY.getValue());
			this.config.setValue(PlayerSettings.PLAYER_Z, posZ.getValue());
		}

		wdl.saveProps();
	}

	/**
	 * Draws the screen and all the components in it.
	 */
        @Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		Utils.drawListBackground(23, 32, 0, 0, height, width);

		String tooltip = null;

		if (this.showPosFields) {
			this.drawString(this.fontRendererObj, "X:", this.width / 2 - 99,
					this.posTextY, 0xFFFFFF);
			this.drawString(this.fontRendererObj, "Y:", this.width / 2 - 31,
					this.posTextY, 0xFFFFFF);
			this.drawString(this.fontRendererObj, "Z:", this.width / 2 + 37,
					this.posTextY, 0xFFFFFF);

			if (posX.isMouseOver()) {
				tooltip = I18n.format("wdl.gui.player.positionTextBox.description", "X");
			} else if (posY.isMouseOver()) {
				tooltip = I18n.format("wdl.gui.player.positionTextBox.description", "Y");
			} else if (posZ.isMouseOver()) {
				tooltip = I18n.format("wdl.gui.player.positionTextBox.description", "Z");
			}

			if (pickPosBtn.isMouseOver()) {
				tooltip = I18n.format("wdl.gui.player.setPositionToCurrentPosition.description");
			}
		}

		if (healthBtn.isMouseOver()) {
			tooltip = healthBtn.getTooltip();
		}
		if (hungerBtn.isMouseOver()) {
			tooltip = hungerBtn.getTooltip();
		}
		if (playerPosBtn.isMouseOver()) {
			tooltip = playerPosBtn.getTooltip();
		}

		super.drawScreen(mouseX, mouseY, partialTicks);

		if (tooltip != null) {
			Utils.drawGuiInfoBox(tooltip, width, height, 48);
		}
	}

	private void upadatePlayerPosVisibility() {
		boolean show = config.getValue(PlayerSettings.PLAYER_POSITION) == PlayerSettings.PlayerPos.XYZ;

		showPosFields = show;
		posX.setVisible(show);
		posY.setVisible(show);
		posZ.setVisible(show);
		pickPosBtn.visible = showPosFields;
	}

	private void setPlayerPosToPlayerPosition() {
		this.posX.setValue((int)wdl.player.posX);
		this.posY.setValue((int)wdl.player.posY);
		this.posZ.setValue((int)wdl.player.posZ);
	}
}

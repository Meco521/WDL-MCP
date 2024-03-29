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
import mcp.wdl.config.settings.GeneratorSettings;
import mcp.wdl.functions.GeneratorFunctions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import mcp.wdl.gui.widget.ButtonDisplayGui;
import mcp.wdl.gui.widget.WDLScreen;
import mcp.wdl.gui.widget.WDLTextField;
import mcp.wdl.gui.widget.SettingButton;
import mcp.wdl.gui.widget.WDLButton;

public class GuiWDLGenerator extends WDLScreen {
	private final GuiScreen parent;
	private final WDL wdl;
	private final IConfiguration config;
	private WDLTextField seedField;
	private SettingButton generatorBtn;
	private SettingButton generateStructuresBtn;
	private WDLButton settingsPageBtn;

	private String seedText;

	public GuiWDLGenerator(GuiScreen parent, WDL wdl) {
		super(new ChatComponentTranslation("wdl.gui.generator.title", WDL.baseFolderName));
		this.parent = parent;
		this.wdl = wdl;
		this.config = wdl.worldProps;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@Override
	public void initGui() {
		this.seedText = I18n.format("wdl.gui.generator.seed");
		int seedWidth = fontRendererObj.getStringWidth(seedText + " ");

		int y = this.height / 4 - 15;
		this.seedField = this.addTextField(new WDLTextField(this.fontRendererObj,
				this.width / 2 - (100 - seedWidth), y, 200 - seedWidth, 18,
				new ChatComponentText(this.seedText)));
		this.seedField.setText(config.getValue(GeneratorSettings.SEED));
		y += 22;
		this.generatorBtn = this.addButton(new SettingButton(
				GeneratorSettings.GENERATOR, this.config, this.width / 2 - 100, y) {
			public @Override void performAction() {
				super.performAction();
				updateSettingsButtonVisibility();
				// Clear any existing custom values, as they don't apply to another generator.
				config.clearValue(GeneratorSettings.GENERATOR_NAME);
				config.clearValue(GeneratorSettings.GENERATOR_VERSION);
				config.clearValue(GeneratorSettings.GENERATOR_OPTIONS);
			}
		});
		y += 22;
		this.generateStructuresBtn = this.addButton(new SettingButton(
				GeneratorSettings.GENERATE_STRUCTURES, this.config, this.width / 2 - 100, y));
		y += 22;
		this.settingsPageBtn = this.addButton(new ButtonDisplayGui(
				this.width / 2 - 100, y, 200, 20,
				"", this::makeGeneratorSettingsGui));
		updateSettingsButtonVisibility();

		this.addButton(new ButtonDisplayGui(this.width / 2 - 100, height - 29,
				200, 20, this.parent));
	}

	/**
	 * Gets the proxy GUI to use for the current settings.
	 */
	private GuiScreen makeGeneratorSettingsGui() {
		GeneratorSettings.Generator generator = config.getValue(GeneratorSettings.GENERATOR);
		String generatorConfig = config.getValue(GeneratorSettings.GENERATOR_OPTIONS);
		return GeneratorFunctions.makeGeneratorSettingsGui(generator, this, generatorConfig,
				value -> config.setValue(GeneratorSettings.GENERATOR_OPTIONS, value));
	}

	@Override
	public void onGuiClosed() {
		config.setValue(GeneratorSettings.SEED, this.seedField.getText());

		wdl.saveProps();
	}

	/**
	 * Draws the screen and all the components in it.
	 */
        @Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		Utils.drawListBackground(23, 32, 0, 0, height, width);

		this.drawString(this.fontRendererObj, seedText, this.width / 2 - 100,
				this.height / 4 - 10, 0xFFFFFF);

		super.drawScreen(mouseX, mouseY, partialTicks);

		String tooltip = null;

		if (seedField.isMouseOver()) {
			tooltip = I18n.format("wdl.gui.generator.seed.description");
		} else if (generatorBtn.isMouseOver()) {
			tooltip = generatorBtn.getTooltip();
		} else if (generateStructuresBtn.isMouseOver()) {
			tooltip = generateStructuresBtn.getTooltip();
		}
		Utils.drawGuiInfoBox(tooltip, width, height, 48);
	}

	/**
	 * Updates whether the {@link #settingsPageBtn} is shown or hidden, and
	 * the text on it.
	 */
	private void updateSettingsButtonVisibility() {
		switch (this.config.getValue(GeneratorSettings.GENERATOR)) {
		case GeneratorSettings.Generator.FLAT:
			settingsPageBtn.visible = true;
			settingsPageBtn.setMessage(I18n.format("wdl.gui.generator.flatSettings"));
			break;
		case GeneratorSettings.Generator.CUSTOMIZED:
			settingsPageBtn.visible = true;
			settingsPageBtn.setMessage(I18n.format("wdl.gui.generator.customSettings"));
			break;
		case GeneratorSettings.Generator.BUFFET:
			settingsPageBtn.visible = true;
			settingsPageBtn.setMessage(I18n.format("wdl.gui.generator.buffetSettings"));
			break;
		default:
			settingsPageBtn.visible = false;
		}
	}
}

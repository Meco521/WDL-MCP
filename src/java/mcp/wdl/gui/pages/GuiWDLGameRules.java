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
import java.util.Map;

import mcp.wdl.WDL;
import mcp.wdl.functions.GameRuleFunctions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.GameRules;
import mcp.wdl.gui.widget.ButtonDisplayGui;
import mcp.wdl.gui.widget.GuiList;
import mcp.wdl.gui.widget.GuiNumericTextField;
import mcp.wdl.gui.widget.WDLButton;
import mcp.wdl.gui.widget.WDLScreen;

public class GuiWDLGameRules extends WDLScreen {
	/**
	 * Text to draw (set from inner classes)
	 */
	private String hoveredToolTip;

	/**
	 * Colors for the text field on numeric entries when there is/is not a
	 * modified rule.
	 */
	private static final int SET_TEXT_FIELD = 0xE0E0E0, DEFAULT_TEXT_FIELD = 0x808080;

	private class GuiGameRuleList extends GuiList<GuiGameRuleList.RuleEntry> {
		/** The entry that was last clicked.  This should be compared by ref. */
		private RuleEntry lastClickedEntry = null;

		public GuiGameRuleList() {
			super(GuiWDLGameRules.this.mc, GuiWDLGameRules.this.width,
					GuiWDLGameRules.this.height, 39,
					GuiWDLGameRules.this.height - 32, 24);
			List<RuleEntry> entries = this.getEntries();
			for (String rule : vanillaGameRules.keySet()) {
				GameRuleFunctions.GameRuleType type = GameRuleFunctions.getRuleType(rules, rule);
				if (type == null) continue;
				switch (type) {
				case INTEGER:
					entries.add(new IntRuleEntry(rule));
					break;
				case BOOLEAN:
					entries.add(new BooleanRuleEntry(rule));
					break;
				}
			}
		}

		private abstract class RuleEntry extends GuiListEntry<RuleEntry> {
			protected final String ruleName;
			private WDLButton resetButton;

			public RuleEntry(String ruleName) {
				this.ruleName = ruleName;
				resetButton = this.addButton(new WDLButton(0, 0, 50, 20,
						I18n.format("wdl.gui.gamerules.resetRule")) {
					public @Override void performAction() {
						performResetAction();
					}
				}, 110, 0);
			}

			@Override
			public void drawEntry(int x, int y, int width, int height, int mouseX, int mouseY) {
				this.resetButton.setEnabled(isRuleNonDefault(this.ruleName));

				super.drawEntry(x, y, width, height, mouseX, mouseY);

				drawString(fontRendererObj, this.ruleName, x, y + 6, 0xFFFFFFFF);

				if (this.isControlHovered()) {
					String key = "wdl.gui.gamerules.rules." + ruleName;
					// if (I18n.hasKey(key)) { // may return false for mods
					// 	hoveredToolTip = I18n.format(key);
					// }
					hoveredToolTip = key;
				}
			}

			@Override
			public boolean mouseDown(int mouseX, int mouseY, int mouseButton) {
				lastClickedEntry = this;
				return super.mouseDown(mouseX, mouseY, mouseButton);
			}

			protected abstract boolean isControlHovered();

			@Override
			public boolean isSelected() {
				return lastClickedEntry == this;
			}

			/** Called when the reset button is clicked. */
			protected void performResetAction() {
				clearRule(this.ruleName);
			}
		}

		private class IntRuleEntry extends RuleEntry {
			private GuiNumericTextField field;

			public IntRuleEntry(String ruleName) {
				super(ruleName);
				field = this.addTextField(new GuiNumericTextField(
						fontRendererObj, 0, 0, 100, 20,
						new ChatComponentTranslation("wdl.gui.gamerules.ruleValue", ruleName)),
						0, 0);
				field.setText(getRule(ruleName));
			}

			@Override
			public void drawEntry(int x, int y, int width, int height, int mouseX, int mouseY) {
				super.drawEntry(x, y, width, height, mouseX, mouseY);
				if (!this.isSelected()) {
					// field.setFocused2(false);
					field.setFocused(false);
				}
				if (isRuleNonDefault(this.ruleName)) {
					field.setTextColor(SET_TEXT_FIELD);
				} else {
					field.setTextColor(DEFAULT_TEXT_FIELD);
				}
			}

			@Override
			public void anyKeyPressed() {
				if (this.field.isFocused()) {
					setRule(ruleName, Integer.toString(this.field.getValue()));
				}
			}

			@Override
			protected boolean isControlHovered() {
				return field.isMouseOver();
			}

			@Override
			protected void performResetAction() {
				super.performResetAction();
				this.field.setText(getRule(this.ruleName)); // Reset field text to default
			}
		}

		private class BooleanRuleEntry extends RuleEntry {
			private WDLButton button;

			public BooleanRuleEntry(String ruleName) {
				super(ruleName);
				button = this.addButton(new WDLButton(0, 0, 100, 20, "") {
					public @Override void performAction() {
						boolean oldValue = getRule(ruleName).equals("true");
						setRule(ruleName, oldValue ? "false" : "true");
					}
				}, 0, 0);
			}

			@Override
			public void drawEntry(int x, int y, int width, int height, int mouseX, int mouseY) {
				this.button.setMessage(getRule(ruleName));
				super.drawEntry(x, y, width, height, mouseX, mouseY);
			}

			@Override
			protected boolean isControlHovered() {
				return button.isMouseOver();
			}
		}

		@Override
		public int getEntryWidth() {
			return 210 * 2;
		}
	}

	private final GuiScreen parent;
	private final WDL wdl;

	/** The gamerules object to modify */
	private final GameRules rules;
	/** All vanilla game rules and their default values; this list is immutable. */
	private final Map<String, String> vanillaGameRules;

	private WDLButton doneButton;

	/**
	 * Gets the value of the given rule.
	 *
	 * @param ruleName
	 *            The name of the rule.
	 * @return The rule's string value. Will be null if no such rule exists.
	 */
	private String getRule(String ruleName) {
		return GameRuleFunctions.getRuleValue(rules, ruleName);
	}

	/**
	 * Updates the value of the given rule.
	 *
	 * @param ruleName
	 *            The name of the rule.
	 * @param value
	 *            The new value. Must not be null; to clear a rule, use
	 *            {@link #clearRule(String)}.
	 */
	private void setRule(String ruleName, String value) {
		GameRuleFunctions.setRuleValue(rules, ruleName, value);
	}

	/**
	 * Returns the given rule to its default value.
	 *
	 * @param ruleName
	 *            The name of the rule.
	 */
	private void clearRule(String ruleName) {
		setRule(ruleName, vanillaGameRules.get(ruleName));
	}

	/**
	 * Checks if the given rule is overridden.
	 *
	 * @param ruleName
	 *            The name of the rule.
	 * @return True if the rule is overriden; false otherwise (not overriden or
	 *         no such rule exists).
	 */
	private boolean isRuleNonDefault(String ruleName) {
		return !vanillaGameRules.get(ruleName).equals(getRule(ruleName));
	}

	public GuiWDLGameRules(GuiScreen parent, WDL wdl) {
		super("wdl.gui.gamerules.title");
		this.parent = parent;
		this.wdl = wdl;
		this.rules = wdl.gameRules;
		// We're not currently modifying the rules on worldClient itself, so they can be considered
		// to be the defaults... probably.
		GameRules defaultRules = this.wdl.worldClient.getGameRules();
		this.vanillaGameRules = GameRuleFunctions.getGameRules(defaultRules);
	}

	@Override
	public void initGui() {
		this.addList(new GuiGameRuleList());

		this.doneButton = this.addButton(new ButtonDisplayGui(this.width / 2 - 100,
				this.height - 29, 200, 20, this.parent));
	}

        @Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		hoveredToolTip = null;

		super.drawScreen(mouseX, mouseY, partialTicks);

		if (this.doneButton.isMouseOver()) {
			Utils.drawGuiInfoBox(I18n.format("wdl.gui.gamerules.doneInfo"),
					width, height, 48);
		} else if (hoveredToolTip != null) {
			Utils.drawGuiInfoBox(hoveredToolTip, width, height, 48);
		}
	}

	@Override
	public void onGuiClosed() {
		// Can't save anywhere until the download actually occurs...
	}
}

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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mcp.wdl.WDL;
import mcp.wdl.config.Configuration;
import mcp.wdl.config.IConfiguration;
import mcp.wdl.config.settings.MiscSettings;
import mcp.wdl.gui.widget.WDLButton;
import mcp.wdl.gui.widget.WDLTextField;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentTranslation;

/**
 * A GUI for selecting which world the player is currently in.
 *
 * TODO: I might want to move the multiworld setup logic elsewhere.
 */
public class GuiWDLMultiworldSelect extends GuiTurningCameraBase {
	private class WorldGuiButton extends WDLButton {
		private final int buttonOffset;
		public WorldGuiButton(int buttonOffset, int x, int y, int width, int height) {
			super(x, y, width, height, "");
			this.buttonOffset = buttonOffset;
		}

		@Override
		public void beforeDraw() {
			MultiworldInfo info = getWorldInfo();
			if (info == null) {
				setMessage(""); // XXX
				setEnabled(false);
			} else {
				setMessage(info.displayName);
				setEnabled(true);
			}

			if (info != null && info == selectedMultiWorld) {
				drawRect(this.xPosition - 2, this.yPosition - 2,
						this.xPosition + width + 2, this.yPosition + height + 2,
						0xFF007F00);
			}
		}

		/**
		 * Gets the world folder name marked by this button.
		 *
		 * @return the world info, or null if there isn't any.
		 */
		public MultiworldInfo getWorldInfo() {
			int location = index + buttonOffset;
			if (location < 0) {
				return null;
			}
			if (location >= linkedWorldsFiltered.size()) {
				return null;
			}

			return linkedWorldsFiltered.get(location);
		}

		@Override
		public void performAction() {
			selectedMultiWorld = this.getWorldInfo();
			acceptBtn.setEnabled(selectedMultiWorld != null);
		}
	}

	/**
	 * Info for a Multiworld.
	 *
	 * TODO: I may want to move this elsewhere.
	 */
	private class MultiworldInfo {
		public MultiworldInfo(String folderName, String displayName) {
			this.folderName = folderName;
			this.displayName = displayName;
		}

		public final String folderName;
		public final String displayName;

		private List<String> description;

		/**
		 * Gets some information about this info.
		 */
		public List<String> getDescription() {
			if (description == null) {
				description = new ArrayList<>();

				// TODO: More info than just dimensions - EG if the
				// chunk the player is in is added, etc.
				description.add("Defined dimensions:");
				File savesFolder = new File(wdl.minecraft.mcDataDir, "saves");
				File world = new File(savesFolder, WDL.getWorldFolderName(folderName));
				File[] subfolders = world.listFiles();

				if (subfolders != null) {
					for (File subfolder : subfolders) {
						if (subfolder.listFiles() == null) {
							// Not a folder
							continue;
						}
						if (subfolder.listFiles().length == 0) {
							// Empty folder - we don't count these.
							continue;
						}
						if (subfolder.getName().equals("region")) {
							description.add(" * Overworld (#0)");
						} else if (subfolder.getName().startsWith("DIM")) {
							String dimension = subfolder.getName().substring(3);
							if (dimension.equals("-1")) {
								description.add(" * Nether (#-1)");
							} else if (dimension.equals("1")) {
								description.add(" * The End (#1)");
							} else {
								description.add(" * #" + dimension);
							}
						}
					}
				}
			}

			return description;
		}
	}

	/**
	 * Interface for a listener when the "cancel" or "use this world" buttons
	 * are clicked.
	 *
	 * Note that implementations must <i>also</i> handle opening the correct
	 * next GUI.
	 */
	public static interface WorldSelectionCallback {
		/**
		 * Called when the cancel button is clicked.
		 */
		public abstract void onCancel();

		/**
		 * Called when the "Use this world" button is clicked.
		 *
		 * @param selectedWorld
		 *            The folder name for the given world.
		 */
		public abstract void onWorldSelected(String selectedWorld);
	}

	private final WDL wdl;

	/**
	 * The callback for when the done button is clicked.
	 */
	private final WorldSelectionCallback callback;
	/**
	 * The accept button.
	 */
	private WDLButton acceptBtn;
	/**
	 * The "New name" field.
	 */
	private WDLTextField newNameField;
	/**
	 * The "Search" field.  Allows filtering.
	 */
	private WDLTextField searchField;
	/**
	 * The "New world" button.
	 */
	private WDLButton newWorldButton;
	/**
	 * Should the {@link #newNameField} be shown?
	 */
	private boolean showNewWorldTextBox;
	/**
	 * Worlds linked together for the given server.
	 */
	private List<MultiworldInfo> linkedWorlds;
	/**
	 * List of worlds to display.
	 */
	private List<MultiworldInfo> linkedWorldsFiltered;
	/**
	 * The currently selected multiworld.
	 */
	private MultiworldInfo selectedMultiWorld;
	/**
	 * Scrolling index.
	 */
	private int index = 0;
	/**
	 * The next button (scrolls the list right).
	 */
	private WDLButton nextButton;
	/**
	 * The previous button (scrolls the list left).
	 */
	private WDLButton prevButton;
	/**
	 * The number of world buttons there are.
	 */
	private int numWorldButtons;
	/**
	 * Text currently being searched for.
	 */
	private String searchText = "";

	public GuiWDLMultiworldSelect(WDL wdl, IChatComponent title, WorldSelectionCallback callback) {
		super(wdl, title);

		this.wdl = wdl;
		this.callback = callback;

		// Build a list of world names.
		String[] worldNames = WDL.serverProps.getValue(MiscSettings.LINKED_WORLDS)
				.split("\\|");
		linkedWorlds = new ArrayList<>();

		for (String worldName : worldNames) {
			if (worldName == null || worldName.isEmpty()) {
				continue;
			}

			IConfiguration props = wdl.loadWorldProps(worldName);

			String displayName = props.getValue(MiscSettings.WORLD_NAME);
			if (displayName.isEmpty()) {
				continue;
			}

			linkedWorlds.add(new MultiworldInfo(worldName, displayName));
		}

		linkedWorldsFiltered = new ArrayList<>();
		linkedWorldsFiltered.addAll(linkedWorlds);
	}

	@Override
	public void initGui() {
		super.initGui();

		numWorldButtons = (this.width - 50) / 155;

		if (numWorldButtons < 1) {
			numWorldButtons = 1;
		}

		int offset = (numWorldButtons * 155 + 45) / 2;
		int y = this.height - 49;

		this.addButton(new WDLButton(this.width / 2 - 155, this.height - 25, 150, 20,
				I18n.format("gui.cancel")) {
			public @Override void performAction() {
				callback.onCancel();
			}
		});

		this.acceptBtn = this.addButton(new WDLButton(
				this.width / 2 + 5, this.height - 25, 150, 20,
				I18n.format("wdl.gui.multiworldSelect.done")) {
			public @Override void performAction() {
				callback.onWorldSelected(selectedMultiWorld.folderName);
			}
		});
		this.acceptBtn.setEnabled(selectedMultiWorld != null);

		prevButton = this.addButton(new WDLButton(this.width / 2 - offset, y, 20, 20, "<") {
			public @Override void performAction() {
				index--;
			}
		});

		for (int i = 0; i < numWorldButtons; i++) {
			this.addButton(new WorldGuiButton(i, this.width / 2 - offset
					+ i * 155 + 25, y, 150, 20));
		}

		nextButton = this.addButton(new WDLButton(
				this.width / 2 - offset + 25 + numWorldButtons * 155,
				y, 20, 20, ">") {
			public @Override void performAction() {
				index++;
			}
		});

		this.newWorldButton = this.addButton(new WDLButton(
				this.width / 2 - 155, 29, 150, 20,
				I18n.format("wdl.gui.multiworldSelect.newName")) {
			public @Override void performAction() {
				showNewWorldTextBox = true;
			}
		});

		this.newNameField = this.addTextField(new WDLTextField(this.fontRendererObj,
				this.width / 2 - 155, 29, 150, 20,
				new ChatComponentTranslation("wdl.gui.multiworldSelect.newName")));

		this.searchField = this.addTextField(new WDLTextField(this.fontRendererObj,
				this.width / 2 + 5, 29, 150, 20,
				new ChatComponentTranslation("wdl.gui.multiworldSelect.search")));
		this.searchField.setText(searchText);
	}

	@Override
	public boolean onCloseAttempt() {
		callback.onCancel();
		return true;
	}

	@Override
	public void charTyped(char keyChar) {
		if (this.newNameField.isFocused() && keyChar == '\n') {
			String newName = this.newNameField.getText();

			if (newName != null && !newName.isEmpty()) {
				//TODO: Ensure that the new world is in view.
				this.addMultiworld(newName);
				this.newNameField.setText("");
				this.showNewWorldTextBox = false;
			}
		}
	}

	@Override
	public void anyKeyPressed() {
		if (this.searchField.isFocused()) {
			this.searchText = searchField.getText();
			rebuildFilteredWorlds();
		}
	}

	/**
	 * Draws the screen and all the components in it.
	 */
        @Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();

		if (index >= linkedWorlds.size() - numWorldButtons) {
			index = linkedWorlds.size() - numWorldButtons;
			nextButton.setEnabled(false);
		} else {
			nextButton.setEnabled(true);
		}
		if (index <= 0) {
			index = 0;
			prevButton.setEnabled(false);
		} else {
			prevButton.setEnabled(true);
		}

		Utils.drawBorder(53, 53, 0, 0, height, width);

		this.drawCenteredString(this.fontRendererObj,
				I18n.format("wdl.gui.multiworldSelect.subtitle"),
				this.width / 2, 18, 0xFF0000);

		newWorldButton.visible = !showNewWorldTextBox;
		this.newNameField.setVisible(showNewWorldTextBox);

		super.drawScreen(mouseX, mouseY, partialTicks);

		//Hint as to what the text box does
		if (this.searchField.getText().isEmpty() && !this.searchField.isFocused()) {
			drawString(fontRendererObj,
					I18n.format("wdl.gui.multiworldSelect.search"),
					searchField.xPosition + 4, searchField.yPosition + 6,
					0x909090);
		}

		drawMultiworldDescription();
	}

	/**
	 * Creates a new multiworld, and adds it to the list.
	 */
	private void addMultiworld(String worldName) {
		String folderName = worldName;
		char[] unsafeChars = "\\/:*?\"<>|.".toCharArray();

		//TODO: ReplaceAll with a regular expression may be cleaner
		for (char unsafeChar : unsafeChars) {
			folderName = folderName.replace(unsafeChar, '_');
		}

		IConfiguration worldProps = new Configuration(WDL.serverProps);
		worldProps.setValue(MiscSettings.WORLD_NAME, worldName);

		String linkedWorldsProp = WDL.serverProps.getValue(MiscSettings.LINKED_WORLDS);
		linkedWorldsProp += "|" + folderName;

		WDL.serverProps.setValue(MiscSettings.LINKED_WORLDS, linkedWorldsProp);
		wdl.saveProps(folderName, worldProps);

		linkedWorlds.add(new MultiworldInfo(folderName, worldName));

		rebuildFilteredWorlds();
	}

	/**
	 * Rebuilds the {@link #linkedWorldsFiltered} list after a change
	 * has occurred to the search or the {@link #linkedWorlds} list.
	 */
	private void rebuildFilteredWorlds() {
		String searchFilter = searchText.toLowerCase();
		linkedWorldsFiltered.clear();
		for (MultiworldInfo info : linkedWorlds) {
			if (info.displayName.toLowerCase().contains(searchFilter)) {
				linkedWorldsFiltered.add(info);
			}
		}

	}

	/**
	 * Draws info about the selected world.
	 */
	private void drawMultiworldDescription() {
		if (selectedMultiWorld == null) {
			return;
		}

		String title = "Info about "
				+ selectedMultiWorld.displayName;
		List<String> description = selectedMultiWorld.getDescription();

		int maxWidth = fontRendererObj.getStringWidth(title);
		for (String line : description) {
			int width = fontRendererObj.getStringWidth(line);
			if (width > maxWidth) {
				maxWidth = width;
			}
		}

		Gui.drawRect(2, 61, 5 + maxWidth + 3, height - 61, 0x80000000);

		drawString(fontRendererObj, title, 5, 64, 0xFFFFFF);

		int y = 64 + fontRendererObj.FONT_HEIGHT;
		for (String s : description) {
			drawString(fontRendererObj, s, 5, y, 0xFFFFFF);
			y += fontRendererObj.FONT_HEIGHT;
		}
	}
}

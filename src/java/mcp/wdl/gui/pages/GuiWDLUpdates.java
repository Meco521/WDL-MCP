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

import java.util.List;

import mcp.wdl.VersionConstants;
import mcp.wdl.WDL;
import mcp.wdl.functions.GuiFunctions;
import mcp.wdl.update.Release;
import mcp.wdl.update.WDLUpdateChecker;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
// import net.minecraft.init.SoundEvents;
import net.minecraft.util.ChatComponentText;
import mcp.wdl.gui.widget.WDLButton;
import mcp.wdl.gui.widget.ButtonDisplayGui;
import mcp.wdl.gui.widget.GuiList;
import mcp.wdl.gui.widget.WDLScreen;
import mcp.wdl.gui.widget.TextList;

/**
 * Gui that lists updates fetched via {@link wdl.update.GithubInfoGrabber}.
 */
public class GuiWDLUpdates extends WDLScreen {
	private final GuiScreen parent;

	/**
	 * Margins for the top and the bottom of the list.
	 */
	private static final int TOP_MARGIN = 39, BOTTOM_MARGIN = 32;

	private class UpdateList extends GuiList<UpdateList.VersionEntry> {
		public UpdateList() {
			super(GuiWDLUpdates.this.mc, GuiWDLUpdates.this.width,
					GuiWDLUpdates.this.height, TOP_MARGIN,
					GuiWDLUpdates.this.height - BOTTOM_MARGIN,
					(fontRendererObj.FONT_HEIGHT + 1) * 6 + 2);
		}

		private class VersionEntry extends GuiListEntry<VersionEntry> {
			private final Release release;

			private String title;
			private String caption;
			private String body1;
			private String body2;
			private String body3;
			private String time;

			private final int fontHeight;
			private int y;

			public VersionEntry(Release release) {
				this.release = release;
				this.fontHeight = fontRendererObj.FONT_HEIGHT + 1;

				this.title = buildReleaseTitle(release);
				List<String> body = Utils.wordWrap(release.textOnlyBody, getListWidth());

				body1 = (body.size() >= 1 ? body.get(0) : "");
				body2 = (body.size() >= 2 ? body.get(1) : "");
				body3 = (body.size() >= 3 ? body.get(2) : "");

				time = I18n.format("wdl.gui.updates.update.releaseDate", release.date);
			}

			@Override
			public void drawEntry(int x, int y, int entryWidth, int entryHeight,
					int mouseX, int mouseY) {
				this.y = y;

				String title;
				//The 'isSelected' parameter is actually 'isHovered'
				if (this.isSelected()) {
					title = I18n.format("wdl.gui.updates.currentVersion",
							this.title);
				} else if (this.release == recomendedRelease) {
					title = I18n.format("wdl.gui.updates.recomendedVersion",
							this.title);
				} else {
					title = this.title;
				}

				drawString(fontRendererObj, title, x, y + fontHeight * 0, 0xFFFFFF);
				drawString(fontRendererObj, caption, x, y + fontHeight * 1, 0x808080);
				drawString(fontRendererObj, body1, x, y + fontHeight * 2, 0xFFFFFF);
				drawString(fontRendererObj, body2, x, y + fontHeight * 3, 0xFFFFFF);
				drawString(fontRendererObj, body3, x, y + fontHeight * 4, 0xFFFFFF);
				drawString(fontRendererObj, time, x, y + fontHeight * 5, 0x808080);

				if (mouseX > x && mouseX < x + entryWidth && mouseY > y
						&& mouseY < y + slotHeight) {
					drawRect(x - 2, y - 2, x + entryWidth - 3, y + slotHeight + 2,
							0x1FFFFFFF);
				}
			}

			@Override
			public boolean mouseDown(int x, int y, int mouseButton) {
				if (y > this.y && y < this.y + slotHeight) {
					mc.displayGuiScreen(new GuiWDLSingleUpdate(GuiWDLUpdates.this,
							this.release));

					// mc.getSoundHandler().play(SimpleSound.master(
					// 		SoundEvents.UI_BUTTON_CLICK, 1.0f));
					return true;
				}
				return false;
			}

			@Override
			public void mouseUp(int x, int y, int mouseButton) { }

			@Override
			public boolean isSelected() {
				String currentTag = "v" + VersionConstants.getModFullVersion();
				return currentTag.equals(this.release.tag);
			}
		}

		/**
		 * Release that should be used.
		 */
		private Release recomendedRelease;

		/**
		 * Regenerates the {@linkplain #displayedVersions version list}.
		 *
		 * TODO: This is probably a bit laggy; cache this data?  Right now it's
		 * being called each frame.
		 */
		private void regenerateVersionList() {
			getEntries().clear();

			if (WDLUpdateChecker.hasNewVersion()) {
				recomendedRelease = WDLUpdateChecker.getRecomendedRelease();
			} else {
				recomendedRelease = null;
			}

			List<Release> releases = WDLUpdateChecker.getReleases();

			if (releases == null) {
				return;
			}

			releases.stream().map(VersionEntry::new).forEachOrdered(getEntries()::add);
		}

		@Override
		public int getEntryWidth() {
			return width - 30;
		}

		@Override
		public int getScrollBarX() {
			return width - 10;
		}
	}

	private UpdateList list;

	public GuiWDLUpdates(GuiScreen parent, WDL wdl) {
		super("wdl.gui.updates.title");
		this.parent = parent;
	}

	@Override
	public void initGui() {
		this.list = this.addList(new UpdateList());

		this.addButton(new ButtonDisplayGui(this.width / 2 - 100, this.height - 29,
				200, 20, this.parent));
	}

	@Override
	public void onGuiClosed() {
		WDL.saveGlobalProps();
	}

        @Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.list.regenerateVersionList();

		super.drawScreen(mouseX, mouseY, partialTicks);

		if (!WDLUpdateChecker.hasFinishedUpdateCheck()) {
			drawCenteredString(fontRendererObj,
					I18n.format("wdl.gui.updates.pleaseWait"), width / 2,
					height / 2, 0xFFFFFF);
		} else if (WDLUpdateChecker.hasUpdateCheckFailed()) {
			String reason = WDLUpdateChecker.getUpdateCheckFailReason();

			drawCenteredString(fontRendererObj,
					I18n.format("wdl.gui.updates.checkFailed"), width / 2,
					height / 2 - fontRendererObj.FONT_HEIGHT / 2, 0xFF5555);
			drawCenteredString(fontRendererObj, I18n.format(reason), width / 2,
					height / 2 + fontRendererObj.FONT_HEIGHT / 2, 0xFF5555);
		}
	}

	/**
	 * Gets the translated title for the given release.
	 */
	private String buildReleaseTitle(Release release) {
		String version = release.tag;

		if (release.prerelease) {
			return I18n.format("wdl.gui.updates.update.title.prerelease", version, "1.8.9");
		} else {
			return I18n.format("wdl.gui.updates.update.title.release", version, "1.8.9");
		}
	}

	/**
	 * Gui that shows a single update.
	 */
	private class GuiWDLSingleUpdate extends WDLScreen {
		private final GuiWDLUpdates parent;
		private final Release release;

		public GuiWDLSingleUpdate(GuiWDLUpdates parent, Release releaseToShow) {
			super(new ChatComponentText(buildReleaseTitle(releaseToShow))); // Already translated
			this.parent = parent;
			this.release = releaseToShow;
		}

		@Override
		public void initGui() {
			this.addButton(new WDLButton(
					this.width / 2 - 155, 18, 150, 20,
					I18n.format("wdl.gui.updates.update.viewOnline")) {
				public @Override void performAction() {
					GuiFunctions.openLink(release.URL);
				}
			});

			this.addButton(new ButtonDisplayGui(
					this.width / 2 - 100, this.height - 29, 200, 20, this.parent));

			TextList list = new TextList(mc, width, height, TOP_MARGIN, BOTTOM_MARGIN);

			list.addLine(buildReleaseTitle(release));
			list.addLine(I18n.format("wdl.gui.updates.update.releaseDate", release.date));
			list.addBlankLine();
			list.addLine(release.textOnlyBody);

			this.addList(list);
		}
	}
}

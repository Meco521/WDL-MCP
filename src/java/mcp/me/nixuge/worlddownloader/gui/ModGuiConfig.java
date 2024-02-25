package mcp.me.nixuge.worlddownloader.gui;

import mcp.me.nixuge.worlddownloader.McMod;
import mcp.wdl.gui.pages.GuiWDL;
import net.minecraft.client.gui.GuiScreen;

public class ModGuiConfig extends GuiWDL {
    public ModGuiConfig(final GuiScreen guiScreen) {
        super(guiScreen, McMod.wdl);
    }
}
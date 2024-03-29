package mcp.wdl.gui.notifications.shapes.rectangle;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import mcp.wdl.gui.notifications.shapes.base.Shape;
import mcp.wdl.gui.notifications.shapes.base.ShapeNoDraw;

public abstract class RectangleShape extends Shape implements ShapeNoDraw {
    @Override
    public void drawToggleAttribs(int xOffset) {
        toggleOnAttribs();
        
        // System.out.println("drawing");
        draw(xOffset);

        toggleOffAttribs();
    }

    @Override
    public void toggleOnAttribs() {
        GlStateManager.pushAttrib();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void toggleOffAttribs() {
        GlStateManager.popAttrib();
    }
}

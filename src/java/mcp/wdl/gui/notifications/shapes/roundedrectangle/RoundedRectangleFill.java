package mcp.wdl.gui.notifications.shapes.roundedrectangle;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import mcp.wdl.gui.notifications.shapes.base.ShapeContainer;
import mcp.wdl.gui.notifications.shapes.builders.RectangleBuilder;
import mcp.wdl.gui.notifications.shapes.data.BorderPosition;
import mcp.wdl.gui.notifications.shapes.data.CornerType;
import mcp.wdl.gui.notifications.shapes.data.Position;
import mcp.wdl.gui.notifications.shapes.rectangle.RectangleBorder;
import mcp.wdl.gui.notifications.shapes.rectangle.RectangleFill;
import mcp.wdl.gui.notifications.shapes.roundedcorner.RoundedCornerFill;

public class RoundedRectangleFill extends ShapeContainer {
    private RoundedCornerFill[] corners;
    private RectangleFill mainRectangle;
    private RectangleFill[] sideRectangles = new RectangleFill[2];
    private Map<CornerType, RectangleFill> straightCorners = new HashMap<>();
    
    private RectangleBorder rectangleBorder;

    private int radius;

    public RoundedRectangleFill(Position position, int radius, int color, CornerType[] enabledCorners) {
        super();
        this.radius = radius;

        // Create rounded corners
        this.corners = new RoundedCornerFill[enabledCorners.length];
        for(int i = 0; i < enabledCorners.length; i++) {
            corners[i] = new RoundedCornerFill(enabledCorners[i], null, radius, color);
        }
        // Create map w square corners
        for (CornerType cornerType : CornerType.getOtherCorners(enabledCorners)) {
            straightCorners.put(cornerType, new RectangleFill(null, color));
        }

        // Create rectangle spanning from top to bottom in the middle
        mainRectangle = new RectangleFill(null, color);

        // Create side rectangles
        sideRectangles[0] = new RectangleFill(null, color);
        sideRectangles[1] = new RectangleFill(null, color);

        Map<BorderPosition, Float> enabledBorders = new HashMap<>();
        enabledBorders.put(BorderPosition.BOTTOM, 2f);
        enabledBorders.put(BorderPosition.LEFT, 6f);

        this.rectangleBorder = new RectangleBuilder()
            .setEnabledBorders(enabledBorders)
            .setColor(0x33FFFF55)
            .buildBorder();


        // Finally, set the position of the rect
        setPosition(position);
    }

    //NOTE:
    //THIS ISN'T WORKING AS OF NOW!
    @Override
    public void draw(int xOffset) {
        for (int i = 0; i < corners.length; i++) {
            corners[i].draw(xOffset);
        }

        mainRectangle.draw(xOffset);

        sideRectangles[0].draw(xOffset);
        sideRectangles[1].draw(xOffset);

        for (RectangleFill rectangle : straightCorners.values()) {
            rectangle.draw(xOffset);
        }
    }
    
    @Override
    public void drawToggleAttribs(int xOffset) {
        GlStateManager.pushAttrib();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);
        

        for (int i = 0; i < corners.length; i++) {
            corners[i].draw(xOffset);
        }

        mainRectangle.draw(xOffset);
        rectangleBorder.draw(xOffset);

        sideRectangles[0].draw(xOffset);
        sideRectangles[1].draw(xOffset);

        for (RectangleFill rectangle : straightCorners.values()) {
            rectangle.draw(xOffset);
        }

        GlStateManager.popAttrib();
    }

    @Override
    public void setPosition(Position position) {
        if (position == null)
            return;
        
        if (this.radius * 2 > position.right() - position.left())
            System.out.println("2*radius is bigger than the whole rectangle width");
        if (this.radius * 2 > position.top() - position.bottom())
            System.out.println("2*radius is bigger than the whole rectangle height");

        this.position = position;

        // Update corners positions
        for(int i = 0; i < corners.length; i++) {
            RoundedCornerFill currentCorner = corners[i];
            currentCorner.setPosition(currentCorner.getCornerType().getFixedPositionRounded(position, radius));
        }

        // Update main rectangle position
        mainRectangle.setPosition(new Position(position.left() + radius, position.top(), position.right() - radius, position.bottom()));
        // Update side rectangles positions
        sideRectangles[0].setPosition(new Position(position.left(), position.top() + radius, position.left() + radius, position.bottom() - radius));
        sideRectangles[1].setPosition(new Position(position.right() - radius, position.top() + radius, position.right(), position.bottom() - radius));
        // Update straight corners
        for (Entry<CornerType, RectangleFill> entry : straightCorners.entrySet()) {
            entry.getValue().setPosition(
                entry.getKey().getRectanglePosition(position, radius));
        }
        rectangleBorder.setPosition(new Position(position.left() + radius, position.top(), position.right() - radius, position.bottom()));

    }
}

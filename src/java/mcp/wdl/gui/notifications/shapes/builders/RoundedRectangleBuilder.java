package mcp.wdl.gui.notifications.shapes.builders;

import mcp.wdl.gui.notifications.shapes.roundedrectangle.RoundedRectangleFill;
import mcp.wdl.gui.notifications.shapes.data.CornerType;
// import wdl.gui.notifications.shapes.roundedrectangle.RoundedRectangleBorder;


public class RoundedRectangleBuilder extends BaseBuilder<RoundedRectangleBuilder> {
    CornerType[] enabledCorners;

    public RoundedRectangleBuilder setEnabledCorners(CornerType[] enabledCorners) {
        this.enabledCorners = enabledCorners;
        return this;
    }

    public RoundedRectangleFill buildFill() {
        return new RoundedRectangleFill(this.position, this.radius, this.color, this.enabledCorners);
    }

    // public RoundedRectangleBorder buildRectangleBorder() {        
    //     return new RoundedRectangleBorder(this.position, this.radius, this.color, this.enabledCorners, this.borderWidth);
    // }
}

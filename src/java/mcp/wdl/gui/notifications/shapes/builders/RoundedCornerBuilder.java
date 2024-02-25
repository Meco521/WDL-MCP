package mcp.wdl.gui.notifications.shapes.builders;

import mcp.wdl.gui.notifications.shapes.roundedcorner.RoundedCornerBorder;
import mcp.wdl.gui.notifications.shapes.roundedcorner.RoundedCornerFill;
import mcp.wdl.gui.notifications.shapes.data.CornerType;

public class RoundedCornerBuilder extends BaseBuilder<RoundedCornerBuilder> {
    CornerType cornerType;

    public RoundedCornerBuilder setCornerType(CornerType cornerType) {
        this.cornerType = cornerType;
        return this;
    }

    public RoundedCornerFill buildFill() {
        return new RoundedCornerFill(this.cornerType, this.position, this.radius, this.color);
    }

    public RoundedCornerBorder buildBorder() {
        return new RoundedCornerBorder(this.cornerType, this.position, this.radius, this.color, 2);
    }
}

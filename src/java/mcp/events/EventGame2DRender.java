package mcp.events;

public class EventGame2DRender {
    private final float partialTicks;

    public EventGame2DRender(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}

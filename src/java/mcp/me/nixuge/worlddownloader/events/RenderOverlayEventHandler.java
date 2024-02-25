package mcp.me.nixuge.worlddownloader.events;

import dev.mmcb.api.event.component.EventTarget;
import dev.mmcb.api.event.handler.EventManager;
import mcp.events.EventGame2DRender;
import mcp.wdl.gui.notifications.NotificationManager;

public class RenderOverlayEventHandler {
    public NotificationManager notificationManager;

    public RenderOverlayEventHandler() {
        this.notificationManager = NotificationManager.getInstance();
        EventManager.register(this);
    }

    @EventTarget
    public void onRenderGameOverlay(EventGame2DRender event) {
        notificationManager.draw(event.getPartialTicks());
    }
}
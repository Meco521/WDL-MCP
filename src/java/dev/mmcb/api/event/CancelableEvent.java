/**
 * @author Aq1u
 * @date 2/22/2024
 */
package dev.mmcb.api.event;

public class CancelableEvent {
    private boolean cancel;

    public void cancelEvent() {
        this.cancel = true;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public boolean isCancel() {
        return cancel;
    }
}

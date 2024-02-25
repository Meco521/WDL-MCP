/**
 * @author Aq1u
 * @date 2/22/2024
 */
package dev.mmcb.api.event.component;


public enum Priority {
    Highest(0),
    High(1),
    Normal(2),
    Low(3),
    Lowest(4);

    private final int value;

    Priority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

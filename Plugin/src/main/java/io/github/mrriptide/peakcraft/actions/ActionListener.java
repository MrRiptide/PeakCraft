package io.github.mrriptide.peakcraft.actions;

public interface ActionListener {

    default PriorityLevel getListeningLevel() {
        return PriorityLevel.MIDDLE;
    }

    boolean listensTo(Action action);

    void onAction(Action action);

    enum PriorityLevel{
        FIRST, MIDDLE, LAST
    }
}

package io.github.mrriptide.peakcraft.actions;

public interface ActionListener {

    PriorityLevel getListeningLevel();

    boolean listensTo(Action action);

    void onAction(Action action);

    enum PriorityLevel{
        FIRST, MIDDLE, LAST
    }
}

package io.github.mrriptide.peakcraft.actions;

import io.github.mrriptide.peakcraft.entity.wrappers.LivingEntityWrapper;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Action {
    protected ArrayList<ActionListener> firstListeners = new ArrayList<>();
    protected ArrayList<ActionListener> middleListeners = new ArrayList<>();
    protected ArrayList<ActionListener> lastListeners = new ArrayList<>();
    protected LivingEntityWrapper primaryEntity;

    public Action(LivingEntityWrapper primaryEntity){
        this.primaryEntity = primaryEntity;
        this.primaryEntity.resetAttributes();
        this.primaryEntity.registerListeners(this);
    }

    public void registerListener(ActionListener listener){
        if (listener.listensTo(this)){
            switch (listener.getListeningLevel()){
                case FIRST -> firstListeners.add(listener);
                case MIDDLE -> middleListeners.add(listener);
                case LAST -> lastListeners.add(listener);
            }
        }
    }

    public void runAction(){
        for (ArrayList<ActionListener> listeners : Arrays.asList(firstListeners, middleListeners, lastListeners)){
            for (ActionListener action : listeners){
                action.onAction(this);
            }
        }
        finalizeAction();
    }

    protected abstract void finalizeAction();

    public LivingEntityWrapper getPrimaryEntity() {
        return primaryEntity;
    }
}

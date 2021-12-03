package io.github.mrriptide.peakcraft.actions;

import io.github.mrriptide.peakcraft.entity.wrappers.LivingEntityWrapper;

public class RightClickAction extends Action{
    public RightClickAction(LivingEntityWrapper primaryEntity) {
        super(primaryEntity);
    }

    @Override
    protected void finalizeAction() {
        // I dont think you need to do anything
    }
}

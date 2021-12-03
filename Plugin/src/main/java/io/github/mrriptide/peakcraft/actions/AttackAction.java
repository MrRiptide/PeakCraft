package io.github.mrriptide.peakcraft.actions;

import io.github.mrriptide.peakcraft.entity.player.PlayerWrapper;
import io.github.mrriptide.peakcraft.entity.wrappers.CombatEntityWrapper;
import io.github.mrriptide.peakcraft.entity.wrappers.LivingEntityWrapper;
import io.github.mrriptide.peakcraft.util.CustomColors;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AttackAction extends Action {
    protected DamagedAction damagedAction;

    public AttackAction(LivingEntityWrapper primaryEntity, EntityDamageEvent.DamageCause cause, CombatEntityWrapper secondaryEntity){
        super(secondaryEntity);
        this.damagedAction = new DamagedAction(primaryEntity, cause, 0);
        secondaryEntity.registerListeners(this);
        List<ArrayList<ActionListener>> primaryListeners = Arrays.asList(damagedAction.firstListeners, damagedAction.middleListeners, damagedAction.lastListeners);
        List<ArrayList<ActionListener>> secondaryListeners = Arrays.asList(this.firstListeners, this.middleListeners, this.lastListeners);
        for (int i = 0; i < 3; i++){
            secondaryListeners.get(i).addAll(primaryListeners.get(i));
        } // this is a really bad way of doing this wow
    }

    @Override
    public void finalizeAction() {
        if (this.getPrimaryEntity() instanceof PlayerWrapper){
            double multiplier = 0;
            // if the player is on full cooldown, do the random chance to
            if ((((PlayerWrapper) getPrimaryEntity()).getSource()).getAttackCooldown() == 1.0 &&
                    Math.random() <= ((PlayerWrapper)getPrimaryEntity()).getCritChance().getFinal()){
                multiplier = ((PlayerWrapper)getPrimaryEntity()).getCritDamage().getFinal();
                this.damagedAction.setDamageColor(CustomColors.CRIT_ATTACK);
            } else {
                multiplier = (((PlayerWrapper) getPrimaryEntity()).getSource()).getAttackCooldown() - 1;
                this.damagedAction.setDamageColor(CustomColors.WEAK_ATTACK);
            }
            this.damagedAction.getDamage().getDamage(Damage.DamageType.GENERIC).addMulti(multiplier);
        }

        this.damagedAction.finalizeAction();
    }

    public EntityDamageEvent.DamageCause getCause() {
        return this.damagedAction.getCause();
    }

    public Damage getDamage() {
        return this.damagedAction.getDamage();
    }

    public DamagedAction getDamagedAction() {
        return damagedAction;
    }
}

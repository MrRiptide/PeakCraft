package io.github.mrriptide.peakcraft.actions;

import io.github.mrriptide.peakcraft.entity.player.PlayerWrapper;
import io.github.mrriptide.peakcraft.entity.wrappers.CombatEntityWrapper;
import io.github.mrriptide.peakcraft.entity.wrappers.LivingEntityWrapper;
import io.github.mrriptide.peakcraft.items.EnchantableItem;
import io.github.mrriptide.peakcraft.util.CustomColors;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class AttackAction extends Action {
    protected DamagedAction damagedAction;

    public AttackAction(LivingEntityWrapper primaryEntity, EntityDamageEvent.DamageCause cause, CombatEntityWrapper secondaryEntity){
        super(secondaryEntity);
        this.damagedAction = new DamagedAction(primaryEntity, cause,
                (secondaryEntity.getWeapon() == null
                        || secondaryEntity.getWeapon().getId().equalsIgnoreCase("air")
                        || !(secondaryEntity.getWeapon() instanceof EnchantableItem)) ? 10 :
                        ((EnchantableItem)secondaryEntity.getWeapon()).getAttribute("damage").getFinal(),
        //        (secondaryEntity.getWeapon() instanceof WeaponItem) ? ((WeaponItem) secondaryEntity.getWeapon()).getAttributeOrDefault("damage", 10).getFinal() : 10,
                        secondaryEntity);
        /*List<ArrayList<ActionListener>> primaryListeners = Arrays.asList(damagedAction.firstListeners, damagedAction.middleListeners, damagedAction.lastListeners);
        List<ArrayList<ActionListener>> secondaryListeners = Arrays.asList(this.firstListeners, this.middleListeners, this.lastListeners);
        for (int i = 0; i < 3; i++){
            secondaryListeners.get(i).addAll(primaryListeners.get(i));
        } // this is a really bad way of doing this wow*/
    }

    @Override
    public void runAction(){
        // this is so cursed i need to find a new and better way to do this
        int i = 0;
        Action[] actions = {this, damagedAction};
        for (ArrayList<ActionListener> listeners : Arrays.asList(
                firstListeners, damagedAction.firstListeners,
                middleListeners, damagedAction.middleListeners,
                lastListeners, damagedAction.lastListeners)){
            for (ActionListener action : listeners){
                action.onAction(actions[i % 2]);
            }
            i++;
        }
        finalizeAction();
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

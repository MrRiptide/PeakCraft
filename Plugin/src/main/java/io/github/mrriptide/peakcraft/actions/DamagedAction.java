package io.github.mrriptide.peakcraft.actions;

import io.github.mrriptide.peakcraft.entity.wrappers.LivingEntityWrapper;
import io.github.mrriptide.peakcraft.util.CustomColors;
import io.github.mrriptide.peakcraft.util.HoloDisplay;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.entity.EntityDamageEvent;

import java.text.NumberFormat;

public class DamagedAction extends Action {
    protected EntityDamageEvent.DamageCause cause;
    protected ChatColor damageColor = CustomColors.NORMAL_ATTACK;
    protected Damage damage;

    public DamagedAction(LivingEntityWrapper primaryEntity, EntityDamageEvent.DamageCause cause, double amount){
        super(primaryEntity);
        this.cause = cause;
        this.damage = new Damage(amount, cause);
    }

    @Override
    public void finalizeAction() {
        HoloDisplay damageDisplay = new HoloDisplay(getPrimaryEntity().getEntity().getLocation().add(Math.random()*1-0.5, Math.random()*1-1.5, Math.random()*1 -0.5));

        NumberFormat format = NumberFormat.getInstance();
        format.setGroupingUsed(true);

        double damagePotential = damage.finalizeDamage();

        damageDisplay.showThenDie(damageColor + "" + format.format((int)damagePotential), 40);

        primaryEntity.damage(damagePotential);
    }

    public void setDamageColor(ChatColor color){
        this.damageColor = color;
    }

    public EntityDamageEvent.DamageCause getCause() {
        return cause;
    }

    public Damage getDamage() {
        return damage;
    }
}

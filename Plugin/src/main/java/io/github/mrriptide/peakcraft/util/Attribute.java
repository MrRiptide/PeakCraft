package io.github.mrriptide.peakcraft.util;

public class Attribute {
    private double base;
    private double additive;
    private double multi;

    public Attribute(double base){
        this(base, 0);
    }

    public Attribute(double base, double additive){
        this(base, additive, 0);
    }

    public Attribute(double base, double additive, double multi){
        this.base = base;
        this.additive = additive;
        this.multi = multi;
    }

    public double getFinal(){
        return (base+additive)*(1+multi);
    }

    public void reset() {
        this.additive = 0;
        this.multi = 0;
    }

    public void addMulti(double increment){
        this.multi += increment;
    }

    public void addAdditive(double increment){
        this.additive += increment;
    }

    public double getBase() {
        return base;
    }

    public void setBase(double base) {
        this.base = base;
    }

    public double getAdditive() {
        return additive;
    }

    public void setAdditive(double additive) {
        this.additive = additive;
    }

    public double getMulti() {
        return multi;
    }

    public void setMulti(double multi) {
        this.multi = multi;
    }
}

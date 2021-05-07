package io.github.mrriptide.peakcraft.items;

import io.github.mrriptide.peakcraft.PeakCraft;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;

import java.awt.*;
import java.beans.*;

public class ItemBeanInfo implements BeanInfo {
    @Override
    public BeanDescriptor getBeanDescriptor() {
        return new BeanDescriptor(Item.class);
    }

    @Override
    public EventSetDescriptor[] getEventSetDescriptors() {
        return new EventSetDescriptor[0];
    }

    @Override
    public int getDefaultEventIndex() {
        return 0;
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
            String[] easyFields = new String[] {"id", "oreDict", "displayName", "rarity", "type", "description"};
            PropertyDescriptor[] descriptors = new PropertyDescriptor[easyFields.length + 1];
            for (int i = 0; i < easyFields.length; i++){
                descriptors[i] = new PropertyDescriptor(easyFields[i], Item.class);
            }
            descriptors[easyFields.length] = new PropertyDescriptor("material", Item.class, "getMaterialStr", "setMaterialFromStr");
            return descriptors;
        } catch (IntrospectionException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getDefaultPropertyIndex() {
        return 6;
    }

    @Override
    public MethodDescriptor[] getMethodDescriptors() {
        return null;
    }

    @Override
    public BeanInfo[] getAdditionalBeanInfo() {
        return null;
    }

    @Override
    public Image getIcon(int iconKind) {
        return null;
    }
}

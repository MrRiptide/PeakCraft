package io.github.mrriptide.peakcraft.util;

import org.apache.commons.lang.WordUtils;

public abstract class Formatter {
    public static String formatCommas(double number){
        return formatCommas((int)number);
    }

    public static String formatCommas(int number){
        return "";
    }

    public static String humanize(String orig){
        return WordUtils.capitalizeFully(orig.toLowerCase().replace("_", " "));
    }
}

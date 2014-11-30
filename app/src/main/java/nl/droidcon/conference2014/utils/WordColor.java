package nl.droidcon.conference2014.utils;

import android.graphics.Color;

/**
 * Utility class to generate a color similar to the one from the web using a word
 * Created by Arnaud on 29/05/2014.
 */
public class WordColor {

    private static final float SATURATION = 0.55f;
    private static final float LIGHTNESS = 0.8f;

    /**
     * Simplified version with default Saturation and Lightness
     * @param word the word we want to find a color.
     * @return the color
     */
    public static int generateColor(String word) {
        return generateColor(word, SATURATION, LIGHTNESS);
    }

    /**
     * Generate a color based on a word
     * @param word the word we want to find a color.
     * @param saturation the saturation (from 0 to 1)
     * @param lightness the lightness (from 0 to 1)
     * @return the color
     */
    public static int generateColor(String word, float saturation, float lightness) {
        int ordinal = 0;
        float hue, sum = 0f;
        int wordLength = word.length();
        word = word.toUpperCase().charAt(0) + word.substring(1).toLowerCase();

        for (char c: word.toCharArray()) {
            ordinal = (int)c;
            sum += Math.cos(ordinal);
        }
        hue = Math.abs((float)Math.sin(sum * (wordLength / 1.2)));

        float[] hsv = new float[] { hue*360, saturation, lightness};
        return Color.HSVToColor(hsv);
    }

}

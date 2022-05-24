package cc.sleek.client.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Random;


/**
 * @author Kansio
 */
public class MathUtil {

    public static Random rng;

    public static double round(final double value, final int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    public static double getRandomInRange(double min, double max) {
        Random random = new Random();
        double range = max - min;
        double scaled = random.nextDouble() * range;
        if (scaled > max) {
            scaled = max;
        }
        double shifted = scaled + min;

        if (shifted > max) {
            shifted = max;
        }
        return shifted;
    }

    public static float getRandomInRange(float min, float max) {
        Random random = new Random();
        float range = max - min;
        float scaled = random.nextFloat() * range;
        float shifted = scaled + min;
        return shifted;
    }

    public static int getRandomInRange(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public double doRound(final double d, final int r) {
        String round = "#";
        for (int i = 0; i < r; ++i) {
            round = round + ".#";
        }
        final DecimalFormat twoDForm = new DecimalFormat(round);
        return Double.parseDouble(twoDForm.format(d));
    }

    public static int getMiddle(final int i, final int i2) {
        return (i + i2) / 2;
    }

    public static Random getRng() {
        return MathUtil.rng;
    }

    public static int getNumberFor(final int start, final int end) {
        if (end >= start) {
            return 0;
        }
        if (end - start < 0) {
            return 0;
        }
        return end - start;
    }

    public static double roundToPlace(final double value, final int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static float getRandom() {
        return MathUtil.rng.nextFloat();
    }

    public static int getRandom(final int cap) {
        return MathUtil.rng.nextInt(cap);
    }

    public static int getRandom(final int floor, final int cap) {
        return floor + MathUtil.rng.nextInt(cap - floor + 1);
    }
    public static double getRandomf(final double min, final double max) {
        return min + MathUtil.rng.nextDouble() * (max - min + 1.0);
    }

    public static double getMiddleDouble(final int i, final int i2) {
        return (i + i2) / 2.0;
    }

    public static int toTicks(int seconds) {
        return seconds * 20;
    }
    public static int toTicks(long millis) {
        return (int) (millis / 50L);
    }

    static {
        MathUtil.rng = new Random();
    }

}

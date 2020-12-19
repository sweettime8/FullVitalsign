package com.elcom.vitalsign.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author anhdv
 */
public class NumberUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(NumberUtil.class);

    public static List<Integer> generateRandomArray(int n) {
        ArrayList<Integer> list = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            list.add( generateIntegerFromRange(-100, 200) );
        }
        return list;
    }

    public static double generateDoubleFromRange(double rangeMin, double rangeMax) {
        try {
            return Math.round((rangeMin + (rangeMax - rangeMin) * new Random().nextDouble()) * 100.0) / 100.0;
        } catch (Exception ex) {
            LOGGER.error(ex.toString());
        }
        return 0.0;
    }

    public static int generateIntegerFromRange(int rangeMin, int rangeMax) {
        try {
            return ThreadLocalRandom.current().nextInt(rangeMin, rangeMax + 1);
        } catch (Exception ex) {
            LOGGER.error(ex.toString());
        }
        return 0;
    }
}

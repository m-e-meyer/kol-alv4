/* Copyright (c) 2020-2020, developers of the Ascension Log Visualizer
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom
 * the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.googlecode.alv.util;

import java.util.Map;

import com.googlecode.alv.logdata.Statgain;

/**
 * This enumeration represents the three stat classes, plus Maximum. Maximum is
 * for the Plumber, because his leveling is based on whichever stat is highest.
 */
public enum StatClass {
    MUSCLE("Muscularity Points"), 
    MYSTICALITY("Mana Points"), 
    MOXIE("Mojo Points"),
    MAXIMUM(Constants.N_A);

    /**
     * Table for associating substat names with stats
     */
    public static final Map<String, StatClass> SUBSTATS = Maps.immutableMapOf(
            Pair.of("Beefiness", StatClass.MUSCLE), 
            Pair.of("Fortitude", StatClass.MUSCLE),
            Pair.of("Muscleboundness", StatClass.MUSCLE),
            Pair.of("Strengthliness", StatClass.MUSCLE), 
            Pair.of("Strongness", StatClass.MUSCLE),
            Pair.of("Enchantedness", StatClass.MYSTICALITY),
            Pair.of("Magicalness", StatClass.MYSTICALITY),
            Pair.of("Mysteriousness", StatClass.MYSTICALITY),
            Pair.of("Wizardliness", StatClass.MYSTICALITY), 
            Pair.of("Cheek", StatClass.MOXIE),
            Pair.of("Chutzpah", StatClass.MOXIE), 
            Pair.of("Roguishness", StatClass.MOXIE),
            Pair.of("Sarcasm", StatClass.MOXIE), 
            Pair.of("Smarm", StatClass.MOXIE));

    /**
     * Given a substat name and an amount, return the appropriate Statgain.
     *
     * @param substat Name of a substat, such as Beefiness
     * @param amount  Number of substats
     * @return Object representing the stat gain
     */
    public static Statgain getStatgain(
            final String substat,
            final int amount) {

        switch (SUBSTATS.get(substat)) {
        case MUSCLE:
            return new Statgain(amount, 0, 0);
        case MYSTICALITY:
            return new Statgain(0, amount, 0);
        case MOXIE:
            return new Statgain(0, 0, amount);
        default:
            return Statgain.NO_STATS;
        }
    }

    private String pointsName;

    StatClass(
            final String pointsName) { this.pointsName = pointsName; }

    /**
     * @return The name for MP for a given StatClass
     */
    public String getPointsName() { return pointsName; }
}
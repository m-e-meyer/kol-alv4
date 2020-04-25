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

/**
 * This enumeration represents all known (to ALV) character classes.
 */
public enum CharacterClass {
    SEAL_CLUBBER("Seal Clubber", StatClass.MUSCLE, "clobber"),
    TURTLE_TAMER("Turtle Tamer", StatClass.MUSCLE, "toss"),
    PASTAMANCER("Pastamancer", StatClass.MYSTICALITY, "spaghetti spear"),
    SAUCEROR("Sauceror", StatClass.MYSTICALITY, "salsaball"),
    DISCO_BANDIT("Disco Bandit", StatClass.MOXIE, "suckerpunch"),
    ACCORDION_THIEF("Accordion Thief", StatClass.MOXIE, "sing"),
    AVATAR_OF_BORIS("Avatar of Boris", StatClass.MUSCLE, Constants.N_A),
    AVATAR_OF_JARLSBERG("Avatar of Jarlsberg", StatClass.MYSTICALITY, Constants.N_A),
    AVATAR_OF_SNEAKY_PETE("Avatar of Sneaky Pete", StatClass.MOXIE, Constants.N_A),
    ED("Ed", StatClass.MYSTICALITY, Constants.N_A),
    VAMPYRE("Vampyre", StatClass.MYSTICALITY, Constants.N_A),
    PLUMBER("Plumber", StatClass.MAXIMUM, Constants.N_A),    
    NOT_DEFINED("not defined", StatClass.MUSCLE, Constants.N_A);

    
    private static final Map<String, CharacterClass> stringToEnum = Maps.newHashMap();

    static {
        for (final CharacterClass op : values())
            stringToEnum.put(op.toString(), op);
    }

    private final String className;

    private final StatClass statClass;
    
    private final String trivialSkill;

    CharacterClass(final String className, 
                   final StatClass statClass,
                   final String trivialSkill) 
    {
        this.className = className;
        this.statClass = statClass;
        this.trivialSkill = trivialSkill;
    }

    /**
     * @return The mainstat of this character class.
     */
    public StatClass getStatClass() { return statClass; }
    
    /**
     * @return The trivial skill for this character class, or, if not applicable,
     *      {@link Constants.N_A}.
     */
    public String getTrivialSkill() { return trivialSkill; }

    @Override
    public String toString() 
    {
        return className;
    }

    /**
     * @param className The name of the adventurer's class
     * @return The enum whose toString method returns a string which is
     *         equal to the given string. If no match is found this method
     *         will return {@code NOT_DEFINED}.
     */
    public static CharacterClass fromString(final String className) 
    {
        if (className == null)
            throw new NullPointerException("Class name must not be null.");

        final CharacterClass characterClass = stringToEnum.get(className);

        return characterClass != null ? characterClass : NOT_DEFINED;
    }
}
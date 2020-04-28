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
 * 
 *
 */
public enum Counter
{
    // Note: Enum's natural ordering is the order listed here, so should list
    // them here by alphabetical order of name
    BASTILLE("Bastille", 1),
    BEACH_HEAD_COLD("Beach Head/Cold", 1),
    BEACH_HEAD_FAMILIAR_WT("Beach Head/Familiar Wt", 1),
    BEACH_HEAD_HOT("Beach Head/Hot", 1),
    BEACH_HEAD_INITIATIVE("Beach Head/Initiative", 1),
    BEACH_HEAD_MOXIE("Beach Head/Moxie", 1),
    BEACH_HEAD_MUSCLE("Beach Head/Muscle", 1),
    BEACH_HEAD_MYSTICALITY("Beach Head/Mysticality", 1),
    BEACH_HEAD_SLEAZE("Beach Head/Sleaze", 1),
    BEACH_HEAD_SPOOKY("Beach Head/Spooky", 1),
    BEACH_HEAD_STATS("Beach Head/Stats", 1),
    BEACH_HEAD_STENCH("Beach Head/Stench", 1),
    CHEAT_CODE("CHEAT CODE", 100),
    CLIP_ART("Clip Art", 3),
    DAYCARE_SPA("Daycare Spa", 1),
    DOCTOR_BAG_HAMMER("Doctor Bag/Hammer", 3),
    DOCTOR_BAG_OTOSCOPE("Doctor Bag/Otoscope", 3),
    DOCTOR_BAG_XRAY("Doctor Bag/X-ray", 3),
    FORTUNE_TELLER("Fortune Teller", 1),
    PILLKEEPER("Pillkeeper", 6),
    SABER_UPGRADE("Saber/Upgrade", 1),
    SABER_USE_FORCE("Saber/Use the Force", 5),
    VAMPYRIC_CLOAKE("Vampyric Cloake", 10)
    ;
    
    public static final String REPLACE_ENEMY = "Replace Enemy";
    
    private String name;
    private int limit;
    
    public int getLimit() { return limit; }
    public String getName() { return name; }
    
    private Counter(String name, int limit)
    {
        this.name = name;
        this.limit = limit;
    }
    
    public static Counter from(String s)
    {
        for (Counter c : Counter.values()) {
            if (c.getName().equalsIgnoreCase(s))
                return c;
        }
        return valueOf(s);
    }
    
    /**
     * Convenience map for using strings to look up their corresponding Counter
     * and name of counter use.
     */
    public static final Map<String, Pair<Counter, String>> LIMITED_USE_MAP
        = Maps.immutableMapOf(
                Pair.of("1387/1", 
                        Pair.of(Counter.SABER_USE_FORCE, "Not the adventurer")),
                Pair.of("1387/2", 
                        Pair.of(Counter.SABER_USE_FORCE, "Find friends")),
                Pair.of("1387/1", 
                        Pair.of(Counter.SABER_USE_FORCE, "Not the adventurer")),
                Pair.of("1387/2", 
                        Pair.of(Counter.SABER_USE_FORCE, "Find friends")),
                Pair.of("1387/3", 
                        Pair.of(Counter.SABER_USE_FORCE, "Drop things")),
                Pair.of("1386/1", 
                        Pair.of(Counter.SABER_UPGRADE, "MP regen")),
                Pair.of("1386/2", 
                        Pair.of(Counter.SABER_UPGRADE, "+20 ML")),
                Pair.of("1386/3", 
                        Pair.of(Counter.SABER_UPGRADE, "+3 prismatic res")),
                Pair.of("1386/4", 
                        Pair.of(Counter.SABER_UPGRADE, "+10 familiar wt")),
                Pair.of("1395/1", 
                        Pair.of(Counter.PILLKEEPER, "Explodinall")),
                Pair.of("1395/2", 
                        Pair.of(Counter.PILLKEEPER, "Extendicillin")),
                Pair.of("1395/3", 
                        Pair.of(Counter.PILLKEEPER, "Sneakisol")),
                Pair.of("1395/4", 
                        Pair.of(Counter.PILLKEEPER, "Rainbowolin")),
                Pair.of("1395/5", 
                        Pair.of(Counter.PILLKEEPER, "Hulkien")),
                Pair.of("1395/6", 
                        Pair.of(Counter.PILLKEEPER, "Fidoxene")),
                Pair.of("1395/7", 
                        Pair.of(Counter.PILLKEEPER, "Surprise Me")),
                Pair.of("1395/8", 
                        Pair.of(Counter.PILLKEEPER, "Telecybin")),
                Pair.of("cheat code: replace enemy", 
                        Pair.of(Counter.CHEAT_CODE, Counter.REPLACE_ENEMY)),
                Pair.of("cheat code: triple size", 
                        Pair.of(Counter.CHEAT_CODE, "Triple Size")),
                Pair.of("cheat code: invisible avatar", 
                        Pair.of(Counter.CHEAT_CODE, "Invisible Avatar")),
                Pair.of("cheat code: shrink enemy", 
                        Pair.of(Counter.CHEAT_CODE, "Shrink Enemy")),
                Pair.of("hot-headed", 
                        Pair.of(Counter.BEACH_HEAD_HOT, "")),
                Pair.of("cold as nice", 
                        Pair.of(Counter.BEACH_HEAD_COLD, "")),
                Pair.of("a brush with grossness", 
                        Pair.of(Counter.BEACH_HEAD_STENCH, "")),
                Pair.of("does it have a skull in there??", 
                        Pair.of(Counter.BEACH_HEAD_SPOOKY, "")),
                Pair.of("oiled, slick", 
                        Pair.of(Counter.BEACH_HEAD_SLEAZE, "")),
                Pair.of("lack of body-building", 
                        Pair.of(Counter.BEACH_HEAD_MUSCLE, "")),
                Pair.of("we're all made of starfish", 
                        Pair.of(Counter.BEACH_HEAD_MYSTICALITY, "")),
                Pair.of("pomp & circumsands", 
                        Pair.of(Counter.BEACH_HEAD_MOXIE, "")),
                Pair.of("resting beach face", 
                        Pair.of(Counter.BEACH_HEAD_INITIATIVE, "")),
                Pair.of("do i know you from somewhere?", 
                        Pair.of(Counter.BEACH_HEAD_FAMILIAR_WT, "")),
                Pair.of("you learned something maybe!", 
                        Pair.of(Counter.BEACH_HEAD_STATS, "")),
                Pair.of("reflex hammer", 
                        Pair.of(Counter.DOCTOR_BAG_HAMMER, "")),
                Pair.of("otoscope", 
                        Pair.of(Counter.DOCTOR_BAG_OTOSCOPE, "")),
                Pair.of("chest x-ray", 
                        Pair.of(Counter.DOCTOR_BAG_XRAY, "")),
                Pair.of("become a bat", 
                        Pair.of(Counter.VAMPYRIC_CLOAKE, "Bat")),
                Pair.of("become a wolf", 
                        Pair.of(Counter.VAMPYRIC_CLOAKE, "Wolf")),
                Pair.of("become a cloud of mist", 
                        Pair.of(Counter.VAMPYRIC_CLOAKE, "Mist")),
                Pair.of("summon clip art", 
                        Pair.of(Counter.CLIP_ART, ""))
                );
        //= new HashMap<String, Pair<Counter, String>>();
/*
    static {
        Pair.of("1387/1", 
                Pair.of(Counter.SABER_USE_FORCE, "Not the adventurer"));
        Pair.of("1387/2", 
                Pair.of(Counter.SABER_USE_FORCE, "Find friends"));
        Pair.of("1387/3", 
                Pair.of(Counter.SABER_USE_FORCE, "Drop things"));
        Pair.of("1386/1", 
                Pair.of(Counter.SABER_UPGRADE, "MP regen"));
        Pair.of("1386/2", 
                Pair.of(Counter.SABER_UPGRADE, "+20 ML"));
        Pair.of("1386/3", 
                Pair.of(Counter.SABER_UPGRADE, "+3 prismatic res"));
        Pair.of("1386/4", 
                Pair.of(Counter.SABER_UPGRADE, "+10 familiar wt"));
        Pair.of("1395/1", 
                Pair.of(Counter.PILLKEEPER, "Explodinall"));
        Pair.of("1395/2", 
                Pair.of(Counter.PILLKEEPER, "Extendicillin"));
        Pair.of("1395/3", 
                Pair.of(Counter.PILLKEEPER, "Sneakisol"));
        Pair.of("1395/4", 
                Pair.of(Counter.PILLKEEPER, "Rainbowolin"));
        Pair.of("1395/5", 
                Pair.of(Counter.PILLKEEPER, "Hulkien"));
        Pair.of("1395/6", 
                Pair.of(Counter.PILLKEEPER, "Fidoxene"));
        Pair.of("1395/7", 
                Pair.of(Counter.PILLKEEPER, "Surprise Me"));
        Pair.of("1395/8", 
                Pair.of(Counter.PILLKEEPER, "Telecybin"));
        Pair.of("cheat code: replace enemy", 
                Pair.of(Counter.CHEAT_CODE, Counter.REPLACE_ENEMY));
        Pair.of("cheat code: triple size", 
                Pair.of(Counter.CHEAT_CODE, "Triple Size"));
        Pair.of("cheat code: invisible avatar", 
                Pair.of(Counter.CHEAT_CODE, "Invisible Avatar"));
        Pair.of("cheat code: shrink enemy", 
                Pair.of(Counter.CHEAT_CODE, "Shrink Enemy"));
        Pair.of("hot-headed", 
                Pair.of(Counter.BEACH_HEAD_HOT, ""));
        Pair.of("cold as nice", 
                Pair.of(Counter.BEACH_HEAD_COLD, ""));
        Pair.of("a brush with grossness", 
                Pair.of(Counter.BEACH_HEAD_STENCH, ""));
        Pair.of("does it have a skull in there??", 
                Pair.of(Counter.BEACH_HEAD_SPOOKY, ""));
        Pair.of("oiled, slick", 
                Pair.of(Counter.BEACH_HEAD_SLEAZE, ""));
        Pair.of("lack of body-building", 
                Pair.of(Counter.BEACH_HEAD_MUSCLE, ""));
        Pair.of("we're all made of starfish", 
                Pair.of(Counter.BEACH_HEAD_MYSTICALITY, ""));
        Pair.of("pomp & circumsands", 
                Pair.of(Counter.BEACH_HEAD_MOXIE, ""));
        Pair.of("resting beach face", 
                Pair.of(Counter.BEACH_HEAD_INITIATIVE, ""));
        Pair.of("do i know you from somewhere?", 
                Pair.of(Counter.BEACH_HEAD_FAMILIAR_WT, ""));
        Pair.of("you learned something maybe!", 
                Pair.of(Counter.BEACH_HEAD_STATS, ""));
        Pair.of("reflex hammer", 
                Pair.of(Counter.DOCTOR_BAG_HAMMER, ""));
        Pair.of("otoscope", 
                Pair.of(Counter.DOCTOR_BAG_OTOSCOPE, ""));
        Pair.of("chest x-ray", 
                Pair.of(Counter.DOCTOR_BAG_XRAY, ""));
        Pair.of("become a bat", 
                Pair.of(Counter.VAMPYRIC_CLOAKE, "Bat"));
        Pair.of("become a wolf", 
                Pair.of(Counter.VAMPYRIC_CLOAKE, "Wolf"));
        Pair.of("become a cloud of mist", 
                Pair.of(Counter.VAMPYRIC_CLOAKE, "Mist"));
        Pair.of("summon clip art", 
                Pair.of(Counter.CLIP_ART, ""));
    }
*/
}
/* Copyright (c) 2008-2020, developers of the Ascension Log Visualizer
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

package com.googlecode.alv.logdata.summary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.googlecode.alv.logdata.Statgain;
import com.googlecode.alv.util.DataNumberPair;

/**
 * This class accumulates data on uses of items that have a limited number of uses
 * per day, such as the Cosplay Saber's Force and the Powerful Glove cheat codes.
 *
 */
public class LimitedUseData {
    
    /**
     * 
     *
     */
    public static enum Counter
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
    }
    
    public static class CounterUsePair 
    {
        public Counter counter;
        public String use;
        CounterUsePair(Counter c, String u)
        {
            counter = c;
            use = u;
        }
        
        public static CounterUsePair make(Counter c, String u)
        {
            return new CounterUsePair(c, u);
        }
    }
    
    /**
     * Convenience map for using strings to look up their corresponding Counter
     * and name of counter use.
     */
    public static final Map<String, CounterUsePair> LIMITED_USE_MAP
        = new HashMap<String, CounterUsePair>();

    static {
        LIMITED_USE_MAP.put("1387/1", 
                new CounterUsePair(Counter.SABER_USE_FORCE, "Not the adventurer"));
        LIMITED_USE_MAP.put("1387/2", 
                new CounterUsePair(Counter.SABER_USE_FORCE, "Find friends"));
        LIMITED_USE_MAP.put("1387/3", 
                new CounterUsePair(Counter.SABER_USE_FORCE, "Drop things"));
        LIMITED_USE_MAP.put("1386/1", 
                new CounterUsePair(Counter.SABER_UPGRADE, "MP regen"));
        LIMITED_USE_MAP.put("1386/2", 
                new CounterUsePair(Counter.SABER_UPGRADE, "+20 ML"));
        LIMITED_USE_MAP.put("1386/3", 
                new CounterUsePair(Counter.SABER_UPGRADE, "+3 prismatic res"));
        LIMITED_USE_MAP.put("1386/4", 
                new CounterUsePair(Counter.SABER_UPGRADE, "+10 familiar wt"));
        LIMITED_USE_MAP.put("1395/1", 
                new CounterUsePair(Counter.PILLKEEPER, "Explodinall"));
        LIMITED_USE_MAP.put("1395/2", 
                new CounterUsePair(Counter.PILLKEEPER, "Extendicillin"));
        LIMITED_USE_MAP.put("1395/3", 
                new CounterUsePair(Counter.PILLKEEPER, "Sneakisol"));
        LIMITED_USE_MAP.put("1395/4", 
                new CounterUsePair(Counter.PILLKEEPER, "Rainbowolin"));
        LIMITED_USE_MAP.put("1395/5", 
                new CounterUsePair(Counter.PILLKEEPER, "Hulkien"));
        LIMITED_USE_MAP.put("1395/6", 
                new CounterUsePair(Counter.PILLKEEPER, "Fidoxene"));
        LIMITED_USE_MAP.put("1395/7", 
                new CounterUsePair(Counter.PILLKEEPER, "Surprise Me"));
        LIMITED_USE_MAP.put("1395/8", 
                new CounterUsePair(Counter.PILLKEEPER, "Telecybin"));
        LIMITED_USE_MAP.put("cheat code: replace enemy", 
                new CounterUsePair(Counter.CHEAT_CODE, Counter.REPLACE_ENEMY));
        LIMITED_USE_MAP.put("cheat code: triple size", 
                new CounterUsePair(Counter.CHEAT_CODE, "Triple Size"));
        LIMITED_USE_MAP.put("cheat code: invisible avatar", 
                new CounterUsePair(Counter.CHEAT_CODE, "Invisible Avatar"));
        LIMITED_USE_MAP.put("cheat code: shrink enemy", 
                new CounterUsePair(Counter.CHEAT_CODE, "Shrink Enemy"));
        LIMITED_USE_MAP.put("hot-headed", 
                new CounterUsePair(Counter.BEACH_HEAD_HOT, ""));
        LIMITED_USE_MAP.put("cold as nice", 
                new CounterUsePair(Counter.BEACH_HEAD_COLD, ""));
        LIMITED_USE_MAP.put("a brush with grossness", 
                new CounterUsePair(Counter.BEACH_HEAD_STENCH, ""));
        LIMITED_USE_MAP.put("does it have a skull in there??", 
                new CounterUsePair(Counter.BEACH_HEAD_SPOOKY, ""));
        LIMITED_USE_MAP.put("oiled, slick", 
                new CounterUsePair(Counter.BEACH_HEAD_SLEAZE, ""));
        LIMITED_USE_MAP.put("lack of body-building", 
                new CounterUsePair(Counter.BEACH_HEAD_MUSCLE, ""));
        LIMITED_USE_MAP.put("we're all made of starfish", 
                new CounterUsePair(Counter.BEACH_HEAD_MYSTICALITY, ""));
        LIMITED_USE_MAP.put("pomp & circumsands", 
                new CounterUsePair(Counter.BEACH_HEAD_MOXIE, ""));
        LIMITED_USE_MAP.put("resting beach face", 
                new CounterUsePair(Counter.BEACH_HEAD_INITIATIVE, ""));
        LIMITED_USE_MAP.put("do i know you from somewhere?", 
                new CounterUsePair(Counter.BEACH_HEAD_FAMILIAR_WT, ""));
        LIMITED_USE_MAP.put("you learned something maybe!", 
                new CounterUsePair(Counter.BEACH_HEAD_STATS, ""));
        LIMITED_USE_MAP.put("reflex hammer", 
                new CounterUsePair(Counter.DOCTOR_BAG_HAMMER, ""));
        LIMITED_USE_MAP.put("otoscope", 
                new CounterUsePair(Counter.DOCTOR_BAG_OTOSCOPE, ""));
        LIMITED_USE_MAP.put("chest x-ray", 
                new CounterUsePair(Counter.DOCTOR_BAG_XRAY, ""));
        LIMITED_USE_MAP.put("become a bat", 
                new CounterUsePair(Counter.VAMPYRIC_CLOAKE, "Bat"));
        LIMITED_USE_MAP.put("become a wolf", 
                new CounterUsePair(Counter.VAMPYRIC_CLOAKE, "Wolf"));
        LIMITED_USE_MAP.put("become a cloud of mist", 
                new CounterUsePair(Counter.VAMPYRIC_CLOAKE, "Mist"));
        LIMITED_USE_MAP.put("summon clip art", 
                new CounterUsePair(Counter.CLIP_ART, ""));

    }

    /**
     * 
     *
     */
    public static class Use implements Comparable<Use>
    {
        private int day;
        private Counter counter;
        private int turn;
        private String use;
        private Statgain statgain;
        
        public Use(int day, int turn, Counter counter, String use, Statgain statgain)
        {
            this.day = day;
            this.counter = counter;
            this.turn = turn;
            this.use = use;
            this.statgain = statgain;
        }
        
        public int getDay() { return day; }
        public int getTurn() { return turn; }
        public Counter getCounter() { return counter; }
        public String getUse() { return use; }
        public Statgain getStatgain() { return statgain; }

        @Override
        public int compareTo(Use arg0) {
            Use other = (Use) arg0;
            if (counter == other.getCounter()) {
                if (use.equals(other.getUse())) {
                    return turn - other.getTurn();
                } else {
                    return use.compareTo(other.getUse());
                }
            } 
            return counter.compareTo(other.getCounter());
        }
    }
    
    /**
     * 
     *
     */
    public static class CounterUses implements Comparable<CounterUses>
    {
        private Counter counter;
        private ArrayList<Use> uses = new ArrayList<Use>();
        
        CounterUses(Counter counter)
        {
            this.counter = counter;
        }
        
        public void add(int day, int turn, String use, Statgain statgain)
        {
            Use newuse = new Use(day, turn, counter, use, statgain);
            uses.add(newuse);
        }
        
        public void add(int day, int turn, String use) 
        {
            add(day, turn, use, Statgain.NO_STATS);
        }
        
        private void add(Use use)
        {
            uses.add(use);
        }
        
        public Counter getCounter() { return counter; }
        public Use[] getUses() 
        {
            Use[] result = new Use[uses.size()];
            uses.toArray(result);
            Arrays.sort(result);
            return result;
        }

        @Override
        public int compareTo(CounterUses arg0) 
        {
            return counter.compareTo(((CounterUses)arg0).getCounter());
        }
    }
    
    /**
     * 
     *
     */
    public static class DailyUses 
    {
        private int day;
        private TreeMap<Counter, CounterUses> counterUses 
            = new TreeMap<Counter, CounterUses>();

        DailyUses(int day)
        {
            this.day = day;
        }
        
        public int getDay() { return day; }
        public TreeMap<Counter, CounterUses> getCounterUses() { return counterUses; }
        public CounterUses getCounterUses(Counter counter) 
        {
            return counterUses.get(counter);
        }
        
        public void add(int day, int turn, Counter counter, String use, Statgain statgain) 
        {
            if (! counterUses.containsKey(counter))
                counterUses.put(counter, new CounterUses(counter));
            counterUses.get(counter).add(day, turn, use, statgain);
        }
        
        public void add(int day, int turn, Counter counter, String use)
        {
            add(day, turn, counter, use, Statgain.NO_STATS);
        }
        
        private void add(Use use)
        {
            Counter counter = use.getCounter();
            if (! counterUses.containsKey(counter))
                counterUses.put(counter, new CounterUses(counter));
            counterUses.get(counter).add(use);                
        }
    }
    
    public final ArrayList<DailyUses> dailyUses 
        = new ArrayList<DailyUses>();

    /**
     * 
     * @param uses List of item/skill use events
     * @param daycount Number of days in the run
     */
    public LimitedUseData(List<DataNumberPair<Use>> uses, int daycount) {
        ensureDaycount(daycount);
        for (DataNumberPair<Use> use : uses) {
            add(use.getData());
        }
    }

    public ArrayList<DailyUses> getDailyUses() { return dailyUses; }
    
    private void add(Use use)
    {
        DailyUses dayUses = dailyUses.get(use.getDay() - 1);
        dayUses.add(use);
    }
    
    public void add(int day, int turn, Counter counter, String use, Statgain statgain)
    {
        ensureDaycount(day);
        DailyUses dayUses = dailyUses.get(day-1);
        dayUses.add(day, turn, counter, use);
    }
    
    public void add(int day, int turn, Counter counter, String use)
    {
        add(day, turn, counter, use, Statgain.NO_STATS);
    }
    
    public void ensureDaycount(int daycount)
    {
        while (daycount > dailyUses.size())
            dailyUses.add(new DailyUses(dailyUses.size()+1));
    }
}

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
import java.util.TreeMap;

/**
 * This class accumulates data on uses of items that have a limited number of uses
 * per day, such as the Cosplay Saber's Force and the Powerful Glove cheat codes.
 * <p>
 * Internally, the uses are collected as follows:
 * ArrayList of DailyUses
 *   DailyUses: List of CounterUses
 *     CounterUses: List of Uses of each counter
 *
 */
public class LimitedUseData {
    
    /**
     * 
     *
     */
    public enum Counter
    {
        // Note: Enum's natural ordering is the order listed here
        SABER_UPGRADE("Saber: Upgrade", 1),
        SABER_USE_FORCE("Saber: Use the Force", 5),
        ;
        
        private String name;
        private int limit;
        
        public int getLimit() { return limit; }
        public String getName() { return name; }
        
        private Counter(String name, int limit)
        {
            this.name = name;
            this.limit = limit;
        }
    }
    
    /**
     * 
     *
     */
    public class Use implements Comparable<Use>
    {
        private int day;
        private Counter counter;
        private int turn;
        private String use;
        
        Use(int day, int turn, Counter counter, String use)
        {
            this.day = day;
            this.counter = counter;
            this.turn = turn;
            this.use = use;
        }
        
        public int getDay() { return day; }
        public int getTurn() { return turn; }
        public Counter getCounter() { return counter; }
        public String getUse() { return use; }

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
    public class CounterUses implements Comparable<CounterUses>
    {
        private Counter counter;
        private ArrayList<Use> uses = new ArrayList<Use>();
        
        CounterUses(Counter counter)
        {
            this.counter = counter;
        }
        
        public void add(int day, int turn, String use)
        {
            Use newuse = new Use(day, turn, counter, use);
            uses.add(newuse);
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
    public class DailyUses 
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
        
        public void add(int day, int turn, Counter counter, String use) 
        {
            if (! counterUses.containsKey(counter))
                counterUses.put(counter, new CounterUses(counter));
            counterUses.get(counter).add(day, turn, use);
        }
    }
    
    
    public final ArrayList<DailyUses> dailyUses 
        = new ArrayList<DailyUses>();

    public LimitedUseData() {
        // Nothing to do here
    }

    public ArrayList<DailyUses> getDailyUses() { return dailyUses; }
    
    public void add(int day, int turn, Counter counter, String use)
    {
        // Get uses for that day, creating it if needed
        while (day > dailyUses.size())
            dailyUses.add(new DailyUses(dailyUses.size()+1));
        DailyUses dayUses = dailyUses.get(day-1);
        dayUses.add(day, turn, counter, use);
    }
}

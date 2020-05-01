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

package com.googlecode.alv.logdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import com.googlecode.alv.util.Counter;
import com.googlecode.alv.util.DataNumberPair;

/**
 * This class accumulates data on uses of items that have a limited number of uses
 * per day, such as the Cosplay Saber's Force and the Powerful Glove cheat codes.
 *
 */
public class LimitedUse {
        
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
    public LimitedUse(List<DataNumberPair<Use>> uses, int daycount) {
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

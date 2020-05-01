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

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.googlecode.alv.logdata.LimitedUse;
import com.googlecode.alv.util.Counter;

/**
 * Summarizes the in-run uses of limited items, grouping them by day
 * and then by counter.
 */
public class LimitedUseSummary {

    private Map<Integer, Map<Counter, Integer>> summary = new TreeMap<>();
    
    public LimitedUseSummary(List<LimitedUse> limitedUses) { 
        int nextDay = 1;
        for (LimitedUse use : limitedUses) {
            int day = use.getDay();
            if (! summary.containsKey(day)) {
                while (nextDay <= day) {
                    summary.put(nextDay++, new TreeMap<Counter, Integer>());
                }
            }
            Map<Counter, Integer> dayUses = summary.get(day);
            Counter counter = use.getCounter();
            if (! dayUses.containsKey(counter)) {
                dayUses.put(counter, 0);
            }
            int count = dayUses.get(counter);
            // Increment counter (Powerful Glove is different, though)
            if (counter == Counter.CHEAT_CODE) {
                count += 5;
                if (use.getUse() == Counter.REPLACE_ENEMY) {
                    count += 5;
                }
            } else {
                count ++;
            }
            dayUses.put(counter, count);
        }
    }
    
    public Map<Integer, Map<Counter, Integer>> getSummary() { return summary; }

}

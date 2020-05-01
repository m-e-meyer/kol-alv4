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

import com.googlecode.alv.util.Counter;

/**
 * Represents use of an item that have a limited number of uses
 * per day, such as the Cosplay Saber's Force and the Powerful Glove cheat codes.
 *
 */
public class LimitedUse implements Comparable<LimitedUse> {
        
    private int day;
    private Counter counter;
    private int turn;
    private String use;
    private Statgain statgain;
    
    public LimitedUse(int day, int turn, Counter counter, String use, Statgain statgain)
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
    public int compareTo(LimitedUse arg0) {
        LimitedUse other = (LimitedUse) arg0;
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

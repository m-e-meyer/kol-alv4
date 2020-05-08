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

package com.googlecode.alv.run;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.alv.logdata.turn.DetailedTurnInterval;
import com.googlecode.alv.logdata.turn.Turn;
import com.googlecode.alv.logdata.turn.TurnInterval;

public class Run {

    private boolean isDetailed;
    
    private List<RunTurn> turns = new ArrayList<>();
    
    private List<RunTurnInterval> turnIntervals = new ArrayList<>();
    
    // State
    int mus = 0;
    int mys = 0;
    int mox = 0;
    int meat = 0;
    
    public Run(
            final boolean isDetailed) {
        
        this.isDetailed = isDetailed;
        // Make sure lists have at least one element
        RunTurn startTurn = new RunTurn(1, 1, "Start of run", "Start of run");
        RunTurnInterval startTurnInterval = new RunTurnInterval("Start of run", isDetailed);
        turns.add(startTurn);
        turnIntervals.add(startTurnInterval);
        startTurnInterval.addTurn(startTurn);
    }

    public List<RunTurn> getTurns() { return turns; }
    
    public List<RunTurnInterval> getTurnIntervals() { return turnIntervals; }
    
    public boolean isDetailed() { return this.isDetailed; }
    
    public void addTurn(int day, int turnNumber, String location, String name) {
        // Create turn and add to list
        RunTurn turn = new RunTurn(day, turnNumber, location, name);
        turns.add(turn);
        // Create new turn interval if necessary
        RunTurnInterval lastInterval = getLastTurnInterval();
        if (lastInterval.getLocation().equals(location)) {
            lastInterval.addTurn(turn);
        } else {
            RunTurnInterval newInterval = new RunTurnInterval(location, isDetailed);
            newInterval.addTurn(turn);
            turnIntervals.add(newInterval);
        }
    }
    
    public RunTurn getLastTurn() {
        return turns.get(turns.size() - 1);
    }
    
    public RunTurnInterval getLastTurnInterval() {
        return turnIntervals.get(turnIntervals.size() - 1);
    }
    
    public void dump() {
        for (RunTurnInterval interval : turnIntervals) {
            System.out.println("[" + interval.getFirstTurnNumber() + "-" + interval.getLastTurnNumber() + "] " + interval.getLocation());
        }
    }
    
}

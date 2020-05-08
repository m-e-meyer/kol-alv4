package com.googlecode.alv.run;

import java.util.ArrayList;
import java.util.List;

public class RunTurnInterval {

    private int firstTurn = 0;
    
    private int lastTurn = 0;
    
    private String location = "none";
    
    private List<RunTurn> turns = new ArrayList<>();
    
    RunTurnInterval(String location, boolean isDetailed) { 
        this.location = location;
    }

    public String getLocation() { return location; }
    
    public int getFirstTurnNumber() { return firstTurn; }
    
    public int getLastTurnNumber() { return lastTurn; }

    public void addTurn(RunTurn turn) {
        int turnNumber = turn.getTurnNumber();
        if (firstTurn == 0) {
            firstTurn = turnNumber;
            lastTurn = turnNumber;
        } else {
            if (turnNumber < lastTurn)
                throw new IllegalArgumentException("Turn number out of order: expected " + lastTurn + ", got " + turnNumber);
            lastTurn = turnNumber;
        }
        turns.add(turn);
    }
    
}

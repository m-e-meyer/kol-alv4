package com.googlecode.alv.run;

public class RunTurn {

    private int dayNumber;
    
    private int turnNumber;
    
    private String location;
    
    private String name;
    
    public RunTurn(int dayNumber, int turnNumber, String location, String name) { 
        this.dayNumber = dayNumber;
        this.turnNumber = turnNumber;
        this.location = location;
        this.name = name;
    }

    public int getDayNumber() { return dayNumber; }
    
    public int getTurnNumber() { return turnNumber; }
    
    public String getLocation() { return location; }
    
    public String getName() { return name; }
    
    
}

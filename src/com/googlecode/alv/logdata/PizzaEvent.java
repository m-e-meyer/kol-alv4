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

/**
 * Represents one of two events involving the diabolic pizza cube: Crafting a pizza,
 * or getting an effect from eating a pizza.
 */
public class PizzaEvent {

    private int dayNumber;
    private int turnNumber;
    private String description;
    private int duration;
    
    public PizzaEvent(int dayNumber, int turnNumber, String desc, int duration) { 
        this.dayNumber = dayNumber;
        this.turnNumber = turnNumber;
        this.description = desc;
        this.duration = duration;
    }

    public int getDayNumber() { return dayNumber; }

    public int getTurnNumber() { return turnNumber; }

    public String getDescription( ) { return description; }
    
    public int getDuration() { return duration; }

}

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

package com.googlecode.alv.parser.mafiablock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.alv.logdata.LogDataHolder;
import com.googlecode.alv.logdata.turn.SingleTurn;
import com.googlecode.alv.logdata.turn.TurnVersion;
import com.googlecode.alv.logdata.turn.action.EquipmentChange;
import com.googlecode.alv.logdata.turn.action.FamiliarChange;
import com.googlecode.alv.parser.line.ItemAcquisitionLineParser;

public class ServiceBlockParser implements LogBlockParser
{
    private static final Pattern CHOICE_PATTERN = Pattern.compile("Took choice 1089[/]([0-9]*):");

    private static final Pattern ADVENTURE_PATTERN = Pattern.compile("You lose ([0-9]*) Adventure");

    private static final ItemAcquisitionLineParser acquisitionParser = new ItemAcquisitionLineParser();

    private static final Map<String, String> SERVICES = new HashMap<String, String>();
    
    static {
        SERVICES.put("1", "Donate Blood");
        SERVICES.put("2", "Feed the Children (But Not Too Much)");
        SERVICES.put("3", "Build Playground Mazes");
        SERVICES.put("4", "Feed Conspirators");
        SERVICES.put("5", "Breed More Collies");
        SERVICES.put("6", "Reduce Gazelle Population");
        SERVICES.put("7", "Make Sausage");
        SERVICES.put("8", "Be a Living Statue");
        SERVICES.put("9", "Make Margaritas");
        SERVICES.put("10", "Clean Steam Tunnels");
        SERVICES.put("11", "Coil Wire");
        SERVICES.put("30", "Donate Body");
    }
    
    @Override
    public void parseBlock(List<String> block, LogDataHolder logData) 
    {
        // Get choice 
        Matcher m = CHOICE_PATTERN.matcher(block.get(0));
        m.find();
        String choice = m.group(1);
        String service = "unknown";
        if (SERVICES.containsKey(choice))
            service = SERVICES.get(choice);
        // Lose adventures
        int adventures = 0;
        if (service != "Donate Body" && service != "unknown") {
            m = ADVENTURE_PATTERN.matcher(block.get(2));
            m.find();
            adventures = Integer.parseInt(m.group(1));
        }
        
        // Now record the service by adding multiple turns
        EquipmentChange equipmentChange = logData.getLastEquipmentChange();
        FamiliarChange familiarChange = logData.getLastFamiliarChange();
        int dayNumber = logData.getCurrentDayNumber();
        // If the last turn probably spent an adventure, increment the turn counter
        SingleTurn prev = (SingleTurn) logData.getLastTurnSpent();
        int turnNumber = prev.getTurnNumber();
        //if (probablySpentTurn(prev))
        if (! prev.isFreeTurn())
            turnNumber++;
        // Now add the turns
        while (adventures > 0) {
            SingleTurn turn = new SingleTurn("Community Service: " + service,
                                             service,
                                             turnNumber++,
                                             dayNumber,
                                             equipmentChange,
                                             familiarChange);
            turn.setTurnVersion(TurnVersion.OTHER);
            logData.addTurnSpent(turn);
            adventures --;
        }
        // Acquire item
        acquisitionParser.parseLine(block.get(3), logData);
    }
}

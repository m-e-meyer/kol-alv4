package com.googlecode.alv.parser.line;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.alv.logdata.LogDataHolder;
import com.googlecode.alv.logdata.turn.SingleTurn;
import com.googlecode.alv.util.Counter;
import com.googlecode.alv.util.Pair;

/**
 * 
 * This class handles lines that start with "Took choice", processing them
 * if they are important.
 *
 */
public class MafiaTookChoiceLineParser extends AbstractLineParser 
{

    private static final Pattern TOOK_CHOICE_PATTERN 
        = Pattern.compile("^Took choice (\\d+/\\d+):.*");
    
    private final Matcher tookChoiceMatcher = TOOK_CHOICE_PATTERN.matcher("");

    // No constructor
    
    @Override
    protected boolean isCompatibleLine(String line)
    {
        if (! tookChoiceMatcher.reset(line).find())
            return false;
        String choice = tookChoiceMatcher.group(1);
        return Counter.LIMITED_USE_MAP.containsKey(choice);
    }

    @Override
    protected void doParsing(String line, LogDataHolder logData) 
    {
        tookChoiceMatcher.reset(line).find();
        String choice = tookChoiceMatcher.group(1);
        Pair<Counter, String> cu = Counter.LIMITED_USE_MAP.get(choice);
        Counter counter = cu.getVar1();
        String use = cu.getVar2();
        // If using the Force, find out on whom
        SingleTurn st = (SingleTurn) logData.getLastTurnSpent();
        if (counter == Counter.SABER_USE_FORCE) {
            use = use + ", " + st.toEncounter().getEncounterName();
        }
        logData.addLimitedUse(counter, use);
    }

}

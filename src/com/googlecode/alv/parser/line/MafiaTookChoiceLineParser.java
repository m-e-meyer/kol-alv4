package com.googlecode.alv.parser.line;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.alv.logdata.LogDataHolder;
import com.googlecode.alv.logdata.summary.LimitedUseData;
import com.googlecode.alv.logdata.summary.LimitedUseData.CounterUsePair;
import com.googlecode.alv.logdata.turn.SingleTurn;

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
        return LimitedUseData.LIMITED_USE_MAP.containsKey(choice);
    }

    @Override
    protected void doParsing(String line, LogDataHolder logData) 
    {
        tookChoiceMatcher.reset(line).find();
        String choice = tookChoiceMatcher.group(1);
        CounterUsePair cu = LimitedUseData.LIMITED_USE_MAP.get(choice);
        SingleTurn st = (SingleTurn) logData.getLastTurnSpent();
        logData.addLimitedUse(st.getDayNumber(), st.getTurnNumber(), cu.counter, cu.use);
    }

}

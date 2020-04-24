package com.googlecode.alv.parser.mafiablock;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.alv.logdata.LogDataHolder;
import com.googlecode.alv.logdata.summary.LimitedUseData;
import com.googlecode.alv.logdata.summary.LimitedUseData.CounterUsePair;
import com.googlecode.alv.logdata.turn.SingleTurn;

/**
 * Parses a Combing block, which records a Beach Combing visit to a Beach Head.  
 * This block consists of just two lines, an action (with turn number) and acquiring
 * an effect.  We key off the effect to generate a LimitedUse, since each beach head
 * can only be visited once a day.
 */
public class CombingBlockParser implements LogBlockParser {

    Pattern effectPattern = Pattern.compile("^You acquire an effect:\\s*(.*?)\\s*[(]\\d+[)]\\s*$");
    
    // Use parent constructor
    
    @Override
    public void parseBlock(List<String> block, LogDataHolder logData) {
         Matcher m = effectPattern.matcher(block.get(1));
         if (m.find()) {
             String effect = m.group(1).toLowerCase();
             CounterUsePair cu = LimitedUseData.LIMITED_USE_MAP.get(effect);
             if (cu != null) {
                 SingleTurn st = (SingleTurn) logData.getLastTurnSpent();
                 logData.addLimitedUse(st.getDayNumber(), st.getTurnNumber(), cu.counter, cu.use);
             } else {
                 System.out.println("You shouldn't be here with effect: " + effect);
             }
         }
    }

}

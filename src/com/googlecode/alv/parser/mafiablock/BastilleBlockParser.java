package com.googlecode.alv.parser.mafiablock;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.alv.logdata.LogDataHolder;
import com.googlecode.alv.logdata.Statgain;
import com.googlecode.alv.logdata.summary.LimitedUseData.Counter;
import com.googlecode.alv.logdata.turn.SingleTurn;

/**
 * Parses a BASTILLE block, which represents getting stats, items, and an effect
 * from the Bastille Battalion control rig.
 *
 */
public class BastilleBlockParser implements LogBlockParser 
{

    private static final Pattern EFFECT 
        = Pattern.compile("You acquire an effect:\\s*(.*?)\\s*[(]");
    
    private final Matcher effectMatcher = EFFECT.matcher("");
    
    // Use parent's constructor

    @Override
    public void parseBlock(List<String> block, LogDataHolder logData)
    {
        String barbican = "";
        String drawbridge = "";
        String murderhole = "";
        Statgain statgain = Statgain.NO_STATS;
        
        for (String line : block) {
            if (effectMatcher.reset(line).find()) {
                String effect = effectMatcher.group(1).toLowerCase();
                switch (effect) {
                case "bastille braggadocio":
                    murderhole = "gesture";
                    break;
                case "bastille budgeteer":
                    murderhole = "cannon";
                    break;
                case "bastille bourgeoisie":
                    murderhole = "catapult";
                    break;
                default:
                    break;
                }
            }
            statgain = statgain.addStats(1, 2, 3);
        }
        String settings = barbican + " " + drawbridge + " " + murderhole;
        SingleTurn st = (SingleTurn) logData.getLastTurnSpent();
        st.addStatGain(statgain);
        logData.addLimitedUse(st.getDayNumber(), 
                              st.getTurnNumber(), 
                              Counter.BASTILLE, 
                              settings,
                              statgain);
    }

}

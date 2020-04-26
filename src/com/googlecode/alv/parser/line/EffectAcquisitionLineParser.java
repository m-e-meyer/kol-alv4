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

package com.googlecode.alv.parser.line;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.alv.logdata.LogDataHolder;
import com.googlecode.alv.logdata.MPGain;
import com.googlecode.alv.logdata.summary.LimitedUseData.Counter;
import com.googlecode.alv.logdata.turn.SingleTurn;
import com.googlecode.alv.parser.UsefulPatterns;
import com.googlecode.alv.util.DataNumberPair;
import com.googlecode.alv.util.Lists;

/**
 * This class parses a line for acquisition of effects of particular interest, and
 * records them in a LogDataHolder accordingly.
 * <p>
 * This class incorporates the behavior of the MafiaDisintegratedLineParser,
 * OnTheTrailLineParser, and PoolMPBuffLineParser classes, which have been deleted.
 */
public class EffectAcquisitionLineParser extends AbstractLineParser {

    private static final List<String> INTERESTING_EFFECTS 
        = Lists.immutableListOf("everything looks yellow",
                                "on the trail",
                                // Boxing Day spa
                                "muddled",
                                "ten out of ten",
                                "uncucumbered",
                                "flagrantly fragrant",
                                // Fortune Teller
                                "a girl named sue",
                                "there's no n in love",
                                "meet the meat",
                                "gunther than thou",
                                "everybody calls him gorgon",
                                "they call him shifty because...",
                                // Clan swimming pool
                                "mental a-cue-ity"
                                );

    private static final String ACQUIRE_EFFECT = "You acquire an effect:";
    
    private static final Pattern EFFECT_PATTERN 
        = Pattern.compile("^You acquire an effect:\\s*(.*?)\\s*[(]\\d+[)]\\s*$");
    
    private static final Pattern MAJOR_YELLOW_RAY = Pattern.compile("Round \\d+: .+? swings his eyestalk around and unleashes a massive"
            + " ray of yellow energy, completely disintegrating your opponent.");

    private final Matcher majorYellowRayMatcher = MAJOR_YELLOW_RAY.matcher(UsefulPatterns.EMPTY_STRING);
    
    private static final Matcher EFFECT_MATCHER = EFFECT_PATTERN.matcher("");

    
    @Override
    protected boolean isCompatibleLine(String line) 
    {
        if (line.startsWith(ACQUIRE_EFFECT)) {
            if (! EFFECT_MATCHER.reset(line).find()) 
                return false;
            String effect = EFFECT_MATCHER.group(1).toLowerCase();
            return (INTERESTING_EFFECTS.contains(effect));
        }
        // Also get the effect from a He-Boulder combat action
        return (line.startsWith(UsefulPatterns.COMBAT_ROUND_LINE_BEGINNING_STRING) 
                && majorYellowRayMatcher.reset(line).matches());
    }

    @Override
    protected void doParsing(String line, LogDataHolder logData) 
    {
        String effect = "";
        if (EFFECT_MATCHER.reset(line).find()) 
            effect = EFFECT_MATCHER.group(1).toLowerCase();
        else {
            // If we're not here because of acquiring an effect, maybe we're here
            // because of He-Boulder's yellow ray
            if (line.startsWith(UsefulPatterns.COMBAT_ROUND_LINE_BEGINNING_STRING) 
                    && majorYellowRayMatcher.reset(line).matches())
                effect = "everything looks yellow";     // if so, pretend we have effect
            else
                return;     // else give up -- whay are we here?
        }
        
        SingleTurn st = (SingleTurn) logData.getLastTurnSpent();
        int day = st.getDayNumber();
        int turn = st.getTurnNumber();
        switch (effect) {
        // Note: Maybe keeping these in order newest to oldest would be more efficient
        case "everything looks yellow":
            st.setDisintegrated(true);
            break;
        case "on the trail":
            logData.addHuntedCombat(DataNumberPair.of(st.getEncounterName(), turn));
            break;
        // Boxing Day spa
        case "muddled":
            logData.addLimitedUse(day, turn, Counter.DAYCARE_SPA, "Mud bath");
            break;
        case "ten out of ten":
            logData.addLimitedUse(day, turn, Counter.DAYCARE_SPA, "Mani-pedi");
            break;
        case "uncucumbered":
            logData.addLimitedUse(day, turn, Counter.DAYCARE_SPA, "Eye treatment");
            break;
        case "flagrantly fragrant":
            logData.addLimitedUse(day, turn, Counter.DAYCARE_SPA, "Aromatherapy");
            break;
        // Fortune Teller
        case "a girl named sue":
            logData.addLimitedUse(day, turn, Counter.FORTUNE_TELLER, "Susie");
            break;
        case "there's no n in love":
            logData.addLimitedUse(day, turn, Counter.FORTUNE_TELLER, "Hagnk");
            break;
        case "meet the meat":
            logData.addLimitedUse(day, turn, Counter.FORTUNE_TELLER, "Meatsmith");
            break;
        case "gunther than thou":
            logData.addLimitedUse(day, turn, Counter.FORTUNE_TELLER, "Gunther");
            break;
        case "everybody calls him gorgon":
            logData.addLimitedUse(day, turn, Counter.FORTUNE_TELLER, "Gorgonzola");
            break;
        case "they call him shifty because...":
            logData.addLimitedUse(day, turn, Counter.FORTUNE_TELLER, "Shifty");
            break;
        // Clan swimming pool
        case "mental a-cue-ity":
            st.addMPGain(new MPGain(100, 0, 0, 0, 0));
            break;
        }
    }

}

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

import java.util.regex.Matcher;

import com.googlecode.alv.logdata.LogDataHolder;
import com.googlecode.alv.parser.UsefulPatterns;
import com.googlecode.alv.util.StatClass;

/**
 * A parser for the substats gained/lost notation in mafia logs.
 * <p>
 * The format looks like this:
 * <p>
 * {@code You gain _substatAmount_ _substatName_}
 * <p>
 * OR
 * <p>
 * {@code You lose _substatAmount_ _substatName_}
 */
public final class StatLineParser extends AbstractLineParser {
    // String length of "You gain " or "You lose " is 9.
    public static final int GAIN_LOSE_START_STRING_LENGTH = 9;

    private static final String LOSE_STRING = "You lose";

    private final Matcher gainLoseMatcher = UsefulPatterns.GAIN_LOSE.matcher("");

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doParsing(final String line, final LogDataHolder logData) 
    {
        int substrLength = GAIN_LOSE_START_STRING_LENGTH;

        if (line.startsWith(UsefulPatterns.AFTER_BATTLE_STRING)) {
            substrLength += UsefulPatterns.AFTER_BATTLE_STRING.length();
        }

        final String informationPart = line.substring(substrLength);
        final int whiteSpaceIndex = informationPart.indexOf(" ");

        final String substatName = informationPart.substring(whiteSpaceIndex + 1,
                                                             informationPart.length());
        final String amountString = informationPart.substring(0, whiteSpaceIndex);

        // Substat gains higher than the integer limit should not happen and
        // will be ignored.
        int amount;
        try {
            amount = Integer.parseInt(amountString.replace(",", ""));
        } catch (final NumberFormatException e) {
            e.printStackTrace();
            return;
        }

        if (line.startsWith(LOSE_STRING))
            amount *= -1;

        logData.getLastTurnSpent().addStatGain(StatClass.getStatgain(substatName, amount));
    }

    /*
    /**
     * 
     * @param line Line to parse
     * @return Whether the line is a stat gain log
     *
    public static boolean isCompatibleLineStatic(final String line)
    {
        Matcher matcher = UsefulPatterns.GAIN_LOSE.matcher(line);
        if (! matcher.matches())
            return false;
        final String gainName = line.substring(line.lastIndexOf(" ") + 1);
        return StatClass.SUBSTATS.containsKey(gainName);
    }
    */
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isCompatibleLine(final String line)
    {
        if (gainLoseMatcher.reset(line).matches()) {
            final String gainName = line.substring(line.lastIndexOf(" ") + 1);

            return StatClass.SUBSTATS.containsKey(gainName);
        }

        return false;
    }
}

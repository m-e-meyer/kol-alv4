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
import java.util.regex.Pattern;

import com.googlecode.alv.logdata.LogDataHolder;
import com.googlecode.alv.logdata.MeatGain;

/**
 * A parser for the meat gained notation in mafia logs.
 * <p>
 * The format looks like this:
 * <p>
 * {@code You gain _amount_ Meat}
 */
public final class MeatLineParser extends AbstractLineParser {

    private static final Pattern MEAT_GAIN = Pattern.compile("^You (gain|lose) (\\d*,?\\d+) Meat");

    private final Matcher meatGainMatcher = MEAT_GAIN.matcher("");

    private final MeatGainType meatGainType;

    /**
     * @param type
     *            The meat gain type which decides to which kind of meat gain all
     *            parsed mp gains from this line parser will be added to.
     */
    public MeatLineParser(
                          final MeatGainType type) {
        meatGainType = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doParsing(
                             final String line, final LogDataHolder logData) {
        meatGainMatcher.reset(line).find();
        final int amount = Integer.parseInt(meatGainMatcher.group(2).replace(",", ""));

        MeatGain meatgain; 
        if (meatGainMatcher.group(1).equals("lose")) {
            meatgain = new MeatGain(0, 0, amount);
        } else if (meatGainType == MeatGainType.ENCOUNTER) {
            meatgain = new MeatGain(amount, 0, 0);
            //logData.getLastTurnSpent().addMeat(new MeatGain(amount, 0, 0));
        } else {
            meatgain = new MeatGain(0, amount, 0);
            //logData.getLastTurnSpent().addMeat(new MeatGain(0, amount, 0));
        }
        logData.getLastTurnSpent().addMeat(meatgain);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isCompatibleLine(
                                       final String line) {
        return meatGainMatcher.reset(line).matches();
    }

    public static enum MeatGainType {
        ENCOUNTER, OTHER;
    }
}

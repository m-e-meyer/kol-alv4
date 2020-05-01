/* Copyright (c) 2020-2020, developers of the Ascension Log Visualizer
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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.alv.logdata.LogDataHolder;
import com.googlecode.alv.util.Counter;
import com.googlecode.alv.util.Pair;

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
             Pair<Counter, String> cu = Counter.LIMITED_USE_MAP.get(effect);
             if (cu != null) {
                 logData.addLimitedUse(cu.getVar1(), cu.getVar2());
             } else {
                 System.out.println("You shouldn't be here with effect: " + effect);
             }
         }
    }

}

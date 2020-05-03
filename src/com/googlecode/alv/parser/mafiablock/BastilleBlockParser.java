package com.googlecode.alv.parser.mafiablock;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.alv.logdata.LogDataHolder;
import com.googlecode.alv.logdata.Statgain;
import com.googlecode.alv.parser.line.ItemAcquisitionLineParser;
import com.googlecode.alv.util.Counter;
import com.googlecode.alv.util.StatClass;

/**
 * Parses a BASTILLE block, which represents getting stats, items, and an effect
 * from the Bastille Battalion control rig.
 *
 */
public class BastilleBlockParser implements LogBlockParser 
{
    private static final int SINGLE_ITEM_STRING_LENGTH
        = ItemAcquisitionLineParser.SINGLE_ITEM_STRING.length();
        
    private static final Pattern EFFECT 
        = Pattern.compile("You acquire an effect:\\s*(.*?)\\s*[(]");
    
    private static final Pattern STATGAIN
        = Pattern.compile("You gain (\\d+) ([^ ]*)");
    
    private final Matcher effectMatcher = EFFECT.matcher("");
    
    // Use parent's constructor

    @Override
    public void parseBlock(List<String> block, LogDataHolder logData)
    {
        String barbican = "?";
        String drawbridge = "?";
        String murderhole = "?";
        Statgain statgain = Statgain.NO_STATS;
        
        for (String line : block) {
            if (effectMatcher.reset(line).find()) {
                // We have to tell from the effect what murderhole was chosen.
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
                continue;
            }
            if (line.startsWith(ItemAcquisitionLineParser.SINGLE_ITEM_STRING)) {
                // We have to tell from the item drop what drawbridge was chosen.
                // The item drop must be single, and will be of the form 
                //   You acquire an item: Draftsman's driving gloves
                String item = line.substring(SINGLE_ITEM_STRING_LENGTH).strip().toLowerCase();
                switch (item) {
                case "draftsman's driving gloves":
                    drawbridge = "draftsman";
                    break;
                case "nouveau nosering":
                    drawbridge = "art";
                    break;
                case "brutal brogues":
                    drawbridge = "brutalist";
                    break;
                }
                continue;
            }
            Matcher statgainMatcher = STATGAIN.matcher(line);
            if (statgainMatcher.find()) {
                int amount = Integer.parseInt(statgainMatcher.group(1));
                String substatName = statgainMatcher.group(2);
                if (! StatClass.SUBSTATS.containsKey(substatName))
                    continue;  // probably "cheese!" or something
                statgain = StatClass.getStatgain(substatName, amount);
                if (statgain.mus != 0)
                    barbican = "babar";
                else if (statgain.myst != 0)
                    barbican = "barbecue";
                else
                    barbican = "barbershop";
                continue;
            }
        }
        String settings = barbican + " " + drawbridge + " " + murderhole;
        logData.addLimitedUse(Counter.BASTILLE, 
                              settings,
                              statgain);
    }

}

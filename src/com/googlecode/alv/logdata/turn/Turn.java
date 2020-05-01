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

package com.googlecode.alv.logdata.turn;

import java.util.Collection;
import java.util.List;

import com.googlecode.alv.logdata.CombatItem;
import com.googlecode.alv.logdata.Item;
import com.googlecode.alv.logdata.MPGain;
import com.googlecode.alv.logdata.MeatGain;
import com.googlecode.alv.logdata.PizzaEvent;
import com.googlecode.alv.logdata.Skill;
import com.googlecode.alv.logdata.Statgain;
import com.googlecode.alv.logdata.consumables.Consumable;

/**
 * An interface for mutable turns of an ascension log.
 * <p>
 * This interface should define all methods necessary to change the data
 * accessible through the {@link TurnEntity} interface.
 */
public interface Turn extends TurnEntity {
    /***
     *
     * @param combatItem the combat item to add
     */
    void addCombatItemUsed(
            final CombatItem combatItem);

    /**
     * @param consumable The consumable to add.
     */
    void addConsumableUsed(
            final Consumable consumable);

    /**
     * @param droppedItem The item to add.
     */
    void addDroppedItem(
            final Item droppedItem);

    /**
     * @param freeRunaways The number of successful free runaways to add.
     */
    void addFreeRunaways(
            final int freeRunaways);

    /**
     * @param meat The meat data to add.
     */
    void addMeat(
            final MeatGain meat);

    /**
     * @param mpGain The MP gains to add.
     */
    void addMPGain(
            final MPGain mpGain);

    /**
     * Adds the given notes to this turn. The already existing notes and the ones
     * added will be divided by a line break ({@code "\n"}).
     *
     * @param notes The notes tagged to this turn to add.
     */
    void addNotes(
            final String notes);

    /**
     * 
     * @param pizzaEvent The pizza event to add to this turn
     */
    void addPizzaEvent(
            final PizzaEvent pizzaEvent);
    
    /**
     * @param skill The skill to add.
     */
    void addSkillCast(
            final Skill skill);

    /**
     * @param stats The stat gains to add.
     */
    void addStatGain(
            final Statgain stats);

    /**
     * @param combatItems collection of combat items to set
     */
    void setCombatItemsUsed(
            final Collection<CombatItem> combatItems);

    /**
     * @param consumablesUsed The consumables used to set.
     */
    void setConsumablesUsed(
            final Collection<Consumable> consumablesUsed);

    /**
     * @param droppedItems The dropped items to set.
     */
    void setDroppedItems(
            final Collection<Item> droppedItems);

    /**
     * @param freeRunaways The number of successful free runaways to set.
     */
    void setFreeRunaways(
            final int freeRunaways);

    /**
     * @param meat The meat data to set.
     */
    void setMeat(
            final MeatGain meat);

    /**
     * @param mpGain The MP gains to set.
     */
    void setMPGain(
            final MPGain mpGain);

    /**
     * @param notes The notes tagged to this turn to set.
     */
    void setNotes(
            final String notes);

    /**
     * 
     * @param pizzaEvents The pizza event list to set.
     */
    void setPizzaEvents(
            final List<PizzaEvent> pizzaEvents);
        
    /**
     * @param skillsCast The skills cast to set.
     */
    void setSkillsCast(
            final Collection<Skill> skillsCast);

    /**
     * @param stats The stat gains to set.
     */
    void setStatGain(
            final Statgain stats);
}
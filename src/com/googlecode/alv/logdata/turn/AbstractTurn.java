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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.googlecode.alv.logdata.CombatItem;
import com.googlecode.alv.logdata.Item;
import com.googlecode.alv.logdata.LimitedUse;
import com.googlecode.alv.logdata.LogComment;
import com.googlecode.alv.logdata.MPGain;
import com.googlecode.alv.logdata.MeatGain;
import com.googlecode.alv.logdata.PizzaEvent;
import com.googlecode.alv.logdata.Skill;
import com.googlecode.alv.logdata.Statgain;
import com.googlecode.alv.logdata.consumables.Consumable;
import com.googlecode.alv.util.Countable;
import com.googlecode.alv.util.CountableSet;

/**
 * This abstract class handles most of the data which a turn can collect and
 * should be used as a starting point of an actual implementation of the
 * {@link Turn} interface.
 * <p>
 * Note that all value classes handled by this class, which implement the
 * {@link Countable} interface, don't need to take special actions to make sure
 * no data corruption happens by sharing instances. The internal data
 * collections of this class will take care of this on their own. However, when
 * an object is added to this class, it should always be expected that it has
 * been cloned in some way.
 * <p>
 * All methods in this class throw a {@link NullPointerException} if a null
 * object reference is passed in any parameter.
 */
public abstract class AbstractTurn implements Turn {
    private final String areaName;

    private MeatGain meat = MeatGain.NO_MEAT;

    private MPGain mpGain = MPGain.NO_MP;

    private Statgain statGain = Statgain.NO_STATS;

    private final CountableSet<Item> droppedItems = new CountableSet<>();

    private final CountableSet<Skill> skillsCast = new CountableSet<>();

    private final CountableSet<CombatItem> combatItemsUsed = new CountableSet<>();

    private final CountableSet<Consumable> consumablesUsed = new CountableSet<>();
    
    private final List<LimitedUse> limitedUses = new ArrayList<>();
    
    private final List<PizzaEvent> pizzaEvents = new ArrayList<>();

    private int successfulFreeRunaways = 0;

    protected LogComment comment = new LogComment();

    private boolean isFreeTurn = false;

    /**
     * @param areaName The name of the area to set.
     */
    public AbstractTurn(
            final String areaName) {

        if (areaName == null) {
            throw new NullPointerException("Area name must not be null.");
        }

        this.areaName = areaName;
    }

    /**
     * @see Turn
     */
    @Override
    public void addCombatItemUsed(
            final CombatItem ci) { this.combatItemsUsed.addElement(ci); }

    /**
     * @see Turn
     */
    @Override
    public void addConsumableUsed(
            final Consumable consumable) {

        consumablesUsed.addElement(consumable);
    }

    /**
     * @see Turn
     */
    @Override
    public void addDroppedItem(
            final Item droppedItem) { droppedItems.addElement(droppedItem); }

    /**
     * @see Turn
     */
    @Override
    public void addFreeRunaways(
            final int freeRunaways) { successfulFreeRunaways += freeRunaways; }

    /**
     * @see Turn
     */
    @Override
    public void addLimitedUse(
            final LimitedUse limitedUse) { limitedUses.add(limitedUse); }
    
    /**
     * @see Turn
     */
    @Override
    public void addMeat(
            final MeatGain meat) { this.meat = this.meat.addMeatData(meat); }

    /**
     * @see Turn
     */
    @Override
    public void addMPGain(
            final MPGain mpGain) { this.mpGain = this.mpGain.addMPGains(mpGain); }

    /**
     * @see Turn
     */
    @Override
    public void addNotes(
            final String notes) { comment.addComments(notes); }

    /**
     * @see Turn
     */
    @Override
    public void addPizzaEvent(
            final PizzaEvent pizzaEvent) { pizzaEvents.add(pizzaEvent); }
    
    /**
     * @see Turn
     */
    @Override
    public void addSkillCast(
            final Skill skill) { skillsCast.addElement(skill); }

    /**
     * @see Turn
     */
    @Override
    public void addStatGain(
            final Statgain stats) { statGain = statGain.plus(stats); }

    /**
     * @param turn The turn whose data will be added to this turn.
     */
    protected void addTurnData(
            final Turn turn) {

        if (turn == null) {
            throw new NullPointerException("Turn must not be null.");
        }

        meat = meat.addMeatData(turn.getMeat());
        statGain = statGain.plus(turn.getStatGain());
        mpGain = mpGain.addMPGains(turn.getMPGain());
        successfulFreeRunaways += turn.getFreeRunaways();
        addNotes(turn.getNotes());
        for (final Item i : turn.getDroppedItems()) {
            addDroppedItem(i);
        }
        for (final Skill s : turn.getSkillsCast()) {
            addSkillCast(s);
        }
        for (final Consumable c : turn.getConsumablesUsed()) {
            addConsumableUsed(c);
        }
        for (final CombatItem ci : turn.getCombatItemsUsed()) {
            addCombatItemUsed(ci);
        }
        for (final LimitedUse use : turn.getLimitedUses()) {
            addLimitedUse(use);
        }
        for (final PizzaEvent pe : turn.getPizzaEvents()) {
            addPizzaEvent(pe);
        }
    }

    /**
     * Empties out all internal data collections.
     */
    protected void clearAllTurnDataCollections() {

        droppedItems.clear();
        skillsCast.clear();
        consumablesUsed.clear();
    }

    @Override
    @SuppressWarnings("unlikely-arg-type")
    public boolean equals(
            final Object o) {

        if (o == null) {
            return false;
        }

        if (o == this) {
            return true;
        }

        if (o instanceof AbstractTurn) {
            final AbstractTurn at = (AbstractTurn) o;

            return meat.equals(at.getMeat()) && mpGain.equals(at.getMPGain())
                    && statGain.equals(at.getStatGain()) && areaName.equals(at.getAreaName())
                    && droppedItems.getElements().equals(at.droppedItems)
                    && skillsCast.getElements().equals(at.skillsCast)
                    && consumablesUsed.getElements().equals(at.consumablesUsed)
                    && comment.equals(at.comment) && successfulFreeRunaways == at.getFreeRunaways();
        }

        return false;
    }

    /**
     * @see TurnEntity
     */
    @Override
    public String getAreaName() { return areaName; }

    /**
     * @see TurnEntity
     */
    @Override
    public Collection<CombatItem> getCombatItemsUsed() {

        return this.combatItemsUsed.getElements();
    }

    /**
     * @see TurnEntity
     */
    @Override
    public Collection<Consumable> getConsumablesUsed() { return consumablesUsed.getElements(); }

    /**
     * @see TurnEntity
     */
    @Override
    public Collection<Item> getDroppedItems() { return droppedItems.getElements(); }

    /**
     * @see TurnEntity
     */
    @Override
    public int getFreeRunaways() { return successfulFreeRunaways; }

    /**
     * @see TurnEntity
     */
    @Override
    public List<LimitedUse> getLimitedUses() { return limitedUses; }

    /**
     * @see TurnEntity
     */
    @Override
    public MeatGain getMeat() { return meat; }

    /**
     * @see TurnEntity
     */
    @Override
    public MPGain getMPGain() { return mpGain; }

    /**
     * @see TurnEntity
     */
    @Override
    public String getNotes() { return comment.getComments(); }

    /**
     * @see TurnEntity
     */
    public List<PizzaEvent> getPizzaEvents() { return pizzaEvents; }
    
    /**
     * @see TurnEntity
     */
    @Override
    public Collection<Skill> getSkillsCast() { return skillsCast.getElements(); }

    /**
     * @see TurnEntity
     */
    @Override
    public Statgain getStatGain() { return statGain; }

    /**
     * @see TurnEntity
     */
    @Override
    public Statgain getTotalStatGain() {

        Statgain totalStatgain = statGain;
        for (final Consumable c : consumablesUsed.getElements()) {
            totalStatgain = totalStatgain.plus(c.getStatGain());
        }
        // One limited use, the Bastille, has stat gains too
        for (final LimitedUse use : limitedUses) {
            totalStatgain = totalStatgain.plus(use.getStatgain());
        }

        return totalStatgain;
    }

    @Override
    public int hashCode() {

        int result = 48;
        result = 31 * result + meat.hashCode();
        result = 31 * result + mpGain.hashCode();
        result = 31 * result + statGain.hashCode();
        result = 31 * result + areaName.hashCode();
        result = 31 * result + droppedItems.hashCode();
        result = 31 * result + skillsCast.hashCode();
        result = 31 * result + consumablesUsed.hashCode();
        result = 31 * result + comment.hashCode();
        result = 31 * result + successfulFreeRunaways;

        return result;
    }

    /**
     * @see TurnEntity
     */
    @Override
    public boolean isCombatItemUsed(
            final CombatItem ci) {

        return this.combatItemsUsed.contains(ci);
    }

    /**
     * @see TurnEntity
     */
    @Override
    public boolean isCombatItemUsed(
            final String combatItemName) {

        return this.combatItemsUsed.containsByName(combatItemName);
    }

    /**
     * @see TurnEntity
     */
    @Override
    public boolean isConsumableUsed(
            final Consumable c) { return consumablesUsed.contains(c); }

    /**
     * @see TurnEntity
     */
    @Override
    public boolean isConsumableUsed(
            final String c) { return consumablesUsed.containsByName(c); }

    /**
     * @return Whether or not this turn was "Free"
     */
    public boolean isFreeTurn() { return this.isFreeTurn; }

    /**
     * @see TurnEntity
     */
    @Override
    public boolean isItemDropped(
            final Item i) { return droppedItems.contains(i); }

    /**
     * @see TurnEntity
     */
    @Override
    public boolean isItemDropped(
            final String i) { return droppedItems.containsByName(i); }

    /**
     * @see TurnEntity
     */
    @Override
    public boolean isSkillCast(
            final Skill s) { return skillsCast.contains(s); }

    /**
     * @see TurnEntity
     */
    @Override
    public boolean isSkillCast(
            final String s) { return skillsCast.containsByName(s); }

    /**
     * @see Turn
     */
    @Override
    public void setCombatItemsUsed(
            final Collection<CombatItem> combatItemsUsed) {

        this.combatItemsUsed.setElements(combatItemsUsed);
    }

    /**
     * @see Turn
     */
    @Override
    public void setConsumablesUsed(
            final Collection<Consumable> consumablesUsed) {

        this.consumablesUsed.setElements(consumablesUsed);
    }

    /**
     * @see Turn
     */
    @Override
    public void setDroppedItems(
            final Collection<Item> droppedItems) {

        this.droppedItems.setElements(droppedItems);
    }

    /**
     * @see Turn
     */
    @Override
    public void setFreeRunaways(
            final int freeRunaways) { successfulFreeRunaways = freeRunaways; }

    /**
     * Flags a turn as being free or not
     * 
     * @param isFreeTurn Whether the turn should be marked free
     */
    public void setFreeTurn(
            final boolean isFreeTurn) { this.isFreeTurn = isFreeTurn; }

    /**
     * @see Turn
     */
    @Override
    public void setLimitedUses( 
            final List<LimitedUse> limitedUses) {
        this.limitedUses.clear();
        for (LimitedUse use : limitedUses) {
            this.limitedUses.add(use);
        }
    }
    
    /**
     * @see Turn
     */
    @Override
    public void setMeat(
            final MeatGain meat) { this.meat = meat; }

    /**
     * @see Turn
     */
    @Override
    public void setMPGain(
            final MPGain mpGain) { this.mpGain = mpGain; }

    /**
     * @see Turn
     */
    @Override
    public void setNotes(
            final String notes) { comment.setComments(notes); }

    /**
     * @see Turn
     */
    @Override
    public void setPizzaEvents( 
            final List<PizzaEvent> pizzaEvents) {
        this.pizzaEvents.clear();
        for (PizzaEvent pe : pizzaEvents) {
            this.pizzaEvents.add(pe);
        }
    }
    
    /**
     * @see Turn
     */
    @Override
    public void setSkillsCast(
            final Collection<Skill> skillsCast) {

        this.skillsCast.setElements(skillsCast);
    }

    /**
     * @see Turn
     */
    @Override
    public void setStatGain(
            final Statgain stats) { statGain = stats; }
}

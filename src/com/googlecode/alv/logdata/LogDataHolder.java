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

package com.googlecode.alv.logdata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.googlecode.alv.logdata.LimitedUse;
import com.googlecode.alv.logdata.consumables.Consumable;
import com.googlecode.alv.logdata.summary.LevelData;
import com.googlecode.alv.logdata.summary.LogSummaryData;
import com.googlecode.alv.logdata.turn.DetailedTurnInterval;
import com.googlecode.alv.logdata.turn.SimpleTurnInterval;
import com.googlecode.alv.logdata.turn.SingleTurn;
import com.googlecode.alv.logdata.turn.Turn;
import com.googlecode.alv.logdata.turn.TurnInterval;
import com.googlecode.alv.logdata.turn.TurnVersion;
import com.googlecode.alv.logdata.turn.action.DayChange;
import com.googlecode.alv.logdata.turn.action.EquipmentChange;
import com.googlecode.alv.logdata.turn.action.FamiliarChange;
import com.googlecode.alv.logdata.turn.action.PlayerSnapshot;
import com.googlecode.alv.logdata.turn.action.Pull;
import com.googlecode.alv.util.CharacterClass;
import com.googlecode.alv.util.Counter;
import com.googlecode.alv.util.DataNumberPair;
import com.googlecode.alv.util.Lists;
import com.googlecode.alv.util.LookAheadIterator;
import com.googlecode.alv.util.Maps;
import com.googlecode.alv.util.Pair;
import com.googlecode.alv.util.Sets;

/**
 * This class is basically the representation of an ascension log. It can hold
 * all the important data accumulated during an ascension.
 * <p>
 * All methods in this class throw a {@link NullPointerException} if a null
 * object reference is passed in any parameter.
 */
public final class LogDataHolder {
    /**
     * This enumeration represents the different ascension paths of KoL.
     */
    public enum AscensionPath {
        NO_PATH("No-Path"), 
        TEETOTALER("Teetotaler"), 
        BOOZETAFARIAN("Boozetafarian"),
        OXYGENARIAN("Oxygenarian"),
        // 2011
        BEES_HATE_YOU("Bees Hate You"), 
        WAY_OF_THE_SURPRISING_FIST("Way of the Surprising Fist"),
        TRENDY("Trendy"),
        // 2012
        AVATAR_OF_BORIS("Avatar of Boris"), 
        BUGBEAR_INVASION("Bugbear Invasion"),
        ZOMBIE_SLAYER("Zombie Slayer"),
        // 2013
        AVATAR_OF_JARLSBERG("Avatar of Jarlsberg"), 
        BIG("BIG!"), 
        KOLHS("KOLHS"),
        CLASS_ACT_II("Class Act II: A Class For Pigs"),
        // 2014
        CLASS_ACT("Class Act"), // This 2012 needs to come after Class Act II as it's a substring
        AVATAR_OF_SNEAKY_PETE("Avatar of Sneaky Pete"), 
        SLOW_AND_STEADY("Slow and Steady"),
        HEAVY_RAINS("Heavy Rains"), PICKY("Picky"),
        // 2015
        STANDARD("Standard"), 
        ED("Actually Ed the Undying"), 
        OCRS("One Crazy Random Summer"),
        COMMUNITY_SERVICE("Community Service"),
        // 2016
        AVATAR_OF_WOL("Avatar of West of Loathing"), 
        THE_SOURCE("The Source"),
        NUCLEAR_AUTUMN("Nuclear Autumn"),
        // 2017
        GELATINOUS_NOOB("Gelatinous Noob"), 
        LICENSE_TO_ADVENTURE("License to Advenure"),
        LIVE_ASCEND_REPEAT("Live. Ascend. Repeat."),
        // 2018
        POCKET_FAMILIARS("Pocket Familiars"), 
        G_LOVER("G-Lover"),
        DISGUISES_DELIMIT("Disguises Delimit"),
        // 2019
        DARK_GYFFTE("Dark Gyffte"), 
        TWO_CRS("Two Crazy Random Summer"),
        KINGDOM_OF_EXPLOATHING("Kingdom of Exploathing"),
        // 2020
        PLUMBER("Path of the Plumber"), 
        NOT_DEFINED("not defined");

        private static final Map<String, AscensionPath> stringToEnum = Maps.newHashMap();

        static {
            for (final AscensionPath op : values()) {
                stringToEnum.put(op.toString(), op);
            }
        }

        /**
         * @param className The name of the adventurer's class
         * @return The enum whose toString method returns a string which is equal to the
         *         given string. If no match is found this method will return
         *         {@code NOT_DEFINED}.
         */
        public static AscensionPath fromString(
                final String className) {

            if (className == null) {
                throw new NullPointerException("Class name must not be null.");
            }

            final AscensionPath path = stringToEnum.get(className);

            return path != null ? path : NOT_DEFINED;
        }

        private final String pathName;

        AscensionPath(
                final String pathName) { this.pathName = pathName; }

        @Override
        public String toString() { return pathName; }
    }

    /**
     * This enumeration represents the different game modes of KoL.
     */
    public enum GameMode {
        CASUAL("Casual"), SOFTCORE("Softcore"), HARDCORE("Hardcore"), NOT_DEFINED("not defined");

        private static final Map<String, GameMode> stringToEnum = Maps.newHashMap();

        static {
            for (final GameMode op : values()) {
                stringToEnum.put(op.toString(), op);
            }
        }

        /**
         * @param className The name of the adventurer's class
         * @return The enum whose toString method returns a string which is equal to the
         *         given string. If no match is found this method will return
         *         {@code NOT_DEFINED}.
         */
        public static GameMode fromString(
                final String className) {

            if (className == null) {
                throw new NullPointerException("Class name must not be null.");
            }

            final GameMode mode = stringToEnum.get(className);

            return mode != null ? mode : NOT_DEFINED;
        }

        private final String modeName;

        GameMode(
                final String modeName) { this.modeName = modeName; }

        @Override
        public String toString() { return modeName; }
    }

    /**
     * This enumeration represents the different parsers which could have created a
     * parsed ascension log.
     */
    public enum ParsedLogClass {
        LOG_VISUALIZER, AFH_PARSER, NOT_DEFINED;
    }

    // Only used internally. Not useful for sorting turn intervals.
    private static final Comparator<TurnInterval> TURN_INTERVAL_COMPARATOR = new Comparator<TurnInterval>() {
        @Override
        public int compare(
                final TurnInterval o1,
                final TurnInterval o2) {

            if (!o1.getAreaName().equals(o2.getAreaName())) {
                return -1;
            }
            return o1.getEndTurn() - o2.getEndTurn();
        }
    };

    /**
     * @param map    The sorted map in which the element should be looked for.
     * @param number The number on or after which the last element in the map should
     *               be returned of.
     * @return The last element before the given number. Returns {@code null} if
     *         there is no such element.
     */
    private static <V> V getFirstElementAfterInteger(
            final SortedMap<Integer, V> map,
            final Integer number) {

        final SortedMap<Integer, V> tailMap = map.tailMap(number);

        return tailMap.isEmpty() ? null : tailMap.get(tailMap.firstKey());
    }

    /**
     * @param map    The sorted map in which the element should be looked for.
     * @param number The number before which the last element in the map should be
     *               returned of.
     * @return The last element before the given number. Returns {@code null} if
     *         there is no such element.
     */
    private static <V> V getLastElementBeforeInteger(
            final SortedMap<Integer, V> map,
            final Integer number) {

        final SortedMap<Integer, V> headMap = map.headMap(number);

        return headMap.isEmpty() ? null : headMap.get(headMap.lastKey());
    }

    private final List<SingleTurn> turnsSpent = Lists.newArrayList(1500);

    private final List<TurnInterval> turnIntervalsSpent = Lists.newArrayList(500);

    private Turn lastTurn;

    private Turn penultimateTurn;

    private final SortedMap<Integer, FamiliarChange> familiarChanges = new TreeMap<>();

    private final SortedMap<Integer, DayChange> dayChanges = new TreeMap<>();

    private final SortedMap<DayChange, HeaderFooterComment> dayHeaderFooterComments = new TreeMap<>(
            new Comparator<DayChange>() {
                @Override
                public int compare(
                        final DayChange o1,
                        final DayChange o2) {

                    return o1.getDayNumber() - o2.getDayNumber();
                }
            });

    private final SortedMap<Integer, LevelData> levels = new TreeMap<>();

    private final SortedMap<Integer, PlayerSnapshot> playerSnapshots = new TreeMap<>();

    private final SortedMap<Integer, EquipmentChange> equipmentChanges = new TreeMap<>();

    private final List<Pull> pulls = Lists.newArrayList(100);

    private final List<DataNumberPair<String>> learnedSkills = Lists.newArrayList();

    private final List<DataNumberPair<String>> hybridization = Lists.newArrayList();

    private final List<DataNumberPair<String>> huntedCombats = Lists.newArrayList();

    private final List<DataNumberPair<String>> lostCombats = Lists.newArrayList();
    
    private final boolean isDetailedLog;

    private CharacterClass characterClass = CharacterClass.NOT_DEFINED;

    private GameMode gameMode = GameMode.NOT_DEFINED;

    private AscensionPath ascensionPath = AscensionPath.NOT_DEFINED;

    private ParsedLogClass parsedLogCreator = ParsedLogClass.NOT_DEFINED;

    private boolean isSubintervalLog = false;

    private boolean isEdited = false;

    private boolean isMafiaTurnIteration = true;

    private String logName;

    private LogSummaryData logSummary;

    public LogDataHolder(
            final boolean isDetailedLog) {

        this.isDetailedLog = isDetailedLog;

        // The start of an ascension is always on day 1.
        addDayChange(new DayChange(1, 0));
        // A new ascension starts at level 1.
        levels.put(1, new LevelData(1, 0));
        // You don't have anything equipped at the start of an ascension.
        equipmentChanges.put(0, EquipmentChange.NO_EQUIPMENT);
        // The familiar is not known at the very start of an ascension.
        familiarChanges.put(0, FamiliarChange.NO_FAMILIAR);
        // A dummy turn for the start of an ascension.
        final Turn first;
        if (isDetailedLog) {
            first = new SingleTurn("Ascension Start", "Ascension Start", 0, 1,
                    getLastEquipmentChange(), getLastFamiliarChange());
            turnsSpent.add((SingleTurn) first);
        } else {
            first = new SimpleTurnInterval("Ascension Start", 0, 0);
            turnIntervalsSpent.add((TurnInterval) first);
        }
        penultimateTurn = first;
        lastTurn = first;
    }

    /**
     * @param dayChange The day change to add.
     */
    public void addDayChange(
            final DayChange dayChange) {

        if (dayChange == null) {
            throw new NullPointerException("Day change must not be null.");
        }

        int dayNumber = dayChange.getDayNumber();
        dayChanges.put(dayNumber, dayChange);
        dayHeaderFooterComments.put(dayChange, new HeaderFooterComment());
    }

    /**
     * Adds the {@link EquipmentChange}.
     * <p>
     * Note that {@link EquipmentChange}s should only be added with turn numbers
     * equal or greater than the biggest turn number in the {@link EquipmentChange}s
     * collection, otherwise the integrity of said collection cannot be guaranteed.
     *
     * @param equipmentChange The equipment change to add.
     */
    public void addEquipmentChange(
            final EquipmentChange equipmentChange) {

        if (equipmentChange == null) {
            throw new NullPointerException("Equipment change must not be null.");
        }

        final Integer turnNumber = Integer.valueOf(equipmentChange.getTurnNumber());

        // Only the last equipment change of a turn should be saved.
        equipmentChanges.remove(turnNumber);

        // If the new equipment change is to equipment that was already used
        // before the change, do not add the equipment change, because it would
        // be redundant.
        if (equipmentChanges.isEmpty()
                || !getLastEquipmentChange().equalsIgnoreTurn(equipmentChange)) {
            equipmentChanges.put(turnNumber, equipmentChange);
        }
    }

    /**
     * Adds the {@link FamiliarChange}.
     * <p>
     * Note that {@link FamiliarChange}s should only be added with turn numbers
     * equal or greater than the biggest turn number in the {@link FamiliarChange}s
     * collection, otherwise the integrity of said collection cannot be guaranteed.
     *
     * @param familiarChange The familiar change to add.
     */
    public void addFamiliarChange(
            final FamiliarChange familiarChange) {

        if (familiarChange == null) {
            throw new NullPointerException("Familiar change must not be null.");
        }

        final Integer turnNumber = Integer.valueOf(familiarChange.getTurnNumber());

        // Only the last familiar change of a turn should be saved.
        familiarChanges.remove(turnNumber);

        // If the new familiar change is to a familiar that was already used
        // before the change, do not add the familiar change, because it would
        // be redundant.
        if (familiarChanges.isEmpty() || !getLastFamiliarChange().getFamiliarName()
                .equals(familiarChange.getFamiliarName())) {
            familiarChanges.put(turnNumber, familiarChange);
        }
    }

    /**
     * @param huntedCombat The hunted combat to add.
     */
    public void addHuntedCombat(
            final DataNumberPair<String> huntedCombat) {

        if (huntedCombat == null) {
            throw new IllegalArgumentException("Hunted combat must not be null.");
        }

        huntedCombats.add(huntedCombat);
    }

    /**
     * Adds a hybridation element to the current log, it should either be a make -
     * Makes a Gene tonic hybridizing - Gives intrinsic 1/day
     * 
     * @param hybridData should be in the form {turnNumber}, {stringDescription}
     */
    public void addHybridContent(
            final DataNumberPair<String> hybridData) {

        if (hybridData == null || hybridData.getData() == null || hybridData.getNumber() == null) {
            throw new IllegalArgumentException(
                    "Hybrid data must not be null and have a turn number and description");
        }
        boolean dataAdded = false;

        for (final DataNumberPair<String> pairInMap : this.hybridization) {
            if (pairInMap.getNumber() == hybridData.getNumber()) {
                if (pairInMap.getData().startsWith(hybridData.getData())) {
                    final int ndxExisting = this.hybridization.indexOf(pairInMap);

                    String newDataString = null;
                    if (pairInMap.getData().contains("(") && pairInMap.getData().contains(")")) {
                        final int startNdx = pairInMap.getData().indexOf('(');
                        final int endNdx = pairInMap.getData().indexOf(')', startNdx);

                        if (startNdx < endNdx) {

                            final String numberString = pairInMap.getData().substring(startNdx + 1,
                                    endNdx);
                            try {
                                final int count = Integer.parseInt(numberString) + 1;
                                newDataString = pairInMap.getData().substring(0, startNdx - 1)
                                        + " (" + count + ")";
                            } catch (final NumberFormatException ex) {
                                System.out.println(ex);
                                newDataString = pairInMap.getData() + " +1";
                            }
                        } else {
                            newDataString = pairInMap.getData() + " (2)";
                        }
                    } else {
                        newDataString = hybridData.getData() + " (2)";
                    }

                    this.hybridization.remove(ndxExisting);
                    this.hybridization
                            .add(DataNumberPair.of(newDataString, hybridData.getNumber()));
                    dataAdded = true;
                    break; // Since we have modified the structure and would get a concurrent issue
                           // if we continued
                }
            }
        }

        if (!dataAdded) {
            this.hybridization.add(hybridData);
        }

    }

    /**
     * Adds a learned skill entry to the list of entries if multiple entries learned
     * on same turn will combine up to three of them in one line separated by commas
     * 
     * @param learnedSkillData the skill being learned on a given turn
     */
    public void addLearnedSkill(
            final DataNumberPair<String> learnedSkillData) {

        if (learnedSkillData == null || learnedSkillData.getData() == null
                || learnedSkillData.getNumber() == null) {
            throw new IllegalArgumentException(
                    "Learned Skill data must not be null, and contain a turn number and description");
        }
        boolean skillAdded = false;

        for (final DataNumberPair<String> skillInMap : this.learnedSkills) {
            if (skillInMap.getNumber().intValue() == learnedSkillData.getNumber().intValue()) {
                int numCommas = 0;
                int startNdx = 0;
                String skills = skillInMap.getData();
                while (numCommas < 4) {
                    if (startNdx == -1 || skills.indexOf(';', startNdx) < 0) {
                        break; // No more commas left
                    }
                    numCommas++;
                    startNdx = skills.indexOf(';', skills.indexOf(';', startNdx) + 1);
                }

                if (numCommas < 4) {
                    skills = skills + "; " + learnedSkillData.getData();
                    this.learnedSkills.remove(this.learnedSkills.indexOf(skillInMap));

                    final DataNumberPair<String> combinedEntry = DataNumberPair.of(skills,
                            learnedSkillData.getNumber());
                    this.learnedSkills.add(combinedEntry);
                    skillAdded = true;
                    break;
                }
            }
        }
        if (!skillAdded) {
            this.learnedSkills.add(learnedSkillData);
        }
    }

    /**
     * @param level The level data to add.
     */
    public void addLevel(
            final LevelData level) {

        if (level == null) {
            throw new NullPointerException("Level must not be null.");
        }

        levels.put(level.getLevelNumber(), level);
    }

    /**
     * Adds a use of a daily-limited item to the list of uses.
     *
     * @param counter Use counter applicable to the event
     * @param subUse  String denoting the use decrementing the counter
     */
    public void addLimitedUse(
            final Counter counter,
            final String subUse) {

        addLimitedUse(counter, subUse, Statgain.NO_STATS);
    }

    /**
     * Adds a use of a daily-limited item to the list of uses.  The day and turn
     * numbers are taken from the last turn spent.
     *
     * @param counter  Use counter applicable to the event
     * @param subUse   String denoting the use decrementing the counter
     * @param statgain Statgain for stats, if any, accrued by using this item
     */
    public void addLimitedUse(
            final Counter counter,
            final String subUse,
            final Statgain statgain) {

        SingleTurn turn = (SingleTurn) this.getLastTurnSpent();
        final LimitedUse use = new LimitedUse(turn.getDayNumber(), turn.getTurnNumber(), 
                counter, subUse, statgain);
        turn.addLimitedUse(use);
    }

    /**
     * @param lostCombat The lost combat to add.
     */
    public void addLostCombat(
            final DataNumberPair<String> lostCombat) {

        if (lostCombat == null) {
            throw new NullPointerException("Lost combat must not be null.");
        }

        lostCombats.add(lostCombat);
    }

    /**
     * Construct a PizzaEvent and add it to the most recent turn.  The PizzaEvent
     * is initialized with the day and turn numbers of the last turn in the
     * LogDataHolder.
     * 
     * @param description Description of the event
     * @param duration Duration of the event.  Zero if pizza creation, non-zero
     *      if an effect being acquired
     */
    public void addPizzaEvent(
            final String description,
            final int duration) {
        
        SingleTurn turn = (SingleTurn) this.getLastTurnSpent();
        final PizzaEvent event 
            = new PizzaEvent(turn.getDayNumber(), turn.getTurnNumber(), 
                             description, duration);
        turn.addPizzaEvent(event);
    }
    
    /**
     * @param playerSnapshot The player snapshot to add.
     */
    public void addPlayerSnapshot(
            final PlayerSnapshot playerSnapshot) {

        if (playerSnapshot == null) {
            throw new NullPointerException("Player snapshot must not be null.");
        }

        // Add the player snapshot.
        playerSnapshots.put(Integer.valueOf(playerSnapshot.getTurnNumber()), playerSnapshot);
    }

    /**
     * @param pull The pull to add.
     */
    public void addPull(
            final Pull pull) {

        if (pull == null) {
            throw new NullPointerException("Pull must not be null.");
        }

        pulls.add(pull);
    }

    /**
     * Add the given turn interval to the log data.
     * <p>
     * Note that the integrity of the log data cannot be guaranteed if the given
     * turn interval would be entered right in the middle of the already existing
     * turn collection.
     *
     * @param turnInterval The turn interval to add.
     * @throws IllegalStateException if this LogDataHolder is a detailed log data
     *                               holder, see {@link #isDetailedLog()}
     */
    public void addTurnIntervalSpent(
            final TurnInterval turnInterval) {

        if (turnInterval == null) {
            throw new NullPointerException("Turn interval must not be null.");
        }

        if (isDetailedLog) {
            throw new IllegalStateException(
                    "This LogDataHolder is based on a detailed log, please only add single turns.");
        }

        penultimateTurn = lastTurn;
        lastTurn = turnInterval;
        turnIntervalsSpent.add(turnInterval);
    }

    private void addTurnMafia(
            final SingleTurn turn) {

        final SingleTurn lastSingleTurn = (SingleTurn) lastTurn;
        
        if (lastTurn.getTurnNumber() == turn.getTurnNumber()) {
            // Flag the last turn as a free turn since it didn't increment the turn count
            lastSingleTurn.setFreeTurn(true);

            // Bombar Change: Needed for Florist, if we detect a free action, log zone you
            // were in
            if (lastSingleTurn.getDayNumber() == ((SingleTurn) penultimateTurn)
                    .getDayNumber()
                    && lastTurn.getAreaName().equals(penultimateTurn.getAreaName())) {
                // If the last turn has the same turn number as the to be added turn,
                // add the data of the last turn to the penultimate turn. Also, in that
                // case, check if that turn was a navel ring free runaway.
                
                // Note that the turn number of the previous turn needs to be used.
                ((SingleTurn) penultimateTurn).addEncounter(
                        lastSingleTurn.toEncounter(((SingleTurn) penultimateTurn).getTurnNumber()));
                ((SingleTurn) penultimateTurn).addSingleTurnData(lastSingleTurn);
                if (lastSingleTurn.isRanAwayOnThisTurn() 
                        && lastSingleTurn.isRunawaysEquipmentEquipped()) {
                    penultimateTurn.addFreeRunaways(1);
                }
                turnsSpent.remove(turnsSpent.size() - 1);
            } else {
                penultimateTurn = lastTurn;
            }
        } else {
            penultimateTurn = lastTurn;
        }

        lastTurn = turn; 
        turnsSpent.add(turn);
    }

    private void addTurnNotMafia(
            final SingleTurn turn) {

        if (lastTurn.getTurnNumber() == turn.getTurnNumber()) {
            // Flag the last turn as a free turn since it didn't increment the turn count
            ((SingleTurn) lastTurn).setFreeTurn(true);
        }

        // If the last turn has the same turn number as the to be added turn,
        // add the data of the to be added turn to the last turn. Also, in that
        // case, check if that turn was a navel ring free runaway.
        if (lastTurn.getAreaName().equals(turn.getAreaName())
                && ((SingleTurn) lastTurn).getDayNumber() == turn.getDayNumber()
                && lastTurn.getTurnNumber() == turn.getTurnNumber()) {
            if (turn.getFreeRunaways() == 0 && turn.isRanAwayOnThisTurn()
                    && turn.isRunawaysEquipmentEquipped()) {
                turn.addFreeRunaways(1);
            }

            ((SingleTurn) lastTurn).addEncounter(turn.toEncounter());
            ((SingleTurn) lastTurn).addSingleTurnData(turn);
        } else {
            penultimateTurn = lastTurn;
            lastTurn = turn;
            turnsSpent.add(turn);
        }
    }

    /**
     * Add the given turn to the log data.
     * <p>
     * Note that the integrity of the log data cannot be guaranteed if the given
     * single turn would be entered right in the middle of the already existing turn
     * collection.
     *
     * @param turn The single turn to add.
     * @throws IllegalStateException if this LogDataHolder is not a detailed log
     *                               data holder, see {@link #isDetailedLog()}
     */
    public void addTurnSpent(
            final SingleTurn turn) {

        if (turn == null) {
            throw new NullPointerException("Turn must not be null.");
        }

        if (!isDetailedLog) {
            throw new IllegalStateException(
                    "This LogDataHolder is not based on a detailed log, please only add turn intervals.");
        }

        if (isMafiaTurnIteration) {
            addTurnMafia(turn);
        } else {
            addTurnNotMafia(turn);
        }
    }

    /**
     * Creates the log summary from the data of this log.
     * <p>
     * Calling this method should be done after all data additions are finished
     * (through parsing a log or otherwise). Otherwise, the summary will be
     * incomplete.
     * <p>
     * If this LogDataHolder is backed by a detailed log (see
     * {@link #isDetailedLog()}), this method will also create the turn intervals
     * from the single turn collection.
     */
    public void createLogSummary() {

        if (isDetailedLog) {
            final LookAheadIterator<SingleTurn> index = new LookAheadIterator<>(
                    turnsSpent.iterator());
            final LookAheadIterator<SingleTurn> worker = new LookAheadIterator<>(
                    turnsSpent.iterator());

            while (index.hasNext()) {
                SingleTurn turn = index.next();
                SingleTurn other;
                boolean isTurnFreeInterval = true;

                do {
                    other = worker.next();
                    if (other.getAreaName().equals(turn.getAreaName())) {
                        if (!other.isFreeTurn()) {
                            isTurnFreeInterval = false;
                        }
                    }

                } while (worker.hasNext()
                        && worker.peek().getAreaName().equals(turn.getAreaName()));

                final TurnInterval interval = new DetailedTurnInterval(turn, isTurnFreeInterval);

                while (index.hasNext()) {
                    turn = index.peek();
                    if (turn.getAreaName().equals(interval.getAreaName())) {
                        interval.addTurn(turn);
                        index.next();
                    } else {
                        break;
                    }
                }

                turnIntervalsSpent.add(interval);
            }
        }

        logSummary = new LogSummaryData(this);
    }

    public List<CombatItem> getAllCombatItemsUsed() { return logSummary.getCombatItemsUsed(); }

    /**
     * Returns a list of all consumables used during this ascension.
     * <p>
     * Note that this a convenience method. It is equal to
     * {@code logData.getLogSummary().getAllConsumablesUsed()}.
     *
     * @return A list of all consumables used during this ascension.
     */
    public List<Consumable> getAllConsumablesUsed() {

        return logSummary.getAllConsumablesUsed();
    }

    /**
     * Returns a list of all skills cast during this ascension.
     * <p>
     * Note that this a convenience method. It is equal to
     * {@code logData.getLogSummary().getSkillsCast()}.
     *
     * @return A list of all skills cast during this ascension.
     */
    public List<Skill> getAllSkillsCast() { return logSummary.getSkillsCast(); }

    /**
     * @return The ascension path of this ascension log. If no ascension path has
     *         been specified this method will return
     *         {@link AscensionPath#NOT_DEFINED}.
     */
    public AscensionPath getAscensionPath() { return ascensionPath; }

    /**
     * @return The character class of this ascension log. If no character class has
     *         been specified this method will return
     *         {@link CharacterClass#NOT_DEFINED} .
     */
    public CharacterClass getCharacterClass() { return characterClass; }

    /**
     * @return A list of all turn intervals containing somehow copied monsters.
     */
    public List<TurnInterval> getCopiedTurns() {

        if (turnIntervalsSpent.isEmpty()) {
            throw new IllegalStateException("There are no turn intervals present.");
        }

        final List<TurnInterval> results = Lists.newArrayList();

        for (final TurnInterval ti : turnIntervalsSpent) {
            final String areaName = ti.getAreaName().toLowerCase(Locale.ENGLISH);

            if (areaName.equals("spooky putty monster") || areaName.equals("shaking 4-d camera")
                    || areaName.equals("photocopied monster")
                    || areaName.equals("rain-doh box full of monster")
                    || areaName.equals("ice sculpture") || areaName.equals("rain man")
                    || areaName.equals("chateau painting")) {
                results.add(ti);
            }
        }

        return results;
    }

    /**
     * @param turnNumber The turn number specifying the point of which the day is
     *                   wanted.
     * @return The day of the given turn number.
     * @throws IllegalArgumentException if turn is negative.
     */
    public DayChange getCurrentDay(
            final int turnNumber) {

        if (turnNumber < 0) {
            throw new IllegalArgumentException("Turn number cannot be negative.");
        }

        // Initialise with day 1, because it is always present.
        DayChange currentDay = dayChanges.get(Integer.valueOf(1));
        for (final DayChange day : getDayChanges()) {
            // If the turn number of the day change is higher than the specified
            // turn number, stop the loop.
            if (day.getTurnNumber() > turnNumber) {
                break;
            }

            // As long as loop isn't stopped, the checked day change happened
            // before the given turn number.
            currentDay = day;
        }

        return currentDay;
    }

    /**
     * @return the current day number according to the LogDataHolder.  May not be the
     *      same as the day of the last turn spent
     */
    public int getCurrentDayNumber() {
        DayChange dayChange = getLastDayChange();
        if (dayChange == null) {
            return 1;
        }
        return dayChange.getDayNumber();
    }
    
    /**
     * @param turnNumber The turn number specifying the point of which the level is
     *                   wanted.
     * @return The level reached at the given turn number.
     * @throws IllegalArgumentException if turn is negative.
     */
    public LevelData getCurrentLevel(
            final int turnNumber) {

        if (turnNumber < 0) {
            throw new IllegalArgumentException("Turn number cannot be negative.");
        }

        // Initialise with level 1, because it is always present.
        LevelData currentLevel = levels.get(Integer.valueOf(1));
        for (final LevelData level : getLevels()) {
            // If the turn number of the level change is higher than the
            // specified turn number, stop the loop.
            if (level.getLevelReachedOnTurn() > turnNumber) {
                break;
            }

            // As long as loop isn't stopped, the checked level change happened
            // before the given turn number.
            currentLevel = level;
        }

        return currentLevel;
    }

    /**
     * Returns a sorted collection of all day changes of this ascension log.
     * <p>
     * Note that the given collection and its contents is directly backed by the
     * internal collections of this class. This means that changing elements will in
     * the same way effect the internal collections. To ensure that the underlying
     * collection is not corrupted, the returned collection is read-only.
     *
     * @return The day changes.
     */
    public Collection<DayChange> getDayChanges() {

        return Collections.unmodifiableCollection(dayChanges.values());
    }

    /**
     * Returns a list of all dropped items during this ascension. The list is not
     * sorted.
     * <p>
     * Note that this a convenience method. It is equal to
     * {@code logData.getLogSummary().getDroppedItems()}.
     *
     * @return A list of all dropped items during this ascension.
     */
    public List<Item> getDroppedItems() { return logSummary.getDroppedItems(); }

    /**
     * Returns a sorted collection of all equipment changes of this ascension log.
     * <p>
     * Note that the given collection and its contents is directly backed by the
     * internal collections of this class. This means that changing elements will in
     * the same way effect the internal collections. To ensure that the underlying
     * collection is not corrupted, the returned collection is read-only.
     *
     * @return The equipment changes.
     */
    public Collection<EquipmentChange> getEquipmentChanges() {

        return Collections.unmodifiableCollection(equipmentChanges.values());
    }

    /**
     * Returns a sorted collection of all familiar changes of this ascension log.
     * <p>
     * Note that the given collection and its contents is directly backed by the
     * internal collections of this class. This means that changing elements will in
     * the same way effect the internal collections. To ensure that the underlying
     * collection is not corrupted, the returned collection is read-only.
     *
     * @return The familiar changes.
     */
    public Collection<FamiliarChange> getFamiliarChanges() {

        return Collections.unmodifiableCollection(familiarChanges.values());
    }

    /**
     * @param turn The turn number after which the first equipment change should be
     *             returned of.
     * @return The first equipment change on or after the given turn of this
     *         ascension. Returns {@code null} if there are no such equipment
     *         changes.
     * @throws IllegalArgumentException if turn is negative.
     */
    public EquipmentChange getFirstEquipmentChangeAfterTurn(
            final int turn) {

        if (turn < 0) {
            throw new IllegalArgumentException("Turn number cannot be negative.");
        }

        return getFirstElementAfterInteger(equipmentChanges, Integer.valueOf(turn));
    }

    /**
     * @param turn The turn number after which the first familiar change should be
     *             returned of.
     * @return The first familiar change on or after the given turn of this
     *         ascension. Returns {@code null} if there are no such familiar
     *         changes.
     * @throws IllegalArgumentException if turn is negative.
     */
    public FamiliarChange getFirstFamiliarChangeAfterTurn(
            final int turn) {

        if (turn < 0) {
            throw new IllegalArgumentException("Turn number cannot be negative.");
        }

        return getFirstElementAfterInteger(familiarChanges, Integer.valueOf(turn));
    }

    /**
     * @param turn The turn number after which the first player snapshot should be
     *             returned of.
     * @return The first player snapshot on or after the given turn of this
     *         ascension. Returns {@code null} if there are no such player
     *         snapshots.
     * @throws IllegalArgumentException if turn is negative.
     */
    public PlayerSnapshot getFirstPlayerSnapshotAfterTurn(
            final int turn) {

        if (turn < 0) {
            throw new IllegalArgumentException("Turn number cannot be negative.");
        }

        return getFirstElementAfterInteger(playerSnapshots, Integer.valueOf(turn));
    }

    /**
     * @return The game mode of this ascension log. If no game mode has been
     *         specified this method will return {@link GameMode#NOT_DEFINED}.
     */
    public GameMode getGameMode() { return gameMode; }

    /**
     * @param dc Day in run
     * @return The header/footer comment of the given day.
     */
    public HeaderFooterComment getHeaderFooterComment(
            final DayChange dc) {

        return dayHeaderFooterComments.get(dc);
    }

    /**
     * Returns a sorted collection of all header/footer comments per day of this
     * ascension log.
     * <p>
     * Note that the given collection and its contents is directly backed by the
     * internal collections of this class. This means that changing elements will in
     * the same way effect the internal collections.
     *
     * @return The header/footer comment and the day it is mapped to.
     */
    public Collection<Pair<DayChange, HeaderFooterComment>> getHeaderFooterComments() {

        final List<Pair<DayChange, HeaderFooterComment>> result = Lists.newArrayList();
        for (final Entry<DayChange, HeaderFooterComment> e : dayHeaderFooterComments.entrySet()) {
            result.add(Pair.of(e.getKey(), e.getValue()));
        }

        return result;
    }

    /**
     * Returns a list of all hunted combats. This list is empty if no combats were
     * hunted.
     * <p>
     * Note that while this list usually is sorted after the turn numbers of the
     * combats, this cannot be guaranteed.
     * <p>
     * Also, please note that the given list is read-only.
     *
     * @return The hunted combats.
     */
    public List<DataNumberPair<String>> getHuntedCombats() {

        return Collections.unmodifiableList(huntedCombats);
    }

    public List<DataNumberPair<String>> getHybridContent() {

        return Collections.unmodifiableList(this.hybridization);
    }

    /**
     * @return The last day change of this ascension. Returns {@code null} if there
     *         are no day changes.
     */
    public DayChange getLastDayChange() {

        return dayChanges.isEmpty() ? null : dayChanges.get(dayChanges.lastKey());
    }

    /**
     * @return The last equipment change of this ascension. Returns {@code null} if
     *         there are no equipment changes.
     */
    public EquipmentChange getLastEquipmentChange() {

        return equipmentChanges.isEmpty() ? null : equipmentChanges.get(equipmentChanges.lastKey());
    }

    /**
     * @param turn The turn number before which the last equipment change should be
     *             returned of.
     * @return The last equipment change before the given turn of this ascension.
     *         Returns {@code null} if there are no such equipment changes.
     * @throws IllegalArgumentException if turn is negative.
     */
    public EquipmentChange getLastEquipmentChangeBeforeTurn(
            final int turn) {

        if (turn < 0) {
            throw new IllegalArgumentException("Turn number cannot be negative.");
        }

        return getLastElementBeforeInteger(equipmentChanges, Integer.valueOf(turn));
    }

    /**
     * @return The last familiar change of this ascension. Returns {@code null} if
     *         there are no familiar changes.
     */
    public FamiliarChange getLastFamiliarChange() {

        return familiarChanges.isEmpty() ? null : familiarChanges.get(familiarChanges.lastKey());
    }

    /**
     * @param turn The turn number before which the last familiar change should be
     *             returned of.
     * @return The last familiar change before the given turn of this ascension.
     *         Returns {@code null} if there are no such familiar changes.
     * @throws IllegalArgumentException if turn is negative.
     */
    public FamiliarChange getLastFamiliarChangeBeforeTurn(
            final int turn) {

        if (turn < 0) {
            throw new IllegalArgumentException("Turn number cannot be negative.");
        }

        return getLastElementBeforeInteger(familiarChanges, Integer.valueOf(turn));
    }

    /**
     * @return The last header/footer comment.
     */
    public HeaderFooterComment getLastHeaderFooterComment() {

        return dayHeaderFooterComments.get(getLastDayChange());
    }

    /**
     * @return The last level reached of this ascension. Returns {@code null} if
     *         there are no levels reached.
     */
    public LevelData getLastLevel() {

        return levels.isEmpty() ? null : levels.get(levels.lastKey());
    }

    /**
     * @return The last player snapshot of this ascension. Returns {@code null} if
     *         there are no player snapshots.
     */
    public PlayerSnapshot getLastPlayerSnapshot() {

        return playerSnapshots.isEmpty() ? null : playerSnapshots.get(playerSnapshots.lastKey());
    }

    /**
     * @param turn The turn number before which the last player snapshot should be
     *             returned of.
     * @return The last player snapshot before the given turn of this ascension.
     *         Returns {@code null} if there are no such player snapshots.
     * @throws IllegalArgumentException if turn is negative.
     */
    public PlayerSnapshot getLastPlayerSnapshotBeforeTurn(
            final int turn) {

        if (turn < 0) {
            throw new IllegalArgumentException("Turn number cannot be negative.");
        }

        return getLastElementBeforeInteger(playerSnapshots, Integer.valueOf(turn));
    }

    /**
     * @return The last turn spent.  If the current day is greater than the day number
     *      of the last turn, then a new Start of Day turn is added and that new turn
     *      is returned.
     */
    public Turn getLastTurnSpent() { 
        
        final SingleTurn lastSingleTurn = (SingleTurn) lastTurn;
        
        if (getCurrentDayNumber() > lastSingleTurn.getDayNumber()) {
            // The turn number is that of the previous turn, unless that turn
            // wasn't free, then we add 1
            int turnNumber = lastSingleTurn.getTurnNumber();
            if (! lastSingleTurn.isFreeTurn()) {
                turnNumber ++;
            }
            if (isDetailedLog) {
                SingleTurn startOfDay = new SingleTurn("Start of Day", "Start of Day", 
                        turnNumber, getCurrentDayNumber(),
                        getLastEquipmentChange(), getLastFamiliarChange());
                startOfDay.setFreeTurn(true);
                startOfDay.setTurnVersion(TurnVersion.OTHER);
                penultimateTurn = lastTurn;
                turnsSpent.add(startOfDay);
                lastTurn = startOfDay; 
            } else {
                // TODO Figure out what to do if we get here
            }

        }
        return lastTurn; 
    }

    public List<DataNumberPair<String>> getLearnedSkills() {

        return Collections.unmodifiableList(this.learnedSkills);
    }

    /**
     * Returns a sorted collection of all level data of this ascension log.
     * <p>
     * Note that the given collection and its contents is directly backed by the
     * internal collections of this class. This means that changing elements will in
     * the same way effect the internal collections. To ensure that the underlying
     * collection is not corrupted, the returned collection is read-only.
     *
     * @return The level data.
     */
    public Collection<LevelData> getLevels() {

        return Collections.unmodifiableCollection(levels.values());
    }

    /**
     * 
     * @return The limited uses that have been accumulated to this point.  This is
     *      done by gathering them from the turn intervals.
     */
    public List<LimitedUse> getLimitedUses() {

        ArrayList<LimitedUse> limitedUses = new ArrayList<>();
        for (TurnInterval st : this.getTurnIntervalsSpent()) {
            for (LimitedUse use : st.getLimitedUses()) {
                limitedUses.add(use);
            }
        }
        return Collections.unmodifiableList(limitedUses);
    }

    /**
     * The name of this ascension log. The Format <b>usually</b> is:
     * CharacterName-StartdateOfAscension
     *
     * @return The name of this ascension log.
     */
    public String getLogName() { return logName; }

    /**
     * @return A summary of various parts of this ascension log.
     * @throws IllegalStateException if this method is called before a log summary
     *                               is created by calling
     *                               {@link #createLogSummary()}
     */
    public LogSummaryData getLogSummary() {

        if (logSummary == null) {
            throw new IllegalStateException("Log summary has to be created first.");
        }

        return logSummary;
    }

    /**
     * Returns a list of all lost combats. This list is empty if no combats were
     * lost.
     * <p>
     * Note that while this list usually is sorted after the turn numbers of the
     * combats, this cannot be guaranteed.
     * <p>
     * Also, please note that the given list is read-only.
     *
     * @return The lost combats.
     */
    public List<DataNumberPair<String>> getLostCombats() {

        return Collections.unmodifiableList(lostCombats);
    }

    /**
     * If this LogDataHolder hasn't been created by a parsed ascension log or the
     * log creator hasn't been set (most probably because it couldn't be
     * determined), this method should return {@code NOT_DEFINED}.
     *
     * @return The program which created the parsed ascension log behind this
     *         LogDataHolder.
     */
    public ParsedLogClass getParsedLogCreator() { return parsedLogCreator; }

    /**
     * Returns a sorted collection of all player snapshots of this ascension log.
     * <p>
     * Note that the given collection and its contents is directly backed by the
     * internal collections of this class. This means that changing elements will in
     * the same way effect the internal collections. To ensure that the underlying
     * collection is not corrupted, the returned collection is read-only.
     *
     * @return The player snapshots.
     */
    public Collection<PlayerSnapshot> getPlayerSnapshots() {

        return Collections.unmodifiableCollection(playerSnapshots.values());
    }

    /**
     * Returns the pull list. This list is empty if no pulls were made.
     * <p>
     * Note that while this list usually is sorted after the turn numbers of the
     * pulls, this cannot be guaranteed.
     * <p>
     * Also, please note that the given list and its contents is directly backed by
     * the internal collections of this class. This means that changing elements
     * will in the same way effect the internal collections. Therefore, great care
     * should be taken when working with this collection. To ensure that the
     * underlying collection is not corrupted through remove or add operations, the
     * returned collection is read-only.
     *
     * @return The pulls.
     */
    public List<Pull> getPulls() { return Collections.unmodifiableList(pulls); }

    /**
     * Returns a sub interval of this LogDataHolder that includes all turns and
     * other data that is inside the given interval (both {@code startTurn} and
     * {@code endTurn} are inclusive).
     * <p>
     * Note that in case this LogDataHolder is based on a detailed log (see
     * {@link #isDetailedLog()}) turn intervals that start before the interval, but
     * end inside it will be included in the returned LogDataHolder. The same is
     * true for turn intervals that start inside the interval and end outside it.
     *
     * @param startTurn The start of the interval.
     * @param endTurn   The end of the interval.
     * @return A LogDataHolder including all the data inside the given start and end
     *         points.
     * @throws IllegalArgumentException if {@code endTurn} is not greater than
     *                                  {@code startTurn}; if {@code endTurn} is not
     *                                  greater than 0
     */
    public LogDataHolder getSubIntervalLogData(
            final int startTurn,
            final int endTurn) {

        if (endTurn <= startTurn) {
            throw new IllegalArgumentException("The end turn must be greater than the start turn.");
        }
        if (endTurn <= 0) {
            throw new IllegalArgumentException("The end turn must be greater than zero.");
        }

        final LogDataHolder subLog = new LogDataHolder(isDetailedLog);
        subLog.isSubintervalLog = true;
        subLog.logName = logName;
        subLog.parsedLogCreator = parsedLogCreator;
        subLog.characterClass = characterClass;
        // Remove "Ascension Start" turn interval that is always included in a
        // newly created LogDataHolder.
        subLog.turnsSpent.clear();
        // Remove the "none" familiar from the familiar change list.
        subLog.familiarChanges.clear();
        // Remove day 1 from the log, it'll get added again, if its part of the
        // log interval.
        subLog.dayChanges.clear();

        // Add turns.
        if (isDetailedLog) {
            for (final SingleTurn st : turnsSpent) {
                // Stop the iteration once we are outside the interval
                if (st.getTurnNumber() > endTurn) {
                    break;
                }

                // Inside interval
                if (st.getTurnNumber() >= startTurn && st.getTurnNumber() <= endTurn) {
                    subLog.turnsSpent.add(st);
                }
            }
        } else {
            for (final TurnInterval ti : turnIntervalsSpent) {
                // Stop the iteration once we are outside the interval
                if (ti.getStartTurn() > endTurn) {
                    break;
                }

                // Both start and end are inside the interval
                if (ti.getStartTurn() >= startTurn && ti.getEndTurn() <= endTurn) {
                    subLog.turnIntervalsSpent.add(ti);
                }

                // The start is not, but the end is inside the interval
                if (ti.getEndTurn() <= endTurn && ti.getEndTurn() > startTurn) {
                    subLog.turnIntervalsSpent.add(ti);
                }

                // The start is inside the interval, but the end is not
                if (ti.getStartTurn() >= startTurn && ti.getStartTurn() < endTurn) {
                    subLog.turnIntervalsSpent.add(ti);
                }
            }
        }

        // Add familiar changes
        final FamiliarChange famChange = getLastFamiliarChangeBeforeTurn(startTurn);
        if (famChange != null) {
            subLog.addFamiliarChange(famChange);
        }
        for (final FamiliarChange fc : getFamiliarChanges()) {
            // Stop the iteration once we are outside the interval
            if (fc.getTurnNumber() > endTurn) {
                break;
            }

            // Add if inside the interval
            if (fc.getTurnNumber() >= startTurn && fc.getTurnNumber() <= endTurn) {
                subLog.addFamiliarChange(fc);
            }
        }

        // Add day changes
        for (final DayChange dc : getDayChanges()) {
            // Stop the iteration once we are outside the interval
            if (dc.getTurnNumber() > endTurn) {
                break;
            }

            // Add if inside the interval
            if (dc.getTurnNumber() >= startTurn && dc.getTurnNumber() < endTurn) {
                subLog.addDayChange(dc);
            }
        }
        // If present, the last day change before the first day change of the
        // sub interval should also be added, it might be necessary in case the
        // sub log starts at the beginning of a day.
        final DayChange previousDay = getLastElementBeforeInteger(dayChanges,
                subLog.dayChanges.get(subLog.dayChanges.firstKey()).getDayNumber());
        if (previousDay != null) {
            subLog.addDayChange(previousDay);
        }

        // Add header/footer comments
        subLog.dayHeaderFooterComments.clear();
        for (final Pair<DayChange, HeaderFooterComment> p : getHeaderFooterComments()) {
            final int turnNumber = p.getVar1().getTurnNumber();

            // Stop the iteration once we are outside the interval
            if (turnNumber > endTurn) {
                break;
            }

            // Add if inside the interval
            if (turnNumber >= startTurn && turnNumber < endTurn) {
                subLog.dayHeaderFooterComments.put(p.getVar1(), p.getVar2());
            }
        }

        // Add level changes
        LevelData lastLevelBeforeInterval = null;
        for (final LevelData ld : getLevels()) {
            // Stop the iteration once we are outside the interval
            if (ld.getLevelReachedOnTurn() > endTurn) {
                break;
            }

            if (ld.getLevelReachedOnTurn() < startTurn) {
                lastLevelBeforeInterval = ld;
            }

            // Add if inside the interval
            if (ld.getLevelReachedOnTurn() >= startTurn && ld.getLevelReachedOnTurn() <= endTurn) {
                subLog.addLevel(ld);
            }
        }
        if (lastLevelBeforeInterval != null) {
            subLog.addLevel(lastLevelBeforeInterval);
        }

        // Add player snapshots
        final PlayerSnapshot playSnap = getLastPlayerSnapshotBeforeTurn(startTurn);
        if (playSnap != null) {
            subLog.addPlayerSnapshot(playSnap);
        }
        for (final PlayerSnapshot ps : getPlayerSnapshots()) {
            // Stop the iteration once we are outside the interval
            if (ps.getTurnNumber() > endTurn) {
                break;
            }

            // Add if inside the interval
            if (ps.getTurnNumber() >= startTurn && ps.getTurnNumber() < endTurn) {
                subLog.addPlayerSnapshot(ps);
            }
        }

        // Add equipment changes
        final EquipmentChange equipChange = getLastEquipmentChangeBeforeTurn(startTurn);
        if (equipChange != null) {
            subLog.addEquipmentChange(equipChange);
        }
        for (final EquipmentChange ec : getEquipmentChanges()) {
            // Stop the iteration once we are outside the interval
            if (ec.getTurnNumber() > endTurn) {
                break;
            }

            // Add if inside the interval
            if (ec.getTurnNumber() > startTurn && ec.getTurnNumber() < endTurn) {
                subLog.addEquipmentChange(ec);
            }
        }

        // Add pulls
        final Set<Integer> includedDays = Sets.newHashSet();
        for (final DayChange dc : subLog.getDayChanges()) {
            includedDays.add(dc.getDayNumber());
        }
        for (final Pull p : getPulls()) {
            // Stop the iteration once we are outside the interval
            if (p.getTurnNumber() > endTurn) {
                break;
            }

            // Add if inside the interval
            if (p.getTurnNumber() >= startTurn && p.getTurnNumber() <= endTurn
                    && includedDays.contains(p.getDayNumber())) {
                subLog.addPull(p);
            }
        }

        // Add hunted combats
        for (final DataNumberPair<String> dnp : getHuntedCombats()) {
            final int turnNumber = dnp.getNumber();
            // Stop the iteration once we are outside the interval
            if (turnNumber > endTurn) {
                break;
            }

            // Add if inside the interval
            if (turnNumber >= startTurn && turnNumber <= endTurn) {
                subLog.addHuntedCombat(dnp);
            }
        }

        // Add lost combats
        for (final DataNumberPair<String> dnp : getLostCombats()) {
            final int turnNumber = dnp.getNumber();
            // Stop the iteration once we are outside the interval
            if (turnNumber > endTurn) {
                break;
            }

            // Add if inside the interval
            if (turnNumber >= startTurn && turnNumber <= endTurn) {
                subLog.addLostCombat(dnp);
            }
        }

        // Create log summary based on the sub interval
        subLog.createLogSummary();

        // Set log comments of the turn intervals in the subinterval log to use
        // the same LogComment objects as their counterparts in the full log.
        int currentIndex = 0;
        for (final TurnInterval ti : subLog.getTurnIntervalsSpent()) {
            for (int i = currentIndex; i < turnIntervalsSpent.size(); i++) {
                if (TURN_INTERVAL_COMPARATOR.compare(ti, turnIntervalsSpent.get(i)) == 0) {
                    ti.setPreIntervalComment(turnIntervalsSpent.get(i).getPreIntervalComment());
                    ti.setPostIntervalComment(turnIntervalsSpent.get(i).getPostIntervalComment());
                    currentIndex = i + 1;
                    break;
                }
            }
        }

        return subLog;
    }

    /**
     * Returns a list of all turn intervals of this ascension log.
     * <p>
     * Note that the given collection and its contents is directly backed by the
     * internal collections of this class. This means that changing elements will in
     * the same way effect the internal collections, so great care should be taken
     * when doing so.
     * <p>
     * Also, note that the returned collection is read-only.
     *
     * @return The turns spent.
     * @throws IllegalStateException if this LogDataHolder is a detailed log data
     *                               holder, it first needs to create the turn
     *                               interval collection through a call of
     *                               {@link #createLogSummary()} before it can be
     *                               accessed
     */
    public List<TurnInterval> getTurnIntervalsSpent() {

        if (turnIntervalsSpent.isEmpty()) {
            throw new IllegalStateException(
                    "The turn interval collection has to be created before you can access it.");
        }

        return Collections.unmodifiableList(turnIntervalsSpent);
    }

    /**
     * Returns a list of all turns of this ascension log.
     * <p>
     * Note that the given collection and its contents is directly backed by the
     * internal collections of this class. This means that changing elements will in
     * the same way effect the internal collections, so great care should be taken
     * when doing so.
     * <p>
     * Also, note that the returned collection is read-only.
     *
     * @return The turns spent.
     * @throws IllegalStateException if this LogDataHolder is not a detailed log
     *                               data holder, see {@link #isDetailedLog()}
     */
    public List<SingleTurn> getTurnsSpent() {

        if (!isDetailedLog) {
            throw new IllegalStateException("Only detailed LogDataHolders contain single turns!");
        }

        return Collections.unmodifiableList(turnsSpent);
    }

    public void handleParseFinished() {

        if (lastTurn.getTurnNumber() == penultimateTurn.getTurnNumber()) {
            if (lastTurn.getAreaName().equals(penultimateTurn.getAreaName())) {
                final SingleTurn tmp = (SingleTurn) lastTurn;

                // Note that the turn number of the previous turn needs to be used.
                ((SingleTurn) penultimateTurn).addEncounter(
                        tmp.toEncounter(((SingleTurn) penultimateTurn).getTurnNumber()));
                ((SingleTurn) penultimateTurn).addSingleTurnData(tmp);
                if (tmp.isRanAwayOnThisTurn() && tmp.isRunawaysEquipmentEquipped()) {
                    penultimateTurn.addFreeRunaways(1);
                }
                turnsSpent.remove(turnsSpent.size() - 1);
            }
        }
    }

    /**
     * A detailed log is a log that is based on a single turn-by-turn account of the
     * ascension, whereas a non-detailed log is based on turn intervals only. In
     * general, this is the difference between a pre-parsed ascension log and a log
     * based on data directly produced by a program that logs all your actions while
     * you play the game (e.g. KolMafia).
     *
     * @return {@code true} if this LogDataHolder is a based on a detailed log,
     *         otherwise {@code false}.
     */
    public boolean isDetailedLog() { return isDetailedLog; }

    /**
     * This flag shows whether this LogDataHolder has been edited. (e.g. some log
     * notes were added)
     * <p>
     * Note that this flag has to be set manually by the use site that edited the
     * log data. (see {@link #setEdited(boolean)})
     *
     * @return True if this LogDataHolder has been edited. (e.g. some log notes were
     *         added)
     */
    public boolean isEdited() { return isEdited; }

    /**
     * This flag shows whether this LogDataHolder handles single turn additions
     * correctly with respect to how KolMafia logs them in its sessions logs.
     * <p>
     * This is a specific handling of multiple turns with the same turn number.
     * KolMafia logs them with the turn that actually took an adventure appearing at
     * the end and thus all the non-adventure-spending turns having to be appended
     * to the last turn before their turn number. To give an example:
     *
     * <pre>
     * [9] Cobb's Knob
     * [10] Haunted Pantry
     * [10] Haunted Pantry
     * [10] Haunted Pantry
     * [10] Haunted Pantry
     * [11] Haunted Billiards Room
     * </pre>
     *
     * In this example only the Cobb's Knob turn, the last Haunted Pantry turn and
     * the Haunted Billiards Room turn took an adventure and the data of the first
     * three Haunted Pantry turns would have to added to the Cobb's Knob turn as
     * they still happened on [9] as the actual turn number.
     * <p>
     * With this flag on {@code false}, the above interval would be seen as the
     * Cobb's Knob turn, the first Haunted Pantry turn and the Haunted Billiards
     * Room turn taking an adventure and the data of the last three Haunted Pantry
     * turns being appended to the first Haunted Pantry turn.
     * <p>
     * The default value of this flag is {@code true}.
     *
     * @return The flag as to whether turn additions will be added as KolMafia logs
     *         them. The default is {@code true}.
     */
    public boolean isMafiaTurnIteration() { return isMafiaTurnIteration; }

    /**
     * @return True if this LogDataHolder instance is a subinterval of another
     *         LogDataHolder, otherwise false.
     */
    public boolean isSubintervalLog() { return isSubintervalLog; }

    /**
     * This method will set the ascension path of this ascension log.
     *
     * @param ascensionPath The ascension path to set.
     */
    public void setAscensionPath(
            final AscensionPath ascensionPath) {

        this.ascensionPath = ascensionPath;
    }

    /**
     * This method will set the character class of this ascension log.
     *
     * @param characterClass The character class to set.
     */
    public void setCharacterClass(
            final CharacterClass characterClass) {

        this.characterClass = characterClass;
    }

    /**
     * This method will set the character class of this ascension log based on the
     * given string. If the string doesn't match any of the character class names,
     * the character class of this ascension log will be set to {@code NOT_DEFINED}.
     *
     * @param characterClassName The name of the character class to set.
     */
    public void setCharacterClass(
            final String characterClassName) {

        setCharacterClass(CharacterClass.fromString(characterClassName));
    }

    /**
     * Sets the flag of whether this LogDataHolder has been edited in some way.
     * (e.g. some log notes were added)
     *
     * @param isEdited The flag to set.
     */
    public void setEdited(
            final boolean isEdited) { this.isEdited = isEdited; }

    /**
     * Sets the equipment changes of this ascension log.
     *
     * Note that the given collection does not need to be sorted, nor is it needed
     * to remove redundant equipment change entries.
     *
     * @param equipmentChanges Collection of equipment changes
     */
    public void setEquipmentChanges(
            final Collection<EquipmentChange> equipmentChanges) {

        final List<EquipmentChange> sortedList = Lists.sort(Lists.newArrayList(equipmentChanges));

        // Clear the old familiar change collection and add the new items.
        this.equipmentChanges.clear();
        for (final EquipmentChange ec : sortedList) {
            addEquipmentChange(ec);
        }
    }

    /**
     * Sets the familiar changes of this ascension log.
     * <p>
     * Note that the given collection does not need to be sorted, nor is it needed
     * to remove redundant familiar change entries.
     * 
     * @param familiarChanges Familiar change collection
     */
    public void setFamiliarChanges(
            final Collection<FamiliarChange> familiarChanges) {

        final List<FamiliarChange> sortedList = Lists.sort(Lists.newArrayList(familiarChanges));

        // Clear the old familiar change collection and add the new items.
        this.familiarChanges.clear();
        for (final FamiliarChange fc : sortedList) {
            addFamiliarChange(fc);
        }
    }

    /**
     * This method will set the game mode of this ascension log.
     *
     * @param gameMode The game mode to set.
     */
    public void setGameMode(
            final GameMode gameMode) { this.gameMode = gameMode; }

    /**
     * Set the name of this ascension log. The format should be
     * CharacterName-StartdateOfAscension.
     *
     * @param logName The name of this log.
     */
    public void setLogName(
            final String logName) {

        if (logName == null) {
            throw new NullPointerException("Log name must not be null.");
        }

        this.logName = logName;
    }

    /**
     * Sets the flag of whether turn additions will be added as KolMafia logs them.
     * (see {@link #isMafiaTurnIteration()} for detailed explanation of what that
     * means)
     *
     * @param isMafiaTurnIteration The flag to set.
     */
    public void setMafiaTurnIteration(
            final boolean isMafiaTurnIteration) {

        this.isMafiaTurnIteration = isMafiaTurnIteration;
    }

    /**
     * @param parsedLogCreator The program which created the parsed ascension log
     *                         behind this LogDataHolder to set.
     */
    public void setParsedLogCreator(
            final ParsedLogClass parsedLogCreator) {

        if (parsedLogCreator == null) {
            throw new NullPointerException("The parsed log creator must not be null.");
        }

        this.parsedLogCreator = parsedLogCreator;
    }
}

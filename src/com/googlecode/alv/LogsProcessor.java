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

package com.googlecode.alv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.alv.creator.TextLogCreator;
import com.googlecode.alv.creator.XMLLogCreator;
import com.googlecode.alv.logdata.LogDataHolder;
import com.googlecode.alv.logdata.turn.Encounter;
import com.googlecode.alv.parser.MafiaLogParser;
import com.googlecode.alv.parser.UsefulPatterns;
import com.googlecode.alv.util.Constants;
import com.googlecode.alv.util.Lists;
import com.googlecode.alv.util.LogOutputFormat;
import com.googlecode.alv.util.LogsCache;
import com.googlecode.alv.util.Pair;

/**
 * This class gives access to methods to create condensed mafia ascension logs
 * and parsed ascension logs.
 * <p>
 * Note that all methods of this class are thread-safe as long as not the same
 * files are given to different threads or the created files by the given method
 * have the same file name and are stored in the same directory.
 */
public final class LogsProcessor {

    /**
     * A helper class to condense mafia logs into holding a single ascension per
     * file.
     */
    private static final class CondensedMafiaLogsCreator {
        private static final Pattern NOT_USER_NAME_PATTERN = Pattern.compile("_\\d+\\.txt");

        private static final Pattern ASCENDED_PATTERN = Pattern.compile(
                "ascend\\.php\\?action=ascend.*confirm=on.*confirm2=on.*|\\s+Beginning New Ascension\\s+");

        private static final List<String> months = Lists.immutableListOf("January", "February",
                "March", "April", "May", "June", "July", "August", "September", "October",
                "November", "December");

        private final Matcher ascendedMatcher = ASCENDED_PATTERN.matcher("");

        private final File[] mafiaLogs;

        private PrintWriter currentCondensedLogWriter;

        /**
         * @param mafiaLogs The mafia logs which should be turned into parsed ascension
         *                  logs.
         * @throws NullPointerException     if mafiaLogs is {@code null}
         * @throws IllegalArgumentException if mafiaLogs does not contain any elements
         */
        CondensedMafiaLogsCreator(
                final File[] mafiaLogs) {

            if (mafiaLogs == null) {
                throw new NullPointerException("The File array mafiaLogs must not be null.");
            }
            if (mafiaLogs.length == 0) {
                throw new IllegalArgumentException("The File array mafiaLogs must not be empty.");
            }

            // Sort array in case it isn't already in the proper order, which is
            // oldest mafia log first.
            Arrays.sort(mafiaLogs, LogsCache.FILE_COMPARATOR);
            this.mafiaLogs = mafiaLogs;
        }

        /**
         * Creates and returns condensed mafia logs which hold single ascensions from
         * start to end in a single file.
         * <p>
         * Day changes (the junction between two normal log files of a single ascension)
         * will be separated by the string {@code ===Day
         * _dayNumber_===}, which is in essence the same as the one used in parsed
         * ascension logs to show day changes. Note that if the were multiple days on
         * which no login occurred, it will be catched by the parsing mechanism behind
         * this method, resulting in the right number of day change strings right after
         * each other.
         * <p>
         * There are sometimes cases of KolMafia stating that it is the same real-life
         * date, while the KoL date changed (this strongly depends on the users time
         * zone). If such a case is recognised, the line "Day change occurred" will be
         * added to the player snapshot in which this date change was noticed.
         * <p>
         * Please note that the condensed mafia logs created by this method are stored
         * in the directory for temporal data as denoted by
         * {@link Constants#TEMP_LOCATION}. These files should be deleted after use.
         *
         * @return The condensed mafia logs.
         * @throws IOException if there was a problem while accessing the given mafia
         *                     logs or writing the condensed ones
         */
        File[] condense()
                throws IOException {

            // Since we can no longer assume the temp directory will be empty because of
            // debugging, we need to keep track of what files we generate
            final TreeSet<File> condensedFiles = new TreeSet<>(LogsCache.FILE_COMPARATOR);

            String userName = mafiaLogs[0].getName()
                    .substring(0, mafiaLogs[0].getName().lastIndexOf("_")).toLowerCase();
            String lastKolDate = null;
            int dayNumber = 1;

            Calendar lastLogDate = UsefulPatterns.getMafiaLogCalendarDate(mafiaLogs[0]);
            condensedFiles.add(openNextWritingFile(mafiaLogs[0].getName()));

            for (final File f : mafiaLogs) {
                final String currentLogUserName = f.getName()
                        .substring(0, f.getName().lastIndexOf("_")).toLowerCase();
                if (!userName.equals(currentLogUserName)) {
                    condensedFiles.add(openNextWritingFile(f.getName()));
                    dayNumber = 1;
                    lastLogDate = UsefulPatterns.getMafiaLogCalendarDate(f);
                    userName = currentLogUserName;
                    lastKolDate = null;
                }

                final Calendar currentLogDate = UsefulPatterns.getMafiaLogCalendarDate(f);
                long dayDiff = (currentLogDate.getTimeInMillis() - lastLogDate.getTimeInMillis())
                        / 86400000;
                while (dayDiff > 0) {
                    dayDiff--;
                    dayNumber++;
                    lastKolDate = null;
                    currentCondensedLogWriter.println();
                    currentCondensedLogWriter.println("===Day " + dayNumber + "===");
                    currentCondensedLogWriter.println();
                }
                lastLogDate = currentLogDate;

                final BufferedReader br = new BufferedReader(new FileReader(f));
                String tmpLine;

                while ((tmpLine = br.readLine()) != null) {
                    currentCondensedLogWriter.println(tmpLine);

                    for (final String s : months) {
                        if (tmpLine.startsWith(s) && !tmpLine.startsWith("April Fool's Day")) {
                            final String currentKolDate = tmpLine
                                    .substring(tmpLine.lastIndexOf("-") + 2);
                            if (lastKolDate == null) {
                                lastKolDate = currentKolDate;
                            } else if (!currentKolDate.equals(lastKolDate)) {
                                currentCondensedLogWriter.println("Day change occurred");
                                dayNumber++;
                                lastLogDate.add(Calendar.DAY_OF_MONTH, 1);
                                lastKolDate = currentKolDate;
                            }
                        }
                    }

                    if (ascendedMatcher.reset(tmpLine).matches()) {
                        condensedFiles.add(openNextWritingFile(f.getName()));
                        dayNumber = 1;
                    }
                }

                br.close();
            }

            // Close print-stream after the last log was read.
            if (currentCondensedLogWriter != null) {
                currentCondensedLogWriter.close();
            }

            File[] result = new File[condensedFiles.size()];
            result = condensedFiles.toArray(result);
            return result;
        }

        /**
         * Closes the current PrintWriter if one is present starts a new condensed mafia
         * log with a file name based on the current mafia log.
         * <p>
         * The file name will use the format {@code USERNAME-YYYYMMDD.txt}, where Y is
         * the year, M is the month and D is the day of the current mafia log, which
         * also is the start date of the ascension represented be the condensed mafia
         * log.
         *
         * @param currentMafiaLogFileName The file name of the current mafia log.
         * @return The File object representing the new condensed log file.
         */
        private File openNextWritingFile(
                final String currentMafiaLogFileName)
                throws IOException {

            if (currentCondensedLogWriter != null) {
                currentCondensedLogWriter.close();
            }

            final Scanner scanner = new Scanner(currentMafiaLogFileName);
            scanner.useDelimiter(NOT_USER_NAME_PATTERN);

            final String fileName = scanner.next().replace("_", " ") + "-"
                    + UsefulPatterns.getLogDate(currentMafiaLogFileName) + ".txt";

            scanner.close();

            final File currentCondensedFile = new File(Constants.TEMP_LOCATION, fileName);
            currentCondensedLogWriter = new PrintWriter(currentCondensedFile.getAbsolutePath());
            return currentCondensedFile;
        }
    }

    /**
     * Creates and returns condensed mafia logs which hold single ascensions from
     * start to end in a single file.
     * <p>
     * Day changes (the junction between two normal log files of a single ascension)
     * will be separated by the string {@code ===Day _dayNumber_===}, which is in
     * essence the same as the one used in parsed ascension logs to show day
     * changes. Note that if the were multiple days on which no login occurred, it
     * will be catched by the parsing mechanism behind this method, resulting in the
     * right number of day change strings right after each other.
     * <p>
     * There are sometimes cases of KolMafia stating that it is the same real-life
     * date, while the KoL date changed (this strongly depends on the users time
     * zone). If such a case is recognised, the line "Day change occurred" will be
     * added to the player snapshot in which this date change was noticed.
     * <p>
     * The number of files depends on the number of ascensions present inside the
     * given mafia logs.
     * <p>
     * Please note that the condensed mafia logs created by this method are stored
     * in the directory for temporary data as denoted by
     * {@link Constants#TEMP_LOCATION}. It is the <b>responsibility of the
     * programmer using this method to delete these files as soon as they are not
     * needed anymore</b> to ensure that no bugs appear because of leftover files
     * and no size bloat of the Ascension Log Visualizer directory happens.
     *
     * @param mafiaLogs The mafia logs which should be condensed into mafia logs
     *                  which each hold a single ascension.
     * @return The condensed mafia logs.
     * @throws IOException              if there was a problem while accessing the
     *                                  given mafia logs or writing the condensed
     *                                  ones
     * @throws NullPointerException     if mafiaLogs is {@code null}
     * @throws IllegalArgumentException if mafiaLogs does not contain any elements
     */
    public static File[] createCondensedMafiaLogs(
            final File[] mafiaLogs)
            throws IOException {

        return new CondensedMafiaLogsCreator(mafiaLogs).condense();
    }

    /**
     * Create a new, empty parsed log file, overwriting the old one if any,
     * corresponding to the given condensed log file.
     *
     * @param f         The condensed Mafia log which is ready for parsing
     * @param destDir   Destination directory
     * @param logFormat Format in which to write the parsed logs
     * @return Newly-created, empty file
     * @throws IOException If an exception occurs while working with the filesystem
     */
    private static File createNewLog(
            final File f,
            final File destDir,
            final LogOutputFormat logFormat)
            throws IOException {

        final File parsedLogFile = new File(destDir, parsedLogName(f.getName(), logFormat));
        if (parsedLogFile.exists()) {
            parsedLogFile.delete();
        }
        parsedLogFile.createNewFile();
        return parsedLogFile;
    }

    /**
     * @see #createParsedLogs(File[], File, EnumSet, int)
     *
     *      The number of logs to parse defaults to the maximum possible.
     *
     * @param mafiaLogs     The mafia logs which should be turned into parsed
     *                      ascension logs.
     * @param savingDestDir The directory inside which the parsed ascension logs
     *                      should be saved in.
     * @param logFormats    The output formats in which to print the parsed logs.
     * @return A list containing pairs with filenames and turns of condensed mafia
     *         log files that were attempted to be parsed, but had an exception
     *         thrown during the parsing process. The included turn the turn after
     *         which the exception occurred. This list will be empty if all files
     *         were correctly parsed.
     * @throws IOException if there was a problem while accessing or writing files
     *                     handled by this method
     */
    public static List<Pair<String, Encounter>> createParsedLogs(
            final File[] mafiaLogs,
            final File savingDestDir,
            final EnumSet<LogOutputFormat> logFormats)
            throws IOException {

        return createParsedLogs(mafiaLogs, savingDestDir, logFormats, Integer.MAX_VALUE);
    }

    /**
     * Creates and saves parsed ascension logs. The format of those logs is similar
     * to the one used by the AFH MafiaLog Parser. ( {@link TextLogCreator} handles
     * the log format)
     * <p>
     * The file names of the created logs have the format
     * {@code USERNAME_ascendYYYYMMDD.txt}, where Y is the year, M is the month and
     * D is the day of the first day of that ascension.
     * <p>
     * Note that only the last n ascensions will be parsed.
     *
     * @param mafiaLogs     The mafia logs which should be turned into parsed
     *                      ascension logs.
     * @param savingDestDir The directory inside which the parsed ascension logs
     *                      should be saved in.
     * @param logFormats    The output format in which to print the parsed logs.
     * @param logsToParse   The last n ascensions that should be parsed. If n is not
     *                      given, then all ascensions should be parsed.
     * @return A list containing pairs with filenames and turns of condensed mafia
     *         log files that were attempted to be parsed, but had an exception
     *         thrown during the parsing process. The included turn the turn after
     *         which the exception occurred. This list will be empty if all files
     *         were correctly parsed.
     * @throws IOException              if there was a problem while accessing or
     *                                  writing files handled by this method
     * @throws NullPointerException     if mafiaLogs is {@code null}; if
     *                                  savingDestDir is {@code null}
     * @throws IllegalArgumentException if mafiaLogs does not contain any elements;
     *                                  if the directory savingDestDir does not
     *                                  exist; if savingDestDir is not a directory;
     *                                  if logsToParse is smaller than 1
     */
    public static List<Pair<String, Encounter>> createParsedLogs(
            final File[] mafiaLogs,
            final File savingDestDir,
            final EnumSet<LogOutputFormat> logFormats,
            final int logsToParse)
            throws IOException {

        if (!savingDestDir.exists()) {
            throw new IllegalArgumentException("The directory doesn't exist.");
        }
        if (!savingDestDir.isDirectory()) {
            throw new IllegalArgumentException("The given file is not a directory.");
        }
        if (logsToParse <= 0) {
            throw new IllegalArgumentException("The number of logs to parse must not be below 1.");
        }

        final List<Pair<String, Encounter>> errorFileList = Collections
                .synchronizedList(new ArrayList<Pair<String, Encounter>>());

        final File[] condensedMafiaLogs = createCondensedMafiaLogs(mafiaLogs);
        Arrays.sort(condensedMafiaLogs, LogsCache.FILE_COMPARATOR);

        // 4 Threads per CPU should be a high enough number to not slow the
        // computation too much down by scheduler overhead while still making
        // use of threaded computing.
        final ExecutorService executor = Executors
                .newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 4);

        int logsLeftToParse = logsToParse;
        for (final File condensedLog : condensedMafiaLogs) {
            if (logsLeftToParse <= 0) {
                break;
            } else {
                logsLeftToParse--;
            }

            executor.execute(new Runnable() {
                @Override
                public void run() {

                    final MafiaLogParser parser = new MafiaLogParser(condensedLog,
                            Settings.getBoolean("Include mafia log notes"));
                    LogOutputFormat format = null;
                    
                    try {
                        parser.parse();

                        for (LogOutputFormat logFormat : logFormats) {
                            format = logFormat;
                            final File newLogFile = createNewLog(condensedLog, savingDestDir, logFormat);
                            System.out.println("Writing " + newLogFile.getAbsolutePath() + "...");
                            final LogDataHolder logData = parser.getLogData();
                            if (logFormat == LogOutputFormat.XML_LOG) {
                                XMLLogCreator.createXMLLog(logData, savingDestDir);
                            } else {
                                TextLogCreator.saveTextualLogToFile(logData, newLogFile, logFormat);
                            }
                        }
                    } catch (final Exception e) {
                        // Add the erroneous log to the error file list.
                        if (format == null) {
                            // If here, we haven't started writing parsed logs yet
                            errorFileList.add(Pair.of(condensedLog.getName(),
                                    (Encounter) parser.getLogData().getLastTurnSpent()));
                        } else {
                            errorFileList.add(Pair.of(parsedLogName(condensedLog.getName(), format),
                                    (Encounter) parser.getLogData().getLastTurnSpent()));
                        }
                        // Print stack trace and the file name of the file in
                        // which the error happened.
                        System.err.println(condensedLog.getName());
                        e.printStackTrace();
                    }
                }
            });
        }

        // Wait for all threads to finish.
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

        // Temporary files should be deleted after use. Possible subdirectories
        // are ignored here.
        // Don't delete if we're in debug mode.
        if (!Settings.getBoolean(Settings.DEBUG)) {
            for (final File f : Constants.TEMP_LOCATION.listFiles()) {
                if (!f.isDirectory()) {
                    f.delete();
                }
            }
        }

        return errorFileList;
    }

    /**
     * Takes the file name of a condensed mafia log and changes it into the proper
     * format for parsed ascension logs.
     * <p>
     * File names of condensed mafia logs use the format
     * {@code USERNAME-YYYYMMDD.txt}, where Y is the year, M is the month and D is
     * the day of the first day of that ascension.
     *
     * @param condensedLogFileName File name of the condensed mafia log.
     * @param logFormat            The output format of the parsed log.
     * @return The proper parsed ascension log file name.
     */
    public static String parsedLogName(
            final String condensedLogFileName,
            final LogOutputFormat logFormat) {

        final String userName = condensedLogFileName.substring(0,
                condensedLogFileName.lastIndexOf("-"));
        String tail = condensedLogFileName.substring(userName.length()).replace("-", "_ascend");
        if (logFormat == LogOutputFormat.HTML_LOG) {
            tail = tail.replace(".txt", ".html");
        }

        return userName + tail;
    }

    // This class is not to be instanced.
    private LogsProcessor() {}
}

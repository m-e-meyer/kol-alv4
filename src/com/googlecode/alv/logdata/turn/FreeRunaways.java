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

/**
 * This immutable class is a representation of the free runaway usage.
 */
public final class FreeRunaways {
    private static final String SLASH = "/";

    private static final String FREE_RETREATS_STRING = "free retreats";

    private final int numberOfAttemptedRunaways;

    private final int numberOfSuccessfulRunaways;

    /**
     * Creates a new FreeRunaways instance with the given number of runaways and
     * successful runaways.
     * 
     * @param numberOfAttemptedRunaways Number of runaways
     * @param numberOfSuccessfulRunaways Number of successful runaways
     * @throws IllegalArgumentException
     *             if either numberOfSuccessfulUsages or numberOfAttemptedUsages
     *             is below zero; if numberOfSuccessfulUsages is greater than
     *             numberOfAttemptedUsages
     */
    public FreeRunaways(
                        final int numberOfAttemptedRunaways, final int numberOfSuccessfulRunaways) {
        if (numberOfAttemptedRunaways < 0 || numberOfSuccessfulRunaways < 0)
            throw new IllegalArgumentException("Number of runaways must not be below 0.");
        if (numberOfSuccessfulRunaways > numberOfAttemptedRunaways)
            throw new IllegalArgumentException("Number of successful usages must not be below number of usages.");

        this.numberOfAttemptedRunaways = numberOfAttemptedRunaways;
        this.numberOfSuccessfulRunaways = numberOfSuccessfulRunaways;
    }

    /**
     * @return The number of attempted runaway usages.
     */
    public int getNumberOfAttemptedRunaways() {
        return numberOfAttemptedRunaways;
    }

    /**
     * @return The number of successful runaway usages.
     */
    public int getNumberOfSuccessfulRunaways() {
        return numberOfSuccessfulRunaways;
    }

    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder(25);

        str.append(numberOfSuccessfulRunaways);
        str.append(" ");
        str.append(SLASH);
        str.append(" ");
        str.append(numberOfAttemptedRunaways);
        str.append(" ");
        str.append(FREE_RETREATS_STRING);

        return str.toString();
    }

    @Override
    public boolean equals(
                          final Object o) {
        if (o == this)
            return true;

        if (o == null)
            return false;

        if (o instanceof FreeRunaways)
            return ((FreeRunaways) o).getNumberOfSuccessfulRunaways() == numberOfSuccessfulRunaways;

        return false;
    }

    @Override
    public int hashCode() {
        int result = 687;
        result = 31 * result + super.hashCode();
        result = 31 * result + numberOfSuccessfulRunaways;

        return result;
    }
}
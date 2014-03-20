package org.knuth.mgf;

/**
 * <p>Represents a time span of a given length. With this time-span,
 *  it's possible to create simple timers and other time-related
 *  tasks.</p>
 * <p>This class is not directly instantiable. Use the corresponding
 *  {@code fromXX()}-methods to create a new instances.</p>
 *
 * @author Lukas Knuth
 * @version 1.0
 */
public class TimeSpan implements Comparable<TimeSpan> {

    /** One millisecond in nanoseconds */
    private static final int MILLISECOND_IN_NANOSECONDS = 1000000;

    /** The time-span in nanoseconds */
    protected final long nano_seconds;

    /** A time-span with the length of zero */
    public static final TimeSpan ZERO = new TimeSpan(0);

    /**
     * Create a new {@code TimeSpan} from a given amount of <b>days</b>.
     * @param days the time-span in days.
     * @return the new created {@code TimeSpan}-object.
     */
    public static TimeSpan fromDays(int days){
        return fromHours(24 * days);
    }

    /**
     * Create a new {@code TimeSpan} from a given amount of <b>hours</b>.
     * @param hours the time-span in hours.
     * @return the new created {@code TimeSpan}-object.
     */
    public static TimeSpan fromHours(int hours){
        return fromMinutes(60 * hours);
    }

    /**
     * Create a new {@code TimeSpan} from a given amount of <b>minutes</b>.
     * @param minutes the time-span in minutes.
     * @return the new created {@code TimeSpan}-object.
     */
    public static TimeSpan fromMinutes(int minutes){
        return fromSeconds(60 * minutes);
    }

    /**
     * Create a new {@code TimeSpan} from a given amount of <b>seconds</b>.
     * @param seconds the time-span in seconds.
     * @return the new created {@code TimeSpan}-object.
     */
    public static TimeSpan fromSeconds(int seconds){
        return fromMilliSeconds(1000 * seconds);
    }

    /**
     * Create a new {@code TimeSpan} from a given amount of <b>milliseconds</b>.
     * @param milliseconds the time-span in milliseconds.
     * @return the new created {@code TimeSpan}-object.
     */
    public static TimeSpan fromMilliSeconds(long milliseconds){
        return new TimeSpan(MILLISECOND_IN_NANOSECONDS * milliseconds);
    }

    /**
     * Private constructor, use the static builder methods.
     * @param nano_seconds the time-span in nanoseconds.
     */
    TimeSpan(long nano_seconds){
        this.nano_seconds = nano_seconds;
    }

    /**
     * Returns a new {@code TimeSpan} which is this time-span subtracted by
     *  the given time-span.
     * @param second_span the time-span to subtract form this time-span.
     * @return the new time-span.
     */
    public TimeSpan subtract(TimeSpan second_span){
        return new TimeSpan(this.nano_seconds - second_span.nano_seconds);
    }

    /**
     * Returns a new {@code TimeSpan} which is this time-span plus the given
     *  time-span.
     * @param second_span the time-span which should be added to this time-span.
     * @return the new time-span.
     */
    public TimeSpan add(TimeSpan second_span){
        return new TimeSpan(this.nano_seconds + second_span.nano_seconds);
    }

    /**
     * Checks, if this time-span is greater (longer) then the given time-span.
     * @param second_span the time-span to check with.
     * @return {@code true} if this time-span is greater, {@code false} otherwise.
     * @see #isLessThen(TimeSpan)
     */
    public boolean isGreaterThen(TimeSpan second_span){
        return this.nano_seconds > second_span.nano_seconds;
    }

    /**
     * Checks if this time-span is less (shorter) then the given time-span.
     * @param second_span the time-span to check with.
     * @return {@code true} if this time-span is less, {@code false} otherwise.
     * @see #isGreaterThen(TimeSpan)
     */
    public boolean isLessThen(TimeSpan second_span){
        return this.nano_seconds < second_span.nano_seconds;
    }

    @Override
    public int compareTo(TimeSpan o) {
        if (this.isGreaterThen(o)) return 1;
        else if (this.isLessThen(o)) return -1;
        else return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeSpan timeSpan = (TimeSpan) o;

        if (nano_seconds != timeSpan.nano_seconds) return false;

        return true;
    }

    @Override
    public int hashCode() {
        // See Effective Java Second Edition, Item 9
        return 31 * 42 + (int) (nano_seconds ^ (nano_seconds >>> 32));
    }

    @Override
    public String toString() {
        return String.valueOf(nano_seconds);
    }
}

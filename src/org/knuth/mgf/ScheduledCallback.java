package org.knuth.mgf;

/**
 * <p>Represents a {@link org.knuth.mgf.Callback}, scheduled for execution.</p>
 * @see org.knuth.mgf.GameLoop#scheduleCallback(Callback, TimeSpan)
 * @see org.knuth.mgf.GameLoop#rescheduleCallback(ScheduledCallback)
 * @author Lukas Knuth
 * @version 1.0
 */
public class ScheduledCallback implements Comparable<ScheduledCallback>{

    private final Callback callback;
    private TimeSpan when;
    private final TimeSpan wait_for;
    private final Scene parent_scene;
    private TimeSpan rest_time = TimeSpan.ZERO;

    ScheduledCallback(Callback callback, TimeSpan wait_for, Scene parent_scene, TimeSpan when) {
        this.callback = callback;
        this.when = when;
        this.wait_for = wait_for;
        this.parent_scene = parent_scene;
    }

    void execute(){
        this.callback.call();
    }

    TimeSpan when(){
        return this.when;
    }

    TimeSpan getTimeToWait(){
        if (rest_time != TimeSpan.ZERO){
            // We've been paused, so un-pause now:
            TimeSpan wait = this.rest_time;
            this.rest_time = TimeSpan.ZERO;
            return wait;
        } else {
            // Restart from the beginning:
            return this.wait_for;
        }
    }

    void reschedule(TimeSpan when){
        this.when = when;
    }

    /**
     * Pauses the timeout to this schedule. It can be resumed using the
     *  {@link #unpause()}-method.
     */
    public void pause(){
        rest_time = when.subtract(parent_scene.getSceneTime());
        parent_scene.cancelCallback(this);
    }

    /**
     * <p>Un-pauses this schedules timeout, if it had been paused (using the
     *  {@link #pause()}-method) earlier.</p>
     * <p>If this schedule has not been paused, calling this method will
     *  have no effect.</p>
     */
    public void unpause(){
        if (rest_time != TimeSpan.ZERO){
            GameLoop.INSTANCE.rescheduleCallback(this);
        }
    }

    /**
     * <p>Cancels this callback so it will not be executed.</p>
     * <p>Canceled callbacks can be re-scheduled, using the
     *  {@link org.knuth.mgf.GameLoop#rescheduleCallback(ScheduledCallback)}-
     *  method.</p>
     * @see org.knuth.mgf.GameLoop#rescheduleCallback(ScheduledCallback)
     */
    public void cancel(){
        parent_scene.cancelCallback(this);
    }

    @Override
    public int compareTo(ScheduledCallback o) {
        // Orders them by their scheduled-time.
        return this.when.compareTo(o.when);
    }
}
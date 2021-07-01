/**
 * Pacman
 * Author: Peter Mitchell (2021)
 *
 * ActionTimer class:
 * Can be used to keep track of progress timers.
 * Will tick on a fixed increment defined in GamePanel when the update() method is called.
 * The isTriggered() will become true once the timer reaches 0.
 * reset() can be used to reset back to the default time and setTimer() to clear
 * with a new time.
 */
public class ActionTimer {
    /**
     * Time used when resetting the timer.
     */
    private int startTime;
    /**
     * The timer's progress that ticks down continually every update().
     */
    private int timeRemaining;
    /**
     * True when the timeRemaining has reached 0 and stays true until reset.
     */
    private boolean triggered;

    /**
     * Sets the initial timer on the timer and makes it ready to begin ticking on updates.
     *
     * @param startTime The time to start with and is used for each reset.
     */
    public ActionTimer(int startTime) {
        this.startTime = startTime;
        reset();
    }

    /**
     * Ticks the time remaining down on a fixed interval defined in GamePanel.
     * If the timer reaches 0 it will set the triggered to true.
     */
    public void update() {
        timeRemaining -= GamePanel.TIME_BETWEEN_UPDATES;
        if(timeRemaining <= 0) {
            triggered = true;
        }
    }

    /**
     * Gets the current triggered state of the timer.
     *
     * @return True when the timer has recently triggered.
     */
    public boolean isTriggered() {
        return triggered;
    }

    /**
     * Sets the timer immediately to the specified time and changes
     * the time that will be used for resets.
     *
     * @param time The timer to set the new timer interval to.
     */
    public void setTimer(int time) {
        startTime = time;
        reset();
    }

    /**
     * Resets the time remaining back to the full interval again.
     * And changes the triggered state back to the default of false.
     */
    public void reset() {
        timeRemaining = startTime;
        triggered = false;
    }
}

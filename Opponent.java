package players.groupD;

import utils.Types;

import java.util.ArrayList;

public class Opponent {
    private Types.TILETYPE id;
    private ArrayList<Integer> timesOfBombsDropped;        // the time tick at which the bomb is dropped
    private int timeDetected;           // the time tick at which the player is detected

    public Opponent(Types.TILETYPE id) {
        this.id = id;
        timesOfBombsDropped = new ArrayList<>();
        this.timeDetected = -1;
    }

    public void didDropBomb(int timeTick) {
        timesOfBombsDropped.add(timeTick);
    }

    public boolean isPlayer(Types.TILETYPE idx) {
        return idx == this.id;
    }

    public void appearAt(int timeTick) {
        timeDetected = timeTick;
    }

    /**
     * Determine how the coefficient of aggressiveness using logistic function(range from 0.5 to 1).
     *
     * @return The output of logistic function given total number of bombs dropped
     */
    public double getAggressiveness() {
        return Math.pow(1.0 + Math.exp(-timesOfBombsDropped.size()), -1);
    }

    /**
     * Show how often the player drops bombs
     *
     * @return
     */
    public double bombFrequency() {
        // at least 2 bombs are taken into account
        if(timesOfBombsDropped.size() <= 1) {
            return 0;
        }

        // for each period between the time this opponent is detected and the time it disappears
        int firstBombIdx = 0;
        for(int i = 0;i < timesOfBombsDropped.size();i++) {
            if(timesOfBombsDropped.get(i) >= timeDetected) {
                firstBombIdx = i;
                break;
            }
        }
        return (timesOfBombsDropped.size() - firstBombIdx)
                / (timesOfBombsDropped.get(timesOfBombsDropped.size() - 1) - (timeDetected * 1.0));
    }
}

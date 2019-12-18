package players.groupD;

import core.GameState;
import players.heuristics.CustomHeuristic;
import utils.Types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class OpponentModeller {
    private GameState gameState;
    private int selfID;
    private Map<Types.TILETYPE, Opponent> opponents;
    private ArrayList<Types.TILETYPE> detectedOpponents;

    public OpponentModeller(int selfID) {
        this.opponents = new HashMap<>();
        this.detectedOpponents = new ArrayList<>();
        this.selfID = selfID;
    }

    public void updateGameState(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * Search for opponents and process their information.
     */
    public void searchOpponents() {
        removeDeadOpponents();
        detectedOpponents.clear();
        Types.TILETYPE[][] board = gameState.getBoard();
        int currentGameTick = gameState.getTick();
        // scan through the board
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[i].length; j++) {
                // not model itself
                if (board[i][j].getKey() == selfID) continue;
                switch (board[i][j]) {
                    case AGENT0: case AGENT1: case AGENT2: case AGENT3:
                        // Add new opponent
                        if (!opponents.containsKey(board[i][j])) {
                            Opponent opponent = new Opponent(board[i][j]);
                            opponent.appearAt(currentGameTick);
                            opponents.put(board[i][j], opponent);
                        }
                        // If the opponent is detected
                        if (!detectedOpponents.contains(board[i][j])) {
                            detectedOpponents.add(board[i][j]);
                        }
                        // determine if the player has dropped bomb
                        if (hasDroppedBomb(i, j)) {
                            getOpponent(board[i][j]).didDropBomb(currentGameTick);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public ArrayList<Opponent> getDetectedOpponents() {
        ArrayList<Opponent> seenOpponents = new ArrayList<>();
        for (Types.TILETYPE playerID : detectedOpponents) {
            seenOpponents.add(getOpponent(playerID));
        }
        return seenOpponents;
    }

    public Opponent getOpponent(Types.TILETYPE player) {
        return opponents.get(player);
    }

    private void removeDeadOpponents() {
        ArrayList<Types.TILETYPE> aliveEnemies = gameState.getAliveEnemyIDs();
        // all enemies
        for(Types.TILETYPE enemyID : gameState.getEnemies()) {
            // dead player
            if (!aliveEnemies.contains(enemyID)) {
                opponents.remove(enemyID);
            }
        }
    }

    private boolean hasDroppedBomb(int i, int j) {
        int[][] bombLifes = gameState.getBombLife();
        // four adjacent grids
        int i_up = Math.max(0, i - 1), i_down = Math.min(bombLifes.length - 1, i + 1);
        int j_left = Math.max(0, j - 1), j_right = Math.min(bombLifes[0].length - 1, j + 1);

        return bombLifes[i_up][j] >= Types.BOMB_LIFE - 2 || bombLifes[i_down][j] >= Types.BOMB_LIFE - 2
                || bombLifes[i][j_left] >= Types.BOMB_LIFE - 2 || bombLifes[i][j_right] >= Types.BOMB_LIFE - 2;
    }
}

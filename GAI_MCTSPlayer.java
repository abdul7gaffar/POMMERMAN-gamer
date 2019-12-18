package players.groupD;

import core.GameState;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import players.optimisers.ParameterizedPlayer;
import players.Player;
import utils.ElapsedCpuTimer;
import utils.Types;
import core.ForwardModel;

import java.util.ArrayList;
import java.util.Random;

public class GAI_MCTSPlayer extends ParameterizedPlayer {

    /**
     * Random generator.
     */
    private Random m_rnd;
    /**
     * All actions available.
     */
    public Types.ACTIONS[] actions;
    private GameState rootState;

    private Types.TILETYPE[][] memoryBoard;
    private int[][] memoryboardIDs;

    private OpponentModeller modeller;

    /** Types.ACTIONS.all();
        int width = board.length;
     * Params for this MCTS
     */
    public GAI_MCTSParams params;

    public GAI_MCTSPlayer(long seed, int id) {
        this(seed, id, new GAI_MCTSParams());
    }

    public GAI_MCTSPlayer(long seed, int id, GAI_MCTSParams params) {
        super(seed, id, params);
        reset(seed, id);

        ArrayList<Types.ACTIONS> actionsList = Types.ACTIONS.all();
        actions = new Types.ACTIONS[actionsList.size()];
        int i = 0;
        for (Types.ACTIONS act : actionsList) {
            actions[i++] = act;
        }
    }

    @Override
    public void reset(long seed, int playerID) {
        this.seed = seed;
        this.playerID = playerID;
        m_rnd = new Random(seed);

        this.params = (GAI_MCTSParams) getParameters();
        if (this.params == null) {
            this.params = new GAI_MCTSParams();
            super.setParameters(this.params);
        }
    }

    @Override
    public Types.ACTIONS act(GameState gs) {
        if (modeller == null) modeller = new OpponentModeller(this.playerID);
        modeller.updateGameState(gs);
        modeller.searchOpponents();

        // TODO update gs
        if (gs.getGameMode().equals(Types.GAME_MODE.TEAM_RADIO)){
            int[] msg = gs.getMessage();
        }

        ElapsedCpuTimer ect = new ElapsedCpuTimer();
        ect.setMaxTimeMillis(params.num_time);

        // Number of actions available
        int num_actions = actions.length;

        // Root of the tree
        GAI_SingleTreeNode m_root = new GAI_SingleTreeNode(params, m_rnd, num_actions, actions);
        m_root.setRootGameState(gs);
//        this.rootState = gs;

        //Determine the action using MCTS...
        this.getSeenBoard(gs);
        m_root.mctsSearch(ect, memoryBoard);

        //Determine the best action to take and return it.
        int action = m_root.mostVisitedAction();

        // TODO update message memory

        //... and return it.
        return actions[action];
    }

    public void getSeenBoard(GameState state){
//        GameState state = rootState.copy();
        Types.TILETYPE[][] board = state.getBoard();

        if(memoryBoard == null){
            memoryBoard = board;
            memoryboardIDs = new int[memoryBoard.length][memoryBoard[0].length];
        }

        for(int i = 1; i < board.length; i++){
            for(int j = 1; j < board[i].length; j++) {
                //System.out.print(board[i][j] + ", ");
                if(board[i][j] != Types.TILETYPE.FOG){

                    memoryBoard[i][j] = board[i][j];

                    //get refelctive visual flipped section
                    if(board[i][j] == Types.TILETYPE.PASSAGE || board[i][j] == Types.TILETYPE.RIGID){
                        memoryBoard[j][i] = board[i][j];//flipped
                    }else{
                        memoryBoard[j][i] = Types.TILETYPE.PASSAGE;
                    }

                    memoryboardIDs[i][j] = memoryBoard[i][j].getKey();


                }
//                System.out.print(memoryBoard[i][j] + ", ");

            }
//            System.out.println();
        }
//        System.out.println("================================");
    }

    @Override
    public int[] getMessage() {
        // default message
        int[] message = new int[Types.MESSAGE_LENGTH];
        message[0] = 1;
        return message;
    }

    @Override
    public Player copy() {
        return new GAI_MCTSPlayer(seed, playerID, params);
    }


}
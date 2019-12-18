# Games AI Group D 

## How to run our player. 

To run our agent you must call the function in the same way as you woud call the MCTS agent, however the Class name is `GAI_MCTSPlayer`, and the heuristic function that would be used would be `.ADVANCED_HEURISTIC`. 

An example of us calling our function is shown below: 

```
GAI_MCTSParams gai_mctsParams = new GAI_MCTSParams();
gai_mctsParams.stop_type = gai_mctsParams.STOP_ITERATIONS;
gai_mctsParams.num_iterations = 200;
gai_mctsParams.rollout_depth = 12;
gai_mctsParams.heuristic_method = gai_mctsParams.ADVANCED_HEURISTIC;

p = new GAI_MCTSPlayer(seed, playerID++, gai_mctsParams);
playerStr[i-4] = "GROUP D AGENT";

```

The classes and methods that have been changed are shown in the `GAI_SingleTreeNode.java` and `GAI_MCTSPlayer.java`. 

As mentioned in the report we modified the parameters of such methods, the below methods, with modification shown below: 

`void mctsSearch(ElapsedCpuTimer elapsedTimer, Types.TILETYPE[][] board)`

`private double rollOut(GameState state, Types.TILETYPE[][] board)`

`private int safeRandomAction(GameState state, Types.TILETYPE[][] board)`

And then within the `GAI_MCTSPlayer`, we modified the `act()` method as such: 

```
GAI_SingleTreeNode m_root = new GAI_SingleTreeNode(params, m_rnd, num_actions, actions);
m_root.setRootGameState(gs);

//Determine the action using MCTS...
this.getSeenBoard(gs);
m_root.mctsSearch(ect, memoryBoard);
```

The addition method we added within the MCTSPlayer class are shown below, this is where we do our visibility. 

```
public void getSeenBoard(GameState state){
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
                    //only get relevent tiles, such as passage and rigid is shown in the reflected 
                    if(board[i][j] == Types.TILETYPE.PASSAGE || board[i][j] == Types.TILETYPE.RIGID){
                        memoryBoard[j][i] = board[i][j];//flipped
                    }else{
                        memoryBoard[j][i] = Types.TILETYPE.PASSAGE;
                    }
                    memoryboardIDs[i][j] = memoryBoard[i][j].getKey();
                }
            }
        }
    }
```
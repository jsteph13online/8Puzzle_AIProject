/*
    CSC 480 AI: Fall 2022
    Assignment 2: Dots and Boxes (Modified)
    Joscelyn Stephens
 */

import java.sql.Array;
import java.util.*;

public class GameState {
    
    private int boardSize; // 2 = 2x2, 3 = 3x3, etc.
    private int boxNum; // count of completed boxes
    private Line[][] lines;
    private Box[][] currentBoxes; // size of boxNum, 0 if no box, else point value of Box
    private int hashState; // of lines
    private boolean isTerminal; // if all boxes have been created
    private boolean isPlayerState; // true = player turn state; false = ai turn state
    private int[] action; // Horizontal/Vertival (to get to this state)
    private GameState parent; // Parent state to get to this state (when backtracking, not saved in hash)
    List<Integer> explored;


    private int playerScore;
    private int aiScore;
    private int minmaxcost; // aiScore - playerScore
    private int plyDepth; // depth of state in search(defaults to 0 until used in search)
    
    public GameState(){}

    //cstr used when player makes a move
    public GameState(GameState state, int xIndex, int yIndex, boolean b){
        // copy values from parent
        this.boardSize = state.getBoardSize();
        this.boxNum = state.getBoxNum();
        boolean linesSetup = DeepCopyLinesAndBoxes(state); // deep copy lines and boxes
        this.parent = state; // save parent
        this.isPlayerState = b; // mark as a player move or ai move
        this.isTerminal = false;
        getSingleLine(xIndex, yIndex).DrawLine(); // draw the new line in the state
        hashState= Arrays.hashCode(lines);
        updateAndStoreBoxes(); // Update and store new box info
        plyDepth=0;
        action = new int[]{xIndex, yIndex};
        explored = new ArrayList<>();
        MinMaxValue(); // calc ai/player scores for state, and this min/max value

    }

    //cstr used for initState
    public GameState(int size, Line[][] l, Box[][] boxes, boolean b){
        boardSize = size; lines = l; hashState = Arrays.hashCode(lines);
        currentBoxes = boxes;
        boxNum = 0;
        isTerminal = false;
        MinMaxValue();
        plyDepth=0;
        action = null;
        parent = null;
        isPlayerState = b;
        explored = new ArrayList<>();
    }


    // calculate minmax (ai score - player score)
    private void MinMaxValue(){
        calcScore();
        minmaxcost = aiScore - playerScore;
        //System.out.println("A Score = " + aiScore);
        //System.out.println("P Score = " + playerScore);
        //System.out.println("MinMaxCost = " + minmaxcost);
    }

    public int getHash(){return hashState;}
    public boolean getBoolTerminal(){
        for(Box[] b: currentBoxes){
            for(Box c: b){
                if(!c.getCreated())
                    return false;
            }
        }
        isTerminal = true;
        return true;
    }
    public int getBoardSize(){return boardSize;}
    public int getBoxNum(){return boxNum;}
    public int getMinMaxCost(){return minmaxcost;}
    public int[] getAction(){return action;}
    //public int getPlyDepth(){return plyDepth;}
    public boolean checkIfLineDrawn(int i1, int i2){
        if(lines[i1][i2].getIsDrawn())
            return true;
        else
            return false;
    }
    public boolean getPlayer(){return isPlayerState;}
    public int getPlayerScore(){return playerScore;}
    public int getAiScore(){return aiScore;}
    public int isPlayerWinning(){
        if(playerScore>aiScore)
            return 1; // Player winning
        else if(aiScore>playerScore)
            return -1; // AI winning
        else
            return 0; // TIE
    }
    //return boolean isCreated from Box (no evaluation about box state)
    public boolean checkIfBox(int index, int index2){
        if(index >= currentBoxes.length)
        {System.out.println("ERROR: Invalid Index Sent"); return false;}
        if(currentBoxes[index][index2].getCreated())
            return true;
        else
            return false;
    }
    //return box from currentboxes[][] at given x,y index
    public Box getBox(int i1, int i2){
        if(i1 >= currentBoxes.length && i2 >= currentBoxes[0].length) {
            System.out.println("ERROR: Invalid Index Sent, returning box[0][0]");
            return currentBoxes[0][0];}
        return currentBoxes[i1][i2];}
    // return int of completed boxes (for GUI display)
    public int countBoxes(){
        int count = 0;
        for(Box[] b: currentBoxes){
            for(Box c: b){
                if(c.isCreated)count++;
            }
        }
        return count;
    }
    // calculate score for player and ai
    public void calcScore(){
        playerScore=0;
        aiScore=0;
        for(Box[] b: currentBoxes){
            for(Box c: b){
                if(c.isCreated)
                    if(c.getOwnerBool())playerScore+=c.getValue();
                    else aiScore+=c.getValue();
            }
        }
    }
    // return int of drawn lines (for GUI display)
    public int countLines(){
        int count = 0;
        for(int i = 0; i< lines.length; i++){
            for(int j = 0; j< lines[i].length; j++){
                if(lines[i][j]!=null)
                {if(lines[i][j].isDrawn)count++;}
            }
        }
        return count;
    }
    public GameState getParent(){return parent;}
    // return lines[][]
    public Line[][] getLines(){return lines;}
    // return Line obj at given x,y index
    public Line getSingleLine(int x, int y){return lines[x][y];}
    // evaluation of boxes -> which are created, who owns them, if state is terminal
    public void updateAndStoreBoxes(){
        boxNum=0;
        for(Box[] b: currentBoxes){
            for(Box c: b){
                if(c.evaluateCreated(isPlayerState));
                    boxNum++;
                }
            }
    }


    // RECURSIVE MINMAX ALPHA BETA PRUNING
    public int[] MiniMaxPruning(int currdepth, int maxDepth, int alpha, int beta){
        ArrayList<GameState> next = MakeBabies();
        int[] currentBest = new int[2];
        int[] currentMove = new int[2];
        if(next.isEmpty() || currdepth == maxDepth){
            return this.action;
        }
        else{
            for(GameState s: next){
                // check if we've already explored this play
                int h = s.getHash();
                boolean ex = false;
                for(int i: explored){
                    if (h == i){
                        // dont explore, we've already looked at node
                        ex=true;
                    }
                }
                // add this play to the explored list
                if(!ex) {
                    explored.add(s.getHash());
                    boolean isPlayerState = s.getPlayer();
                    if (isPlayerState) { // player state node (MAX)
                        currentMove = s.MiniMaxPruning(currdepth + 1,  maxDepth, alpha, beta);
                        GameState temp = new GameState(this, currentMove[0], currentMove[1], true);
                        if (temp.getMinMaxCost() > alpha) {
                            alpha = this.getMinMaxCost();
                            currentBest = currentMove;
                        }
                    } else { // ai state node (MIN)
                        currentMove = s.MiniMaxPruning(currdepth + 1, maxDepth, alpha, beta);
                        GameState temp = new GameState(this, currentMove[0], currentMove[1], true);
                        if (temp.getMinMaxCost() < beta) {
                            beta = temp.getMinMaxCost();
                            currentBest = currentMove;
                        }
                    }
                    if (alpha >= beta) {
                        if(currentBest == null) currentBest = this.getAction();
                        return currentBest; // if alpha is no longer larger than beta, stop exploring children
                    }
                }
            }
            return currentBest;
        }
    }
    // Function: create children for state, return as arraylist<state>
    public ArrayList<GameState> MakeBabies(){
        ArrayList<GameState> children = new ArrayList<>();
        boolean b = this.isPlayerState;
        for(int i=0; i<lines.length-1; i++){
            for(int j=0; j<lines[i].length; j++){
                if(i%2==0){// horizontal lines
                    if(j<lines[i].length-1){
                        if(!lines[i][j].getIsDrawn()){
                            GameState child = new GameState(this, i, j, !b); // make sure this doesnt mess stuff up
                            children.add(child);
                        }
                    }
                }
                else{
                    if(!lines[i][j].getIsDrawn()){
                        GameState child = new GameState(this, i, j, !b); // make sure this doesnt mess stuff up
                        children.add(child);
                    }
                }
            }
        }
        return children;
    }

    /* HELPER FUNCTION: Ensure a copy of value (not reference) of the state[lines and boxes]
    from the parent, so when we make changes, it does not change the parent state as well.
    */
    private boolean DeepCopyLinesAndBoxes(GameState parent){
        // deep copy boxes (value, not reference)
        this.currentBoxes = new Box[boardSize][boardSize];
        for(int i=0; i<boardSize; i++){
            for(int j=0; j<boardSize; j++){
                this.currentBoxes[i][j] = new Box(parent.getBox(i,j));
            }
        }
        // deep copy lines (value, not reference)
        this.lines = new Line[(boardSize+1)*2][boardSize+1];
        int br=0;
        for(int i = 0; i< this.lines.length; i++){ //ROW
            for(int j = 0; j< this.lines[i].length; j++){ // COL
                if(i%2==0){ // horizontal lines
                    if(j<this.lines[i].length-1) {this.lines[i][j] = new Line(parent.getSingleLine(i,j));}
                } // horizontal lines
                else if (i<this.lines.length-1){this.lines[i][j] = new Line(parent.getSingleLine(i,j));} // vertical lines
            }
        }
        //Tie Lines to Boxes
        for(int i = 2; i< this.lines.length; i++){ //ROW
            for(int j = 0; j< this.lines[i].length-1; j++){ // COL
                if(i%2==0 && br<boardSize) {
                    this.currentBoxes[br][j].SetLine(this.lines[i-2][j]); //top
                    this.currentBoxes[br][j].SetLine(this.lines[i][j]); //bottom
                } // horizontal lines
            }
            if(i%2==0)br++; // only increase boxes row counter every 2 rows
        }
        //Tie Lines to Boxes
        br=0;
        for(int i = 1; i< this.lines.length; i++){ //ROW
            for(int j = 0; j< this.lines[i].length-1; j++){ // COL
                if(i%2!=0 && br<boardSize){
                    this.currentBoxes[br][j].SetLine(this.lines[i][j]); //left
                    this.currentBoxes[br][j].SetLine(this.lines[i][j+1]); //right
                } // vertical lines
            }
            if(i%2==0)br++; // only increase boxes row counter every 2 rows
        }
        return true;
    }


}

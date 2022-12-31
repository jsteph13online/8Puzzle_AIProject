/*
    CSC 480 AI: Fall 2022
    Assignment 2: Dots and Boxes (Modified)
    Joscelyn Stephens
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class Solver {
    private final int MIN_PLY = 1;
    private final int MAX_PLY = 100;
    private final int MIN_SIZE = 2;
    private final int MAX_SIZE = Integer.MAX_VALUE;

    private int boardSize;
    private int ply;
    private boolean aiIsMax;
    private boolean aiTurn;

    private GameState initState;
    private ArrayList<GameState> Plays;

    GameBoard gameBoard;

    Solver(){  aiIsMax=false;}

    public void StartGame(int p, int size, GameBoard gb){
        gameBoard = gb;
        boardSize = size;
        ply = p;

        //Determine if player is Min or Max with coin flip
        //Set aiTurn bool true/false accordingly
        if (Math.random() < 0.5){aiTurn=true;}
        else{aiTurn = false;}

        // Init state setup and init gameboard setup
        gameBoard.SetupBoard(boardSize);
        gameBoard.InitScores(aiTurn);
        initState = InitBoxesLines();

        //save init state into a root node
        Plays = new ArrayList<>();
        Plays.add(initState);

        //Visual setup
        gameBoard.UpdateBoard(initState);

        if(aiTurn)AiTurn();

    }

    // FUNCTION: Init the boxes and lines objects, create the init state with the
    // boxes and lines inside of it
    public GameState InitBoxesLines(){
        //Init array of blank "uncompleted, unowned" boxes
        Box[][] boxes = new Box[boardSize][boardSize];
        for(int i = 0; i< boxes.length; i++){
            for(int j=0; j< boxes[i].length; j++){
                boxes[i][j] = new Box();
            }
        }
        //Create Line Objects
        int linesNum = (boardSize*(boardSize+1))*2;
        System.out.println("Number of lines is: " + linesNum); //For Testing
        //Init Lines
        Line[][] lines = new Line[(boardSize+1)*2][boardSize+1];
        int br=0;
        for(int i = 0; i< lines.length; i++){ //ROW
            for(int j = 0; j< lines[i].length; j++){ // COL
                if(i%2==0){ // horizontal lines
                    if(j<lines[i].length-1) {lines[i][j] = new Line(false, i, j);}
                } // horizontal lines
                else if (i<lines.length-1){lines[i][j] = new Line(true, i, j);} // vertical lines
            }
        }
        //Tie Lines to Boxes (Horizontal Lines)
        for(int i = 2; i< lines.length; i++){ //ROW
            for(int j = 0; j< lines[i].length-1; j++){ // COL
                if(i%2==0 && br<boardSize) {
                        boxes[br][j].SetLine(lines[i-2][j]); //top
                        boxes[br][j].SetLine(lines[i][j]); //bottom
                } // horizontal lines
            }
            if(i%2==0)br++; // only increase boxes row counter every 2 rows
        }
        //Tie Lines to Boxes (Vertical Lines)
        br=0;
        for(int i = 1; i< lines.length; i++){ //ROW
            for(int j = 0; j< lines[i].length-1; j++){ // COL
                if(i%2!=0 && br<boardSize){
                    boxes[br][j].SetLine(lines[i][j]); //left
                    boxes[br][j].SetLine(lines[i][j+1]); //right
                } // vertical lines
            }
            if(i%2==0)br++; // only increase boxes row counter every 2 rows
        }
        initState = new GameState(boardSize, lines, boxes, !aiTurn);
        return initState;
    }

    //Process Player Move, Create New State, Signal AI Turn
    public void PlayerMove(int xInd, int yInd){
        GameState current = Plays.get(Plays.size()-1); // get latest play from list
        //Create a new game state from the current one + player's move
        GameState next = new GameState(current, xInd, yInd, true);
        //if move is terminal, enter endGame
        if(next.getBoolTerminal()){EndGame(next); return;}
        // else, add move to plays, update GUI and signal AI turn
        Plays.add(next);
        aiTurn=true;
        gameBoard.UpdateScoresPlayer(aiTurn, next);
        gameBoard.UpdateBoard(next);
        AiTurn();
    }
    //AI calls minimaxpruning alg, Creates new state, signals player turn
    public void AiTurn(){
        GameState current = Plays.get(Plays.size()-1); // get latest play from list
        //call the minmaxpruning algorithm to select next move
        int[] chosenMove = current.MiniMaxPruning(0, ply, Integer.MIN_VALUE, Integer.MAX_VALUE);
        GameState next = new GameState(current, chosenMove[0], chosenMove[1], false);
        System.out.println("AI Move Made");
        //check if this move ended the game
        if(next.getBoolTerminal()){EndGame(next); return;}
        // else, add move to plays, update GUI and signal Player turn
        Plays.add(next);
        aiTurn=false;
        gameBoard.UpdateScoresAi(aiTurn, next);
        gameBoard.UpdateBoard(next);

    }
    //If Player/AI move ended game, calls this and ends game
    public void EndGame(GameState endState){
        System.out.println("End Game Reached!");
        gameBoard.UpdateScoresEndGame(endState);
        gameBoard.UpdateBoard(endState);
    }


    //GETTERS
    public int getMAX_PLY() {return MAX_PLY;}
    public int getMIN_PLY() {return MIN_PLY;}
    public int getMIN_SIZE(){return MIN_SIZE;}
    public int getMAX_SIZE(){return MAX_SIZE;}
}

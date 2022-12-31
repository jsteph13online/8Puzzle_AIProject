/*
    CSC 480 AI: Fall 2022
    Assignment 2: Dots and Boxes (Modified)
    Joscelyn Stephens
 */


//LIST OF COMPONENTS OF DOTS AND BOXES:
/*
    COMPONENTS OF THIS 8-PUZZLE PROGRAM:
        Main.java

 */

/*
DotBox Project Jave Files:
    Main.java - main(), creates Solver, GameBoard and UserSelection objects
    UserSelection.java - GUI for user selection
    GameBoard.java - GUI for gameboard
    Solver.java - runs game logic
    GameState.java - nodes. contains lines and boxes
    Line.java - knows if line is drawn, is vertical/horix, its index in array (for debugging)
    Box.java - knows ts score, if it is drawn, its owner, lines attached to it
 */

public class Main{

    UserSelection userSelection;
    GameBoard gameBoard;
    Solver solver;

    // MAIN : init user selection window, solver, results window (hidden)
    public static void main(String[] args) {
        System.out.println("Starting Dot Block Program");
        Main run = new Main();
        //run.CreateResultsFrame();
        run.CreateBrains();
        run.CreateWindows();
    }

    //INITs CLASS: SOLVER, which handles the brains and solving portion of the program
    public void CreateBrains(){ solver = new Solver();}


    // INIT CLASS: GUI class UserSelection
    public void CreateWindows() {
        gameBoard = new GameBoard(solver);
        userSelection = new UserSelection(solver, gameBoard);
    }

}

/*
    CSC 480 AI: Fall 2022
    Assignment 2: Dots and Boxes (Modified)
    Joscelyn Stephens
 */

//graphic components for GUI
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;



public class GameBoard implements ActionListener/*, ItemListener */ {
    // Frame objects that need to be referenced from multiple methods
    // (problem and alg selections)
    JFrame frame;
    JPanel p;

    private final Color MIN_COLOR = Color.CYAN;
    private final Color MAX_COLOR = Color.ORANGE;

    private int gameSize;
    private int boxNum;
    private int lineNum;

    JTextArea text;
    final String BASE_TEXT = "Preparing Game Board...\n";
    final String ERR_VALUE = "Invalid Selection. Enter New Coordinates.";
    final String ERR_MOVE = "Line has already been drawn. Enter New Coordinates.";

    JButton confirm;
    JTextField x;
    JTextField y;
    JLabel errorMsg;
    JLabel playerTurnIdentifier;
    JLabel aiScore;
    JLabel playerScore;
    JLabel boxesMade;
    JLabel linesLeft;
    JRadioButton Top;
    JRadioButton Bottom;
    JRadioButton Right;
    JRadioButton Left;

    Solver solver;
    GameState currentState;

    //Setup of game board (invisible initially)
    GameBoard(Solver s) {
        solver = s;
        //==Create Window
        //Create JFrame, make sure applications closes if we close Window
        frame = new JFrame("Dots and Boxes Game Board");
        //==Creation and Setup of Window Elements==
        // User Interaction Objects
        confirm = new JButton("Move");
        confirm.setBounds(10, 730, 70, 30);
        confirm.addActionListener(this);
        JLabel inst = new JLabel("Enter coordinates for desired Box,");
        inst.setBounds(10, 605, 300, 30);
        JLabel inst2 = new JLabel("And select which line in that box you wish to select:");
        inst2.setBounds(10, 625, 300, 30);
        x = new JTextField("x");
        x.setBounds(10, 680, 50, 30);
        y = new JTextField("y");
        y.setBounds(80, 680, 50, 30);

        Top = new JRadioButton("Top Line", true);
        Top.setBounds(200, 650, 75, 30);
        Bottom = new JRadioButton("Bottom", false);
        Bottom.setBounds(200, 710, 75, 30);
        Right = new JRadioButton("Right", false);
        Right.setBounds(230, 680, 75, 30);
        Left = new JRadioButton("Left", false);
        Left.setBounds(170, 680, 50, 30);
        ButtonGroup lineGroup = new ButtonGroup();
        lineGroup.add(Top);
        lineGroup.add(Bottom);
        lineGroup.add(Right);
        lineGroup.add(Left);

        // Error Message
        errorMsg = new JLabel(ERR_VALUE);
        errorMsg.setBounds(10, 760, 300, 30);
        errorMsg.setVisible(false);
        // Score and Turn Identifiers
        playerTurnIdentifier = new JLabel("n/a");
        playerTurnIdentifier.setBounds(330, 630, 300, 30);
        boxesMade = new JLabel(" 00/00 Boxes");
        boxesMade.setBounds(470, 660, 300, 30);
        linesLeft = new JLabel(" 00/00 Lines");
        linesLeft.setBounds(470, 690, 300, 30);
        aiScore = new JLabel("00");
        aiScore.setBounds(470, 720, 300, 30);
        playerScore = new JLabel("00");
        playerScore.setBounds(470, 750, 300, 30);
        frame.add(confirm);
        frame.add(x);
        frame.add(y);
        frame.add(inst);
        frame.add(inst2);
        frame.add(errorMsg);
        frame.add(playerTurnIdentifier);
        frame.add(boxesMade);
        frame.add(linesLeft);
        frame.add(aiScore);
        frame.add(playerScore);
        frame.add(Top);
        frame.add(Bottom);
        frame.add(Right);
        frame.add(Left);

        // Scrollable text area
        p = new JPanel();
        p.setSize(800, 600);
        text = new JTextArea(BASE_TEXT);
        text.setLineWrap(false);
        text.setEditable(false);
        JScrollPane scroll = new JScrollPane(text);
        scroll.setPreferredSize(new Dimension(800, 600));
        p.add(scroll, BorderLayout.CENTER);
        frame.getContentPane().add(p, BorderLayout.CENTER);
        frame.setSize(850, 830);
        //==Display Window
        frame.setLocationRelativeTo(null); //position
        frame.setVisible(false); //visibility
    }

    public void SetupBoard(int size) {
        gameSize = size;
        boxNum = size * size;
        lineNum = (size * (size + 1)) * 2;
        //Make Visible
        frame.setVisible(true);
    }


    //====Update displays of Score/Turn Values ====
    //helper - Update ai score display
    public void UpdateAIScore(int i) {
        SetLabel(("AI Score: " + i), aiScore);
    }
    //helper - Update player score display
    public void UpdatePlayerScore(int i) {
        SetLabel(("Player Score: " + i), playerScore);
    }
    //helper - Update box count display
    public void UpdateBoxCount(int i) {
        SetLabel((i + "/" + boxNum + " Boxes Made"), boxesMade);
    }
    //helper - Update line count display
    public void UpdateLineCount(int i) {
        SetLabel((i + "/" + lineNum + " Lines Drawn"), linesLeft);
    }
    // helper - Update display of whose turn it is, set button to active/inactive
    public void UpdateTurn(boolean b) {
        if (b) {
            playerTurnIdentifier.setForeground(Color.RED);
            SetLabel(("AI Turn"), playerTurnIdentifier);
            confirm.setEnabled(false);
        } else {
            playerTurnIdentifier.setForeground(Color.BLUE);
            SetLabel(("Player Turn"), playerTurnIdentifier);
            confirm.setEnabled(true);
        }
    }
    // FUNCTION: Init the scores displayed on the game board (all 0)
    public void InitScores(boolean b) {
        UpdateAIScore(0);
        UpdatePlayerScore(0);
        UpdateBoxCount(0);
        UpdateLineCount(0);
        UpdateTurn(b);
    }
    // FUNCTION: update displays  (player turn complete)
    public void UpdateScoresPlayer(boolean b, GameState s) {
        UpdatePlayerScore(s.getPlayerScore());
        UpdateBoxCount(s.countBoxes());
        UpdateLineCount(s.countLines());
        UpdateTurn(b);
    }
    // FUNCTION: update scores (ai turn complete)
    public void UpdateScoresAi(boolean b, GameState s) {
        UpdateAIScore(s.getAiScore());
        UpdateBoxCount(s.countBoxes());
        UpdateLineCount(s.countLines());
        UpdateTurn(b);
    }
    // FUNCTION: update scores and display to signal END OF GAME AND WINNER
    public void UpdateScoresEndGame(GameState s){
        UpdatePlayerScore(s.getPlayerScore());
        UpdateAIScore(s.getAiScore());
        UpdateBoxCount(s.countBoxes());
        UpdateLineCount(s.countLines());
        confirm.setEnabled(false);// turn off confirm button since game is over
        // change turn identifier label into a "who is winner" identifier
        int winner = s.isPlayerWinning();
        switch(winner){
            case 1:
                playerTurnIdentifier.setForeground(Color.BLUE);
                SetLabel(("Congratulations! You Win!"), playerTurnIdentifier);
                break;
            case -1:
                playerTurnIdentifier.setForeground(Color.RED);
                SetLabel(("Game Over. AI Wins."), playerTurnIdentifier);
                break;
            default:
                playerTurnIdentifier.setForeground(Color.BLACK);
                SetLabel(("It's a TIE! Try Again!"), playerTurnIdentifier);
                break;
        }
    }
    // FUNCTION: update board display
    public void UpdateBoard(GameState s){
        currentState=s;
        System.out.println("Updating Game Board");
        errorMsg.setVisible(false); // turn off any error message if visible
        Line[][] lines = currentState.getLines();
        String str = "\n";
        String space = "  ";
        String temp = "";
        SetText(str);

        int br = 0;
        // Number the columns
        for (int j = 0; j < lines[0].length; j++) {
                if (j < lines[0].length - 1) { // Numbering the columns
                    if (j == 0) {
                        str += String.format("%10s", space);
                    }
                    if (gameSize < 1000) {
                        temp = String.format("%03d", j);
                    } else {
                        temp = String.format("%10d", j);
                    }
                    str += String.format("%10s", temp);
                }
        }
            AppendText(str);
            str = "\n";
            // == Horizontal lines ==
        for(int i = 0; i< lines.length; i++){ //ROW
            for(int j = 0; j< lines[i].length; j++) { // COL
                if (j==0 && i%2!=0 && br<gameSize){ // row numbering
                    if(gameSize < 1000){temp= String.format("%03d", br);}
                    else{temp= String.format("%-10d", br);}
                    str += String.format("%-10s", temp);
                }
                else if (j==0){ // just spacing for horz. line rows
                    str += String.format("%12s", space);
                }
                if (i % 2 == 0) { // horizontal lines
                    if (j < lines[i].length - 1) {
                        str+= String.format("%-12s", lines[i][j].GetString());
                    }
                }
                //== Vertical lines and box information ==
                else if (i < lines.length - 1) {
                    if(lines[i][j].isDrawn){str+= String.format("%-3s", lines[i][j].GetString());}
                    else{str+= String.format("%-3s", space);}
                    if (br < gameSize && j < gameSize) {
                        if(s.checkIfBox(br, j)){
                            str += String.format("%-10s", s.getBox(br,j).getOwnerString() + s.getBox(br,j).getValue());
                            }
                        else{
                            str += String.format("%-10s", s.getBox(br,j).getValue());
                        }
                    }
                } // =====
            }
            AppendText(str); // print the line
            str = "\n"; // reset the string
            if(i>1 && i%2==0)br++;
        }
    }


    //FUNCTIONS FOR SETTING THE TEXT-BOX TEXT
    public void SetText(String s){
        if(text == null){ FixNullText(s);}
        else {text.setText(s);}}
    public void SetLabel(String s, JLabel l){l.setText(s);}
    public void AppendText(String s){
        if(text == null){ FixNullText(BASE_TEXT + s);}
        else {text.append(s);}}

    // Helper method that re-initiates the text field in certain cases
    public void FixNullText(String s){
        System.out.println("nullTextIssue");
        text = new JTextArea (s);
        text.setLineWrap(true);
        text.setEditable(false);
        text.setVisible(true);
        p.add(text);
    }

    // Action Taken when Confirm button is pressed
    public void actionPerformed(ActionEvent e) {
        errorMsg.setVisible(false);
        String xStr = x.getText();
        String yStr = y.getText();
        int xNum=0;
        int yNum=0;
        try{ // convert string to int
            xNum = Integer.parseInt(xStr);
            yNum = Integer.parseInt(yStr);
        }catch(Exception e1){System.out.println("ERROR: Invalid entry");
            errorMsg.setText(ERR_VALUE);
            errorMsg.setVisible(true);}
        if(xNum > -1 && yNum > -1 && xNum < gameSize+1 && yNum< gameSize){
            CheckIfValidMove(xNum, yNum);
        }
        else // num outside of range
        {
            System.out.println("Error: Number outside of coordinate range.");
            errorMsg.setText(ERR_VALUE);
            errorMsg.setVisible(true);

        }

    }

    // If coordinates entered are obviously valid (are int, are within gameboard bounds,
    // check if the line has already been drawn
    // if not, send move to Solver
    public void CheckIfValidMove(int xNum, int yNum){
        //Find Line()
        int lineSide = 0;
        boolean isVertical = true;
        int yIndex = 0;
        int xIndex = 0;
        if(Top.isSelected()){
            xIndex = (xNum*2); yIndex = yNum;
        }
        else if(Bottom.isSelected()){
            xIndex = (xNum*2)+2; yIndex = yNum;
        }
        else if(Right.isSelected()){
            xIndex = (xNum*2)+1; yIndex = yNum+1;
        }
        else{
            xIndex = (xNum*2)+1; yIndex = yNum;
        }

        /* Making sure correct lines are being selected
        System.out.println("xIndex, yIndex: " + xIndex + " " +yIndex);
        Line l = currentState.getSingleLine(xIndex, yIndex);
        System.out.println("Line x,y " + l.getX() + " " +l.getY()); // */

        if(currentState.checkIfLineDrawn(xIndex, yIndex)){
            //Line Already Drawn
            System.out.println("Error: Invalid Selection - Line Drawn");
            errorMsg.setText(ERR_MOVE);
            errorMsg.setVisible(true);
            return;
        }
        else{
            System.out.println("Making Player Move");
            solver.PlayerMove(xIndex, yIndex);
        }


    }
}

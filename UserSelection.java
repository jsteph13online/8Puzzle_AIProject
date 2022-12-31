/*
    CSC 480 AI: Fall 2022
    Assignment 2: Dots and Boxes (Modified)
    Joscelyn Stephens
 */

//graphic components for GUI
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class UserSelection implements ActionListener{
    // Frame objects that need to be referenced from multiple methods
    // (problem and alg selections)
    JFrame frame;
    JTextField plyField;
    JLabel enterValidNumber;
    JTextField sizeField;
    JLabel enterValidSize;

    Solver solver;
    GameBoard board;

    // Setup of JFRAME for user selection GUI window
    public UserSelection(Solver s, GameBoard b) {
        solver = s;
        board = b;

        //==Create Window
        //Create JFrame, make sure applications closes if we close Window
        frame = new JFrame("Dot Block Game Setup");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //==Creation and Setup of Window Elements==
        //--Title
        JLabel titleLabel = new JLabel("Game Setup:");
        titleLabel.setBounds(10, 10, 300, 30);

        //Ply level for AI
        JLabel plyTitle = new JLabel("Ply Level:");
        plyTitle.setBounds(10, 70, 150, 30);
        JLabel plyInst = new JLabel("(Enter a number between " + solver.getMIN_PLY() + " and " + solver.getMAX_PLY() + ")");
        plyInst.setBounds(10, 100, 300, 30);
        plyField = new JTextField("1");
        plyField.setBounds(10, 130, 50, 30);
        enterValidNumber = new JLabel("Enter a valid ply number between " + solver.getMIN_PLY() + " and " + solver.getMAX_PLY());
        enterValidNumber.setBounds(10, 160, 300, 30);
        enterValidNumber.setVisible(false);

        //--Buttons: Algorithm Mode (Depth, Breadth, Uniform, Greedy, A1, A2)
        JLabel sizeTitle = new JLabel("Game Board Size");
        JLabel sizeInst = new JLabel("Enter 2 for 2x2, 5 for 5x5, etc.");
        sizeTitle.setBounds(350, 70, 300, 30);
        sizeInst.setBounds(350, 100, 300, 30);
        sizeField = new JTextField("2");
        sizeField.setBounds(350, 130, 50, 30);
        enterValidSize = new JLabel("Enter a valid size number between " + solver.getMIN_SIZE()+ " and " + solver.getMAX_SIZE());
        enterValidSize.setBounds(350, 160, 300, 30);
        enterValidSize.setVisible(false);


        //--Button: Confirm
        JButton confirmButton = new JButton("Confirm");
        confirmButton.setBounds(250, 190, 100, 30);
        confirmButton.addActionListener(this);

        // Add elements to frame
        frame.add(titleLabel);
        frame.add(plyTitle);
        frame.add(sizeTitle);
        frame.add(confirmButton);
        frame.add(plyInst);
        frame.add(plyField);
        frame.add(enterValidNumber);
        frame.add(sizeInst);
        frame.add(sizeField);
        frame.add(enterValidSize);
        //======

        //==Display Window
        frame.setLayout(null);
        frame.setSize(650, 280);
        frame.setVisible(true); //visibility
    }

    // CONFIRM BUTTON ACTIONS:
    // Call the SOLVER which is the brain
    public void actionPerformed(ActionEvent e) {
       // TBD - need to change game board?
        enterValidNumber.setVisible(false);
        enterValidSize.setVisible(false);
        int plyLevel = 1;
        int gridSize = 2;
        //Parse Size Field User Entry
        int tempSize = 2;
        try{tempSize = Integer.parseInt(sizeField.getText());}
            catch(Exception e1){System.out.println("Cannot Parse Int"); tempSize = -1;}
        // Parse Ply Field User Entry
        int temp = 1;
        try{temp = Integer.parseInt(plyField.getText());}
            catch(Exception e1){System.out.println("Cannot Parse Int"); temp = -1;}
        // If Ply and Size both Valid, then call the Solver
        if(temp > (solver.getMIN_PLY()-1) && temp < (solver.getMAX_PLY()+1))
        {
            if(tempSize > (solver.getMIN_SIZE()-1) && tempSize<(solver.getMAX_SIZE())){
                plyLevel = temp;
                gridSize = tempSize;
                System.out.println("Ply Level: " + plyLevel);
                System.out.println("Board Size: " + gridSize);
                // Trigger the SOLVER to start doing its thing
                solver.StartGame(plyLevel, gridSize, board);
            }
            else{
                //Invalid Ply Value number entered
                System.out.println("Invalid Size Value" + sizeField.getText());
                enterValidSize.setVisible(true);
            }
        }
        else{
                //Invalid Ply Value number entered
                System.out.println("Invalid Ply Value" + plyField.getText());
                enterValidNumber.setVisible(true);
        }



    }

    // Update display of the current problem
    // Based on which problem is selected
    //public void itemStateChanged(ItemEvent e){    }


}

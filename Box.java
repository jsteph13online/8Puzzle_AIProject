/*
    CSC 480 AI: Fall 2022
    Assignment 2: Dots and Boxes (Modified)
    Joscelyn Stephens
 */

import java.util.ArrayList;

public class Box {
    private final int MIN_BOX_PT = 1; // min pts a box can be worth
    private final int MAX_BOX_PT = 5; // max pts a box can be worth

    boolean ownedByPlayer; // true=PLAYER; false=AI
    boolean isCreated;
    int value;
    public ArrayList lines;

    //Clone cstr
    public Box(Box b){this.isCreated = b.getCreated(); this.value=b.getValue(); this.lines = new ArrayList<Line>(); this.ownedByPlayer = b.getOwnerBool();}

    // regular cstr
    public Box(){isCreated = false; value=generateBoxPoints(); lines = new ArrayList<Line>();}

    // return true = player false = ai
    public boolean getOwnerBool(){return ownedByPlayer;}
    // return a string for GUI display based on who the owner of the box is
    public String getOwnerString(){
        if(ownedByPlayer){return "pl: ";}
        else{return "ai: ";}
    }
    // return the int value of the box
    public int getValue(){return value;}

    // Attach lines (4 total) to the box
    // These are the lines that will be checked to see if box is created or not
    public void SetLine(Line l){
        lines.add(l);
    }

    // return true/false if box is created
    public boolean getCreated(){return isCreated;}
    //evaluate box lines to see if it has been created. if yes, set its owner to state owner
    public boolean evaluateCreated(boolean stateOwner){
        if(isCreated){return true;}
        else if(checkBoxLines(stateOwner)){return true;}
        else return false;
        }

    //FUNCTION: check lines to see if the box is drawn
    // if all 4 lines drawn, set isCreated bool to true
   private boolean checkBoxLines(boolean stateOwner){
        for(Object l: lines){
            Line line = (Line)l;
            // if a line is not drawn, return false
            if(line.getIsDrawn()==false){return false;}
        }
        // if all lines are true, the box is drawn!
        isCreated = true;
        ownedByPlayer = stateOwner;
        //System.out.println("Box belongs to player: " + ownedByPlayer);
        return isCreated;
    }


    // (function used for debugging purposes only )
    // public ArrayList<Line> GetLines(){return lines;}

    // FUNCTION: Generate point value for box between MAX and MIN values
    public int generateBoxPoints(){
        // generate a pseudo-random number between the maximum value (inclusive) and minimum point value (inclusive)
        int kindaRandom = (int)((Math.random() * ((MAX_BOX_PT+1) - MIN_BOX_PT)) + MIN_BOX_PT);
        //System.out.println("Box Point: " + kindaRandom); // FOR DEBUG
        return kindaRandom;
    }
}

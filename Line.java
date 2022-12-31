/*
    CSC 480 AI: Fall 2022
    Assignment 2: Dots and Boxes (Modified)
    Joscelyn Stephens
 */

public class Line {
    final String VERT = "| ";
    final String HOR = ". ____";
    final String HORem = ". . . . . . ";

    boolean isVertical;
    boolean isDrawn;

    int x;
    int y;


    //regular cstr
    Line(boolean v, int xnum, int ynum){
        isDrawn = false; // All start false
        isVertical = v;
        x = xnum;
        y = ynum;
    }

    //cstr for deep copy
    Line(Line l){
        isDrawn = l.getIsDrawn(); // All start false
        isVertical = l.getIsVertical();
        x = l.getX();
        y = l.getY();
    }

    //Set string to DRAWN
    public void DrawLine(){isDrawn = true;}
    //==Getters
    public boolean getIsDrawn(){return isDrawn;}
    public boolean getIsVertical(){return isVertical;}

    // This value used for debugging purposes to ensure lines are correct
    public int getX(){return x;}
    public int getY(){return y;}


    //==String representation of Line
    public String GetString(){
        if(isVertical){return VERT;}
        else if(isDrawn){return HOR;}
        //else{return HOR;}
        else{return HORem;}
    }



}

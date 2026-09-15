package daa3;

import java.awt.Polygon;
import java.util.Arrays;

public class Region { 
	

    private final int id;  // Region id
    private int colorIndex = -1;  // colour of the region (default : uncoloured)
    private boolean fixed = false; // colour of the region is fixed or not
    private Polygon polygon; // polygon for region 
    
    
    public Region(int id) { // constructor 
    	this.id = id;
        this.possible = new boolean[MapPuzzleGame.NUM_COLORS];
        
    }
    
    private boolean[] possible; // tracks which colours are possible for this region

    public void togglePossible(int c) {  // flips the colour states
        if (c >= 0 && c < possible.length)
            possible[c] = !possible[c];
    }
    
    public boolean isPossible(int c) { // checks if colour c is possible for the region or not
        return possible[c];
    }

    public int getId() {  // returns id of region
    	return id; 
    }
    
    public int getColorIndex() { // return the colour of the region
    	return colorIndex; 
    }
    
    public void setColorIndex(int c) {   // sets the colour of the region 
    	colorIndex = c; 
    }
    
    public boolean isFixed() {  // returns whether the colour of the region is fixed or not
    	return fixed; 
    }
    
    public void setFixed(boolean f) {  // sets whether the region's colour is fixed
    	fixed = f; 
    }
    
    public Polygon getPolygon() { // returns the polygon representing the region's shape
    	return polygon; 
    }
    
    public void setPolygon(Polygon p) {  // set the polygon for the region
    	polygon = p; 
    }
}

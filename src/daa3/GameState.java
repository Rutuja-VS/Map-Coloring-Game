package daa3;

import java.util.*;
public class GameState {
	
	private static final String[] COLOR_NAMES = {
	        "Red","Blue","Green","Yellow",
	        "Purple","Orange","Teal","Gray",
	        "Pink","Mint"
	};
	
	private Set<String> failedStates = new HashSet<>();
    private List<Region> regions = new ArrayList<>();
    private Map<Integer, Set<Integer>> adj = new HashMap<>();
    
    // Track backtrack steps during solve
    private int backtrackStepCount = 0;
    private int totalBacktrackSteps = 0; // Track total across all attempts
    private MapPuzzleGame game; // Reference to main game for tracking

    public GameState() {
        reset();
    }
    
    public void setGame(MapPuzzleGame game) {
        this.game = game;
    }

    public List<Region> getAllRegions() { return regions; }
    public Region getRegion(int id) { return regions.get(id); }
    public int getRegionCount() { return regions.size(); }
    public Set<Integer> getNeighbors(int regionId) { return adj.get(regionId); }

    public boolean isComplete() {
        return countColoredRegions() == getRegionCount();
    }
    
    public void reset() {
    	
    	regions.clear();
        adj.clear();
        backtrackStepCount = 0;
        totalBacktrackSteps = 0;
        
        int total = MapPuzzleGame.ROWS * MapPuzzleGame.COLS;
        
        for (int i = 0; i < total; i++) {
            addRegion(new Region(i));
        }
        
        for (int r = 0; r < MapPuzzleGame.ROWS; r++) {
            for (int c = 0; c < MapPuzzleGame.COLS; c++) {
            	
                int id = r * MapPuzzleGame.COLS + c;
                
                if (c < MapPuzzleGame.COLS - 1) {
                    addNeighbor(id, id + 1);
                }
                if (r < MapPuzzleGame.ROWS - 1) {
                    addNeighbor(id, id + MapPuzzleGame.COLS);
                }
            }
        }

        assignColorsRandomly();
    }

    private void assignColorsRandomly() {

        Random rand = new Random();
        List<Integer> regionIds = new ArrayList<>();

        for (int i = 0; i < regions.size(); i++) { 
            regionIds.add(i);  
        }

        Collections.shuffle(regionIds);

        int assigned = 0;

        for (int color = 0; color < MapPuzzleGame.NUM_COLORS && assigned < regionIds.size(); ) {  

            int id = regionIds.get(assigned); 
            
            if (canUseColor(id, color)) {  
                Region r = getRegion(id);
                r.setColorIndex(color);
                r.setFixed(true);
                color++;
            }
            assigned++; 
       }
    }

    void addRegion(Region r) {
        regions.add(r);
        adj.put(r.getId(), new HashSet<>());
    }
   
    void addNeighbor(int a, int b) {
        adj.get(a).add(b);          
        adj.get(b).add(a);
    }

    public void setColor(int id, int c) {
        if (!getRegion(id).isFixed() && canUseColor(id, c)) {
            getRegion(id).setColorIndex(c);
        }
    }
    
    boolean canUseColor(int id, int c) {
        for (int n : adj.get(id)) {
            if (getRegion(n).getColorIndex() == c) 
                return false;
        }
        return true;
        
    }
    
    public boolean isValid() {
        for (Region r : regions) {
            int c = r.getColorIndex();
            if (c == -1) continue;

            for (int n : adj.get(r.getId())) {
                if (getRegion(n).getColorIndex() == c)
                    return false;
            }
        }
        return true;
    }

    public void clearAllNonFixed() {
        for (Region r : regions)
            if (!r.isFixed()) r.setColorIndex(-1); 
    }

    public int countColoredRegions() {
        return (int) regions.stream().filter(r -> r.getColorIndex() != -1).count();
    }
    
    private String encodeState() {
        StringBuilder sb = new StringBuilder();
        for (Region r : regions) {
            sb.append(r.getColorIndex()).append(",");
        }
        return sb.toString();
    }
   
    public boolean solve() {
        failedStates.clear();
        clearAllNonFixed();
        
        totalBacktrackSteps = 0; // Reset total for this solve attempt
        backtrackStepCount = 0; // Reset for each attempt
        
        boolean solved = solveUtil(0);

        System.out.println("\n=================================");
        if (solved) {
            System.out.println("Map coloring completed successfully!");
        } else {
            System.out.println("No valid coloring found.");
        }
        System.out.println("Backtracking steps : " + totalBacktrackSteps);
        System.out.println("=================================\n");

        return solved;
    }
    
    private boolean solveUtil(int id) {

        String state = encodeState();

        if (failedStates.contains(state)) {
            return false;
        }

        if (id == regions.size()) {
        	System.out.println("SOLUTION FOUND");
            return true;
        }

        Region r = regions.get(id);

        if (r.isFixed() || r.getColorIndex() != -1) {
            return solveUtil(id + 1);
        }

        List<Integer> colorOrder = new ArrayList<>();
        for (int i = 0; i < MapPuzzleGame.NUM_COLORS; i++) {
            colorOrder.add(i);
        }

        Collections.shuffle(colorOrder);
        
        System.out.print("Colour Order for Region "+ id + " : ");
        for (int c : colorOrder) {
            System.out.print(COLOR_NAMES[c] + " ");
        }
        System.out.println("");  
       
        System.out.println("Trying Region " + id + " : ");
        for (int c : colorOrder) {
        	
            if (canUseColor(id, c)) {
            	System.out.println("     "+COLOR_NAMES[c] + " → Assign " + COLOR_NAMES[c]+"\n");

                r.setColorIndex(c);

                if (solveUtil(id + 1)) {
                    return true;
                }
                
                // Backtrack
                System.out.println();
                System.out.println("     "+"Backtrack Region " + id + " from " + COLOR_NAMES[c]);
               
                r.setColorIndex(-1);
                                
                backtrackStepCount++;
                totalBacktrackSteps++;

                if (game != null) {
                    game.incrementBacktrackSteps();
                }
            }else { 
	            System.out.println("     " + COLOR_NAMES[c] + " invalid (neighbor conflict)");
            }
        }

        failedStates.add(state);
        return false;
    }
}
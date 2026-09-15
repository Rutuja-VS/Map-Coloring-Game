package daa3;


import javax.swing.*;  // GUI components (provides window or user friendly interface)
import java.awt.*;      // colour, point, polygons, graphics
import java.awt.event.*;   // mouse point handling (events)

public class MapPanel extends JPanel {

    private final MapPuzzleGame game; // object variables
    private final GameState gameState;

    private Region draggedRegion; // stores region (selected by player). turn based, refreshes after each turn
    private boolean rightDrag;  // if right clicked (mouse) => operation happens (boolean)  

    public MapPanel(MapPuzzleGame game, GameState gameState) {
        this.game = game;
        this.gameState = gameState;
        setBackground(MapPuzzleGame.PLAYER_BG); // changes bg colour for each turn (user, system)
        setupMouseListener(); // mouse logic
    } 
    
    private void setupMouseListener() {
    	addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseReleased(e);
            }
        });
    	
    }
    
    public void handleMousePressed(MouseEvent e) { // triggered when mouse pressed
        if (game.isComputerTurn()) return;    // prevents user stores object variables
        
        draggedRegion = findRegion(e.getPoint());  // Find which polygon user clicked.
        rightDrag = SwingUtilities.isRightMouseButton(e); // Checks whether user right clicked or left clicked 
    } 
    
    private void handleMouseReleased(MouseEvent e) {

        if (game.isComputerTurn()) return;

        Region target = findRegion(e.getPoint()); // Find region where mouse was released 
        boolean validHumanMove = false;

        if (isHintAction(target)) { // If right-click + valid empty region 
            processHint(target); // Mark a dot on that empty region 
        } 
        else if (isHumanMoveAction(target)) { // If normal move 
            validHumanMove = processHumanMove(target);
        }

        finalizeTurn(validHumanMove); 
    }

    private boolean isHintAction(Region target) { // Condition for valid hint 
        return rightDrag &&
               draggedRegion != null &&
               target != null &&
               !target.isFixed() &&
               target.getColorIndex() == -1;
    }

    private boolean isHumanMoveAction(Region target) {  // Checks if normal move possible 
        return draggedRegion != null &&
               target != null &&
               !target.isFixed();
    }

    private void processHint(Region target) {  // if hint move (right - click)

        int color = draggedRegion.getColorIndex();  // Gets colour from dragged region

        if (gameState.canUseColor(target.getId(), color)) { // if that color is valid for that region - dot.
            target.togglePossible(color);
        } else {                                           // else , message pop - up
            JOptionPane.showMessageDialog(
                    this,
                    "Hint not allowed!\nAdjacent region already has this colour.",
                    "Invalid Hint",
                    JOptionPane.INFORMATION_MESSAGE 
            ); 
        }
    }

    private boolean processHumanMove(Region target) {  // if normal move (left click)

        int color = draggedRegion.getColorIndex(); // get colour from dragged region

        if (target.getColorIndex() != -1) { // if already coloured 

            showInvalidMessage(
                    "This region is already coloured.\nYou cannot change it."
            );

            penalizeComputer(
                    "Tried to colour an already coloured region"
            );

            return false;
        }

        if (!gameState.canUseColor(target.getId(), color)) { // If neighbor already has same color

            showInvalidMessage(
                    "Invalid move!\nAdjacent regions cannot have the same colour."
            );

            penalizeComputer(
                    "Adjacent regions with same colour"
            );

            return false;
        }
        
        
        int id = target.getId();
        int prev = target.getColorIndex();

        gameState.setColor(id, color);
        game.addPlayerScore();
        game.recordMove(id, prev, color, false); 

        return true; // return true for valid Move
    }
    
    private void finalizeTurn(boolean validHumanMove) {

        draggedRegion = null;  // Resets the dragged region into NULL
        repaint();  // Updates the board
        game.updateStatus();  // Updates Score
        
        if (!validHumanMove) return; // If human move was invalid , stop 

        if (gameState.countColoredRegions() == gameState.getRegionCount()) { // checks if game over or not 
            game.checkGameOver(); 
            return;
        }

        SwingUtilities.invokeLater(game::computerMove); // if valid move and game not over - computer move
    }
    
    private void showInvalidMessage(String msg) { 
        JOptionPane.showMessageDialog(
                this,
                msg,
                "Invalid Move",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void penalizeComputer(String reason) {
        game.addComputerScore();
        game.recordViolation(reason, "Computer", +1);
    }

    
    
    // linear search -------------

    private Region findRegion(Point p) {  
        for (Region r : gameState.getAllRegions()) {  // Goes through every region in the board
            
        	Polygon poly = r.getPolygon(); // stores vertex coordinates
            if (poly != null && poly.contains(p)) {   //checks for mouse pointer in that region
                return r; // If true , returns that region  
            }
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {  // for restart or new game
        super.paintComponent(g);   // clears old screen
        Graphics2D g2 = (Graphics2D) g; // Cast to Graphics2D to use advanced drawing features like stroke control
                
        for (Region r : gameState.getAllRegions()) {
        	
        	// Fill region 
            g2.setColor(
                r.getColorIndex() == -1
                    ? new Color(220, 220, 220)
                    : MapPuzzleGame.COLORS[r.getColorIndex()]
            );
            
            Polygon p = r.getPolygon();
            if (p == null) continue; // skip invalid polygons

            g2.fill(p);  // fill selected colour to polygon

            g2.setColor(Color.BLACK); // border
            g2.setStroke(new BasicStroke(r.isFixed() ? 3 : 1));  // If region is fixed , thickness = 3 else thickness = 1
            g2.draw(p);

            if (game.showNumbers()) { // shows region numbers . (center)
                Rectangle b = p.getBounds();
                g2.drawString("" + r.getId(), b.x + b.width / 2, b.y + b.height / 2);
            }

            Rectangle b = p.getBounds(); // identification of polygons even if numbers are not shown
            // Calculates the center of the polygon 
            int cx = b.x + b.width / 2;  
            int cy = b.y + b.height / 2;

            for (int i = 0; i < MapPuzzleGame.NUM_COLORS; i++) { 
                if (r.isPossible(i)) { // If colour is marked possible
                	//mark a dot 
                	g2.setColor(Color.BLACK); 
                    g2.fillOval(cx + i * 8, cy, 6, 6); 
                }
            }
        }
    }
}
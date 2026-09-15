package daa3;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

public class MapPuzzleGame extends JFrame {
    private Stack<Move> undoStack = new Stack<>();
    private Stack<Move> redoStack = new Stack<>();
    private java.util.List<Violation> violations = new ArrayList<>();
    private boolean isResetting = false;
    private int playerUndoLeft = 5;
    private int computerUndoLeft = 5;
    
    // Performance tracking variables
    private int humanMoves = 0;
    private int computerMoves = 0;
    private int solveAttempts = 0;
    private int backtrackSteps = 0;
    private int undoOps = 0;
    private int redoOps = 0;
    
    // Move time tracking
    private long gameStartTime = 0;
    private long gameEndTime = 0;
    private long totalHumanMoveTime = 0;
    private long totalComputerMoveTime = 0;
    private long totalSolveTime = 0;
    private int humanMoveCount = 0;
    private int computerMoveCount = 0;
    private int solveCount = 0;
    
    private boolean isSolving = false;  
    
    private long humanMoveStartTime = 0;
    private long computerMoveStartTime = 0;
    private long solveStartTime = 0;
    
    public static final int WIDTH = 1200;
    public static final int HEIGHT = 700;
    public static final int PERFORMANCE_PANEL_WIDTH = 380;
    
    public static final Color[] COLORS = {
                    new Color(220, 53, 69),    // Red
                    new Color(0, 123, 255),    // Blue
                    new Color(40, 167, 69),    // Green
                    new Color(255, 193, 7),    // Yellow
                    new Color(111, 66, 193),   // Purple
                    new Color(255, 133, 27),   // Orange
                    new Color(23, 162, 184),   // Teal
                    new Color(108, 117, 125),  // Gray
                    new Color(255, 99, 132),   // Pink
                    new Color(32, 201, 151)    // Mint
                };
    public static final Color PLAYER_BG = new Color(245,245,245);
    public static final Color COMPUTER_BG = new Color(255, 200, 120);
    public static int ROWS;
    public static  int COLS;
    public static int NUM_COLORS;

    private GameState gameState;
    private MapPanel mapPanel;
    private JLabel statusLabel;
    private JLabel performanceLabel;

    private boolean showRegionNumbers = false;
    private boolean isComputerTurn = false;
    private int playerScore = 0;
    private int computerScore = 0;
    
    private JButton undoBtn;
    private JButton redoBtn;

    private Font performanceTitleFont = new Font("Monospaced", Font.BOLD, 20);
    private Font performanceTextFont = new Font("Monospaced", Font.PLAIN, 16);
    private Font buttonFont = new Font("Arial", Font.PLAIN, 14);
    private Font statusFont = new Font("Arial", Font.BOLD, 16);
	private int avgHumanMoveTime;
    
    public MapPuzzleGame() {
        setTitle("Map Puzzle – Four Color Theorem");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ROWS = askValue("Enter number of rows (4–10):", 4, 10);
        COLS = askValue("Enter number of columns (4–12):", 4, 12);
        NUM_COLORS = askValue("Enter number of colors (3–10):", 3, 10);
        
        gameState = new GameState();
        gameState.setGame(this);
        
        mapPanel = new MapPanel(this, gameState);

        setupUI();
        initializeGame();

        setVisible(true);
    }
    
    void addPlayerScore() { 
        playerScore++;
        humanMoves++;
        
        
        if (humanMoveStartTime > 0) {
        	long moveTime = System.currentTimeMillis() - humanMoveStartTime;
        	totalHumanMoveTime += moveTime;
        	humanMoveCount++;
        	humanMoveStartTime = 0;
        }
        
        updatePerformanceDisplay();
    }
    
    void addComputerScore() { 
        computerScore++;
        
        if (computerMoveStartTime > 0) {
        	long moveTime = System.currentTimeMillis() - computerMoveStartTime;
        	totalComputerMoveTime += moveTime;
        	computerMoveCount++;
        	computerMoveStartTime = 0;
        }
        
        updatePerformanceDisplay();
    }
    
    boolean isComputerTurn() { return isComputerTurn; }
    void setComputerTurn(boolean v) { isComputerTurn = v; }
    boolean showNumbers() { return showRegionNumbers; }
    
    void incrementBacktrackSteps() { 
        if (isSolving) {
            backtrackSteps++; 
            updatePerformanceDisplay();
        }
    }
    
       void incrementUndoOps() { undoOps++; updatePerformanceDisplay(); }
       void incrementRedoOps() { redoOps++; updatePerformanceDisplay(); }
    
    void startHumanMoveTimer() {
        humanMoveStartTime = System.currentTimeMillis();
    }
    
    void startComputerMoveTimer() {
        computerMoveStartTime = System.currentTimeMillis();
    }
    
    void startSolveTimer() {
        solveStartTime = System.currentTimeMillis();
        isSolving = true;
    }
    
    void endSolveTimer() {
        if (solveStartTime > 0) {
            long solveTime = System.currentTimeMillis() - solveStartTime;
            totalSolveTime += solveTime;
            solveCount++;
            solveStartTime = 0;
        }
        isSolving = false;
    }
    
    private void updatePerformanceDisplay() {
        if (performanceLabel != null) {
            int avgHumanMoveTime = humanMoveCount > 0 ? (int)(totalHumanMoveTime / humanMoveCount) : 0;
            int avgComputerMoveTime = computerMoveCount > 0 ? (int)(totalComputerMoveTime / computerMoveCount) : 0;
            int avgSolveTime = solveCount > 0 ? (int)(totalSolveTime / solveCount) : 0;
            int progress = (int)((gameState.countColoredRegions() * 100.0) / gameState.getRegionCount());
            // Color-code backtrack steps based on number of colors
            String backtrackColor;
            if (NUM_COLORS <= 4) {
                backtrackColor = "#C0392B"; // Red for few colors (more backtracking)
            } else if (NUM_COLORS <= 6) {
                backtrackColor = "#E67E22"; // Orange for medium
            } else if (NUM_COLORS <= 8) {
                backtrackColor = "#F39C12"; // Yellow-orange for medium-high
            } else {
                backtrackColor = "#27AE60"; // Green for many colors (less backtracking)
            }
            
            String performanceText = String.format(
                "<html><div style='font-family: Consolas, monospace; font-size: 15pt;'>" +
                
                "<span style='color: #2980B9; font-weight: bold;'> Current Mode:</span> <span style='color: #27AE60; background: #ECF0F1; padding: 2px 8px;'>%s</span><br>" +
                "──────────────────────────────────<br>" +
                
                "<span style='color: #E67E22;'>HUMAN MOVES:</span>          <span style='color: #000000; font-weight: bold;'>%d</span><br>" +
                "<span style='color: #8E44AD;'>COMPUTER MOVES:</span>       <span style='color: #000000; font-weight: bold;'>%d</span><br>" +
                "──────────────────────────────────<br>" +
                
                "<span style='color: #C0392B;'>SOLVE ATTEMPTS:</span>       <span style='color: #000000; font-weight: bold;'>%d</span><br>" +
                "<span style='color: %s; font-weight: bold;'>↩ BACKTRACK STEPS:</span>       <span style='color: #000000; font-weight: bold;'>%d</span><br>" +
                "──────────────────────────────────<br>" +
                
                "<span style='color: #7F8C8D;'>UNDO OPERATIONS:</span>       <span style='color: #000000; font-weight: bold;'>%d</span><br>" +
                "<span style='color: #7F8C8D;'>REDO OPERATIONS:</span>       <span style='color: #000000; font-weight: bold;'>%d</span><br>" +
                "──────────────────────────────────<br>" +
                
                "<span style='color: #2980B9;'>AVG HUMAN MOVE TIME:</span>   <span style='color: #000000; font-weight: bold;'>%d ms</span><br>" +
                "<span style='color: #2980B9;'>AVG COMPUTER MOVE TIME:</span> <span style='color: #000000; font-weight: bold;'>%d ms</span><br>" +
                "<span style='color: #2980B9;'>AVG SOLVE TIME:</span>        <span style='color: #000000; font-weight: bold;'>%d ms</span><br>" +
                "──────────────────────────────────<br>" +
                
                "<span style='color: #8E44AD;'>GAME PROGRESS:</span>        <span style='color: #000000; font-weight: bold;'>%d%%</span><br>" +
                "<span style='background: #ECF0F1; display: block; width: %dpx; height: 15px; border-radius: 5px; margin-top: 5px;'>" +
                "<span style='background: #27AE60; display: block; width: %dpx; height: 15px; border-radius: 5px;'></span></span><br>"+
                "</div></html>",
                isComputerTurn ? "COMPUTER TURN" : "HUMAN TURN",
                humanMoves, computerMoves,
                solveAttempts,
                backtrackColor, backtrackSteps,
                undoOps, redoOps,
                avgHumanMoveTime, avgComputerMoveTime, avgSolveTime,
                progress,
                300, (progress * 300) / 100
            );
            performanceLabel.setText(performanceText);
        }
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        mapPanel.setPreferredSize(new Dimension(WIDTH - PERFORMANCE_PANEL_WIDTH, HEIGHT - 100));
        mainPanel.add(mapPanel, BorderLayout.CENTER);
        
        JPanel performancePanel = createPerformancePanel();
        mainPanel.add(performancePanel, BorderLayout.EAST);
        
        add(mainPanel, BorderLayout.CENTER);

        JPanel top = createTopPanel();
        add(top, BorderLayout.NORTH);

        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
        
        updatePerformanceDisplay();
    }
    
    private JPanel createPerformancePanel() {
        JPanel performancePanel = new JPanel(new BorderLayout());
        performancePanel.setPreferredSize(new Dimension(PERFORMANCE_PANEL_WIDTH, HEIGHT));
        performancePanel.setBackground(new Color(248, 249, 250));
        
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 73, 94), 3),
            " Performance Stats ",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            performanceTitleFont,
            new Color(41, 128, 185)
        );
        performancePanel.setBorder(titledBorder);
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(null);
        scrollPane.setBackground(new Color(248, 249, 250));
        
        performanceLabel = new JLabel();
        performanceLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        scrollPane.setViewportView(performanceLabel);
        
        performancePanel.add(scrollPane, BorderLayout.CENTER);
           
        return performancePanel;
    }
    
    private JPanel createTopPanel() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        top.setBackground(new Color(240, 240, 245));
        top.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton newGame = createStyledButton("New Game");
        JButton solve = createStyledButton("Solve");
        JButton clear = createStyledButton("Clear");
        undoBtn = createStyledButton("Undo");
        redoBtn = createStyledButton("Redo");

        top.add(undoBtn);
        top.add(redoBtn);

        JToggleButton nums = new JToggleButton("Region Numbers");
        nums.setFont(new Font("Arial", Font.PLAIN, 14));
        nums.setBackground(new Color(220, 220, 230));
        nums.setFocusPainted(false);

        newGame.addActionListener(e -> initializeGame());
        
        // MODIFIED SOLVE BUTTON - NO POPUP
        solve.addActionListener(e -> {
            solveAttempts++;
            
            startSolveTimer();
            
            int beforeBacktrack = backtrackSteps;
            boolean solved = gameState.solve();
            
            endSolveTimer();
            
            int stepsTaken = backtrackSteps - beforeBacktrack;
            // Just update the display - no popup dialog
            mapPanel.repaint();
            updatePerformanceDisplay();
        });
        
        clear.addActionListener(e -> { 
            gameState.clearAllNonFixed(); 
            resetUndoRedoState();
            resetPerformanceStats();
            isComputerTurn = false;
            updateStatus();            
            mapPanel.repaint();
            updateUndoRedoButtons();
            violations.clear();
            updatePerformanceDisplay();
        });
        
        undoBtn.addActionListener(e -> undoMove());
        redoBtn.addActionListener(e -> redoMove());
        nums.addActionListener(e -> {
            showRegionNumbers = nums.isSelected();
            mapPanel.repaint();
        });

        top.add(newGame);
        top.add(solve);
        top.add(clear);
        top.add(nums);
        
        return top;
    }
    
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(52, 73, 94));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        statusLabel = new JLabel();
        statusLabel.setFont(statusFont);
        statusLabel.setForeground(Color.WHITE);
        bottomPanel.add(statusLabel, BorderLayout.WEST);
        
        
        return bottomPanel;
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(new Color(52, 73, 94));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(41, 128, 185), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(new Color(52, 73, 94));
            }
        });
        
        return button;
    }
    
    private void resetPerformanceStats() {
        humanMoves = 0;
        computerMoves = 0;
        solveAttempts = 0;
        backtrackSteps = 0;
        undoOps = 0;
        redoOps = 0;
        
        totalHumanMoveTime = 0;
        totalComputerMoveTime = 0;
        totalSolveTime = 0;
        humanMoveCount = 0;
        computerMoveCount = 0;
        solveCount = 0;
        
        humanMoveStartTime = 0;
        computerMoveStartTime = 0;
        solveStartTime = 0;
        
        updatePerformanceDisplay();
    }
    
    private int askValue(String message, int min, int max) {
        while (true) {
            String input = JOptionPane.showInputDialog(this, message);
            if (input == null) System.exit(0);

            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) return value;
            } catch (NumberFormatException ignored) {}

            JOptionPane.showMessageDialog(this,
                    "Enter a number between " + min + " and " + max);
        }
    }
    
    private void assignDifferentShapes() {
        java.util.List<Region> regions = gameState.getAllRegions();

        int rows = MapPuzzleGame.ROWS;
        int cols = MapPuzzleGame.COLS;

        int availableWidth = WIDTH - PERFORMANCE_PANEL_WIDTH - 50;
        int cellW = availableWidth / cols;
        int cellH = (HEIGHT - 150) / rows;

        Point[][] pts = new Point[rows + 1][cols + 1];

        for (int r = 0; r <= rows; r++) {
            for (int c = 0; c <= cols; c++) {

                int x = c * cellW + 25;
                int y = r * cellH + 75;

                if (r > 0 && r < rows) {
                    y += (Math.random() - 0.5) * cellH * 0.2;
                }
                if (c > 0 && c < cols) {
                    x += (Math.random() - 0.5) * cellW * 0.2;
                }

                pts[r][c] = new Point(x, y);
            }
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {

                int id = r * cols + c;

                Polygon p = new Polygon();
                p.addPoint(pts[r][c].x, pts[r][c].y);
                p.addPoint(pts[r][c + 1].x, pts[r][c + 1].y);
                p.addPoint(pts[r + 1][c + 1].x, pts[r + 1][c + 1].y);
                p.addPoint(pts[r + 1][c].x, pts[r + 1][c].y);

                regions.get(id).setPolygon(p);
            }
        }
    }

    void initializeGame() {
        gameState.reset();
        assignDifferentShapes();
        
        resetUndoRedoState();
        resetPerformanceStats();
        
        playerScore = computerScore = 0;
        isComputerTurn = false;
        isSolving = false;

        updateStatus();
        mapPanel.repaint();
        
        updateUndoRedoButtons(); 
        violations.clear();
        
        startHumanMoveTimer(); 
        
        gameStartTime = System.currentTimeMillis();
        gameEndTime = 0;
    }

    void computerMove() {
        isComputerTurn = true;
        mapPanel.setBackground(COMPUTER_BG);
        mapPanel.repaint();
        
        startComputerMoveTimer();
        
        updatePerformanceDisplay();

        javax.swing.Timer timer = new javax.swing.Timer(500, e -> {
            ArrayList<Region> candidates = new ArrayList<>();
            for (Region r : gameState.getAllRegions()) {
                if (r.getColorIndex() == -1 && !r.isFixed()) {
                    candidates.add(r);
                }
            }
            
            if (candidates.isEmpty()) {
                isComputerTurn = false;
                mapPanel.setBackground(PLAYER_BG);
                updateStatus();
                mapPanel.repaint();
                updatePerformanceDisplay();
                checkGameOver();
                ((javax.swing.Timer) e.getSource()).stop();
                return;
            }

            Region bestRegion = findBestRegionForComputer(candidates);

            if (bestRegion != null) {
                ArrayList<Integer> colorOrder = new ArrayList<>();
                for (int i = 0; i < NUM_COLORS; i++) {
                    colorOrder.add(i); 
                }
                Collections.shuffle(colorOrder);
                
                for (int c : colorOrder) {
                    int id = bestRegion.getId();
                    int prev = gameState.getRegion(id).getColorIndex();
                    
                    if (gameState.canUseColor(id, c)) {
                        gameState.setColor(id, c);
                        addComputerScore();
                        
                        computerMoves++;
                        undoStack.push(new Move(id, prev, c, true));
                        redoStack.clear();

                        break;
                    }
                }
            }

            isComputerTurn = false;
            mapPanel.setBackground(PLAYER_BG);
            
            startHumanMoveTimer();
            
            updateStatus();
            mapPanel.repaint();
            updatePerformanceDisplay();
            checkGameOver();

            ((javax.swing.Timer) e.getSource()).stop();
        });
        
        timer.setRepeats(false);
        timer.start();
    }
    
    private Region findBestRegionForComputer(ArrayList<Region> regions) {
        if (regions == null || regions.isEmpty()) return null;
        return findBestRegionDC(regions, 0, regions.size() - 1);
    }

    private Region findBestRegionDC(ArrayList<Region> regions, int left, int right) {

        // Base case
        if (left == right) {
            return regions.get(left);
        }

        int mid = (left + right) / 2;

        // Divide
        Region bestLeft = findBestRegionDC(regions, left, mid);
        Region bestRight = findBestRegionDC(regions, mid + 1, right);

        // Conquer (combine)
        int leftScore = countColoredNeighbors(bestLeft);
        int rightScore = countColoredNeighbors(bestRight);

        return (leftScore >= rightScore) ? bestLeft : bestRight;
    }
    
    private int countColoredNeighbors(Region r) {
        int count = 0;
        for (int n : gameState.getNeighbors(r.getId())) {
            if (gameState.getRegion(n).getColorIndex() != -1) {
                count++;
            }
        }
        return count;
    }

    void updateStatus() {
        statusLabel.setText(
            "Human: " + playerScore +
            "  |  Computer: " + computerScore +
            "  |  Colored: " + gameState.countColoredRegions() +
            "/" + gameState.getRegionCount() +
            "  |  Colors: " + NUM_COLORS
        );
        statusLabel.revalidate();
        statusLabel.repaint();
    }
    
    private void undoMove() {
        if (undoStack.isEmpty()) return;
        if (!isComputerTurn && playerUndoLeft == 0) return;
        if (isComputerTurn && computerUndoLeft == 0) return;

        Move m = undoStack.pop();
        gameState.getRegion(m.regionId).setColorIndex(m.prevColor);
        
        undoOps++;
        
        if (isComputerTurn) {
            computerUndoLeft--;
            if (computerScore > 0) {
                computerScore--;
                computerMoves--;
            }
        } else {
            playerUndoLeft--;
            if (playerScore > 0) {
                playerScore--;
                humanMoves--;
            }
            violations.add(new Violation("Undo used", "Human", -1));
        }
        redoStack.push(m);
        isComputerTurn = false;
        
        updateStatus();
        mapPanel.repaint();
        updateUndoRedoButtons();
        updatePerformanceDisplay();
    }

    private void redoMove() {
        if (redoStack.isEmpty()) return;

        Move m = redoStack.pop();

        gameState.getRegion(m.regionId).setColorIndex(m.newColor);
        isComputerTurn = !m.wasComputerTurn;

        redoOps++;
        
        undoStack.push(m);
        updateStatus();
        mapPanel.repaint();
        updateUndoRedoButtons();
        updatePerformanceDisplay();
    }
    
    private void resetUndoRedoState() {
        undoStack.clear();
        redoStack.clear();

        playerUndoLeft = 5;
        computerUndoLeft = 5;

        updateUndoRedoButtons();
        isResetting = false;
    }
    
    private void updateUndoRedoButtons() {
        undoBtn.setEnabled(playerUndoLeft > 0 && !undoStack.isEmpty());
        redoBtn.setEnabled(!redoStack.isEmpty());
    }
    
    public void recordMove(int regionId, int prevColor, int newColor, boolean wasComputer) {
        if(isResetting) return;
        
        undoStack.push(new Move(regionId, prevColor, newColor, wasComputer));
        redoStack.clear();
        
        
        updateUndoRedoButtons(); 
    }
    
    private void insertionSortByFrequency(java.util.List<ViolationSummary> list) {
        for (int i = 1; i < list.size(); i++) {
            ViolationSummary key = list.get(i);
            int j = i - 1;

            while (j >= 0 && list.get(j).count < key.count) {
                list.set(j + 1, list.get(j));
                j--;
            }

            list.set(j + 1, key);
        }
    }

    public void recordViolation(String reason, String who, int points) {
        violations.add(new Violation(reason, who, points));
    }
    
    void checkGameOver() {
        if (gameState.countColoredRegions() == gameState.getRegionCount()) {
        	
        	gameEndTime = System.currentTimeMillis();
        	long manualSolveTime = gameEndTime - gameStartTime;
        	
        	long seconds = manualSolveTime / 1000;
        	long minutes = seconds / 60;
        	seconds = seconds % 60;
        	
            String winner;
            String winnerEmoji;
            if (playerScore > computerScore) {
                winner = "HUMAN WINS!";
                winnerEmoji = "🏆";
            } else if (computerScore > playerScore) {
                winner = "COMPUTER WINS!";
                winnerEmoji = "🤖";
            } else {
                winner = "IT'S A DRAW!";
                winnerEmoji = "🤝";
            }

            java.util.Map<String, Integer> freqMap = new java.util.HashMap<>();
            for (Violation v : violations) {
                freqMap.put(v.reason, freqMap.getOrDefault(v.reason, 0) + 1);
            }

            java.util.List<ViolationSummary> summaryList = new java.util.ArrayList<>();
            for (var entry : freqMap.entrySet()) {
                summaryList.add(new ViolationSummary(entry.getKey(), entry.getValue()));
            }

            insertionSortByFrequency(summaryList);

            StringBuilder violationReport = new StringBuilder();
            violationReport.append("\n══════════════════════════════════\n");
            violationReport.append("    RULE VIOLATIONS SUMMARY    \n");
            violationReport.append("══════════════════════════════════\n");

            if (summaryList.isEmpty()) {
                violationReport.append(" No violations! Perfect play!\n");
            } else {
                for (ViolationSummary vs : summaryList) {
                    violationReport.append("  • ").append(vs.reason).append(": ").append(vs.count);
                    violationReport.append(vs.count > 1 ? " times\n" : " time\n");
                }
            }
            violationReport.append("══════════════════════════════════");

            int avgHumanMoveTime = humanMoveCount > 0 ? (int)(totalHumanMoveTime / humanMoveCount) : 0;
            int avgComputerMoveTime = computerMoveCount > 0 ? (int)(totalComputerMoveTime / computerMoveCount) : 0;
            int avgSolveTime = solveCount > 0 ? (int)(totalSolveTime / solveCount) : 0;
            int progress = (gameState.countColoredRegions() * 100) / gameState.getRegionCount();
            
            JOptionPane.showMessageDialog(
                    this,
                    "GAME OVER!\n\n" +
                    "FINAL SCORES:\n" +
                    "Human : " + playerScore + "\n" +
                    "Computer : " + computerScore + "\n\n" +
                    "Manual Solve Time : " + minutes + " min " + seconds + " sec\n\n" +
                    winner + "\n\n" +
                    violationReport.toString(),
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE
            );  
        }
    }

    // Inner class for Move
    static class Move {
        int regionId;
        int prevColor;
        int newColor;
        boolean wasComputerTurn;

        Move(int regionId, int prevColor, int newColor, boolean wasComputerTurn) {
            this.regionId = regionId;
            this.prevColor = prevColor;
            this.newColor = newColor;
            this.wasComputerTurn = wasComputerTurn;
        }
    }
    
    // Inner class for ViolationSummary
    static class ViolationSummary {
        String reason;
        int count;

        ViolationSummary(String reason, int count) {
            this.reason = reason;
            this.count = count;
        }
    }
    
    // Inner class for Violation
    static class Violation {
        String reason;
        String who;
        int points;
        
        Violation(String reason, String who, int points) {
            this.reason = reason;
            this.who = who;
            this.points = points;
        }
    }

    // MAIN METHOD - Entry point of the application
    public static void main(String[] args) {
        // Launch the game on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MapPuzzleGame();
            }
        });
    }
}
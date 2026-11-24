import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class Board extends JPanel implements ActionListener {

    private Timer timer;
    private Map m;
    private Player p;
    private Skeleton skeleton;

    private boolean win = false;
    private boolean gameOver = false;
    private boolean upPressed, downPressed, leftPressed, rightPressed, shiftPressed;
    private Random rand = new Random();

    // Starting predefined score of 300
    private int score = 300;
    private List<Point> mathTraps = new ArrayList<>();
    private static final int TOTAL_TRAPS = 10;

    // Layout & Padding Dimensions
    private static final int OUTER_PADDING = 30;
    private static final int SCORE_BOX_HEIGHT = 75;
    private static final int SECTION_GAP = 20;
    private static final int MAZE_INNER_PADDING = 12;

    private int mazeWidthPx;
    private int mazeHeightPx;
    private int mazeStartX;
    private int mazeStartY;

    public Board() {
        m = new Map();
        p = new Player();
        skeleton = new Skeleton();

        mazeWidthPx = m.getWidth() * 32;
        mazeHeightPx = m.getHeight() * 32;

        mazeStartX = OUTER_PADDING + MAZE_INNER_PADDING;
        mazeStartY = OUTER_PADDING + SCORE_BOX_HEIGHT + SECTION_GAP + MAZE_INNER_PADDING;

        int totalWidth = mazeWidthPx + (OUTER_PADDING + MAZE_INNER_PADDING) * 2;
        int totalHeight = mazeStartY + mazeHeightPx + MAZE_INNER_PADDING + OUTER_PADDING;

        setPreferredSize(new Dimension(totalWidth, totalHeight));
        setBackground(new Color(245, 245, 247));
        addKeyListener(new Al());
        setFocusable(true);

        spawnRandomMathTiles();

        timer = new Timer(16, this);
        timer.start();
    }

    private void spawnRandomMathTiles() {
        List<Point> candidates = new ArrayList<>();
        for (int y = 0; y < m.getHeight(); y++) {
            for (int x = 0; x < m.getWidth(); x++) {
                if (m.getMap(x, y).equals("g") && !(x == 15 && y == 0)) {
                    candidates.add(new Point(x, y));
                }
            }
        }
        Collections.shuffle(candidates, rand);
        int count = Math.min(TOTAL_TRAPS, candidates.size());
        for (int i = 0; i < count; i++) {
            mathTraps.add(candidates.get(i));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!win && !gameOver) {
            handleContinuousMovement();
            p.update();
            skeleton.update();

            // 1. Check Random Addition Tile Trap
            if (!p.isMoving()) {
                Point currentTile = new Point(p.getTix(), p.getTiy());
                if (mathTraps.contains(currentTile)) {
                    triggerAdditionTileQuestion(currentTile);
                }
            }

            // 2. Check Collision with Center Skeleton
            if (!skeleton.isDefeated() &&
                Math.abs(p.getPixelX() - skeleton.getPixelX()) < 18 &&
                Math.abs(p.getPixelY() - skeleton.getPixelY()) < 18) {
                askSkeletonQuestion();
            }

            // 3. Win Condition
            if (!p.isMoving() && skeleton.isDefeated() && m.getMap(p.getTix(), p.getTiy()).equals("f")) {
                score += 100;
                win = true;
            }
        }
        repaint();
    }

    private void handleContinuousMovement() {
        if (p.isMoving()) return;

        int dx = 0, dy = 0;
        if (upPressed) dy = -1;
        else if (downPressed) dy = 1;
        else if (leftPressed) dx = -1;
        else if (rightPressed) dx = 1;

        if (dx != 0 || dy != 0) {
            int nextX = p.getTix() + dx;
            int nextY = p.getTiy() + dy;
            if (!m.getMap(nextX, nextY).equals("w")) {
                p.move(dx, dy, shiftPressed);
            }
        }
    }

    private void triggerAdditionTileQuestion(Point tile) {
        timer.stop();
        upPressed = downPressed = leftPressed = rightPressed = shiftPressed = false;

        int num1 = rand.nextInt(25) + 1;
        int num2 = rand.nextInt(25) + 1;
        int correctAnswer = num1 + num2;

        String response = JOptionPane.showInputDialog(
            this,
            "Math Tile Challenge!\nWhat is " + num1 + " + " + num2 + "?",
            "Addition Question",
            JOptionPane.QUESTION_MESSAGE
        );

        boolean solved = false;
        if (response != null) {
            try {
                if (Integer.parseInt(response.trim()) == correctAnswer) {
                    solved = true;
                }
            } catch (NumberFormatException ignored) {}
        }

        if (solved) {
            score += 10;
            mathTraps.remove(tile);
            JOptionPane.showMessageDialog(this, "Correct! +10 Points!");
            timer.start();
        } else {
            score = Math.max(0, score - 50);
            JOptionPane.showMessageDialog(this, "Wrong! The answer was " + correctAnswer + ". (-50 Points)");
            if (score <= 0) {
                gameOver = true;
            } else {
                p.pushBack();
                timer.start();
            }
        }
    }

    private void askSkeletonQuestion() {
        timer.stop();
        upPressed = downPressed = leftPressed = rightPressed = shiftPressed = false;

        int num1 = rand.nextInt(30) + 10;
        int num2 = rand.nextInt(30) + 10;
        int correctAnswer = num1 + num2;

        String response = JOptionPane.showInputDialog(
            this,
            "The Skeleton King guards the Crown!\nSolve to pass: What is " + num1 + " + " + num2 + "?",
            "Guardian Challenge",
            JOptionPane.QUESTION_MESSAGE
        );

        boolean solved = false;
        if (response != null) {
            try {
                if (Integer.parseInt(response.trim()) == correctAnswer) {
                    solved = true;
                }
            } catch (NumberFormatException ignored) {}
        }

        if (solved) {
            score += 50;
            JOptionPane.showMessageDialog(this, "Correct! +50 Points! The Skeleton steps aside.");
            skeleton.setDefeated(true);
            timer.start();
        } else {
            score = Math.max(0, score - 50);
            JOptionPane.showMessageDialog(this, "Wrong! The answer was " + correctAnswer + ". (-50 Points)");
            if (score <= 0) {
                gameOver = true;
            } else {
                p.pushBack();
                timer.start();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int containerWidth = mazeWidthPx + (MAZE_INNER_PADDING * 2);
        int mazeContainerHeight = mazeHeightPx + (MAZE_INNER_PADDING * 2);

        // 1. Draw Outer Border Frame
        g2d.setColor(new Color(40, 40, 45));
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawRoundRect(12, 12, getWidth() - 24, getHeight() - 24, 30, 30);

        // 2. Draw Top Rounded Score Panel
        g2d.setColor(new Color(216, 216, 220));
        g2d.fillRoundRect(OUTER_PADDING, OUTER_PADDING, containerWidth, SCORE_BOX_HEIGHT, 28, 28);

        g2d.setColor(score <= 50 ? new Color(190, 30, 30) : new Color(35, 35, 40));
        g2d.setFont(new Font("Monospaced", Font.BOLD, 24));
        String scoreText = "Score: " + score;
        FontMetrics fm = g2d.getFontMetrics();
        int textX = OUTER_PADDING + (containerWidth - fm.stringWidth(scoreText)) / 2;
        int textY = OUTER_PADDING + ((SCORE_BOX_HEIGHT - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(scoreText, textX, textY);

        // 3. Draw Bottom Rounded Maze Container
        int mazeBoxY = OUTER_PADDING + SCORE_BOX_HEIGHT + SECTION_GAP;
        g2d.setColor(new Color(216, 216, 220));
        g2d.fillRoundRect(OUTER_PADDING, mazeBoxY, containerWidth, mazeContainerHeight, 28, 28);

        Shape oldClip = g2d.getClip();
        g2d.setClip(new RoundRectangle2D.Float(mazeStartX, mazeStartY, mazeWidthPx, mazeHeightPx, 16, 16));
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        if (!win && !gameOver) {
            // Draw Map Tiles
            for (int y = 0; y < m.getHeight(); y++) {
                for (int x = 0; x < m.getWidth(); x++) {
                    int drawX = mazeStartX + (x * 32);
                    int drawY = mazeStartY + (y * 32);
                    String tile = m.getMap(x, y);

                    if (tile.equals("w")) {
                        g2d.drawImage(m.getWall(), drawX, drawY, null);
                    } else {
                        g2d.drawImage(m.getGrass(), drawX, drawY, null);
                        if (tile.equals("f") && x == 15 && y == 9) {
                            g2d.drawImage(m.getFinish(), drawX, drawY, null);
                        }
                    }
                }
            }

            // Draw Random Addition Tile Markers
            for (Point trap : mathTraps) {
                int tx = mazeStartX + (trap.x * 32) + 10;
                int ty = mazeStartY + (trap.y * 32) + 10;
                g2d.setColor(new Color(255, 220, 50, 180));
                g2d.fillOval(tx, ty, 12, 12);
                g2d.setColor(Color.WHITE);
                g2d.drawOval(tx, ty, 12, 12);
            }

            // Draw Skeleton
            if (!skeleton.isDefeated() && skeleton.getFrame() != null) {
                int hoverOffset = (int) (Math.sin(System.currentTimeMillis() / 150.0) * 3);
                g2d.drawImage(skeleton.getFrame(), mazeStartX + skeleton.getPixelX(), mazeStartY + skeleton.getPixelY() + hoverOffset, null);
            }

            // Draw Player
            if (p.getPlayerFrame() != null) {
                g2d.drawImage(p.getPlayerFrame(), mazeStartX + p.getPixelX(), mazeStartY + p.getPixelY(), null);
            }
        } else if (gameOver) {
            g2d.setColor(new Color(30, 30, 35));
            g2d.fillRect(mazeStartX, mazeStartY, mazeWidthPx, mazeHeightPx);
            g2d.setColor(new Color(220, 60, 60));
            g2d.setFont(new Font("SansSerif", Font.BOLD, 38));
            String overMsg = "Game Over! You ran out of points.";
            int overX = mazeStartX + (mazeWidthPx - g2d.getFontMetrics().stringWidth(overMsg)) / 2;
            g2d.drawString(overMsg, overX, mazeStartY + (mazeHeightPx / 2));
        } else {
            g2d.setColor(new Color(30, 30, 35));
            g2d.fillRect(mazeStartX, mazeStartY, mazeWidthPx, mazeHeightPx);
            g2d.setColor(Color.ORANGE);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 38));
            String winMsg = "Winner! Final Score: " + score;
            int winX = mazeStartX + (mazeWidthPx - g2d.getFontMetrics().stringWidth(winMsg)) / 2;
            g2d.drawString(winMsg, winX, mazeStartY + (mazeHeightPx / 2));
        }

        g2d.setClip(oldClip);
    }

    public class Al extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) upPressed = true;
            else if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) downPressed = true;
            else if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) leftPressed = true;
            else if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) rightPressed = true;
            else if (key == KeyEvent.VK_SHIFT) shiftPressed = true;
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) upPressed = false;
            else if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) downPressed = false;
            else if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) leftPressed = false;
            else if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) rightPressed = false;
            else if (key == KeyEvent.VK_SHIFT) shiftPressed = false;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Skeleton Math Maze");
        Board board = new Board();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(board);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
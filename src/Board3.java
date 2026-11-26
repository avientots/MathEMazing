import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class Board3 extends JPanel implements ActionListener {

    private static final int LEVEL = 3; // used by GameTheme (colors + marker style)

    private Timer timer;
    private Map3 m;
    private Player p;
    private Mummy mummy;

    private Runnable onNext, onRestart, onMenu;

    public void setCallbacks(Runnable onNext, Runnable onRestart, Runnable onMenu) {
        this.onNext = onNext;
        this.onRestart = onRestart;
        this.onMenu = onMenu;
    }

    private boolean win = false;
    private boolean gameOver = false;
    private boolean upPressed, downPressed, leftPressed, rightPressed, shiftPressed, spacePressed;
    private Random rand = new Random();

    private int score = 150;
    private List<Point> mathTraps = new ArrayList<>();
    private static final int TOTAL_TRAPS = 25;

    private static final int OUTER_PADDING = 20;
    private static final int SCORE_BOX_HEIGHT = 65;
    private static final int SECTION_GAP = 15;
    private static final int MAZE_INNER_PADDING = 12;

    private int tileSize = 32;

    private int mazeWidthPx;
    private int mazeHeightPx;
    private int mazeStartX;
    private int mazeStartY;

    public Board3() {
        m = new Map3();
        p = new Player();
        mummy = new Mummy();

        setPreferredSize(new Dimension(1200, 800));
        setBackground(new Color(28, 12, 38));
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
            mummy.update();

            if (!p.isMoving()) {
                Point currentTile = new Point(p.getTix(), p.getTiy());
                if (mathTraps.contains(currentTile)) {
                    triggerMultiplicationTileQuestion(currentTile);
                }
            }

            if (!mummy.isDefeated() &&
                Math.abs(p.getPixelX() - mummy.getPixelX()) < 18 &&
                Math.abs(p.getPixelY() - mummy.getPixelY()) < 18) {
                askMummyQuestion();
            }

            if (!p.isMoving() && mummy.isDefeated() && m.getMap(p.getTix(), p.getTiy()).equals("f")) {
                score += 100;
                win = true;
                onLevelWon();
            }
        }
        repaint();
    }

    private void handleContinuousMovement() {
        if (p.isMoving() || p.isAttacking()) return;

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

    // Level 3 = MULTIPLICATION
    
    private void triggerMultiplicationTileQuestion(Point tile) {
        timer.stop();
        clearKeys();

        int num1 = rand.nextInt(11) + 2;
        int num2 = rand.nextInt(11) + 2;
        int correctAnswer = num1 * num2;

        String response = QuestionDialog.ask(
            this,
            "Math Tile Challenge!\nWhat is " + num1 + " × " + num2 + "?",
            "Multiplication Question",
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
            QuestionDialog.message(this, "Correct! +10 Points!");
            timer.start();
        } else {
            score = Math.max(0, score - 50);
            QuestionDialog.message(
                this,
                "Wrong! The answer was " + correctAnswer + ". (-50 Points)"
            );

            if (score <= 0) {
                gameOver = true;
                onGameOver();
            } else {
                p.pushBack();
                timer.start();
            }
        }
    }

    
    private void askMummyQuestion() {
        timer.stop();
        clearKeys();

        int a = rand.nextInt(20) + 12;
        int b = rand.nextInt(20) + 12;
        int correctAnswer = a * b;

        String response = QuestionDialog.ask(
            this,
            "THE MUMMY'S FINAL CHALLENGE!\n\n"
            + "Prove your skills to claim the Crown.\n\n"
            + a + " × " + b + " = ?",
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
            QuestionDialog.message(
                this,
                "Correct! +50 Points!\nThe Mummy has been defeated!"
            );
            mummy.setDefeated(true);
            timer.start();
        } else {
            score = Math.max(0, score - 60);
            QuestionDialog.message(
                this,
                "Wrong! The correct answer was "
                + correctAnswer + ". (-60 Points)"
            );

            if (score <= 0) {
                gameOver = true;
                onGameOver();
            } else {
                p.pushBack();
                timer.start();
            }
        }
    }


    private void clearKeys() {
        upPressed = downPressed = leftPressed = rightPressed = shiftPressed = false;
    }

    private void onLevelWon() {
        timer.stop();
        clearKeys();
        repaint();
        StoryDialog.show(SwingUtilities.getWindowAncestor(this), 3);
        if (onNext != null) SwingUtilities.invokeLater(onNext);
        GameRecords.recordLevelScore(3, score);
    }

    private void onGameOver() {
        timer.stop();
        clearKeys();
        repaint();
        Object[] options = {"Try Again", "Back to Menu"};
        int choice = QuestionDialog.choose(this, "You ran out of points!", "Game Over",
    JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
        Runnable next = (choice == 1) ? onMenu : onRestart;
        if (next != null) SwingUtilities.invokeLater(next);
    }

    private void showPauseMenu() {
        if (win || gameOver) return;
        timer.stop();
        clearKeys();
        Object[] options = {"Resume", "Restart Level", "Back to Menu"};
        int choice = QuestionDialog.choose(this, "Game paused", "Paused",
    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (choice == 1 && onRestart != null) onRestart.run();
        else if (choice == 2 && onMenu != null) onMenu.run();
        else timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Calculate responsive tile size
        int availableWidth = getWidth()
                - (OUTER_PADDING + MAZE_INNER_PADDING) * 2;

        int availableHeight = getHeight()
                - OUTER_PADDING * 2
                - SCORE_BOX_HEIGHT
                - SECTION_GAP
                - MAZE_INNER_PADDING * 2;

        if (m.getWidth() <= 0 || m.getHeight() <= 0) return;

        tileSize = Math.min(
            32,
            Math.min(
                availableWidth / m.getWidth(),
                availableHeight / m.getHeight()
            )
        );

        tileSize = Math.max(1, tileSize);

        // Maze dimensions
        mazeWidthPx = m.getWidth() * tileSize;
        mazeHeightPx = m.getHeight() * tileSize;

        int containerWidth =
                mazeWidthPx + MAZE_INNER_PADDING * 2;

        int mazeContainerHeight =
                mazeHeightPx + MAZE_INNER_PADDING * 2;

        // Center maze
        int scoreX = (getWidth() - containerWidth) / 2;

        mazeStartX = (getWidth() - mazeWidthPx) / 2;

        int mazeBoxY =
                OUTER_PADDING + SCORE_BOX_HEIGHT + SECTION_GAP;

        mazeStartY = mazeBoxY + MAZE_INNER_PADDING;

        // Background + frame
        GameTheme.drawBackground(g2d, getWidth(), getHeight(), LEVEL);

        // Score panel
        GameTheme.drawPanel(g2d, scoreX, OUTER_PADDING, containerWidth, SCORE_BOX_HEIGHT, LEVEL);

        g2d.setColor(score <= 50 ? new Color(255, 90, 90) : GameTheme.CREAM);

        g2d.setFont(new Font("Monospaced", Font.BOLD, 24));

        String scoreText = "Score: " + score;

        FontMetrics fm = g2d.getFontMetrics();

        int textX = scoreX
                + (containerWidth - fm.stringWidth(scoreText)) / 2;

        int textY = OUTER_PADDING
                + ((SCORE_BOX_HEIGHT - fm.getHeight()) / 2)
                + fm.getAscent();

        g2d.drawString(scoreText, textX, textY);

        // Maze container
        GameTheme.drawPanel(g2d, scoreX, mazeBoxY, containerWidth, mazeContainerHeight, LEVEL);

        // Clip maze drawing
        Shape oldClip = g2d.getClip();

        g2d.setClip(
            new RoundRectangle2D.Float(
                mazeStartX,
                mazeStartY,
                mazeWidthPx,
                mazeHeightPx,
                16, 16
            )
        );

        g2d.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );

        if (!win && !gameOver) {

            // Draw Map Tiles
            for (int y = 0; y < m.getHeight(); y++) {

                for (int x = 0; x < m.getWidth(); x++) {

                    int drawX = mazeStartX + (x * tileSize);
                    int drawY = mazeStartY + (y * tileSize);

                    String tile = m.getMap(x, y);

                    if (tile.equals("w")) {

                        g2d.drawImage(
                            m.getWall(),
                            drawX, drawY,
                            tileSize, tileSize,
                            null
                        );

                    } else if (tile.equals("f")) {

                        g2d.drawImage(
                            m.getFinish(),
                            drawX, drawY,
                            tileSize, tileSize,
                            null
                        );

                    } else {

                        g2d.drawImage(
                            m.getGrass(),
                            drawX, drawY,
                            tileSize, tileSize,
                            null
                        );
                    }
                }
            }

            // Draw Math Tile Markers
            for (Point trap : mathTraps) {
                GameTheme.drawMarker(g2d, LEVEL,
                    mazeStartX + trap.x * tileSize,
                    mazeStartY + trap.y * tileSize,
                    tileSize);
            }

            // Scaling factor
            double scale = tileSize / 32.0;

            // Draw Mummy
            if (!mummy.isDefeated()) {

                int mummyX = mazeStartX
                        + (int)(mummy.getPixelX() * scale);

                int mummyY = mazeStartY
                        + (int)(mummy.getPixelY() * scale);

                int hoverOffset = (int)(
                    Math.sin(System.currentTimeMillis() / 150.0)
                    * 3 * scale
                );

                g2d.drawImage(
                    mummy.getFrame(),
                    mummyX,
                    mummyY + hoverOffset,
                    tileSize,
                    tileSize,
                    null
                );
            }

            // Draw Player
            int playerX = mazeStartX
                    + (int)(p.getPixelX() * scale);

            int playerY = mazeStartY
                    + (int)(p.getPixelY() * scale);

            if (p.getPlayerFrame() != null) {

                g2d.drawImage(
                    p.getPlayerFrame(),
                    playerX,
                    playerY,
                    tileSize,
                    tileSize,
                    null
                );
            }
        }

        // Restore original clip
        g2d.setClip(oldClip);

        // Game status
        if (win || gameOver) {

            String message = win
                    ? "YOU WIN!"
                    : "GAME OVER";

            g2d.setColor(GameTheme.CREAM);
            g2d.setFont(new Font("Arial", Font.BOLD, 36));

            FontMetrics statusFM = g2d.getFontMetrics();

            int messageX =
                    (getWidth() - statusFM.stringWidth(message)) / 2;

            int messageY = getHeight() / 2;

            g2d.drawString(message, messageX, messageY);
        }
    }

    private class Al extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP)
                upPressed = true;

            else if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN)
                downPressed = true;

            else if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT)
                leftPressed = true;

            else if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT)
                rightPressed = true;

            else if (key == KeyEvent.VK_SHIFT)
                shiftPressed = true;

            else if (key == KeyEvent.VK_SPACE && !spacePressed) {
                spacePressed = true;
                p.attack();
            }
            else if (key == KeyEvent.VK_ESCAPE)
                showPauseMenu();
        }

        @Override
        public void keyReleased(KeyEvent e) {

            int key = e.getKeyCode();

            if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP)
                upPressed = false;

            else if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN)
                downPressed = false;

            else if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT)
                leftPressed = false;

            else if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT)
                rightPressed = false;

            else if (key == KeyEvent.VK_SHIFT)
                shiftPressed = false;

            else if (key == KeyEvent.VK_SPACE)
                spacePressed = false;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Mummy Math Maze");
        Board3 board = new Board3();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(board);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
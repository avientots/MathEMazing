import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.Random;
import javax.swing.*;

public class StartScreen extends JPanel {

    private static final int W = 800, H = 600, TILE = 32;
    private static final Color MOSS = new Color(20, 35, 20);
    private static final Color GRID = new Color(29, 51, 23);
    private static final Color WALL = new Color(44, 74, 34);
    private static final Color GOLD = new Color(242, 182, 50);
    private static final Color GOLD_HOVER = new Color(255, 201, 77);
    private static final Color GOLD_DEEP = new Color(181, 123, 10);
    private static final Color PARCHMENT = new Color(243, 234, 210);

    private final Rectangle button = new Rectangle(W / 2 - 130, 400, 260, 70);
    private final boolean[][] walls = new boolean[W / TILE + 1][H / TILE + 1];
    private boolean hover = false, pressed = false;

    public StartScreen() {
        setPreferredSize(new Dimension(W, H));
        setFocusable(true);

        // Random maze-like walls (fixed seed so it looks the same every time)
        Random rand = new Random(7);
        for (int x = 0; x < walls.length; x++)
            for (int y = 0; y < walls[x].length; y++)
                walls[x][y] = rand.nextInt(100) < 18;

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                boolean over = button.contains(e.getPoint());
                if (over != hover) {
                    hover = over;
                    setCursor(Cursor.getPredefinedCursor(over ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (button.contains(e.getPoint())) { pressed = true; repaint(); }
            }
            @Override public void mouseReleased(MouseEvent e) {
                boolean wasPressed = pressed;
                pressed = false;
                repaint();
                if (wasPressed && button.contains(e.getPoint())) startGame();
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) startGame();
            }
        });
    }

    private void startGame() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) window.dispose();
        // Change Board5 to whichever Board class you want to launch
        Board5.main(new String[0]);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Background tiles
        g2.setColor(MOSS);
        g2.fillRect(0, 0, W, H);
        for (int x = 0; x < walls.length; x++) {
            for (int y = 0; y < walls[x].length; y++) {
                if (walls[x][y]) {
                    g2.setColor(WALL);
                    g2.fillRect(x * TILE, y * TILE, TILE, TILE);
                }
            }
        }
        g2.setColor(GRID);
        for (int x = 0; x <= W; x += TILE) g2.drawLine(x, 0, x, H);
        for (int y = 0; y <= H; y += TILE) g2.drawLine(0, y, W, y);

        // Dark vignette so the logo and button stand out
        g2.setPaint(new RadialGradientPaint(
            new Point2D.Float(W / 2f, H / 2f), W * 0.62f,
            new float[]{0.25f, 1f},
            new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, 225)}));
        g2.fillRect(0, 0, W, H);

        drawLogo(g2);

        g2.setFont(new Font("Monospaced", Font.PLAIN, 20));
        centerText(g2, "Solve the math. Beat the guardian. Claim the crown.", H / 2 + 60, PARCHMENT);

        drawButton(g2);

        g2.setFont(new Font("Monospaced", Font.PLAIN, 15));
        centerText(g2, "Move: W A S D or arrow keys   |   Run: hold Shift", 530, new Color(185, 199, 168));
    }

    private void drawLogo(Graphics2D g2) {
        Font font = new Font("Monospaced", Font.BOLD, 76);
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        String[] parts = {"Math", "E", "Mazing"};
        Color[] colors = {PARCHMENT, GOLD, PARCHMENT};

        int total = 0;
        for (String p : parts) total += fm.stringWidth(p);
        int x = (W - total) / 2, y = 190;

        // Drop shadow, then the text
        int sx = x;
        for (String p : parts) {
            g2.setColor(new Color(0, 0, 0, 140));
            g2.drawString(p, sx + 5, y + 5);
            sx += fm.stringWidth(p);
        }
        for (int i = 0; i < parts.length; i++) {
            g2.setColor(colors[i]);
            g2.drawString(parts[i], x, y);
            x += fm.stringWidth(parts[i]);
        }

        // Dashed "path" under the logo
        g2.setColor(GOLD);
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{10f, 8f}, 0f));
        int left = (W - total) / 2;
        g2.drawLine(left, y + 28, left + total, y + 28);
        g2.setStroke(new BasicStroke(1f));
    }

    private void drawButton(Graphics2D g2) {
        int offset = pressed ? 5 : 0;
        // Shadow / depth
        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRect(button.x, button.y + 10 - offset, button.width, button.height);
        g2.setColor(GOLD_DEEP);
        g2.fillRect(button.x, button.y + 6 - offset, button.width, button.height);
        // Face
        g2.setColor(hover ? GOLD_HOVER : GOLD);
        g2.fillRect(button.x, button.y + offset, button.width, button.height);

        g2.setFont(new Font("Monospaced", Font.BOLD, 34));
        FontMetrics fm = g2.getFontMetrics();
        String label = "Start";
        g2.setColor(new Color(42, 26, 0));
        g2.drawString(label, button.x + (button.width - fm.stringWidth(label)) / 2,
                button.y + offset + (button.height + fm.getAscent()) / 2 - 4);
    }

    private void centerText(Graphics2D g2, String text, int y, Color color) {
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(color);
        g2.drawString(text, (W - fm.stringWidth(text)) / 2, y);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("MathEMazing");
            StartScreen screen = new StartScreen();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(screen);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            screen.requestFocusInWindow();
        });
    }
}
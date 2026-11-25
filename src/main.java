import java.awt.*;
import javax.swing.*;

public class Main extends JPanel {

    private static final Color BG = new Color(11, 26, 16);
    private static final Color GOLD = new Color(242, 182, 50);
    private static final Color GOLD_DARK = new Color(184, 133, 26);
    private static final Color CREAM = new Color(244, 235, 208);
    private static final Color MUTED = new Color(185, 182, 160);

    public Main(Runnable onStart) {
        setLayout(new GridBagLayout());
        setBackground(BG);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        TitleComponent title = new TitleComponent();
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("Solve the math. Beat the guardian. Claim the crown.");
        tagline.setFont(new Font("Courier New", Font.PLAIN, 22));
        tagline.setForeground(new Color(216, 212, 188));
        tagline.setAlignmentX(CENTER_ALIGNMENT);

        PixelButton start = new PixelButton("Start", true, 34);
        start.setPreferredSize(new Dimension(330, 90));
        start.setMaximumSize(new Dimension(330, 90));
        start.addActionListener(e -> onStart.run());

        PixelButton how = new PixelButton("How to play?", false, 24);
        how.setPreferredSize(new Dimension(200, 70));
        how.setMaximumSize(new Dimension(200, 70));
        how.addActionListener(e -> showHowItWorks());

        // Stack the buttons vertically: Start on top, How to play below
        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
        start.setAlignmentX(CENTER_ALIGNMENT);
        how.setAlignmentX(CENTER_ALIGNMENT);
        buttons.add(start);
        buttons.add(Box.createVerticalStrut(28));
        buttons.add(how);
        buttons.setAlignmentX(CENTER_ALIGNMENT);

        JLabel controls = new JLabel("wow peace");
        controls.setFont(new Font("Courier New", Font.PLAIN, 16));
        controls.setForeground(MUTED);
        controls.setAlignmentX(CENTER_ALIGNMENT);

        content.add(title);
        content.add(Box.createVerticalStrut(60));
        content.add(tagline);
        content.add(Box.createVerticalStrut(50));
        content.add(buttons);
        content.add(Box.createVerticalStrut(10));
        content.add(controls);

        add(content);
    }

    private void showHowItWorks() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(owner, "How to play?", Dialog.ModalityType.APPLICATION_MODAL);

        JPanel root = new JPanel(new BorderLayout(0, 20));
        root.setBackground(BG);
        root.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(36, 70, 46), 2),
            BorderFactory.createEmptyBorder(28, 32, 28, 32)));

        JLabel heading = new JLabel("HOW TO PLAY?");
        heading.setFont(new Font("Courier New", Font.BOLD, 30));
        heading.setForeground(GOLD);

        JTextArea text = new JTextArea(
            "1. Explore the maze with W A S D or the arrow keys.\n"
          + "   Hold Shift to run. Press Space to attack.\n"
          + "   Press Esc to pause.\n\n"
          + "2. Yellow dots are math tiles. Step on one to get a\n"
          + "   question. Correct: +10. Wrong: -50 and you are\n"
          + "   pushed back.\n\n"
          + "3. Touch the guardian for the final challenge.\n"
          + "   Correct: +50. Wrong: -60.\n\n"
          + "4. Defeat the guardian, then reach the finish tile\n"
          + "   for +100 and the win. Each level unlocks a part\n"
          + "   of the story.\n\n"
          + "Your score starts at 150. Hit zero and it is game over.");
        text.setEditable(false);
        text.setFocusable(false);
        text.setOpaque(false);
        text.setFont(new Font("Courier New", Font.PLAIN, 17));
        text.setForeground(CREAM);

        PixelButton close = new PixelButton("Got it", true, 22);
        close.setPreferredSize(new Dimension(180, 56));
        close.addActionListener(e -> dialog.dispose());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
        south.setOpaque(false);
        south.add(close);

        root.add(heading, BorderLayout.NORTH);
        root.add(text, BorderLayout.CENTER);
        root.add(south, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setUndecorated(true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth(), h = getHeight(), cell = 40;

        // Pixel grid with scattered lighter tiles (stable across resizes)
        for (int cy = 0; cy * cell < h; cy++) {
            for (int cx = 0; cx * cell < w; cx++) {
                int hash = (cx * 73856093) ^ (cy * 19349663);
                int v = Math.floorMod(hash, 9);
                if (v == 0) {
                    g2.setColor(new Color(29, 61, 34));
                    g2.fillRect(cx * cell, cy * cell, cell, cell);
                } else if (v == 1) {
                    g2.setColor(new Color(22, 48, 27));
                    g2.fillRect(cx * cell, cy * cell, cell, cell);
                }
            }
        }
        g2.setColor(new Color(255, 255, 255, 12));
        for (int x = 0; x < w; x += cell) g2.drawLine(x, 0, x, h);
        for (int y = 0; y < h; y += cell) g2.drawLine(0, y, w, y);

        // Vignette
        float radius = Math.max(w, h) * 0.7f;
        g2.setPaint(new RadialGradientPaint(
            new Point(w / 2, h / 2), radius,
            new float[] {0.45f, 1f},
            new Color[] {new Color(0, 0, 0, 0), new Color(0, 0, 0, 190)}));
        g2.fillRect(0, 0, w, h);
        g2.dispose();
    }

    /** Draws the "MathEMazing" title with a gold E and a dashed underline. */
    private static class TitleComponent extends JComponent {
        TitleComponent() {
            setPreferredSize(new Dimension(820, 150));
            setMaximumSize(new Dimension(820, 150));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(new Font("Courier New", Font.BOLD, 88));
            FontMetrics fm = g2.getFontMetrics();

            String a = "MATH", b = "e", c = "MAZING";
            int total = fm.stringWidth(a + b + c);
            int x = (getWidth() - total) / 2;
            int y = 95;

            String[] parts = {a, b, c};
            Color[] colors = {CREAM, GOLD, CREAM};
            int px = x;
            for (int i = 0; i < parts.length; i++) {
                g2.setColor(Color.BLACK);
                g2.drawString(parts[i], px + 5, y + 5);
                g2.setColor(colors[i]);
                g2.drawString(parts[i], px, y);
                px += fm.stringWidth(parts[i]);
            }

            g2.setColor(GOLD);
            g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10f, new float[] {14f, 8f}, 0f));
            g2.drawLine(x, 118, x + total, 118);
            g2.dispose();
        }
    }

    /** Flat pixel-style button with a thick bottom edge. */
    private static class PixelButton extends JButton {
        private final boolean filled;
        private final int fontSize;

        PixelButton(String label, boolean filled, int fontSize) {
            super(label);
            this.filled = filled;
            this.fontSize = fontSize;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight(), edge = 8;
            boolean hover = getModel().isRollover();
            boolean down = getModel().isPressed();
            int shift = down ? edge / 2 : 0;
            int bodyH = h - edge;

            if (filled) {
                g2.setColor(GOLD_DARK);
                g2.fillRect(0, shift + edge, w, bodyH);
                g2.setColor(hover ? new Color(250, 200, 80) : GOLD);
                g2.fillRect(0, shift, w, bodyH);
                g2.setColor(new Color(42, 29, 5));
            } else {
                g2.setColor(Color.BLACK);
                g2.fillRect(0, shift + edge, w, bodyH);
                g2.setColor(hover ? new Color(24, 52, 32) : BG);
                g2.fillRect(0, shift, w, bodyH);
                g2.setColor(CREAM);
                g2.setStroke(new BasicStroke(3f));
                g2.drawRect(1, shift + 1, w - 3, bodyH - 3);
            }

            g2.setFont(new Font("Courier New", Font.BOLD, fontSize));
            FontMetrics fm = g2.getFontMetrics();
            int tx = (w - fm.stringWidth(getText())) / 2;
            int ty = shift + (bodyH - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(getText(), tx, ty);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("MathEMazing");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setMinimumSize(new Dimension(900, 650));
            showMenu(frame);
            frame.setVisible(true);
        });
    }

    private static void showMenu(JFrame frame) {
        swapTo(frame, new Main(() -> startLevel(frame, 1))); // Start button -> Level 1
    }

    private static void startLevel(JFrame frame, int level) {
        // next: after winning. Level 5 -> "Play Again" goes back to Level 1
        Runnable next    = () -> startLevel(frame, level < 5 ? level + 1 : 1);
        Runnable restart = () -> startLevel(frame, level);
        Runnable menu    = () -> showMenu(frame);

        JPanel board;
        switch (level) {
            case 1 -> { Board  b = new Board();  b.setCallbacks(next, restart, menu); board = b; }
            case 2 -> { Board2 b = new Board2(); b.setCallbacks(next, restart, menu); board = b; }
            case 3 -> { Board3 b = new Board3(); b.setCallbacks(next, restart, menu); board = b; }
            case 4 -> { Board4 b = new Board4(); b.setCallbacks(next, restart, menu); board = b; }
            default -> { Board5 b = new Board5(); b.setCallbacks(next, restart, menu); board = b; }
        }
        swapTo(frame, board);
        board.requestFocusInWindow();
    }

    private static void swapTo(JFrame frame, JComponent panel) {
        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();
    }
}
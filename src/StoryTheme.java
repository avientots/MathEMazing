import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/** Shared look for the story pop-ups. Dynamically matches each maze level's GameTheme palette. */
public class StoryTheme {

    public static final Color GOLD = new Color(242, 182, 50);
    public static final Color GOLD_DARK = new Color(184, 133, 26);
    public static final Color CREAM = new Color(244, 235, 208);

    public static final Font TEXT_FONT = new Font("Courier New", Font.PLAIN, 17);
    public static final Font QUOTE_FONT = new Font("Courier New", Font.BOLD | Font.ITALIC, 16);

    // Base background colors matching GameTheme.PALETTES[level - 1][0]
    private static final Color[] LEVEL_BG = {
        new Color(11, 26, 16), // Level 1: village green
        new Color(8, 24, 38),  // Level 2: deep sea teal
        new Color(28, 12, 38), // Level 3: purple
        new Color(34, 20, 10), // Level 4: ink brown
        new Color(10, 10, 28)  // Level 5: midnight blue
    };

    // Inner frame / hover colors matching GameTheme.PALETTES[level - 1][1]
    private static final Color[] LEVEL_EDGE = {
        new Color(29, 61, 34), // Level 1
        new Color(20, 56, 84), // Level 2
        new Color(58, 28, 80), // Level 3
        new Color(74, 46, 22), // Level 4
        new Color(30, 30, 70)  // Level 5
    };

    public static Color getBg(int part) {
        int idx = Math.max(0, Math.min(LEVEL_BG.length - 1, part - 1));
        return LEVEL_BG[idx];
    }

    public static Color getEdge(int part) {
        int idx = Math.max(0, Math.min(LEVEL_EDGE.length - 1, part - 1));
        return LEVEL_EDGE[idx];
    }

    public static Color getAccent(int part) {
        return GameTheme.accent(part);
    }

    /** Level-themed background with a pixel-style frame (corner blocks + side ornaments). */
    public static class BackgroundPanel extends JPanel {
        private final Color edgeColor;
        private final Color accentColor;

        public BackgroundPanel(LayoutManager layout) {
            this(layout, 1);
        }

        public BackgroundPanel(LayoutManager layout, int part) {
            super(layout);
            this.edgeColor = getEdge(part);
            this.accentColor = getAccent(part);
            setBackground(getBg(part));
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor, 3),
                BorderFactory.createEmptyBorder(26, 34, 26, 34)));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth(), h = getHeight();
            int in = 12;
            int s = 6;

            // thin inner frame
            g2.setColor(edgeColor);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRect(in, in, w - 2 * in - 1, h - 2 * in - 1);

            // accent pixel "stairs" in the 4 corners
            g2.setColor(accentColor);
            int[][] corners = {{in, in, 1, 1}, {w - in, in, -1, 1}, {in, h - in, 1, -1}, {w - in, h - in, -1, -1}};
            int[][] blocks = {{0, 0}, {1, 0}, {2, 0}, {0, 1}, {0, 2}};
            for (int[] c : corners) {
                for (int[] b : blocks) {
                    int bx = c[2] > 0 ? c[0] + b[0] * s : c[0] - (b[0] + 1) * s;
                    int by = c[3] > 0 ? c[1] + b[1] * s : c[1] - (b[1] + 1) * s;
                    g2.fillRect(bx, by, s, s);
                }
            }

            // small plus-shaped ornaments in the middle of the left and right sides
            int cy = h / 2;
            int[] xs = {in, w - in};
            for (int cx : xs) {
                g2.fillRect(cx - s / 2, cy - s / 2, s, s);
                g2.fillRect(cx - s / 2, cy - s / 2 - s, s, s);
                g2.fillRect(cx - s / 2, cy - s / 2 + s, s, s);
                g2.fillRect(cx - s / 2 - s, cy - s / 2, s, s);
                g2.fillRect(cx - s / 2 + s, cy - s / 2, s, s);
            }
            g2.dispose();
        }
    }

    /** Level-accented title with a shadow and a dashed underline. */
    public static class TitleBar extends JComponent {
        private final String text;
        private final Color accentColor;

        public TitleBar(String text) {
            this(text, 1);
        }

        public TitleBar(String text, int part) {
            this.text = text;
            this.accentColor = getAccent(part);
            setPreferredSize(new Dimension(100, 66));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(new Font("Courier New", Font.BOLD, 30));
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(text);
            int x = (getWidth() - tw) / 2;
            int y = 34;
            g2.setColor(Color.BLACK);
            g2.drawString(text, x + 3, y + 3);
            g2.setColor(accentColor);
            g2.drawString(text, x, y);
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10f, new float[] {12f, 7f}, 0f));
            g2.drawLine(x, 48, x + tw, 48);
            g2.dispose();
        }
    }

    /** Dark translucent box that holds the story text. */
    public static class TextBox extends JPanel {
        private final Color edgeColor;

        public TextBox(JTextArea area) {
            this(area, 1);
        }

        public TextBox(JTextArea area, int part) {
            super(new BorderLayout());
            this.edgeColor = getEdge(part);
            setOpaque(false);
            setBorder(new EmptyBorder(6, 6, 6, 6));

            area.setEditable(false);
            area.setFocusable(false);
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
            area.setOpaque(false);
            area.setFont(TEXT_FONT);
            area.setForeground(CREAM);
            area.setMargin(new Insets(10, 14, 10, 14));

            JScrollPane scroll = new JScrollPane(area);
            scroll.setOpaque(false);
            scroll.getViewport().setOpaque(false);
            scroll.setBorder(null);
            scroll.getVerticalScrollBar().setUnitIncrement(16);
            add(scroll, BorderLayout.CENTER);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(0, 0, 0, 130));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(edgeColor);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
            g2.dispose();
        }
    }

    /** Flat pixel-style button matching each level's palette. */
    public static class PixelButton extends JButton {
        private final boolean filled;
        private final int fontSize;
        private final Color bgNormal;
        private final Color bgHover;
        private final Color accentColor;
        private final Color accentDark;

        public PixelButton(String label, boolean filled, int fontSize) {
            this(label, filled, fontSize, 1);
        }

        public PixelButton(String label, boolean filled, int fontSize, int part) {
            super(label);
            this.filled = filled;
            this.fontSize = fontSize;
            this.bgNormal = getBg(part);
            this.bgHover = getEdge(part);
            this.accentColor = getAccent(part);
            this.accentDark = accentColor.darker();
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
                g2.setColor(accentDark);
                g2.fillRect(0, shift + edge, w, bodyH);
                g2.setColor(hover ? accentColor.brighter() : accentColor);
                g2.fillRect(0, shift, w, bodyH);
                g2.setColor(new Color(24, 18, 10));
            } else {
                g2.setColor(Color.BLACK);
                g2.fillRect(0, shift + edge, w, bodyH);
                g2.setColor(hover ? bgHover : bgNormal);
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
}
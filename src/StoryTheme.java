import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/** Shared look for the story pop-ups. Matches the MathEMazing start screen. */
public class StoryTheme {

    public static final Color BG = new Color(11, 26, 16);
    public static final Color GOLD = new Color(242, 182, 50);
    public static final Color GOLD_DARK = new Color(184, 133, 26);
    public static final Color CREAM = new Color(244, 235, 208);
    public static final Color EDGE = new Color(36, 70, 46);

    public static final Font TEXT_FONT = new Font("Courier New", Font.PLAIN, 17);
    public static final Font QUOTE_FONT = new Font("Courier New", Font.BOLD | Font.ITALIC, 16);

    /** Plain dark green background with a small pixel-style frame (corner blocks + side ornaments). */
    public static class BackgroundPanel extends JPanel {
        public BackgroundPanel(LayoutManager layout) {
            super(layout);
            setBackground(BG);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD, 3),
                BorderFactory.createEmptyBorder(26, 34, 26, 34)));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // plain solid fill
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth(), h = getHeight();
            int in = 12;   // distance of the inner frame from the edge
            int s = 6;     // pixel block size

            // thin inner frame
            g2.setColor(EDGE);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRect(in, in, w - 2 * in - 1, h - 2 * in - 1);

            // gold pixel "stairs" in the 4 corners
            g2.setColor(GOLD);
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

    /** Gold title with a shadow and a dashed underline. */
    public static class TitleBar extends JComponent {
        private final String text;

        public TitleBar(String text) {
            this.text = text;
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
            g2.setColor(GOLD);
            g2.drawString(text, x, y);
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10f, new float[] {12f, 7f}, 0f));
            g2.drawLine(x, 48, x + tw, 48);
            g2.dispose();
        }
    }

    /** Dark translucent box that holds the story text. */
    public static class TextBox extends JPanel {
        public TextBox(JTextArea area) {
            super(new BorderLayout());
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
            g2.setColor(EDGE);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
            g2.dispose();
        }
    }

    /** Flat pixel-style button with a thick bottom edge (same as the start screen). */
    public static class PixelButton extends JButton {
        private final boolean filled;
        private final int fontSize;

        public PixelButton(String label, boolean filled, int fontSize) {
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
}
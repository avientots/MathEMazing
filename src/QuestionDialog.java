import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Game-styled pop-ups that replace JOptionPane (same method parameters, so you only
 * rename the call). The colors follow the level, using GameTheme.
 *
 *   JOptionPane.showInputDialog(...)   ->  QuestionDialog.ask(...)
 *   JOptionPane.showMessageDialog(...) ->  QuestionDialog.message(...)
 *   JOptionPane.showOptionDialog(...)  ->  QuestionDialog.choose(...)
 *
 * The level is detected from the parent panel's class name (Board = 1, Board2 = 2, ... Board5 = 5).
 */
public class QuestionDialog {

    private static final String FONT = "Courier New";

    // ------------------------------------------------------------------ public API

    /** Question with a text box. Returns what the player typed (like JOptionPane.showInputDialog). */
    public static String ask(Component parent, Object message, String title, int messageType) {
        int level = levelOf(parent);
        Color accent = GameTheme.accent(level);
        Dimension size = new Dimension(720, 480);
        Root root = new Root(level);
        JDialog d = makeDialog(parent, size, root);

        // the last non-empty line is the actual question; the lines before it are the intro
        String[] lines = String.valueOf(message).strip().split("\n");
        int last = lines.length - 1;
        while (last > 0 && lines[last].isBlank()) last--;
        StringBuilder intro = new StringBuilder();
        for (int i = 0; i < last; i++) intro.append(lines[i]).append("\n");
        String introText = intro.toString().strip();

        root.add(Box.createVerticalGlue());
        root.add(new FitText(String.valueOf(title).toUpperCase(), accent, 30, 18, accent));
        root.add(Box.createVerticalStrut(14));
       if (!introText.isEmpty()) {
        root.add(centered(
            body(introText, 16, GameTheme.CREAM, size.width - 140)
        ));
    root.add(Box.createVerticalStrut(16));
}
        root.add(new FitText(lines[last].strip(), Color.WHITE, 46, 22, null));
        root.add(Box.createVerticalStrut(18));

        JTextField field = new JTextField();
        field.setFont(new Font(FONT, Font.BOLD, 34));
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setForeground(GameTheme.CREAM);
        field.setCaretColor(accent);
        field.setBackground(new Color(5, 5, 10));
        field.setOpaque(true);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accent, 3),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        field.setPreferredSize(new Dimension(280, 62));
        root.add(centered(field));
        root.add(Box.createVerticalStrut(16));

        LevelButton submit = new LevelButton("Submit", true, 24, accent);
        submit.setPreferredSize(new Dimension(220, 60));
        root.add(centered(submit));
        root.add(Box.createVerticalGlue());

        final String[] result = {null};
        Runnable done = () -> { result[0] = field.getText(); d.dispose(); };
        submit.addActionListener(e -> done.run());
        field.addActionListener(e -> done.run());
        d.addWindowListener(new WindowAdapter() {
            @Override public void windowOpened(WindowEvent e) { field.requestFocusInWindow(); }
        });
        d.setVisible(true); // blocks until closed
        return result[0];
    }

    /** "Correct!" / "Wrong!" style notice with a Continue button. */
    
    public static void message(Component parent, Object message) {
        int level = levelOf(parent);
        Color accent = GameTheme.accent(level);
        Dimension size = new Dimension(660, 340);
        Root root = new Root(level);
        JDialog d = makeDialog(parent, size, root);

        String text = String.valueOf(message).strip();
        boolean good = text.startsWith("Correct");
        boolean bad = text.startsWith("Wrong");

        String head = good ? "CORRECT!" : bad ? "WRONG!" : "NOTICE";
        Color headColor = good ? new Color(120, 255, 140)
                            : bad ? new Color(255, 90, 90)
                                    : accent;

        String bodyText = text.replaceFirst("^(Correct!|Wrong!)\\s*", "");

        root.add(Box.createVerticalGlue());
        root.add(new FitText(head, headColor, 54, 28, headColor));
        root.add(Box.createVerticalStrut(18));

        JLabel messageLabel = body(
            bodyText,
            22,
            GameTheme.CREAM,
            size.width - 140
        );

        root.add(centered(messageLabel));
        root.add(Box.createVerticalStrut(22));

        LevelButton ok = new LevelButton("Continue", true, 24, accent);
        ok.setPreferredSize(new Dimension(240, 60));
        ok.addActionListener(e -> d.dispose());

        root.add(centered(ok));
        root.add(Box.createVerticalGlue());

        d.getRootPane().setDefaultButton(ok);

        d.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                ok.requestFocusInWindow();
            }
        });

        d.setVisible(true);
    }


    /** Menu with buttons (pause menu, game over). Returns the index of the chosen option. */
    public static int choose(Component parent, Object message, String title, int optionType,
                             int messageType, Icon icon, Object[] options, Object initialValue) {
        int level = levelOf(parent);
        Color accent = GameTheme.accent(level);
        Dimension size = new Dimension(760, 340);
        Root root = new Root(level);
        JDialog d = makeDialog(parent, size, root);

        Color headColor = messageType == JOptionPane.ERROR_MESSAGE ? new Color(255, 90, 90) : accent;

        root.add(Box.createVerticalGlue());
        root.add(new FitText(String.valueOf(title).toUpperCase(), headColor, 48, 26, headColor));
        root.add(Box.createVerticalStrut(16));
        root.add(body(String.valueOf(message), 22, GameTheme.CREAM, size.width - 140));
        root.add(Box.createVerticalStrut(24));

        final int[] result = {-1};
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        row.setOpaque(false);
        for (int i = 0; i < options.length; i++) {
            final int index = i;
            LevelButton b = new LevelButton(String.valueOf(options[i]), i == 0, 18, accent);
            b.setPreferredSize(new Dimension(options.length > 2 ? 210 : 250, 60));
            b.addActionListener(e -> { result[0] = index; d.dispose(); });
            row.add(b);
        }
        root.add(centered(row));
        root.add(Box.createVerticalGlue());

        // ESC closes the pause menu (acts like "Resume")
        if ("Paused".equals(title)) {
            root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "resume");
            root.getActionMap().put("resume", new AbstractAction() {
                @Override public void actionPerformed(ActionEvent e) { result[0] = 0; d.dispose(); }
            });
        }
        d.setVisible(true);
        return result[0];
    }

    // ------------------------------------------------------------------ helpers

    private static int levelOf(Component parent) {
        String n = parent == null ? "" : parent.getClass().getSimpleName();
        if (n.startsWith("Board") && n.length() > 5) {
            char c = n.charAt(5);
            if (c >= '1' && c <= '5') return c - '0';
        }
        return 1;
    }

    private static Window windowOf(Component parent) {
        if (parent == null) return null;
        return parent instanceof Window ? (Window) parent : SwingUtilities.getWindowAncestor(parent);
    }

    private static JDialog makeDialog(Component parent, Dimension size, Root root) {
        Window owner = windowOf(parent);
        JDialog d = new JDialog(owner, Dialog.ModalityType.APPLICATION_MODAL);
        d.setUndecorated(true);
        d.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        d.setContentPane(root);
        d.setSize(size);
        d.setLocationRelativeTo(owner);
        Timer anim = new Timer(70, e -> root.repaint()); // makes the sparkles twinkle
        anim.start();
        d.addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) { anim.stop(); }
        });
        return d;
    }

    private static JPanel centered(JComponent c) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p.setOpaque(false);
        p.add(c);
        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height + 4));
        return p;
    }

   
    private static JLabel body(String text, int size, Color color, int widthPx) {
        String safe = text.replace("&", "&amp;")
                        .replace("<", "&lt;")
                        .replace(">", "&gt;")
                        .replace("\n", "<br>");

        JLabel l = new JLabel(
            "<html><div style='text-align:center; width:" + widthPx
            + "px;'>" + safe + "</div></html>",
            SwingConstants.CENTER
        );

        l.setFont(new Font(FONT, Font.BOLD, size));
        l.setForeground(color);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setMaximumSize(new Dimension(widthPx, Integer.MAX_VALUE));

        return l;
    }

    /** Dialog background: the same animated pixel background and frame as the game screen. */
    private static class Root extends JPanel {
        private final int level;

        Root(int level) {
            this.level = level;
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createEmptyBorder(34, 44, 30, 44));
            setBackground(Color.BLACK);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            GameTheme.drawBackground((Graphics2D) g, getWidth(), getHeight(), level);
        }
    }

    /** One line of big text that shrinks to fit, with a shadow and an optional dashed underline. */
    private static class FitText extends JComponent {
        private final String text;
        private final Color color, dash;
        private final int maxSize, minSize;

        FitText(String text, Color color, int maxSize, int minSize, Color dash) {
            this.text = text;
            this.color = color;
            this.maxSize = maxSize;
            this.minSize = minSize;
            this.dash = dash;
            int h = maxSize + (dash != null ? 24 : 14);
            setPreferredSize(new Dimension(100, h));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, h));
            setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int size = maxSize;
            FontMetrics fm;
            while (true) {
                g2.setFont(new Font(FONT, Font.BOLD, size));
                fm = g2.getFontMetrics();
                if (fm.stringWidth(text) <= getWidth() - 10 || size <= minSize) break;
                size--;
            }
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = fm.getAscent() + 2;
            g2.setColor(Color.BLACK);
            g2.drawString(text, x + 3, y + 3);
            g2.setColor(color);
            g2.drawString(text, x, y);
            if (dash != null) {
                g2.setColor(dash);
                g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                    10f, new float[] {12f, 7f}, 0f));
                g2.drawLine(x, y + 12, x + fm.stringWidth(text), y + 12);
            }
            g2.dispose();
        }
    }

    /** Pixel-style button with a thick bottom edge, colored by the level's accent. */
    private static class LevelButton extends JButton {
        private final boolean filled;
        private final int fontSize;
        private final Color accent;

        LevelButton(String label, boolean filled, int fontSize, Color accent) {
            super(label);
            this.filled = filled;
            this.fontSize = fontSize;
            this.accent = accent;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight(), edge = 8;
            boolean hover = getModel().isRollover();
            int shift = getModel().isPressed() ? edge / 2 : 0;
            int bodyH = h - edge;

            if (filled) {
                g2.setColor(accent.darker().darker());
                g2.fillRect(0, shift + edge, w, bodyH);
                g2.setColor(hover ? accent.brighter() : accent);
                g2.fillRect(0, shift, w, bodyH);
                g2.setColor(new Color(20, 15, 10));
            } else {
                g2.setColor(Color.BLACK);
                g2.fillRect(0, shift + edge, w, bodyH);
                g2.setColor(hover ? new Color(40, 40, 55) : new Color(12, 12, 20));
                g2.fillRect(0, shift, w, bodyH);
                g2.setColor(GameTheme.CREAM);
                g2.setStroke(new BasicStroke(3f));
                g2.drawRect(1, shift + 1, w - 3, bodyH - 3);
            }

            g2.setFont(new Font(FONT, Font.BOLD, fontSize));
            FontMetrics fm = g2.getFontMetrics();
            int tx = (w - fm.stringWidth(getText())) / 2;
            int ty = shift + (bodyH - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(getText(), tx, ty);
            g2.dispose();
        }
    }
}

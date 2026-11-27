import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class PauseButtonHelper {

    public static void attach(JPanel board, int level, Runnable onOpenPause, Runnable onResume) {
        board.setLayout(null);
        Color accent = GameTheme.accent(level);

        JButton pauseBtn = new JButton("||") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight(), edge = 5;
                int shift = getModel().isPressed() ? edge / 2 : 0;
                int bodyH = h - edge;

                g2.setColor(new Color(0, 0, 0, 190));
                g2.fillRect(0, shift + edge, w, bodyH);
                g2.setColor(getModel().isRollover() ? new Color(35, 35, 45, 220) : new Color(0, 0, 0, 180));
                g2.fillRect(0, shift, w, bodyH);

                g2.setColor(accent);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRect(1, shift + 1, w - 3, bodyH - 3);

                g2.setFont(new Font("Courier New", Font.BOLD, 20));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (w - fm.stringWidth(getText())) / 2,
                    shift + (bodyH - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        };

        pauseBtn.setContentAreaFilled(false);
        pauseBtn.setBorderPainted(false);
        pauseBtn.setFocusPainted(false);
        pauseBtn.setFocusable(false);
        pauseBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        pauseBtn.setToolTipText("Pause (Esc)");

        Runnable reposition = () -> {
            int btnW = 54, btnH = 54;
            int x = Math.max(24, board.getWidth() - btnW - 28);
            int y = 26;
            pauseBtn.setBounds(x, y, btnW, btnH);
        };

        board.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) { reposition.run(); }
        });
        reposition.run();

        pauseBtn.addActionListener(e -> {
            if (onOpenPause != null) onOpenPause.run();
            showPopup(board, level, onResume);
        });

        board.add(pauseBtn);
    }

    public static void showPopup(Component parent, int level, Runnable onResume) {
        Window owner = parent instanceof Window ? (Window) parent : SwingUtilities.getWindowAncestor(parent);
        Color accent = GameTheme.accent(level);

        JDialog dialog = new JDialog(owner, "Paused", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        JPanel root = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                GameTheme.drawBackground((Graphics2D) g, getWidth(), getHeight(), level);
            }
        };
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(BorderFactory.createEmptyBorder(34, 40, 30, 40));
        root.setBackground(Color.BLACK);

        JLabel title = new JLabel("GAME PAUSED", SwingConstants.CENTER);
        title.setFont(new Font("Courier New", Font.BOLD, 34));
        title.setForeground(accent);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Choose an option below", SwingConstants.CENTER);
        sub.setFont(new Font("Courier New", Font.PLAIN, 18));
        sub.setForeground(GameTheme.CREAM);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        final int[] action = {0}; // 0 = Resume, 1 = Restart, 2 = Back to Main Menu

        JButton resumeBtn = createActionButton("Resume", true, accent);
        resumeBtn.setPreferredSize(new Dimension(190, 56));
        resumeBtn.addActionListener(e -> { action[0] = 0; dialog.dispose(); });

        JButton restartBtn = createActionButton("Restart", false, accent);
        restartBtn.setPreferredSize(new Dimension(190, 56));
        restartBtn.addActionListener(e -> { action[0] = 1; dialog.dispose(); });

        JButton menuBtn = createActionButton("Back to Main Menu", false, accent);
        menuBtn.setPreferredSize(new Dimension(250, 56));
        menuBtn.addActionListener(e -> { action[0] = 2; dialog.dispose(); });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        btnRow.setOpaque(false);
        btnRow.add(resumeBtn);
        btnRow.add(restartBtn);
        btnRow.add(menuBtn);
        btnRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        root.add(Box.createVerticalGlue());
        root.add(title);
        root.add(Box.createVerticalStrut(10));
        root.add(sub);
        root.add(Box.createVerticalStrut(26));
        root.add(btnRow);
        root.add(Box.createVerticalGlue());

        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "resume");
        root.getActionMap().put("resume", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { action[0] = 0; dialog.dispose(); }
        });

        Timer anim = new Timer(70, e -> root.repaint());
        anim.start();
        dialog.addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) { anim.stop(); }
        });

        dialog.setContentPane(root);
        dialog.setSize(820, 300);
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);

        if (action[0] == 1 && owner instanceof JFrame frame) {
            SwingUtilities.invokeLater(() -> Main.startLevel(frame, level));
        } else if (action[0] == 2 && owner instanceof JFrame frame) {
            SwingUtilities.invokeLater(() -> Main.showMenu(frame));
        } else if (onResume != null) {
            onResume.run();
        }
    }

    private static JButton createActionButton(String label, boolean filled, Color accent) {
        JButton btn = new JButton(label) {
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
                    g2.setColor(hover ? new Color(40, 40, 55) : new Color(15, 15, 25));
                    g2.fillRect(0, shift, w, bodyH);
                    g2.setColor(GameTheme.CREAM);
                    g2.setStroke(new BasicStroke(3f));
                    g2.drawRect(1, shift + 1, w - 3, bodyH - 3);
                }

                g2.setFont(new Font("Courier New", Font.BOLD, 18));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (w - fm.stringWidth(getText())) / 2,
                    shift + (bodyH - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
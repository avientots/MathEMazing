import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class GameRecords {

    private static final String DB_FILE = "game_records.txt";
    private static String currentPlayer = "Player";
    private static final int[] sessionScores = new int[5]; // index 0 = L1 ... 4 = L5

    private static final Color BG = new Color(11, 26, 16);
    private static final Color GOLD = new Color(242, 182, 50);
    private static final Color GOLD_DARK = new Color(184, 133, 26);
    private static final Color CREAM = new Color(244, 235, 208);
    private static final Color EDGE = new Color(36, 70, 46);

    /** Starts a fresh run for the given player name. */
    public static void startNewSession(String name) {
        String clean = (name == null || name.trim().isEmpty()) ? "Player" : name.trim().replace("|", "");
        currentPlayer = clean;
        Arrays.fill(sessionScores, 0);
    }

    /** Records the ending score of a cleared level (1..5). */
    public static void recordLevelScore(int level, int score) {
        if (level >= 1 && level <= 5) {
            sessionScores[level - 1] = score;
        }
    }

    /** Saves the completed 5-level run to game_records.txt. */
    public static void saveCompletedRun() {
        int total = 0;
        for (int s : sessionScores) total += s;

        String line = String.format("%d|%d|%d|%d|%d|%d|%s",
            sessionScores[0], sessionScores[1], sessionScores[2],
            sessionScores[3], sessionScores[4], total, currentPlayer);

        try {
            Files.writeString(
                Path.of(DB_FILE),
                line + System.lineSeparator(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            System.err.println("Failed to save record: " + e.getMessage());
        }
    }

    /** Reads all saved records from game_records.txt, sorted highest total first. */
    private static List<String[]> loadAllRecords() {
        List<String[]> rows = new ArrayList<>();
        Path path = Path.of(DB_FILE);
        if (!Files.exists(path)) return rows;

        try {
            for (String raw : Files.readAllLines(path)) {
                if (raw.isBlank()) continue;
                String[] parts = raw.split("\\|", 7);
                if (parts.length == 7) {
                    rows.add(parts);
                }
            }
            rows.sort((a, b) -> Integer.compare(parseIntSafe(b[5]), parseIntSafe(a[5])));
        } catch (IOException e) {
            System.err.println("Failed to load records: " + e.getMessage());
        }
        return rows;
    }

    private static int parseIntSafe(String s) {
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    /** Pops up a styled modal dialog after clicking Start so the player can enter their name. */
    public static String promptPlayerName(Component parent) {
        Window owner = SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner, "Enter Name", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(BG);
        root.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GOLD, 3),
            BorderFactory.createEmptyBorder(26, 32, 26, 32)
        ));

        JLabel heading = new JLabel("ENTER YOUR NAME", SwingConstants.CENTER);
        heading.setFont(new Font("Courier New", Font.BOLD, 28));
        heading.setForeground(GOLD);

        JTextField nameField = new JTextField(16);
        nameField.setFont(new Font("Courier New", Font.BOLD, 22));
        nameField.setForeground(CREAM);
        nameField.setBackground(new Color(18, 42, 26));
        nameField.setCaretColor(GOLD);
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(EDGE, 2),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        final String[] result = {null};

        JButton confirm = createDialogButton("Begin Quest", true);
        confirm.setPreferredSize(new Dimension(200, 50));
        confirm.addActionListener(e -> {
            String typed = nameField.getText().trim();
            result[0] = typed.isEmpty() ? "Player" : typed;
            dialog.dispose();
        });

        JButton cancel = createDialogButton("Cancel", false);
        cancel.setPreferredSize(new Dimension(140, 50));
        cancel.addActionListener(e -> {
            result[0] = null;
            dialog.dispose();
        });

        nameField.addActionListener(e -> confirm.doClick());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        btnRow.setOpaque(false);
        btnRow.add(confirm);
        btnRow.add(cancel);

        root.add(heading, BorderLayout.NORTH);
        root.add(nameField, BorderLayout.CENTER);
        root.add(btnRow, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        SwingUtilities.invokeLater(nameField::requestFocusInWindow);
        dialog.setVisible(true);

        return result[0];
    }

    /** Shows the summary table of all saved records (L1..L5, Total, Name) at the end of Level 5. */
    public static void showSummaryDialog(Window owner) {
        JDialog dialog = new JDialog(owner, "Hall of Records", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(BG);
        root.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GOLD, 3),
            BorderFactory.createEmptyBorder(26, 32, 26, 32)
        ));

        JLabel heading = new JLabel("ADVENTURER SCORE SUMMARY", SwingConstants.CENTER);
        heading.setFont(new Font("Courier New", Font.BOLD, 30));
        heading.setForeground(GOLD);

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-6s %-6s %-6s %-6s %-6s | %-7s | %s%n",
            "LVL 1", "LVL 2", "LVL 3", "LVL 4", "LVL 5", "TOTAL", "PLAYER NAME"));
        sb.append("---------------------------------------------------------------------\n");

        List<String[]> records = loadAllRecords();
        if (records.isEmpty()) {
            sb.append("No completed records yet.\n");
        } else {
            for (String[] r : records) {
                sb.append(String.format("%-6s %-6s %-6s %-6s %-6s | %-7s | %s%n",
                    r[0], r[1], r[2], r[3], r[4], r[5], r[6]));
            }
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFocusable(false);
        area.setFont(new Font("Courier New", Font.BOLD, 16));
        area.setForeground(CREAM);
        area.setBackground(new Color(8, 18, 11));
        area.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(740, 340));
        scroll.setBorder(BorderFactory.createLineBorder(EDGE, 2));

        JButton closeBtn = createDialogButton("Continue", true);
        closeBtn.setPreferredSize(new Dimension(210, 52));
        closeBtn.addActionListener(e -> dialog.dispose());

        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
        south.setOpaque(false);
        south.add(closeBtn);

        root.add(heading, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(south, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }

    private static JButton createDialogButton(String label, boolean filled) {
        JButton btn = new JButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight(), edge = 6;
                int shift = getModel().isPressed() ? edge / 2 : 0;
                int bodyH = h - edge;

                if (filled) {
                    g2.setColor(GOLD_DARK);
                    g2.fillRect(0, shift + edge, w, bodyH);
                    g2.setColor(getModel().isRollover() ? new Color(250, 200, 80) : GOLD);
                    g2.fillRect(0, shift, w, bodyH);
                    g2.setColor(new Color(42, 29, 5));
                } else {
                    g2.setColor(Color.BLACK);
                    g2.fillRect(0, shift + edge, w, bodyH);
                    g2.setColor(getModel().isRollover() ? new Color(24, 52, 32) : BG);
                    g2.fillRect(0, shift, w, bodyH);
                    g2.setColor(CREAM);
                    g2.setStroke(new BasicStroke(2f));
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
import java.awt.*;
import javax.swing.*;

/**
 * Story pop-up for Levels 1-4.
 * Usage:  StoryDialog.show(parentFrame, 1);   // 1..4
 * show() BLOCKS until the player presses Continue.
 */
public class StoryDialog extends JDialog {

    private final JTextArea textArea = new JTextArea();
    private final JLabel quoteLabel = new JLabel(" ", SwingConstants.CENTER);
    private final StoryTheme.PixelButton actionButton;
    private final Timer timer;
    private final String fullText;
    private final String quote;
    private int index = 0;
    private boolean done = false;

    private StoryDialog(Window owner, int part) {
        super(owner, StoryData.TITLES[part - 1], ModalityType.APPLICATION_MODAL);
        this.fullText = StoryData.BODY[part - 1].strip();
        this.quote = StoryData.QUOTES[part - 1];
        this.actionButton = new StoryTheme.PixelButton("Skip", false, 22, part);

        setUndecorated(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        JPanel root = new StoryTheme.BackgroundPanel(new BorderLayout(0, 14), part);
        setContentPane(root);

        root.add(new StoryTheme.TitleBar(StoryData.TITLES[part - 1], part), BorderLayout.NORTH);
        root.add(new StoryTheme.TextBox(textArea, part), BorderLayout.CENTER);

        quoteLabel.setFont(StoryTheme.QUOTE_FONT);
        quoteLabel.setForeground(StoryTheme.getAccent(part));
        quoteLabel.setPreferredSize(new Dimension(620, 88));

        actionButton.setPreferredSize(new Dimension(220, 58));
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnRow.setOpaque(false);
        btnRow.add(actionButton);

        JPanel south = new JPanel(new BorderLayout(0, 8));
        south.setOpaque(false);
        south.add(quoteLabel, BorderLayout.NORTH);
        south.add(btnRow, BorderLayout.SOUTH);
        root.add(south, BorderLayout.SOUTH);

        actionButton.addActionListener(e -> {
            if (!done) finishTyping();
            else dispose();
        });

        // typewriter: 2 characters every 20 ms
        timer = new Timer(20, e -> {
            index = Math.min(index + 2, fullText.length());
            textArea.setText(fullText.substring(0, index));
            textArea.setCaretPosition(textArea.getDocument().getLength());
            if (index >= fullText.length()) finishTyping();
        });

        setSize(720, 650);
        setLocationRelativeTo(owner);
    }

    private void finishTyping() {
        timer.stop();
        textArea.setText(fullText);
        textArea.setCaretPosition(0);
        quoteLabel.setText(
            "<html><div style='text-align:center; width:460px; padding:0 10px;'>"
            + quote
            + "</div></html>"
        );
        actionButton.setText("Continue");
        done = true;
    }

    /** Shows the story for the given part (1 to 4) and waits until it's closed. */
    public static void show(Window owner, int part) {
        StoryDialog d = new StoryDialog(owner, part);
        d.timer.start();
        d.setVisible(true); // blocks here
    }
}
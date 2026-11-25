import java.awt.*;
import javax.swing.*;

/**
 * Level 5 story + ending choice.
 * Usage:
 *     EndingDialog.Result r = EndingDialog.show(parentFrame);
 *     if (r == EndingDialog.Result.PLAY_AGAIN) { ... } else { ... }
 */
public class EndingDialog extends JDialog {

    public enum Result { BACK_TO_MENU, PLAY_AGAIN }

    private final JTextArea textArea = new JTextArea();
    private final CardLayout cards = new CardLayout();
    private final JPanel bottom = new JPanel(cards);

    private Timer timer;
    private String fullText = "";
    private int index = 0;
    private Runnable whenDone;
    private Result result = Result.BACK_TO_MENU;

    private EndingDialog(Window owner) {
        super(owner, StoryData.PART5_TITLE, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        JPanel root = new StoryTheme.BackgroundPanel(new BorderLayout(0, 14));
        setContentPane(root);

        root.add(new StoryTheme.TitleBar(StoryData.PART5_TITLE), BorderLayout.NORTH);
        root.add(new StoryTheme.TextBox(textArea), BorderLayout.CENTER);

        bottom.setOpaque(false);

        // card 1: skip
        StoryTheme.PixelButton skipBtn = new StoryTheme.PixelButton("Skip", false, 22);
        skipBtn.setPreferredSize(new Dimension(220, 58));
        skipBtn.addActionListener(e -> completeTyping());
        JPanel skipPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        skipPanel.setOpaque(false);
        skipPanel.add(skipBtn);

        // card 2: the A / B choice
        JLabel prompt = new JLabel(StoryData.CHOICE_PROMPT, SwingConstants.CENTER);
        prompt.setFont(StoryTheme.QUOTE_FONT);
        prompt.setForeground(StoryTheme.GOLD);

        StoryTheme.PixelButton a = new StoryTheme.PixelButton(StoryData.CHOICE_A, false, 14);
        StoryTheme.PixelButton b = new StoryTheme.PixelButton(StoryData.CHOICE_B, false, 14);
        a.addActionListener(e -> type(StoryData.ENDING_A.strip(), () -> cards.show(bottom, "after")));
        b.addActionListener(e -> type(StoryData.ENDING_B.strip(), () -> cards.show(bottom, "after")));
        JPanel choiceBtns = new JPanel(new GridLayout(1, 2, 14, 0));
        choiceBtns.setOpaque(false);
        choiceBtns.setPreferredSize(new Dimension(100, 64));
        choiceBtns.add(a);
        choiceBtns.add(b);

        JPanel choicePanel = new JPanel(new BorderLayout(0, 8));
        choicePanel.setOpaque(false);
        choicePanel.add(prompt, BorderLayout.NORTH);
        choicePanel.add(choiceBtns, BorderLayout.CENTER);

        // card 3: after the ending
        StoryTheme.PixelButton menuBtn = new StoryTheme.PixelButton("Back to Menu", false, 20);
        StoryTheme.PixelButton againBtn = new StoryTheme.PixelButton("Play Again", true, 20);
        menuBtn.setPreferredSize(new Dimension(240, 58));
        againBtn.setPreferredSize(new Dimension(240, 58));
        menuBtn.addActionListener(e -> { result = Result.BACK_TO_MENU; dispose(); });
        againBtn.addActionListener(e -> { result = Result.PLAY_AGAIN; dispose(); });
        JPanel afterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        afterPanel.setOpaque(false);
        afterPanel.add(menuBtn);
        afterPanel.add(againBtn);

        bottom.add(skipPanel, "skip");
        bottom.add(choicePanel, "choices");
        bottom.add(afterPanel, "after");
        root.add(bottom, BorderLayout.SOUTH);

        setSize(720, 700);
        setLocationRelativeTo(owner);
    }

    /** Typewriter effect; calls onFinish when all text is displayed. */
    private void type(String text, Runnable onFinish) {
        if (timer != null) timer.stop();
        fullText = text;
        index = 0;
        whenDone = onFinish;
        textArea.setText("");
        cards.show(bottom, "skip");
        timer = new Timer(20, e -> {
            index = Math.min(index + 2, fullText.length());
            textArea.setText(fullText.substring(0, index));
            textArea.setCaretPosition(textArea.getDocument().getLength());
            if (index >= fullText.length()) completeTyping();
        });
        timer.start();
    }

    private void completeTyping() {
        if (timer != null) timer.stop();
        textArea.setText(fullText);
        textArea.setCaretPosition(textArea.getDocument().getLength());
        if (whenDone != null) {
            Runnable r = whenDone;
            whenDone = null; // make sure it only runs once
            r.run();
        }
    }

    /** Shows Part 5 + choice + ending. Blocks until the player picks Menu or Play Again. */
    public static Result show(Window owner) {
        EndingDialog d = new EndingDialog(owner);
        d.type(StoryData.PART5_REVEAL.strip(), () -> d.cards.show(d.bottom, "choices"));
        d.setVisible(true); // blocks here
        return d.result;
    }
}
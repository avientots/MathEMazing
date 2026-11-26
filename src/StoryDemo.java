import javax.swing.*;
import java.awt.*;

/** Test launcher. Run this to preview every story pop-up without playing the maze. */
public class StoryDemo {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Story Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new GridLayout(0, 1, 5, 5));

            for (int i = 1; i <= 4; i++) {
                final int part = i;
                JButton btn = new JButton("Show Part " + part);
                btn.addActionListener(e -> StoryDialog.show(frame, part));
                frame.add(btn);
            }

            JButton ending = new JButton("Show Part 5 + Ending");
            ending.addActionListener(e -> {
                EndingDialog.Result r = EndingDialog.show(frame);
                JOptionPane.showMessageDialog(frame, "Player chose: " + r);
            });
            frame.add(ending);

            frame.setSize(300, 280);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class Board3 extends JPanel implements ActionListener {

    private Timer timer;
    private Map3 m;
    private Player p;
    private Mummy mummy;

    private boolean win = false;
    private boolean upPressed, downPressed, leftPressed, rightPressed, shiftPressed;
    private Random rand = new Random();
    private Font font = new Font("SansSerif", Font.BOLD, 42);

    public Board3() {
        m = new Map3();
        p = new Player();
        mummy = new Mummy();

        setPreferredSize(new Dimension(m.getWidth() * 32, m.getHeight() * 32));
        setBackground(new Color(20, 35, 20));
        addKeyListener(new Al());
        setFocusable(true);

        timer = new Timer(16, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!win) {
            handleContinuousMovement();
            p.update();
            mummy.update();

            if (!mummy.isDefeated() &&
                Math.abs(p.getPixelX() - mummy.getPixelX()) < 18 &&
                Math.abs(p.getPixelY() - mummy.getPixelY()) < 18) {
                askRandomMathQuestion();
            }

            if (!p.isMoving() && mummy.isDefeated() && m.getMap(p.getTix(), p.getTiy()).equals("f")) {
                win = true;
            }
        }
        repaint();
    }

    private void handleContinuousMovement() {
        if (p.isMoving()) return;

        int dx = 0, dy = 0;
        if (upPressed) dy = -1;
        else if (downPressed) dy = 1;
        else if (leftPressed) dx = -1;
        else if (rightPressed) dx = 1;

        if (dx != 0 || dy != 0) {
            int nextX = p.getTix() + dx;
            int nextY = p.getTiy() + dy;
            if (!m.getMap(nextX, nextY).equals("w")) {
                p.move(dx, dy, shiftPressed);
            }
        }
    }

    private void askRandomMathQuestion() {
        timer.stop();
        upPressed = downPressed = leftPressed = rightPressed = shiftPressed = false;

        int num1 = rand.nextInt(15) + 1;
        int num2 = rand.nextInt(15) + 1;
        int op = rand.nextInt(3);

        String question;
        int correctAnswer;

        if (op == 0) {
            question = num1 + " + " + num2;
            correctAnswer = num1 + num2;
        } else if (op == 1) {
            question = num1 + " - " + num2;
            correctAnswer = num1 - num2;
        } else {
            num1 = rand.nextInt(10) + 1;
            num2 = rand.nextInt(10) + 1;
            question = num1 + " x " + num2;
            correctAnswer = num1 * num2;
        }

        String response = JOptionPane.showInputDialog(
            this,
            "The Skeleton King blocks the crown!\nSolve to pass: What is " + question + "?",
            "Skeleton Guardian Challenge",
            JOptionPane.QUESTION_MESSAGE
        );

        boolean solved = false;
        if (response != null) {
            try {
                if (Integer.parseInt(response.trim()) == correctAnswer) {
                    solved = true;
                }
            } catch (NumberFormatException ignored) {}
        }

        if (solved) {
            JOptionPane.showMessageDialog(this, "Correct! The Skeleton steps aside.");
            mummy.setDefeated(true);
        } else {
            JOptionPane.showMessageDialog(this, "Wrong! The answer was " + correctAnswer + ". You are pushed back!");
            p.pushBack();
        }

        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        if (!win) {
            for (int y = 0; y < m.getHeight(); y++) {
                for (int x = 0; x < m.getWidth(); x++) {
                    String tile = m.getMap(x, y);
                    if (tile.equals("w")) {
                        g2d.drawImage(m.getWall(), x * 32, y * 32, null);
                    } else {
                        g2d.drawImage(m.getGrass(), x * 32, y * 32, null);
                        if (tile.equals("f") && x == 15 && y == 9) {
                            g2d.drawImage(m.getFinish(), x * 32, y * 32, null);
                        }
                    }
                }
            }

            if (!mummy.isDefeated() && mummy.getFrame() != null) {
                int hoverOffset = (int) (Math.sin(System.currentTimeMillis() / 150.0) * 3);
                g2d.drawImage(mummy.getFrame(), mummy.getPixelX(), mummy.getPixelY() + hoverOffset, null);
            }

            if (p.getPlayerFrame() != null) {
                g2d.drawImage(p.getPlayerFrame(), p.getPixelX(), p.getPixelY(), null);
            }
        } else {
            g2d.setColor(Color.ORANGE);
            g2d.setFont(font);
            g2d.drawString("Winner! You claimed the Antique!", (getWidth() / 2) - 310, getHeight() / 2);
        }
    }

    public class Al extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) upPressed = true;
            else if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) downPressed = true;
            else if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) leftPressed = true;
            else if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) rightPressed = true;
            else if (key == KeyEvent.VK_SHIFT) shiftPressed = true;
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) upPressed = false;
            else if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) downPressed = false;
            else if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) leftPressed = false;
            else if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) rightPressed = false;
            else if (key == KeyEvent.VK_SHIFT) shiftPressed = false;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Skeleton Math Maze");
        Board3 board = new Board3();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(board);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
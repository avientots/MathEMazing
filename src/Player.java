
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Player {

    private int tileX = 15;
    private int tileY = 0;
    private int prevX = 15;
    private int prevY = 0;

    // Smooth visual pixel positions
    private double pixelX = 15 * 32;
    private double pixelY = 0;

    private BufferedImage spriteSheet;

    // Direction: 0 Down, 1 Up, 2 Left, 3 Right
    private int direction = 0;

    private int currentFrame = 0;
    private int animCounter = 0;

    private boolean isMoving = false;
    private boolean isRunning = false;
    private boolean isAttacking = false;

    private static final int FRAME_SIZE = 32;

    // Running animation: {row, starting column, frame count}
    private static final int[][] RUN_MAP = {
        {4, 0, 4}, // Down
        {4, 4, 4}, // Up
        {5, 0, 4}, // Left
        {5, 4, 4}  // Right
    };

    // Sword attack animation
    private static final int ATTACK_ROW = 6;
    private static final int ATTACK_FRAMES = 8;

    public Player() {
        try {
            spriteSheet = ImageIO.read(new File("assets/player.png"));
        } catch (IOException e) {
            System.err.println(
                "Error loading assets/player.png: " + e.getMessage()
            );
        }
    }

    public void move(int dx, int dy, boolean running) {

        if (isMoving || isAttacking) return;

        prevX = tileX;
        prevY = tileY;

        tileX += dx;
        tileY += dy;

        if (dx < 0) direction = 2;
        else if (dx > 0) direction = 3;
        else if (dy > 0) direction = 0;
        else if (dy < 0) direction = 1;

        isMoving = true;
        isRunning = running;

        currentFrame = 0;
        animCounter = 0;
    }

    // Start sword attack
    public void attack() {

        if (isMoving || isAttacking) return;

        isAttacking = true;
        isRunning = false;

        currentFrame = 0;
        animCounter = 0;
    }

    public void pushBack() {

        tileX = prevX;
        tileY = prevY;

        pixelX = tileX * 32;
        pixelY = tileY * 32;

        isMoving = false;
        isRunning = false;
        isAttacking = false;

        currentFrame = 0;
        animCounter = 0;
    }

    public void setPosition(int x, int y) {
        this.tileX = x;
        this.tileY = y;
        this.prevX = x;
        this.prevY = y;
        this.pixelX = x * 32;
        this.pixelY = y * 32;
        this.isMoving = false;
    }

    public void update() {

        // Handle sword attack animation first
        if (isAttacking) {

            animCounter++;

            if (animCounter >= 3) {

                currentFrame++;
                animCounter = 0;

                if (currentFrame >= ATTACK_FRAMES) {

                    currentFrame = 0;
                    isAttacking = false;
                }
            }

            return;
        }

        double targetX = tileX * 32;
        double targetY = tileY * 32;

        double moveSpeed = isRunning ? 6.0 : 3.5;

        if (pixelX < targetX) {
            pixelX = Math.min(targetX, pixelX + moveSpeed);
        } else if (pixelX > targetX) {
            pixelX = Math.max(targetX, pixelX - moveSpeed);
        }

        if (pixelY < targetY) {
            pixelY = Math.min(targetY, pixelY + moveSpeed);
        } else if (pixelY > targetY) {
            pixelY = Math.max(targetY, pixelY - moveSpeed);
        }

        if (pixelX == targetX && pixelY == targetY) {

            isMoving = false;
            isRunning = false;

            currentFrame = 0;
            animCounter = 0;

        } else {

            animCounter++;

            int maxFrames = isRunning ? 4 : 8;

            if (animCounter >= 3) {

                currentFrame = (currentFrame + 1) % maxFrames;
                animCounter = 0;
            }
        }
    }

    public BufferedImage getPlayerFrame() {

        if (spriteSheet == null) return null;

        // Sword attack animation
        if (isAttacking) {

            return spriteSheet.getSubimage(
                currentFrame * FRAME_SIZE,
                ATTACK_ROW * FRAME_SIZE,
                FRAME_SIZE,
                FRAME_SIZE
            );
        }

        // Running animation
        if (isRunning && isMoving) {

            int row = RUN_MAP[direction][0];
            int colOffset = RUN_MAP[direction][1];

            return spriteSheet.getSubimage(
                (colOffset + currentFrame) * FRAME_SIZE,
                row * FRAME_SIZE,
                FRAME_SIZE,
                FRAME_SIZE
            );
        }

        // Normal walking animation
        return spriteSheet.getSubimage(
            currentFrame * FRAME_SIZE,
            direction * FRAME_SIZE,
            FRAME_SIZE,
            FRAME_SIZE
        );
    }

    public int getTix() {
        return tileX;
    }

    public int getTiy() {
        return tileY;
    }

    public int getPixelX() {
        return (int) Math.round(pixelX);
    }

    public int getPixelY() {
        return (int) Math.round(pixelY);
    }

    public boolean isMoving() {
        return isMoving;
    }

    public boolean isAttacking() {
        return isAttacking;
    }
}

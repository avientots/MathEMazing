import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Player4 {

    private int tileX = 15;
    private int tileY = 0;
    private int prevX = 15;
    private int prevY = 0;

    // Smooth visual pixel positions
    private double pixelX = 15 * 32;
    private double pixelY = 0 * 32;

    private BufferedImage spriteSheet;
    private int direction = 0; // 0: Down, 1: Up, 2: Left, 3: Right
    private int currentFrame = 0;
    private int animCounter = 0;
    private boolean isMoving = false;
    private boolean isRunning = false;

    private static final int FRAME_SIZE = 32;
    private static final int[][] RUN_MAP = {
        {4, 0, 4}, // Down
        {5, 0, 4}, // Up
        {4, 4, 4}, // Left
        {5, 4, 4}  // Right
    };

    public Player4() {
        try {
            spriteSheet = ImageIO.read(new File("assets/player4.png"));
        } catch (IOException e) {
            System.err.println("Error loading assets/player4.png: " + e.getMessage());
        }
    }

    public void move(int dx, int dy, boolean running) {
        if (isMoving) return;

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
    }

    public void pushBack() {
        tileX = prevX;
        tileY = prevY;
        pixelX = tileX * 32;
        pixelY = tileY * 32;
        isMoving = false;
    }

    public void update() {
        double targetX = tileX * 32;
        double targetY = tileY * 32;
        double moveSpeed = isRunning ? 6.0 : 3.5;

        if (pixelX < targetX) pixelX = Math.min(targetX, pixelX + moveSpeed);
        else if (pixelX > targetX) pixelX = Math.max(targetX, pixelX - moveSpeed);

        if (pixelY < targetY) pixelY = Math.min(targetY, pixelY + moveSpeed);
        else if (pixelY > targetY) pixelY = Math.max(targetY, pixelY - moveSpeed);

        if (pixelX == targetX && pixelY == targetY) {
            isMoving = false;
            currentFrame = 0;
        } else {
            animCounter++;
            int maxFrames = isRunning ? 4 : 8;
            int speedDiv = 3;
            if (animCounter >= speedDiv) {
                currentFrame = (currentFrame + 1) % maxFrames;
                animCounter = 0;
            }
        }
    }

    public BufferedImage getPlayerFrame() {
        if (spriteSheet == null) return null;
        int row = isRunning ? RUN_MAP[direction][0] : direction;
        int colOffset = isRunning ? RUN_MAP[direction][1] : 0;
        return spriteSheet.getSubimage((colOffset + currentFrame) * FRAME_SIZE, row * FRAME_SIZE, FRAME_SIZE, FRAME_SIZE);
    }

    public int getTix() { return tileX; }
    public int getTiy() { return tileY; }
    public int getPixelX() { return (int) Math.round(pixelX); }
    public int getPixelY() { return (int) Math.round(pixelY); }
    public boolean isMoving() { return isMoving; }
}
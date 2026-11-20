import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Snake4 {

    private int tileX = 14;
    private int tileY = 8;
    private double pixelX = 14 * 32;
    private double pixelY = 8 * 32;
    private boolean defeated = false;

    // Patrol waypoints around the center chamber
    private final int[][] patrolPath = {
        {14, 8}, {16, 8}, {16, 10}, {14, 10}
    };
    private int pathIndex = 0;
    private int waitTimer = 0;

    private BufferedImage spriteSheet;
    private int direction = 0; // 0: Down, 1: Up, 2: Left, 3: Right
    private int currentFrame = 0;
    private int animTimer = 0;

    public Snake4() {
        try {
            spriteSheet = ImageIO.read(new File("assets/Snake4.png"));
        } catch (IOException e) {
            System.err.println("Error loading assets/Snake4.png: " + e.getMessage());
        }
    }

    public void update() {
        if (defeated) return;

        double targetX = tileX * 32;
        double targetY = tileY * 32;
        double speed = 1.5; // Smooth hovering glide speed

        boolean moving = false;

        if (pixelX < targetX) {
            pixelX = Math.min(targetX, pixelX + speed);
            direction = 3; // Right
            moving = true;
        } else if (pixelX > targetX) {
            pixelX = Math.max(targetX, pixelX - speed);
            direction = 2; // Left
            moving = true;
        } else if (pixelY < targetY) {
            pixelY = Math.min(targetY, pixelY + speed);
            direction = 0; // Down
            moving = true;
        } else if (pixelY > targetY) {
            pixelY = Math.max(targetY, pixelY - speed);
            direction = 1; // Up
            moving = true;
        }

        // Once arrived at waypoint, pause briefly then pick next corner
        if (!moving) {
            direction = 0; // Face forward while hovering in place
            waitTimer++;
            if (waitTimer >= 25) {
                waitTimer = 0;
                pathIndex = (pathIndex + 1) % patrolPath.length;
                tileX = patrolPath[pathIndex][0];
                tileY = patrolPath[pathIndex][1];
            }
        }

        // Cycle animation frames smoothly
        animTimer++;
        if (animTimer >= 5) {
            currentFrame = (currentFrame + 1) % 4;
            animTimer = 0;
        }
    }

    public BufferedImage getFrame() {
        if (spriteSheet == null) return null;
        return spriteSheet.getSubimage(currentFrame * 32, direction * 32, 32, 32);
    }

    public int getX() { return tileX; }
    public int getY() { return tileY; }
    public int getPixelX() { return (int) Math.round(pixelX); }
    public int getPixelY() { return (int) Math.round(pixelY); }
    public boolean isDefeated() { return defeated; }
    public void setDefeated(boolean defeated) { this.defeated = defeated; }
}
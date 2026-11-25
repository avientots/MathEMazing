import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class MossyRock3 {

   private int tileX = 14;
    private int tileY = 8;
    private double pixelX = 14 * 32;
    private double pixelY = 8 * 32;
    private boolean defeated = false;

    // Patrol waypoints strictly inside the 5x5 center chamber (x: 14..16, y: 8..10)
    private final int[][] patrolPath = {
        {14, 8}, {16, 8}, {16, 10}, {14, 10}
    };
    
    private int pathIndex = 0;
    private int waitTimer = 0;

    private BufferedImage spriteSheet;
    private int direction = 0;
    private int currentFrame = 0;
    private int animTimer = 0;

    public MossyRock3() {
        try {
            spriteSheet = ImageIO.read(new File("assets/mossyrock.png"));
        } catch (IOException e) {
            System.err.println("Error loading assets/mossyrock.png: " + e.getMessage());
        }
    }

    public void update() {
        if (defeated) return;

        double targetX = tileX * 32;
        double targetY = tileY * 32;
        double speed = 1.5;

        boolean moving = false;

        if (pixelX < targetX) {
            pixelX = Math.min(targetX, pixelX + speed);
            direction = 3;
            moving = true;
        } else if (pixelX > targetX) {
            pixelX = Math.max(targetX, pixelX - speed);
            direction = 2;
            moving = true;
        } else if (pixelY < targetY) {
            pixelY = Math.min(targetY, pixelY + speed);
            direction = 0;
            moving = true;
        } else if (pixelY > targetY) {
            pixelY = Math.max(targetY, pixelY - speed);
            direction = 1;
            moving = true;
        }

        if (!moving) {
            direction = 0;
            waitTimer++;
            if (waitTimer >= 25) {
                waitTimer = 0;
                pathIndex = (pathIndex + 1) % patrolPath.length;
                tileX = patrolPath[pathIndex][0];
                tileY = patrolPath[pathIndex][1];
            }
        }

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
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;

public class Map3 {

    private ArrayList<String> mapRows = new ArrayList<>();
    private Image grass, wall, finish;

    public Map3() {
        try {
            // Auto-crop transparent padding from grass and wall so tiles connect seamlessly
            BufferedImage rawGrass = autoCrop(ImageIO.read(new File("assets/grass3.png")));
            BufferedImage rawWall = autoCrop(ImageIO.read(new File("assets/wall3.jpg")));
            BufferedImage rawFinish = autoCrop(ImageIO.read(new File("assets/Multiplication Antique.png")));

            grass = rawGrass.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            wall = rawWall.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            finish = rawFinish.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            System.err.println("Error loading map images: " + e.getMessage());
        }

        loadMapFile("assets/maze3.txt");
    }

    // Removes empty transparent edges around an image so tiles fit flush together
    private BufferedImage autoCrop(BufferedImage src) {
        int minX = src.getWidth(), minY = src.getHeight();
        int maxX = -1, maxY = -1;

        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int alpha = (src.getRGB(x, y) >> 24) & 0xff;
                if (alpha > 15) {
                    if (x < minX) minX = x;
                    if (x > maxX) maxX = x;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                }
            }
        }
        if (maxX < minX || maxY < minY) return src;
        return src.getSubimage(minX, minY, (maxX - minX + 1), (maxY - minY + 1));
    }

    private void loadMapFile(String path) {
        try (Scanner m = new Scanner(new File(path))) {
            while (m.hasNextLine()) {
                String line = m.nextLine().trim();
                if (!line.isEmpty()) {
                    mapRows.add(line);
                }
                
            }
        } catch (Exception e) {
            System.err.println("Error loading maze3.txt: " + e.getMessage());
        }
    }

    public int getWidth() { return mapRows.isEmpty() ? 0 : mapRows.get(0).length(); }
    public int getHeight() { return mapRows.size(); }

    public Image getGrass() { return grass; }
    public Image getWall() { return wall; }
    public Image getFinish() { return finish; }

    public String getMap(int x, int y) {
        if (y < 0 || y >= getHeight() || x < 0 || x >= getWidth()) {
            return "w";
        }
        return mapRows.get(y).substring(x, x + 1);
    }
}
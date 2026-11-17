
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;

public class Map4 {

    private ArrayList<String> mapRows = new ArrayList<>();

    private Image grass, wall, finish;

    public Map4() {

        try {
            // Load and crop map images
            BufferedImage rawGrass = loadImage("assets/grass4.jpg");
            BufferedImage rawWall = loadImage("assets/wall4.png");
            BufferedImage rawFinish = loadImage("assets/finish4.png");

            // Scale images to 32x32 tiles
            grass = autoCrop(rawGrass).getScaledInstance(
                32, 32, Image.SCALE_SMOOTH
            );

            wall = autoCrop(rawWall).getScaledInstance(
                32, 32, Image.SCALE_SMOOTH
            );

            finish = autoCrop(rawFinish).getScaledInstance(
                32, 32, Image.SCALE_SMOOTH
            );

            System.out.println("Map images loaded successfully!");

        } catch (IOException e) {
            System.err.println("Error loading map images: " + e.getMessage());
        }

        // Load maze text file
        loadMapFile("assets/maze4.txt");
    }

    // Load image and check if file exists
    private BufferedImage loadImage(String path) throws IOException {

        File file = new File(path);

        if (!file.exists()) {
            throw new FileNotFoundException(
                "Image not found: " + file.getAbsolutePath()
            );
        }

        BufferedImage image = ImageIO.read(file);

        if (image == null) {
            throw new IOException(
                "Unsupported image format or unreadable image: " + path
            );
        }

        System.out.println("Loaded image: " + path);

        return image;
    }

    // Remove transparent padding around images
    private BufferedImage autoCrop(BufferedImage src) {

        if (src == null) {
            throw new IllegalArgumentException(
                "Cannot crop a null image."
            );
        }

        int minX = src.getWidth();
        int minY = src.getHeight();

        int maxX = -1;
        int maxY = -1;

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

        // Return original image if no visible pixels are found
        if (maxX < minX || maxY < minY) {
            return src;
        }

        return src.getSubimage(
            minX,
            minY,
            maxX - minX + 1,
            maxY - minY + 1
        );
    }

    // Load maze from text file
    private void loadMapFile(String path) {

        File file = new File(path);

        if (!file.exists()) {
            System.err.println(
                "Maze file not found: " + file.getAbsolutePath()
            );
            return;
        }

        try (Scanner m = new Scanner(file)) {

            while (m.hasNextLine()) {

                String line = m.nextLine().trim();

                if (!line.isEmpty()) {
                    mapRows.add(line);
                }
            }

            if (mapRows.isEmpty()) {
                System.err.println("Maze file is empty!");
            } else {
                System.out.println("Maze loaded successfully!");
                System.out.println(
                    "Map Size: " + getWidth() + " x " + getHeight()
                );
            }

        } catch (IOException e) {
            System.err.println(
                "Error loading maze: " + e.getMessage()
            );
        }
    }

    public int getWidth() {
        return mapRows.isEmpty() ? 0 : mapRows.get(0).length();
    }

    public int getHeight() {
        return mapRows.size();
    }

    public Image getGrass() {
        return grass;
    }

    public Image getWall() {
        return wall;
    }

    public Image getFinish() {
        return finish;
    }

    public String getMap(int x, int y) {

        if (y < 0 || y >= getHeight() ||
            x < 0 || x >= mapRows.get(y).length()) {

            return "w";
        }

        return String.valueOf(mapRows.get(y).charAt(x));
    }
}

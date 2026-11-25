import java.awt.*;

/**
 * Shared look for the in-game screen (behind the maze and the score box).
 * Each level has its own color palette. Use the same level number everywhere
 * if you want one look for the whole game.
 *
 * Palette order: { base, tileLight, tileDark, accent }
 */
public class GameTheme {

    public static final Color CREAM = new Color(244, 235, 208);

    private static final Color[][] PALETTES = {
        // Level 1: green + gold (village)
        { new Color(11, 26, 16), new Color(29, 61, 34), new Color(22, 48, 27), new Color(242, 182, 50) },
        // Level 2: deep sea teal (maps)
        { new Color(8, 24, 38), new Color(20, 56, 84), new Color(14, 40, 62), new Color(90, 200, 230) },
        // Level 3: purple + pink (song)
        { new Color(28, 12, 38), new Color(58, 28, 80), new Color(42, 20, 60), new Color(240, 120, 200) },
        // Level 4: ink brown + amber (the writer)
        { new Color(34, 20, 10), new Color(74, 46, 22), new Color(54, 32, 16), new Color(255, 170, 60) },
        // Level 5: midnight blue + gold (the last page)
        { new Color(10, 10, 28), new Color(30, 30, 70), new Color(20, 20, 50), new Color(255, 215, 100) }
    };

    private static Color[] palette(int level) {
        int idx = Math.max(0, Math.min(PALETTES.length - 1, level - 1));
        return PALETTES[idx];
    }

    public static Color accent(int level) {
        return palette(level)[3];
    }

    /** Full-screen background: pixel tiles, twinkling sparkles, vignette and a pixel frame. */
    public static void drawBackground(Graphics2D g2d, int w, int h, int level) {
        Graphics2D g2 = (Graphics2D) g2d.create();
        Color[] pal = palette(level);
        Color accent = pal[3];

        g2.setColor(pal[0]);
        g2.fillRect(0, 0, w, h);

        int cell = 40;
        long t = System.currentTimeMillis();
        for (int cy = 0; cy * cell < h; cy++) {
            for (int cx = 0; cx * cell < w; cx++) {
                int hash = (cx * 73856093) ^ (cy * 19349663) ^ (level * 83492791);
                int v = Math.floorMod(hash, 11);
                int px = cx * cell, py = cy * cell;
                if (v == 0) {
                    g2.setColor(pal[1]);
                    g2.fillRect(px, py, cell, cell);
                } else if (v == 1) {
                    g2.setColor(pal[2]);
                    g2.fillRect(px, py, cell, cell);
                } else if (v == 2) {
                    // twinkling sparkle (the game repaints every frame, so it animates)
                    double tw = 0.5 + 0.5 * Math.sin(t / 450.0 + (hash & 255));
                    int alpha = (int) (40 + 150 * tw);
                    g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), alpha));
                    int sx = px + 8 + Math.floorMod(hash >> 8, 22);
                    int sy = py + 8 + Math.floorMod(hash >> 16, 22);
                    g2.fillRect(sx, sy, 4, 4);
                    g2.fillRect(sx - 4, sy, 4, 4);
                    g2.fillRect(sx + 4, sy, 4, 4);
                    g2.fillRect(sx, sy - 4, 4, 4);
                    g2.fillRect(sx, sy + 4, 4, 4);
                }
            }
        }

        // faint grid
        g2.setColor(new Color(255, 255, 255, 10));
        for (int x = 0; x < w; x += cell) g2.drawLine(x, 0, x, h);
        for (int y = 0; y < h; y += cell) g2.drawLine(0, y, w, y);

        // vignette
        float radius = Math.max(w, h) * 0.7f;
        g2.setPaint(new RadialGradientPaint(
            new Point(w / 2, h / 2), radius,
            new float[] {0.4f, 1f},
            new Color[] {new Color(0, 0, 0, 0), new Color(0, 0, 0, 190)}));
        g2.fillRect(0, 0, w, h);

        // pixel frame: accent line + corner "stairs"
        int in = 10, s = 8;
        g2.setColor(accent);
        g2.setStroke(new BasicStroke(3f));
        g2.drawRect(in, in, w - 2 * in - 1, h - 2 * in - 1);
        int[][] corners = {{in, in, 1, 1}, {w - in, in, -1, 1}, {in, h - in, 1, -1}, {w - in, h - in, -1, -1}};
        int[][] blocks = {{0, 0}, {1, 0}, {2, 0}, {0, 1}, {0, 2}};
        for (int[] c : corners) {
            for (int[] b : blocks) {
                int bx = c[2] > 0 ? c[0] + b[0] * s : c[0] - (b[0] + 1) * s;
                int by = c[3] > 0 ? c[1] + b[1] * s : c[1] - (b[1] + 1) * s;
                g2.fillRect(bx, by, s, s);
            }
        }
        g2.dispose();
    }

    /** Dark rounded panel with an accent border (used for the score box and the maze container). */
    public static void drawPanel(Graphics2D g2d, int x, int y, int w, int h, int level) {
        Graphics2D g2 = (Graphics2D) g2d.create();
        g2.setColor(new Color(0, 0, 0, 175));
        g2.fillRoundRect(x, y, w, h, 28, 28);
        g2.setColor(accent(level));
        g2.setStroke(new BasicStroke(3f));
        g2.drawRoundRect(x + 1, y + 1, w - 3, h - 3, 28, 28);
        g2.dispose();
    }

    /**
     * Draws one math-tile marker on the tile whose top-left corner is (x, y).
     * Level 1: yellow dot | 2: red X (treasure map) | 3: music note
     * Level 4: ink drop   | 5: glowing sparkle
     */
    public static void drawMarker(Graphics2D g2d, int level, int x, int y, int tileSize) {
        Graphics2D g2 = (Graphics2D) g2d.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = Math.max(8, tileSize / 2);
        int cx = x + tileSize / 2, cy = y + tileSize / 2;
        int left = cx - size / 2, top = cy - size / 2;
        float line = Math.max(2f, tileSize / 10f);

        switch (Math.max(1, Math.min(5, level))) {
            case 2 -> {
                // red X, like a treasure map
                g2.setColor(new Color(200, 30, 30));
                g2.setStroke(new BasicStroke(line + 1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(left, top, left + size, top + size);
                g2.drawLine(left + size, top, left, top + size);
            }
            case 3 -> {
                // music note
                int headW = size * 2 / 3, headH = size / 2;
                int hx = left, hy = top + size - headH;
                g2.setColor(new Color(255, 140, 210));
                g2.fillOval(hx, hy, headW, headH);
                g2.setStroke(new BasicStroke(line, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int stemX = hx + headW - 1;
                g2.drawLine(stemX, hy + headH / 2, stemX, top);
                g2.drawLine(stemX, top, stemX + size / 3, top + size / 3);
            }
            case 4 -> {
                // ink drop
                int d = size * 2 / 3;
                int ox = cx - d / 2, oy = top + size - d;
                g2.setColor(new Color(20, 30, 90));
                g2.fillOval(ox, oy, d, d);
                g2.fillPolygon(new int[] {cx, cx - d / 2 + 1, cx + d / 2 - 1},
                               new int[] {top, oy + d / 3, oy + d / 3}, 3);
                g2.setColor(new Color(255, 170, 60));
                g2.setStroke(new BasicStroke(Math.max(1.5f, line / 2f)));
                g2.drawOval(ox, oy, d, d);
            }
            case 5 -> {
                // glowing sparkle (pulses)
                double tw = 0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 300.0 + x * 0.1 + y * 0.07);
                int glow = (int) (size * (1.1 + 0.4 * tw));
                g2.setColor(new Color(255, 215, 100, (int) (40 + 60 * tw)));
                g2.fillOval(cx - glow / 2, cy - glow / 2, glow, glow);
                int b = Math.max(3, tileSize / 8);
                g2.setColor(new Color(255, 215, 100));
                g2.fillRect(cx - b / 2, cy - b / 2, b, b);
                g2.fillRect(cx - b / 2 - b, cy - b / 2, b, b);
                g2.fillRect(cx - b / 2 + b, cy - b / 2, b, b);
                g2.fillRect(cx - b / 2, cy - b / 2 - b, b, b);
                g2.fillRect(cx - b / 2, cy - b / 2 + b, b, b);
            }
            default -> {
                // Level 1: yellow dot
                g2.setColor(Color.YELLOW);
                g2.fillOval(left + size / 6, top + size / 6, size * 2 / 3, size * 2 / 3);
            }
        }
        g2.dispose();
    }
}
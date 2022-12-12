package juuxel.advent2022;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public final class Day12Renderer {
    private static final int SCALE = 5;
    public static void main(String[] args) throws Exception {
        var lines = Loader.lines(12).toList();
        int width = lines.get(0).length(), height = lines.size();
        BufferedImage image = new BufferedImage(width * SCALE, height * SCALE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.scale(SCALE, SCALE);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                float brightness = (lines.get(y).charAt(x) - 97) / 25f;
                int color = Color.HSBtoRGB(0f, 0f, brightness);
                g.setColor(new Color(color));
                g.fillRect(x, y, 1, 1);
            }
        }
        g.dispose();
        ImageIcon icon = new ImageIcon(image);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("day12");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setContentPane(new JLabel(icon));
            frame.setVisible(true);
            frame.pack();
        });
    }
}

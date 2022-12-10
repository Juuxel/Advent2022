package juuxel.advent2022;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.List;

public final class Day10Visual extends JComponent {
    private static final int SCALE = 10;
    private static final int HORIZONTAL_OFFSET = 100;

    private final List<Day10.Insn> instructions;
    private int cycle = 1;
    private int x = 1;
    private int row = 0;
    private final boolean[][] pixels = new boolean[6][40];
    private final BufferedImage image = new BufferedImage(HORIZONTAL_OFFSET + SCALE * 40, SCALE * 6, BufferedImage.TYPE_INT_ARGB);
    private final Timer timer = new Timer(100, e -> cycle());

    private Day10Visual(List<Day10.Insn> instructions) {
        this.instructions = instructions;
        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        timer.setInitialDelay(0);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
    }

    private void render() {
        Graphics g = image.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());

        g.setColor(Color.GREEN);
        g.drawString("Cycle " + cycle, 5, 20);
        g.drawString(instructions.get(cycle - 1).toString(), 5, 40);

        int index = 1;
        top: for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 40; x++) {
                if (index++ > cycle) break top;
                g.setColor(pixels[y][x] ? Color.GREEN : Color.DARK_GRAY);
                g.fillRect(HORIZONTAL_OFFSET + SCALE * x, SCALE * y, SCALE, SCALE);
            }
        }

        g.dispose();
        repaint();
    }

    private void cycle() {
        if (cycle > 40 * 6) {
            timer.stop();
            return;
        }

        Day10.Insn insn = instructions.get(cycle - 1);

        // DURING phase
        // P2: Draw pixel
        int horizontalPos = (cycle - 1) % 40;
        pixels[row][horizontalPos] = Math.abs(horizontalPos - x) <= 1;
        render();

        // P2: Switch to the next row
        if (cycle % 40 == 0) {
            row++;
        }

        // AFTER phase

        // Apply current addx instruction
        if (insn instanceof Day10.AddX addx) {
            x += addx.value();
        }

        // Move to the next cycle
        cycle++;
    }

    public static void part2(String[] lines) {
        List<Day10.Insn> instructions = Day10.parse(lines);
        SwingUtilities.invokeLater(() -> {
            Day10Visual visual = new Day10Visual(instructions);
            JFrame frame = new JFrame("Day 10");
            frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    e.getWindow().setVisible(false);
                    e.getWindow().dispose();
                    visual.timer.stop();
                }
            });
            frame.setContentPane(visual);
            frame.setVisible(true);
            frame.pack();
            visual.timer.start();
        });
    }
}

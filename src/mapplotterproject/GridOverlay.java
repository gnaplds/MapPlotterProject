// GridOverlay.java
package mapplotterproject;

import java.awt.*;
import javax.swing.*;

public class GridOverlay extends JPanel {
    private int gridSize;

    public GridOverlay(int width, int height) {
        setSize(new Dimension(width, height));
        setOpaque(false);
        this.gridSize = 75; // Default grid size
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid((Graphics2D) g);
    }

    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        int width = getWidth();
        int height = getHeight();

        // Draw vertical lines and label them with x-coordinates
        for (int x = 0; x <= width; x += gridSize) {
            g2d.drawLine(x, 0, x, height);
            g2d.drawString(Integer.toString(x), x + 5, 15);
        }

        // Draw horizontal lines and label them with y-coordinates
        for (int y = 0; y <= height; y += gridSize) {
            g2d.drawLine(0, y, width, y);
            g2d.drawString(Integer.toString(y), 5, y - 5);
        }
    }

    // No @Override here; this is a new method
    public void setGridSize(int size) {
        this.gridSize = size;
        repaint();
    }

    @Override
    public void setSize(Dimension dimension) {
        super.setSize(dimension);
        repaint();
    }
}

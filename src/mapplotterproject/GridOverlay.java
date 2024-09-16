package mapplotterproject;

// GridOverlay.java
import interfaces.GridOverlayInterface;

import java.awt.*;
import javax.swing.*;

public class GridOverlay extends JPanel implements GridOverlayInterface {
    private int gridSize;

    public GridOverlay(int width, int height) {
        setSize(new Dimension(width, height));
        setOpaque(false);
        this.gridSize = 50; // default grid size
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid((Graphics2D) g);
    }

    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        int width = getWidth();
        int height = getHeight();
        for (int x = 0; x <= width; x += gridSize) {
            g2d.drawLine(x, 0, x, height);
            g2d.drawString(Integer.toString(x), x + 5, 15); // Label the x value
        }
        for (int y = 0; y <= height; y += gridSize) {
            g2d.drawLine(0, y, width, y);
            g2d.drawString(Integer.toString(y), 5, y - 5); // Label the y value
        }
    }

    @Override
    public void setGridSize(int size) {
        this.gridSize = size;
        repaint(); // Ensure grid is redrawn with new size
    }

    @Override
    public void setSize(Dimension dimension) {
        super.setSize(dimension);
        repaint(); // Ensure the panel is repainted when size changes
    }
}

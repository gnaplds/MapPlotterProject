package mapplotterproject;

import javax.swing.*;
import java.awt.*;

class GridOverlay extends JPanel {
    private int width;
    private int height;
    private int gridSize = 50; // Default grid size

    public GridOverlay(int width, int height) {
        this.width = width;
        this.height = height;
        setOpaque(false); // Make sure it doesn't obscure the map
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));

        // Draw vertical lines
        for (int x = 0; x <= width; x += gridSize) {
            g2d.drawLine(x, 0, x, height);
            g2d.drawString(Integer.toString(x), x + 5, 15); // Label the x value
        }

        // Draw horizontal lines
        for (int y = 0; y <= height; y += gridSize) {
            g2d.drawLine(0, y, width, y);
            g2d.drawString(Integer.toString(y), 5, y - 5); // Label the y value
        }
    }

    // Method to update the grid size
    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
        repaint(); // Redraw the grid with the new size
    }
}

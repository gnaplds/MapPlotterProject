// GridOverlay.java
package mapplotterproject;

import java.awt.*;
import javax.swing.*;

/**
 * GridOverlay class
 * This class creates a transparent overlay with a customizable grid.
 */
public class GridOverlay extends JPanel {
    // Grid size in pixels
    private int gridSize;

    /**
     * Constructor for GridOverlay
     * @param width Width of the overlay
     * @param height Height of the overlay
     */
    public GridOverlay(int width, int height) {
        initializeOverlay(width, height);
    }

    /**
     * Initialize the overlay properties
     * @param width Width of the overlay
     * @param height Height of the overlay
     */
    private void initializeOverlay(int width, int height) {
        setSize(new Dimension(width, height));
        setOpaque(false);
        this.gridSize = 75; // Default grid size
    }

    /**
     * Override paintComponent to draw the grid
     * @param g Graphics object
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid((Graphics2D) g);
    }

    /**
     * Draw the grid with coordinates
     * @param g2d Graphics2D object
     */
    private void drawGrid(Graphics2D g2d) {
        setupGraphics(g2d);
        int width = getWidth();
        int height = getHeight();

        drawVerticalLines(g2d, width, height);
        drawHorizontalLines(g2d, width, height);
    }

    /**
     * Set up graphics properties for drawing
     * @param g2d Graphics2D object
     */
    private void setupGraphics(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
    }

    /**
     * Draw vertical grid lines and x-coordinates
     * @param g2d Graphics2D object
     * @param width Width of the overlay
     * @param height Height of the overlay
     */
    private void drawVerticalLines(Graphics2D g2d, int width, int height) {
        for (int x = 0; x <= width; x += gridSize) {
            g2d.drawLine(x, 0, x, height);
            g2d.drawString(Integer.toString(x), x + 5, 15);
        }
    }

    /**
     * Draw horizontal grid lines and y-coordinates
     * @param g2d Graphics2D object
     * @param width Width of the overlay
     * @param height Height of the overlay
     */
    private void drawHorizontalLines(Graphics2D g2d, int width, int height) {
        for (int y = 0; y <= height; y += gridSize) {
            g2d.drawLine(0, y, width, y);
            g2d.drawString(Integer.toString(y), 5, y - 5);
        }
    }

    /**
     * Set the grid size and repaint
     * @param size New grid size in pixels
     */
    public void setGridSize(int size) {
        this.gridSize = size;
        repaint();
    }

    /**
     * Override setSize to ensure repaint on size change
     * @param dimension New dimension of the overlay
     */
    @Override
    public void setSize(Dimension dimension) {
        super.setSize(dimension);
        repaint();
    }
}

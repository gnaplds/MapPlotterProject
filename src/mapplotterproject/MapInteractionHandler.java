// MapInteractionHandler.java
package mapplotterproject;

import java.awt.*;
import java.awt.event.*;

public class MapInteractionHandler {
    // Class variables
    private double scale, minScale, maxScale;
    private int offsetX, offsetY;
    private Point dragStart;
    private final Component mapComponent;
    private int mouseX = -1, mouseY = -1;

    /**
     * Constructor for MapInteractionHandler
     * @param mapComponent The component to which this handler is attached
     * @param initialScale Initial zoom scale
     * @param minScale Minimum allowed zoom scale
     * @param maxScale Maximum allowed zoom scale
     */
    public MapInteractionHandler(Component mapComponent, double initialScale, double minScale, double maxScale) {
        this.mapComponent = mapComponent;
        this.scale = initialScale;
        this.minScale = minScale;
        this.maxScale = maxScale;
        this.offsetX = 0;
        this.offsetY = 0;

        setupMouseListeners();
    }

    /**
     * Sets up mouse listeners for zooming and dragging
     */
    private void setupMouseListeners() {
        mapComponent.addMouseWheelListener(this::handleMouseWheelEvent);
        mapComponent.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragStart = e.getPoint();
            }
        });
        mapComponent.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDrag(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                updateMousePosition(e);
            }
        });
    }

    /**
     * Handles mouse wheel events for zooming
     * @param e MouseWheelEvent
     */
    private void handleMouseWheelEvent(MouseWheelEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        double previousScale = scale;

        // Adjust scale based on wheel rotation
        if (e.getWheelRotation() < 0) {
            scale = Math.min(scale + 0.1, maxScale);
        } else {
            scale = Math.max(scale - 0.1, minScale);
        }

        // Adjust offsets to zoom towards cursor position
        adjustOffsetsForZoom(mouseX, mouseY, previousScale);

        mapComponent.revalidate();
        mapComponent.repaint();
    }

    /**
     * Adjusts offsets to ensure zoom centers on cursor
     * @param mouseX X-coordinate of mouse
     * @param mouseY Y-coordinate of mouse
     * @param previousScale Scale before zooming
     */
    private void adjustOffsetsForZoom(int mouseX, int mouseY, double previousScale) {
        offsetX = (int) (mouseX - (mouseX - offsetX) * (scale / previousScale));
        offsetY = (int) (mouseY - (mouseY - offsetY) * (scale / previousScale));
    }

    /**
     * Handles mouse dragging for panning
     * @param e MouseEvent
     */
    private void handleMouseDrag(MouseEvent e) {
        Point dragEnd = e.getPoint();
        offsetX += dragEnd.x - dragStart.x;
        offsetY += dragEnd.y - dragStart.y;
        dragStart = dragEnd;
        mapComponent.repaint();
    }

    /**
     * Updates stored mouse position
     * @param e MouseEvent
     */
    private void updateMousePosition(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        mapComponent.repaint();
    }

    /**
     * Gets the current mouse coordinates adjusted for scale and offset
     * @return Point representing adjusted mouse coordinates
     */
    public Point getMouseCoordinates() {
        if (mouseX == -1 || mouseY == -1) {
            return new Point(-1, -1);  // Invalid point if mouse is not on the map
        }

        int adjustedX = (int) ((mouseX - offsetX) / scale);
        int adjustedY = (int) ((mouseY - offsetY) / scale);

        return new Point(adjustedX, adjustedY);
    }

    // Getters and setters

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = Math.max(minScale, Math.min(scale, maxScale));
        mapComponent.repaint();
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
        mapComponent.repaint();
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
        mapComponent.repaint();
    }

    public double getMaxScale() {
        return maxScale;
    }

    public double getMinScale() {
        return minScale;
    }
}

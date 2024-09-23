// MapInteractionHandler.java
package mapplotterproject;

import java.awt.*;
import java.awt.event.*;

public class MapInteractionHandler {
    private double scale;
    private double minScale;
    private double maxScale;
    private int offsetX;
    private int offsetY;
    private Point dragStart;
    private final Component mapComponent;
    private int mouseX = -1;
    private int mouseY = -1;


    public MapInteractionHandler(Component mapComponent, double initialScale, double minScale, double maxScale) {
        this.mapComponent = mapComponent;
        this.scale = initialScale;
        this.minScale = minScale;
        this.maxScale = maxScale;
        this.offsetX = 0;
        this.offsetY = 0;

        setupMouseListeners();
    }

    private void setupMouseListeners() {
        // Mouse wheel listener for zooming
        mapComponent.addMouseWheelListener(this::handleMouseWheelEvent);

        // Mouse listener for dragging
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
                mouseX = e.getX();
                mouseY = e.getY();
                mapComponent.repaint();  // Repaint to update the overlay
            }
        });
    }

    private void handleMouseWheelEvent(MouseWheelEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        double previousScale = scale;

        // Zoom in or out
        if (e.getWheelRotation() < 0 && scale < maxScale) scale += 0.1;
        else if (e.getWheelRotation() > 0 && scale > minScale) scale -= 0.1;

        // Adjust the offsets to ensure the zoom centers on the cursor
        offsetX = (int) (mouseX - (mouseX - offsetX) * (scale / previousScale));
        offsetY = (int) (mouseY - (mouseY - offsetY) * (scale / previousScale));

        mapComponent.revalidate();
        mapComponent.repaint();
    }

    // Handle mouse dragging
    private void handleMouseDrag(MouseEvent e) {
        Point dragEnd = e.getPoint();
        offsetX += dragEnd.x - dragStart.x;
        offsetY += dragEnd.y - dragStart.y;
        dragStart = dragEnd;
        mapComponent.repaint();
    }

    public Point getMouseCoordinates() {
        if (mouseX == -1 || mouseY == -1) {
            return new Point(-1, -1);  // Return invalid point if mouse is not on the map
        }

        // Adjust the coordinates for scaling and offset
        int adjustedX = (int) ((mouseX - offsetX) / scale);
        int adjustedY = (int) ((mouseY - offsetY) / scale);

        return new Point(adjustedX, adjustedY);
    }

    // Getter for the current scale
    public double getScale() {
        return scale;
    }

    // Setter for the scale, useful for programmatic zoom
    public void setScale(double scale) {
        this.scale = Math.max(minScale, Math.min(scale, maxScale)); // Ensure scale is within bounds
        mapComponent.repaint();
    }

    // Getter for the current X offset
    public int getOffsetX() {
        return offsetX;
    }

    // Setter for the X offset
    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
        mapComponent.repaint();
    }

    // Getter for the current Y offset
    public int getOffsetY() {
        return offsetY;
    }

    // Setter for the Y offset
    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
        mapComponent.repaint();
    }

    // Getter for the maximum scale
    public double getMaxScale() {
        return maxScale;
    }

    // Getter for the minimum scale
    public double getMinScale() {
        return minScale;
    }

}

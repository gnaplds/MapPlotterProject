// MapPlotter.java
package mapplotterproject;

import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MapPlotter extends JPanel {
    // Class variables
    private BufferedImage mapImage;
    private List<Point> coordinates;
    private List<String> names, addresses, cities;
    private boolean showNames, showPoints, showGrid, showBoundaries, showCoordinates;
    private Point highlightedPoint;
    private GridOverlay gridOverlay;
    private MapInteractionHandler interactionHandler;
    private CityBoundaryManager cityBoundaryManager;

    // Constructor
    public MapPlotter() {
        initializeComponents();
        loadData();
        setupInteraction();
        setPreferredSize(new Dimension(mapImage.getWidth(), mapImage.getHeight()));
    }

    // Initialize components
    private void initializeComponents() {
        loadMapImage();
        initializeLists();
        cityBoundaryManager = new CityBoundaryManager();
        createGridOverlay();

        // Set initial visibility states
        showPoints = showGrid = showCoordinates = true;
        showNames = showBoundaries = false;
    }

    // Load map image
    private void loadMapImage() {
        try {
            mapImage = ImageIO.read(new File("src/resources/caviteMapCity.png"));
        } catch (IOException e) {
            System.out.println("Map image not found.");
        }
    }

    // Initialize lists
    private void initializeLists() {
        coordinates = new ArrayList<>();
        names = new ArrayList<>();
        addresses = new ArrayList<>();
        cities = new ArrayList<>();
    }

    // Load data from CSV
    public void loadData() {
        names.clear();
        addresses.clear();
        cities.clear();
        coordinates.clear();

        readCoordinatesFromCSV();

        repaint();
    }

    // Setup interaction handler
    private void setupInteraction() {
        interactionHandler = new MapInteractionHandler(this, 1.0, 0.5, 3.0);
    }

    // Create grid overlay
    private void createGridOverlay() {
        gridOverlay = new GridOverlay(mapImage.getWidth(), mapImage.getHeight());
    }

    // Read coordinates from CSV file
    private void readCoordinatesFromCSV() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/resources/addresses.csv"))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length != 3) continue;
                names.add(data[0]);
                cities.add(data[1]);
                addresses.add(data[2]);
                coordinates.add(getCoordinatesForCity(data[1]));
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV file.");
            e.printStackTrace();
        }
    }

    // Get coordinates for a city
    private Point getCoordinatesForCity(String cityName) {
        for (CityBoundary boundary : cityBoundaryManager.getCityBoundaries()) {
            if (boundary.getCityName().equalsIgnoreCase(cityName)) {
                return boundary.getNextPoint();
            }
        }
        return new Point(0, 0);
    }

    // Create list panel
    public JPanel createListPanel() {
        return new ListPanel(names, addresses, coordinates, cities, this, gridOverlay);
    }

    // Paint component
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        applyTransformations(g2d);
        drawMapComponents(g2d);
    }

    // Apply transformations to graphics
    private void applyTransformations(Graphics2D g2d) {
        g2d.translate(interactionHandler.getOffsetX(), interactionHandler.getOffsetY());
        g2d.scale(interactionHandler.getScale(), interactionHandler.getScale());
    }

    // Draw map components
    private void drawMapComponents(Graphics2D g2d) {
        if (mapImage != null) g2d.drawImage(mapImage, 0, 0, null);
        if (showGrid) gridOverlay.paintComponent(g2d);
        if (showBoundaries) cityBoundaryManager.drawBoundaries(g2d);
        if (showPoints) drawPlotPoints(g2d);
        if (showNames) drawNames(g2d);
        if (showCoordinates) drawCoordinates(g2d);
        if (highlightedPoint != null) drawHighlightedPoint(g2d);
    }

    // Draw plot points
    private void drawPlotPoints(Graphics g) {
        g.setColor(Color.RED);
        for (Point point : coordinates) {
            g.fillOval(point.x - 5, point.y - 5, 5, 5);
        }
    }

    // Draw names
    private void drawNames(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        for (int i = 0; i < coordinates.size(); i++) {
            Point point = coordinates.get(i);
            g.drawString(names.get(i), point.x + 10, point.y);
        }
    }

    // Draw coordinates
    private void drawCoordinates(Graphics g) {
        Point mouseCoords = interactionHandler.getMouseCoordinates();
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("X: " + mouseCoords.x + " Y: " + mouseCoords.y, mouseCoords.x + 10, mouseCoords.y - 10);
    }

    // Draw highlighted point
    private void drawHighlightedPoint(Graphics2D g2d) {
        g2d.setColor(Color.BLUE);
        g2d.fillOval(highlightedPoint.x - 5, highlightedPoint.y - 5, 5, 5);
    }

    // Highlight selected point
    public void highlightSelectedPoint(Point point) {
        this.highlightedPoint = point;
        repaint();
    }

    // Clear highlighted point
    public void clearHighlightedPoint() {
        this.highlightedPoint = null;
        repaint();
    }

    // Zoom to coordinate
    public void zoomToCoordinate(Point point) {
        final double minZoomScale = 2.0;
        final double startScale = interactionHandler.getScale();
        final double targetScale = Math.min(interactionHandler.getMaxScale(), minZoomScale);

        final int startX = interactionHandler.getOffsetX();
        final int startY = interactionHandler.getOffsetY();
        final int targetX = (int) (-point.x * targetScale + getWidth() / 2);
        final int targetY = (int) (-point.y * targetScale + getHeight() / 2);

        animateZoom(startScale, targetScale, startX, startY, targetX, targetY);
    }

    // Animate zoom
    private void animateZoom(double startScale, double targetScale, int startX, int startY, int targetX, int targetY) {
        final int animationSteps = 30;
        final double scaleStep = (targetScale - startScale) / animationSteps;
        final int xStep = (targetX - startX) / animationSteps;
        final int yStep = (targetY - startY) / animationSteps;

        Timer zoomTimer = new Timer(20, new ActionListener() {
            private int step = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (step >= animationSteps) {
                    ((Timer) e.getSource()).stop();
                    return;
                }

                interactionHandler.setScale(startScale + step * scaleStep);
                interactionHandler.setOffsetX(startX + step * xStep);
                interactionHandler.setOffsetY(startY + step * yStep);

                revalidate();
                repaint();
                step++;
            }
        });
        zoomTimer.start();
    }

    // Toggle visibility methods
    public void toggleNames() { showNames = !showNames; repaint(); }
    public void togglePoints() { showPoints = !showPoints; repaint(); }
    public void toggleGrid() { showGrid = !showGrid; repaint(); }
    public void toggleBoundaries() { showBoundaries = !showBoundaries; repaint(); }
    public void toggleCoordinates() { showCoordinates = !showCoordinates; repaint(); }

    // Getter methods for visibility states
    public boolean isNamesVisible() { return showNames; }
    public boolean isPointsVisible() { return showPoints; }
    public boolean isGridVisible() { return showGrid; }
    public boolean isBoundariesVisible() { return showBoundaries; }
    public boolean isCoordinatesVisible() { return showCoordinates; }
}

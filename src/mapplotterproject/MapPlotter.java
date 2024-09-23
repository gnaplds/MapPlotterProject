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
    private BufferedImage mapImage;
    private List<Point> coordinates;
    private List<String> names;
    private List<String> addresses;
    private List<String> cities;
    private boolean showNames = false;
    private boolean showPoints = true;
    private boolean showGrid = true;
    private boolean showBoundaries = true;
    private boolean showCoordinates = true;
    private Point highlightedPoint;
    private GridOverlay gridOverlay;
    private double scale;
    private MapInteractionHandler interactionHandler;
    private CityBoundaryManager cityBoundaryManager;

    public MapPlotter() {
        loadMapImage();
        initializeLists();
        cityBoundaryManager = new CityBoundaryManager();
        readCoordinatesFromCSV("resources/addresses.csv");
        createGridOverlay();
        interactionHandler = new MapInteractionHandler(this, 1.0, 0.5, 3.0);
        setPreferredSize(new Dimension(mapImage.getWidth(), mapImage.getHeight()));
    }

    public JPanel createListPanel() {
        return new ListPanel(names, addresses, coordinates, cities, this, gridOverlay);
    }

    private void loadMapImage() {
        try {
            mapImage = ImageIO.read(new File("resources/caviteMapCity.png"));
        } catch (IOException e) {
            System.out.println("Map image not found.");
        }
    }

    private void initializeLists() {
        coordinates = new ArrayList<>();
        names = new ArrayList<>();
        addresses = new ArrayList<>();
        cities = new ArrayList<>();
    }

    private Point getCoordinatesForCity(String cityName) {
        for (CityBoundary boundary : cityBoundaryManager.getCityBoundaries()) {
            if (boundary.getCityName().equalsIgnoreCase(cityName)) {
                return boundary.getNextPoint();
            }
        }
        return new Point(0, 0);
    }

    private void readCoordinatesFromCSV(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length != 3) continue;
                names.add(data[0]);
                addresses.add(data[2]);
                cities.add(data[1]);
                coordinates.add(getCoordinatesForCity(data[1]));
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV file.");
            e.printStackTrace();
        }
    }

    private void createGridOverlay() {
        gridOverlay = new GridOverlay(mapImage.getWidth(), mapImage.getHeight());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(interactionHandler.getOffsetX(), interactionHandler.getOffsetY());
        g2d.scale(interactionHandler.getScale(), interactionHandler.getScale());

        if (mapImage != null) {
            g2d.drawImage(mapImage, 0, 0, null);
        }

        if (showGrid) {
            gridOverlay.paintComponent(g);
        }

        if (showBoundaries) {
            cityBoundaryManager.drawBoundaries(g2d);
        }

        if (showPoints) {
            drawPlotPoints(g);
        }

        if (showNames) {
            drawNames(g);
        }

        if (showCoordinates) {
            drawCoordinates(g);
        }

        if (highlightedPoint != null) {
            g2d.setColor(Color.BLUE);
            g2d.fillOval(highlightedPoint.x - 5, highlightedPoint.y - 5, 10, 10);
        }
    }

    public void highlightSelectedPoint(Point point) {
        this.highlightedPoint = point;
        repaint();
    }

    public void clearHighlightedPoint() {
        this.highlightedPoint = null;
        repaint();
    }

    private void drawPlotPoints(Graphics g) {
        g.setColor(Color.RED);
        for (Point point : coordinates) {
            g.fillOval(point.x - 5, point.y - 5, 10, 10);
        }
    }

    private void drawNames(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        for (int i = 0; i < coordinates.size(); i++) {
            Point point = coordinates.get(i);
            String name = names.get(i);
            g.drawString(name, point.x + 10, point.y);
        }
    }

    private void drawCoordinates(Graphics g) {
        Point mouseCoords = interactionHandler.getMouseCoordinates();
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 14));

        // Draw the coordinates directly above the mouse position
        int textX = mouseCoords.x + 10; // Offset to the right
        int textY = mouseCoords.y - 10; // Offset above

        g.drawString("X: " + mouseCoords.x + " Y: " + mouseCoords.y, textX, textY);
    }

    @Override
    public Dimension getPreferredSize() {
        int newWidth = (int) (mapImage.getWidth() * scale);
        int newHeight = (int) (mapImage.getHeight() * scale);
        return new Dimension(newWidth, newHeight);
    }

    public void zoomToCoordinate(Point point) {
        final double minZoomScale = 2.0; // Set desired zoom level
        final double startScale = interactionHandler.getScale();
        final double targetScale = Math.min(interactionHandler.getMaxScale(), minZoomScale);

        final int startX = interactionHandler.getOffsetX();
        final int startY = interactionHandler.getOffsetY();
        final int targetX = (int) (-point.x * targetScale + getWidth() / 2);
        final int targetY = (int) (-point.y * targetScale + getHeight() / 2);

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

                // Update scale and offset
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

    public void toggleNames() {
        showNames = !showNames;
        repaint();
    }

    public void togglePoints() {
        showPoints = !showPoints;
        repaint();
    }

    public void toggleGrid() {
        showGrid = !showGrid;
        repaint();
    }

    public void toggleBoundaries() {
        showBoundaries = !showBoundaries;
        repaint();
    }

    public void toggleCoordinates() {
        showCoordinates = !showCoordinates;
        repaint();
    }

    public boolean isNamesVisible() {
        return showNames;
    }

    public boolean isPointsVisible() {
        return showPoints;
    }

    public boolean isGridVisible() {
        return showGrid;
    }

    public boolean isBoundariesVisible() {
        return showBoundaries;
    }

    public boolean isCoordinatesVisible() {
        return showCoordinates;
    }
}

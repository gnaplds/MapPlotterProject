// CityBoundary.java
package mapplotterproject;

import java.awt.*;

public class CityBoundary {
    private String cityName;
    private Polygon boundaryPolygon;
    private int offsetStep = 20;  // Distance between points
    private int nextPointIndex = 0; // To track the next point

    public CityBoundary(String cityName, Polygon boundaryPolygon) {
        this.cityName = cityName;
        this.boundaryPolygon = boundaryPolygon;
    }

    // Method to get the next point in a spreading grid pattern
    public Point getNextPoint() {
        // Get the bounds of the polygon
        Rectangle bounds = boundaryPolygon.getBounds();

        // Get the center of the polygon
        int centerX = bounds.x + bounds.width / 2;
        int centerY = bounds.y + bounds.height / 2;

        // Spread points in a grid pattern from the center
        int xOffset = (nextPointIndex % 5) * offsetStep;  // Spread horizontally every 5 points
        int yOffset = (nextPointIndex / 5) * offsetStep;  // Spread vertically after 5 points

        int x = centerX + xOffset;
        int y = centerY + yOffset;

        // Move to the next point in the sequence
        nextPointIndex++;

        // Ensure the point is within the boundary
        while (!boundaryPolygon.contains(x, y)) {
            xOffset = (nextPointIndex % 5) * offsetStep;  // Update x-offset
            yOffset = (nextPointIndex / 5) * offsetStep;  // Update y-offset
            x = centerX + xOffset;
            y = centerY + yOffset;
            nextPointIndex++;
        }

        return new Point(x, y);
    }

    // Method to draw the boundary of the city
    public void drawBoundary(Graphics2D g) {
        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(2));
        g.drawPolygon(boundaryPolygon);
    }

    // Getter for the city name
    public String getCityName() {
        return cityName;
    }
}

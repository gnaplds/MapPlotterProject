package mapplotterproject;

import java.awt.*;
import java.util.Random;

/**
 * CityBoundary class
 * Represents the boundary of a city and provides methods for point generation and boundary drawing.
 */
public class CityBoundary {
    // Class members
    private final String cityName;
    private final Polygon boundaryPolygon;
    private final Random random;

    /**
     * Constructor for CityBoundary
     * @param cityName Name of the city
     * @param boundaryPolygon Polygon representing the city's boundary
     */
    public CityBoundary(String cityName, Polygon boundaryPolygon) {
        this.cityName = cityName;
        this.boundaryPolygon = boundaryPolygon;
        this.random = new Random();  // Initialize random generator for point generation
    }

    /**
     * Generates a random point within the city boundary
     * @return Point object representing a random point within the boundary
     */
    public Point getNextPoint() {
        // Get the bounding rectangle of the polygon
        Rectangle bounds = boundaryPolygon.getBounds();
        Point randomPoint;

        do {
            // Generate random x and y coordinates within the bounding rectangle
            int x = bounds.x + random.nextInt(bounds.width);
            int y = bounds.y + random.nextInt(bounds.height);
            randomPoint = new Point(x, y);
        } while (!boundaryPolygon.contains(randomPoint));  // Repeat until a point inside the polygon is found

        return randomPoint;
    }

    /**
     * Draws the boundary of the city
     * @param g Graphics2D object for drawing
     */
    public void drawBoundary(Graphics2D g) {
        g.setColor(Color.RED);  // Set the color for the boundary
        g.setStroke(new BasicStroke(2));  // Set the line thickness
        g.drawPolygon(boundaryPolygon);  // Draw the polygon representing the city boundary
    }

    /**
     * Getter for the city name
     * @return String representing the city name
     */
    public String getCityName() {
        return cityName;
    }
}

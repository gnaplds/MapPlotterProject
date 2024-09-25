// CityBoundaryManager.java
package mapplotterproject;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CityBoundaryManager class
 * Manages multiple city boundaries, including their initialization and rendering.
 */
public class CityBoundaryManager {
    // List to store all city boundaries
    private final List<CityBoundary> cityBoundaries;

    /**
     * Constructor for CityBoundaryManager
     * Initializes the list of city boundaries and populates it
     */
    public CityBoundaryManager() {
        cityBoundaries = new ArrayList<>();
        initializeCityBoundaries();
    }

    /**
     * Initializes city boundaries with predefined polygon coordinates
     * Each city is represented by a CityBoundary object with a name and a polygon
     */
    private void initializeCityBoundaries() {
        // Define boundary for Dasmarinas
        addCityBoundary("Dasmarinas",
                new int[]{710, 765, 810, 790, 740},
                new int[]{360, 350, 360, 470, 470});

        // Define boundary for Silang
        addCityBoundary("Silang",
                new int[]{710, 860, 870, 830, 760},
                new int[]{500, 480, 540, 630, 640});

        // Define boundary for Imus
        addCityBoundary("Imus",
                new int[]{665, 708, 770, 725, 705},
                new int[]{260, 225, 335, 335, 340});

        // Define boundary for Bacoor
        addCityBoundary("Bacoor",
                new int[]{720, 760, 820, 818, 790},
                new int[]{210, 195, 310, 350, 330});

        // Define boundary for General Trias
        addCityBoundary("General Trias",
                new int[]{650, 690, 720, 700, 695, 675, 680, 660, 640, 640, 625},
                new int[]{265, 350, 465, 500, 515, 500, 460, 395, 400, 315, 280});

        // Additional cities can be added here using the same pattern
    }

    /**
     * Helper method to add a city boundary to the list
     * @param cityName Name of the city
     * @param xPoints Array of x-coordinates for the city's boundary polygon
     * @param yPoints Array of y-coordinates for the city's boundary polygon
     */
    private void addCityBoundary(String cityName, int[] xPoints, int[] yPoints) {
        Polygon cityBoundary = new Polygon(xPoints, yPoints, xPoints.length);
        cityBoundaries.add(new CityBoundary(cityName, cityBoundary));
    }

    /**
     * Getter for the list of city boundaries
     * @return List of CityBoundary objects
     */
    public List<CityBoundary> getCityBoundaries() {
        return cityBoundaries;
    }

    /**
     * Draws all city boundaries on the provided Graphics2D object
     * @param g2d Graphics2D object for drawing
     */
    public void drawBoundaries(Graphics2D g2d) {
        for (CityBoundary boundary : cityBoundaries) {
            boundary.drawBoundary(g2d);
        }
    }
}

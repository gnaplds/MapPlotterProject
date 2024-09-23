// CityBoundaryManager.java
package mapplotterproject;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CityBoundaryManager {
    private List<CityBoundary> cityBoundaries;

    public CityBoundaryManager() {
        cityBoundaries = new ArrayList<>();
        initializeCityBoundaries();
    }

    private void initializeCityBoundaries() {
        // Define boundary for Dasmarinas
        int[] xPointsA = {710, 765, 810, 790, 740};
        int[] yPointsA = {360, 350, 360, 470, 470};
        Polygon cityABoundary = new Polygon(xPointsA, yPointsA, xPointsA.length);
        cityBoundaries.add(new CityBoundary("Dasmarinas", cityABoundary));

        // Define boundary for Silang
        int[] xPointsB = {710, 860, 870, 830, 760};
        int[] yPointsB = {500, 480, 540, 630, 640};
        Polygon cityBBoundary = new Polygon(xPointsB, yPointsB, xPointsB.length);
        cityBoundaries.add(new CityBoundary("Silang", cityBBoundary));

        // Define boundary for Imus
        int[] xPointsC = {710, 860, 870, 830, 760};
        int[] yPointsC = {500, 480, 540, 630, 640};
        Polygon cityCBoundary = new Polygon(xPointsC, yPointsC, xPointsC.length);
        cityBoundaries.add(new CityBoundary("Imus", cityCBoundary));

        // Add more city boundaries as needed
    }

    public List<CityBoundary> getCityBoundaries() {
        return cityBoundaries;
    }

    public void drawBoundaries(Graphics2D g2d) {
        for (CityBoundary boundary : cityBoundaries) {
            boundary.drawBoundary(g2d);
        }
    }
}

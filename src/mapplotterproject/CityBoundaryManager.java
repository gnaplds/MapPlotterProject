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

        // Define boundary for Trece Martires
        addCityBoundary("Trece Martires",
                new int[]{650, 665, 635, 600, 595},
                new int[]{415, 500, 515, 475, 430});

        // Define boundary for Amadeo
        addCityBoundary("Amadeo",
                new int[]{660, 705, 745, 715, 707},
                new int[]{520, 525, 660, 670, 620});

        // Define boundary for Indang
        addCityBoundary("Indang",
                new int[]{562, 615, 647, 695, 703, 675, 655, 627, 577},
                new int[]{527, 516, 527, 625, 670, 625, 650, 655, 590});

        // Define boundary for Tanza
        addCityBoundary("Tanza",
                new int[]{600, 630, 625, 590, 580, 585, 560, 565, 545, 515},
                new int[]{280, 325, 400, 415, 435, 465, 430, 400, 370, 355});

        // Define boundary for Naic
        addCityBoundary("Naic",
                new int[]{510, 540, 555, 548, 605, 555, 515, 480, 450},
                new int[]{365, 380, 410, 442, 500, 515, 465, 450, 415});

        // Define boundary for Maragondon
        addCityBoundary("Maragondon",
                new int[]{455, 550, 575, 545, 505, 500, 425, 405, 375, 385, 325},
                new int[]{445, 520, 610, 555, 510, 540, 575, 610, 600, 565, 535});

        // Define boundary for General Mariano Alvarez
        addCityBoundary("General Mariano Alvarez",
                new int[]{813, 815, 845, 888, 875, 840, 825},
                new int[]{450, 418, 395, 395, 405, 415, 440});

        // Define boundary for Carmona
        addCityBoundary("Carmona",
                new int[]{817, 845, 880, 915, 875},
                new int[]{460, 423, 410, 395, 465});

        // Define boundary for kawit
        addCityBoundary("Kawit",
                new int[]{710, 710, 693, 666, 660, 659, 661, 670, 673, 692, 700},
                new int[]{200, 215, 218, 247, 240, 222, 209, 215, 207, 206, 215});

        // Define boundary for Cavite City
        addCityBoundary("Cavite City",
                new int[]{648, 670, 705, 710, 675, 658},
                new int[]{208, 155, 140, 165, 170, 205});

        // Define boundary for Noveleta
        addCityBoundary("Noveleta",
                new int[]{648, 655, 655, 663, 655, 638, 637},
                new int[]{213, 222, 242, 251, 257, 257, 243});

        // Define boundary for Rosario
        addCityBoundary("Rosario",
                new int[]{600, 600, 614, 622, 632, 635, 624, 615},
                new int[]{270, 265, 253, 254, 245, 260, 268, 280});

        // Define boundary for Tagaytay
        addCityBoundary("Tagaytay",
                new int[]{655, 750, 840, 865, 735, 672},
                new int[]{705, 665, 640, 670, 727, 740});

        // Define boundary for Mendez
        addCityBoundary("Mendez",
                new int[]{633, 660, 676, 691, 691, 697, 652},
                new int[]{662, 657, 637, 655, 673, 683, 700});

        // Define boundary for Alfonso
        addCityBoundary("Alfonso",
                new int[]{543, 544, 585, 588, 635, 661, 605, 577},
                new int[]{707, 667, 645, 628, 677, 740, 764, 720});

        // Define boundary for General Emilio Aguinaldo
        addCityBoundary("General Emilio Aguinaldo",
                new int[]{508, 543, 582, 543, 515, 499, 500, 505},
                new int[]{522, 565, 642, 663, 630, 590, 550, 545});

        // Define boundary for Magallanes
        addCityBoundary("Magallanes",
                new int[]{405, 425, 493, 494, 509, 540, 539, 523, 488, 455, 432},
                new int[]{625, 585, 555, 590, 630, 666, 710, 713, 662, 650, 662});

        // Define boundary for Ternate
        addCityBoundary("Ternate",
                new int[]{435, 452, 430, 410, 335, 315, 290, 307, 313, 326, 330, 335, 341, 348, 353, 366, 378, 385, 400, 415},
                new int[]{415, 432, 454, 452, 520, 525, 475, 472, 460, 463, 450, 457, 450, 454, 446, 454, 446, 452, 448, 426});

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

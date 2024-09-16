package interfaces;

import java.awt.*;

public interface CityBoundaryInterface {
    Point getRandomPoint();
    void drawBoundary(Graphics2D g);
    String getCityName();
}

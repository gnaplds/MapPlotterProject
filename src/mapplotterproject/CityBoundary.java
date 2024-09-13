package mapplotterproject;

import java.awt.*;

public class CityBoundary {
    String cityName;
    Polygon boundaryPolygon;

    public CityBoundary(String cityName, Polygon boundaryPolygon) {
        this.cityName = cityName;
        this.boundaryPolygon = boundaryPolygon;
    }

    public Point getRandomPoint() {
        Rectangle bounds = boundaryPolygon.getBounds();
        int x, y;
        do {
            x = bounds.x + (int) (Math.random() * bounds.width);
            y = bounds.y + (int) (Math.random() * bounds.height);
        } while (!boundaryPolygon.contains(x, y));
        return new Point(x, y);
    }

    public void drawBoundary(Graphics2D g) {
        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(2));  // Thicker red outline
        g.drawPolygon(boundaryPolygon);   // Draw the polygon boundary
    }
}

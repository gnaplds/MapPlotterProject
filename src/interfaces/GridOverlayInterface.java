package interfaces;

// GridOverlayInterface.java
import java.awt.*;

public interface GridOverlayInterface {
    void paintComponent(Graphics g);
    void setGridSize(int size);
    void setSize(Dimension dimension); // Add this method
}

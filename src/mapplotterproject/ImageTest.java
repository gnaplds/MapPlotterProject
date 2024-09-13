package mapplotterproject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageTest extends JPanel {
    private BufferedImage mapImage;

    public ImageTest() {
        try {
            // Load the Philippines map image
            mapImage = ImageIO.read(new File("resources/cavite_map.png"));
        } catch (IOException e) {
            System.out.println("Map image not found.");
        }

        // Set the preferred size of the panel to match the map image size
        setPreferredSize(new Dimension(800, 1200));  // Adjust according to your image size
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (mapImage != null) {
            g.drawImage(mapImage, 0, 0, null);  // Draw the map image
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Image Test");
        ImageTest panel = new ImageTest();
        frame.add(panel);
        frame.pack();  // Automatically size the window to fit the panel
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

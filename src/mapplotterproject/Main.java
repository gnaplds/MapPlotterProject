// Main.java
package mapplotterproject;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Map Plotter");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            MapPlotter mapPanel = new MapPlotter();
            JPanel listPanel = mapPanel.createListPanel();

            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listPanel, mapPanel);
            splitPane.setDividerLocation(400);

            frame.add(splitPane, BorderLayout.CENTER);
            frame.setSize(1500,800);
            frame.setVisible(true);
        });
    }
}

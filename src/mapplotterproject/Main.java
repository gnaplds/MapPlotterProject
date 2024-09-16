package mapplotterproject;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Map Plotter");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            MapPlotter mapPanel = new MapPlotter();
            JPanel listPanel = mapPanel.createListPanel();

            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mapPanel, listPanel);
            splitPane.setDividerLocation(700);

            frame.add(splitPane, BorderLayout.CENTER);
            frame.setSize(1400,800);
            frame.setVisible(true);
        });
    }
}

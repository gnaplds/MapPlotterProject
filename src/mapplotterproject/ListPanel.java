// ListPanel.java
package mapplotterproject;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ListPanel extends JPanel {
    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;

    public ListPanel(List<String> names, List<String> addresses, List<Point> coordinates, List<String> cities, MapPlotter mapPlotter, GridOverlay gridOverlay) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create and add the table
        createTable(names, addresses, coordinates, cities, mapPlotter);

        // Create and add the search panel
        createSearchPanel();

        // Create and add the controls panel with toggle buttons and grid slider
        createControlsPanel(mapPlotter, gridOverlay);
    }

    private void createTable(List<String> names, List<String> addresses, List<Point> coordinates, List<String> cities, MapPlotter mapPlotter) {
        String[] columnNames = {"Name", "Address", "City"}; // Add "City" column

        Object[][] data = new Object[names.size()][3];
        for (int i = 0; i < names.size(); i++) {
            data[i][0] = names.get(i);
            data[i][1] = cities.get(i);
            data[i][2] = addresses.get(i);
        }

        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 18));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    int modelIndex = table.convertRowIndexToModel(row);
                    Point selectedCoordinate = coordinates.get(modelIndex);

                    mapPlotter.zoomToCoordinate(coordinates.get(modelIndex));
                    mapPlotter.highlightSelectedPoint(selectedCoordinate);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(400, 400));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable();
            }

            private void filterTable() {
                String query = searchField.getText();
                RowFilter<DefaultTableModel, Object> rf = RowFilter.regexFilter("(?i)" + query);
                sorter.setRowFilter(rf);
            }
        });

        searchPanel.add(new JLabel("Search:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.NORTH);
    }

    private void createControlsPanel(MapPlotter mapPlotter, GridOverlay gridOverlay) {
        JPanel controlsPanel = new JPanel(new BorderLayout());
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Create a slider for grid size
        JSlider gridSizeSlider = new JSlider(SwingConstants.HORIZONTAL, 10, 100, 50);
        gridSizeSlider.setMajorTickSpacing(15);
        gridSizeSlider.setMinorTickSpacing(5);
        gridSizeSlider.setPaintTicks(true);
        gridSizeSlider.setPaintLabels(true);
        gridSizeSlider.setSnapToTicks(true);

        // Add a listener to the slider to update the grid size
        gridSizeSlider.addChangeListener(e -> {
            int newGridSize = gridSizeSlider.getValue();
            gridOverlay.setGridSize(newGridSize); // Update grid size in overlay
            mapPlotter.repaint();
        });

        // Create a sub-panel for the toggle buttons
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton toggleNamesButton = new JButton("Toggle Names");
        JButton togglePointsButton = new JButton("Toggle Points");
        JButton toggleGridButton = new JButton("Toggle Grid");
        JButton toggleBoundariesButton = new JButton("Toggle Boundaries");
        JButton clearButton = new JButton("Clear Highlight");
        JButton toggleCoordsButton = new JButton("Toggle Coordinates");

        clearButton.addActionListener(e -> mapPlotter.clearHighlightedPoint());

        toggleNamesButton.addActionListener(e -> {
            mapPlotter.toggleNames();
            toggleNamesButton.setText(mapPlotter.isNamesVisible() ? "Hide Names" : "Show Names");
        });

        togglePointsButton.addActionListener(e -> {
            mapPlotter.togglePoints();
            togglePointsButton.setText(mapPlotter.isPointsVisible() ? "Hide Points" : "Show Points");
        });

        toggleGridButton.addActionListener(e -> {
            mapPlotter.toggleGrid();
            toggleGridButton.setText(mapPlotter.isGridVisible() ? "Hide Grid" : "Show Grid");
        });

        toggleBoundariesButton.addActionListener(e -> {
            mapPlotter.toggleBoundaries();
            toggleBoundariesButton.setText(mapPlotter.isBoundariesVisible() ? "Hide Boundaries" : "Show Boundaries");
        });

        toggleCoordsButton.addActionListener(e -> {
            mapPlotter.toggleCoordinates();
            toggleCoordsButton.setText(mapPlotter.isCoordinatesVisible() ? "Hide Coordinates" : "Show Coordinates");
        });

        // Add buttons to the panel
        buttonPanel.add(toggleNamesButton);
        buttonPanel.add(togglePointsButton);
        buttonPanel.add(toggleGridButton);
        buttonPanel.add(toggleBoundariesButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(toggleCoordsButton);

        controlsPanel.add(new JLabel("   Adjust Grid Size:"), BorderLayout.NORTH);
        controlsPanel.add(gridSizeSlider, BorderLayout.CENTER);
        controlsPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(controlsPanel, BorderLayout.SOUTH);
    }
}

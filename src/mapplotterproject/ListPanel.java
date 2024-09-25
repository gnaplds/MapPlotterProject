// ListPanel.java
package mapplotterproject;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ListPanel extends JPanel {
    // Class members
    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;

    // Constructor
    public ListPanel(List<String> names, List<String> addresses, List<Point> coordinates, List<String> cities, MapPlotter mapPlotter, GridOverlay gridOverlay) {
        initializePanel();
        createTable(names, addresses, coordinates, cities, mapPlotter);
        createSearchPanel();
        createControlsPanel(mapPlotter, gridOverlay);
    }

    // Initialize the panel
    private void initializePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    // Create and set up the table
    private void createTable(List<String> names, List<String> addresses, List<Point> coordinates, List<String> cities, MapPlotter mapPlotter) {
        String[] columnNames = {"Name", "Address", "City"};
        Object[][] data = prepareTableData(names, addresses, cities);
        DefaultTableModel tableModel = createTableModel(data, columnNames);

        table = new JTable(tableModel);
        configureTable();
        setupTableSorter(tableModel);
        addTableSelectionListener(coordinates, mapPlotter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(400, 400));
        add(scrollPane, BorderLayout.CENTER);
    }

    // Prepare data for the table
    private Object[][] prepareTableData(List<String> names, List<String> addresses, List<String> cities) {
        Object[][] data = new Object[names.size()][3];
        for (int i = 0; i < names.size(); i++) {
            data[i][0] = names.get(i);
            data[i][1] = cities.get(i);
            data[i][2] = addresses.get(i);
        }
        return data;
    }

    // Create the table model
    private DefaultTableModel createTableModel(Object[][] data, String[] columnNames) {
        return new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    // Configure table properties
    private void configureTable() {
        table.setFont(new Font("Arial", Font.PLAIN, 18));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    // Set up table sorter
    private void setupTableSorter(DefaultTableModel tableModel) {
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
    }

    // Add selection listener to the table
    private void addTableSelectionListener(List<Point> coordinates, MapPlotter mapPlotter) {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    int modelIndex = table.convertRowIndexToModel(row);
                    Point selectedCoordinate = coordinates.get(modelIndex);
                    mapPlotter.zoomToCoordinate(selectedCoordinate);
                    mapPlotter.highlightSelectedPoint(selectedCoordinate);
                }
            }
        });
    }

    // Create and set up the search panel
    private void createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        addSearchFieldListener();

        searchPanel.add(new JLabel("Search:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.NORTH);
    }

    // Add listener to the search field
    private void addSearchFieldListener() {
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
    }

    // Create and set up the controls panel
    private void createControlsPanel(MapPlotter mapPlotter, GridOverlay gridOverlay) {
        JPanel controlsPanel = new JPanel(new BorderLayout());
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JSlider gridSizeSlider = createGridSizeSlider(mapPlotter, gridOverlay);
        JPanel buttonPanel = createButtonPanel(mapPlotter);

        controlsPanel.add(new JLabel("   Adjust Grid Size:"), BorderLayout.NORTH);
        controlsPanel.add(gridSizeSlider, BorderLayout.CENTER);
        controlsPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(controlsPanel, BorderLayout.SOUTH);
    }

    // Create grid size slider
    private JSlider createGridSizeSlider(MapPlotter mapPlotter, GridOverlay gridOverlay) {
        JSlider gridSizeSlider = new JSlider(SwingConstants.HORIZONTAL, 10, 100, 50);
        gridSizeSlider.setMajorTickSpacing(15);
        gridSizeSlider.setMinorTickSpacing(5);
        gridSizeSlider.setPaintTicks(true);
        gridSizeSlider.setPaintLabels(true);
        gridSizeSlider.setSnapToTicks(true);

        gridSizeSlider.addChangeListener(e -> {
            int newGridSize = gridSizeSlider.getValue();
            gridOverlay.setGridSize(newGridSize);
            mapPlotter.repaint();
        });

        return gridSizeSlider;
    }

    // Create button panel with toggle buttons
    private JPanel createButtonPanel(MapPlotter mapPlotter) {
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        addToggleButton(buttonPanel, "Toggle Names", e -> {
            mapPlotter.toggleNames();
            ((JButton) e.getSource()).setText(mapPlotter.isNamesVisible() ? "Hide Names" : "Show Names");
        });

        addToggleButton(buttonPanel, "Toggle Points", e -> {
            mapPlotter.togglePoints();
            ((JButton) e.getSource()).setText(mapPlotter.isPointsVisible() ? "Hide Points" : "Show Points");
        });

        addToggleButton(buttonPanel, "Toggle Grid", e -> {
            mapPlotter.toggleGrid();
            ((JButton) e.getSource()).setText(mapPlotter.isGridVisible() ? "Hide Grid" : "Show Grid");
        });

        addToggleButton(buttonPanel, "Toggle Boundaries", e -> {
            mapPlotter.toggleBoundaries();
            ((JButton) e.getSource()).setText(mapPlotter.isBoundariesVisible() ? "Hide Boundaries" : "Show Boundaries");
        });

        addToggleButton(buttonPanel, "Clear Highlight", e -> mapPlotter.clearHighlightedPoint());

        addToggleButton(buttonPanel, "Toggle Coordinates", e -> {
            mapPlotter.toggleCoordinates();
            ((JButton) e.getSource()).setText(mapPlotter.isCoordinatesVisible() ? "Hide Coordinates" : "Show Coordinates");
        });

        return buttonPanel;
    }

    // Helper method to add toggle buttons
    private void addToggleButton(JPanel panel, String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        panel.add(button);
    }
}

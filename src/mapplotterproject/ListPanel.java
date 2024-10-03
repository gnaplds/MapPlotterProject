// ListPanel.java
package mapplotterproject;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;

public class ListPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;
    private final MapPlotter mapPlotter;

    public ListPanel(List<String> names, List<String> addresses, List<Point> coordinates, List<String> cities, MapPlotter mapPlotter, GridOverlay gridOverlay) {
        initializePanel();
        createTable(names, addresses, coordinates, cities, mapPlotter);
        createSearchPanel();
        createControlsPanel(mapPlotter, gridOverlay);
        this.mapPlotter = mapPlotter;

        addRightClickMenu();
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void createTable(List<String> names, List<String> addresses, List<Point> coordinates, List<String> cities, MapPlotter mapPlotter) {
        String[] columnNames = {"Name", "Address", "City"};
        Object[][] data = prepareTableData(names, addresses, cities);
        tableModel = createTableModel(data, columnNames);

        table = new JTable(tableModel);
        configureTable();
        setupTableSorter(tableModel);
        addTableSelectionListener(coordinates, mapPlotter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(400, 400));
        add(scrollPane, BorderLayout.CENTER);
    }

    private Object[][] prepareTableData(List<String> names, List<String> addresses, List<String> cities) {
        Object[][] data = new Object[names.size()][3];
        for (int i = 0; i < names.size(); i++) {
            data[i][0] = names.get(i);
            data[i][1] = cities.get(i);
            data[i][2] = addresses.get(i);
        }
        return data;
    }

    private DefaultTableModel createTableModel(Object[][] data, String[] columnNames) {
        return new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void configureTable() {
        table.setFont(new Font("Arial", Font.PLAIN, 18));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void setupTableSorter(DefaultTableModel tableModel) {
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
    }

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

    private void createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        addSearchFieldListener();

        searchPanel.add(new JLabel("Search:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.NORTH);
    }

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

    private JPanel createButtonPanel(MapPlotter mapPlotter) {
        JPanel buttonPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton addButton = new JButton("Add New Person");
        addButton.addActionListener(e -> addNewPerson());
        buttonPanel.add(addButton);

        JButton removePersonButton = new JButton("Remove Person");
        removePersonButton.addActionListener(e -> removeSelectedPerson());
        buttonPanel.add(removePersonButton);

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

    private void addToggleButton(JPanel panel, String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        panel.add(button);
    }

    private void addRightClickMenu() {
        JPopupMenu rightClickMenu = new JPopupMenu();

        JMenuItem editItem = new JMenuItem("Edit Person");
        JMenuItem removeItem = new JMenuItem("Remove Person");

        rightClickMenu.add(editItem);
        rightClickMenu.add(removeItem);

        // Right-click listener
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = table.rowAtPoint(e.getPoint());
                    table.setRowSelectionInterval(row, row);
                    rightClickMenu.show(table, e.getX(), e.getY());
                }
            }
        });

        editItem.addActionListener(e -> editSelectedPerson());

        removeItem.addActionListener(e -> removeSelectedPerson());
    }

    private void addNewPerson() {
        JTextField nameField = new JTextField();
        JTextField cityField = new JTextField();
        JTextField addressField = new JTextField();

        boolean inputValid = false;

        while (!inputValid) {
            JPanel panel = new JPanel(new GridLayout(3, 2));
            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("City:"));
            panel.add(cityField);
            panel.add(new JLabel("Address:"));
            panel.add(addressField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Add New Person", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.CANCEL_OPTION) {
                return;
            }

            String name = nameField.getText().trim();
            String city = cityField.getText().trim();
            String address = addressField.getText().trim();

            if (name.isEmpty() || city.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(null, "All fields must be filled.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } else {
                inputValid = true;
                tableModel.addRow(new Object[]{name, city, address});
                writeToCSV(name, city, address);
                mapPlotter.loadData();
                repaint();
            }
        }
    }

    private void editSelectedPerson() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a person to edit.");
            return;
        }

        // Convert selected row index from view to model
        int modelRow = table.convertRowIndexToModel(selectedRow);

        boolean inputValid = false;

        while (!inputValid) {
            String currentName = tableModel.getValueAt(modelRow, 0).toString();
            String currentCity = tableModel.getValueAt(modelRow, 1).toString();
            String currentAddress = tableModel.getValueAt(modelRow, 2).toString();

            JTextField nameField = new JTextField(currentName);
            JTextField cityField = new JTextField(currentCity);
            JTextField addressField = new JTextField(currentAddress);

            JPanel panel = new JPanel(new GridLayout(3, 2));
            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("City:"));
            panel.add(cityField);
            panel.add(new JLabel("Address:"));
            panel.add(addressField);

            int result = JOptionPane.showConfirmDialog(
                    null, panel, "Edit Person", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.CANCEL_OPTION) {
                return;
            }

            String newName = nameField.getText().trim();
            String newCity = cityField.getText().trim();
            String newAddress = addressField.getText().trim();

            if (newName.isEmpty() || newCity.isEmpty() || newAddress.isEmpty()) {
                JOptionPane.showMessageDialog(null, "All fields must be filled.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } else {
                inputValid = true;
                tableModel.setValueAt(newName, modelRow, 0);
                tableModel.setValueAt(newCity, modelRow, 1);
                tableModel.setValueAt(newAddress, modelRow, 2);
                updateCSV(currentName, newName, newCity, newAddress);
                mapPlotter.loadData();
                repaint();
            }
        }
    }

    private void removeSelectedPerson() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a person to remove.");
            return;
        }

        // Convert selected row index from view to model
        int modelRow = table.convertRowIndexToModel(selectedRow);

        String name = tableModel.getValueAt(modelRow, 0).toString();

        int response = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to remove " + name + "?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (response == JOptionPane.YES_OPTION) {
            tableModel.removeRow(modelRow);
            removeFromCSV(name);

            mapPlotter.loadData();
            repaint();
        }
    }

    private void writeToCSV(String name, String city, String address) {
        try (FileWriter fw = new FileWriter("src/resources/addresses.csv", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw)) {
            pw.println(name + "," + city + "," + address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeFromCSV(String name) {
        File inputFile = new File("src/resources/addresses.csv");
        File tempFile = new File("src/resources/temp_addresses.csv");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    writer.write(line);
                    writer.newLine();
                    isHeader = false;
                    continue;
                }
                String[] data = line.split(",");
                if (data.length == 3 && !data[0].equals(name)) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (inputFile.delete()) {
            tempFile.renameTo(inputFile);
        }
    }

    private void updateCSV(String oldName, String newName, String newCity, String newAddress) {
        File inputFile = new File("src/resources/addresses.csv");
        File tempFile = new File("src/resources/temp.csv");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                // If we find the person with the old name, replace the row with updated data
                if (data[0].equals(oldName)) {
                    writer.write(newName + "," + newCity + "," + newAddress + "\n");
                } else {
                    writer.write(line + "\n");  // Write the line as is if it's not the person being edited
                }
            }

        } catch (IOException e) {
            System.out.println("Error updating the CSV file.");
            e.printStackTrace();
        }

        // Rename the temp file to replace the original CSV file
        if (!inputFile.delete()) {
            System.out.println("Could not delete original CSV file.");
        }
        if (!tempFile.renameTo(inputFile)) {
            System.out.println("Could not rename temp CSV file.");
        }
    }
}

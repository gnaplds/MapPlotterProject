package mapplotterproject;
import interfaces.CityBoundaryInterface;
import interfaces.GridOverlayInterface;

import javax.imageio.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MapPlotter extends JPanel {
    private BufferedImage mapImage;
    private List<Point> coordinates;
    private List<String> names;
    private List<String> addresses;
    private List<String> cities;

    private double scale = 1.0;
    private double minScale = 0.5;
    private double maxScale = 3.0;
    private int offsetX = 0;
    private int offsetY = 0;
    private Point dragStart;
    private boolean showNames = true;
    private boolean showPoints = true;
    private boolean showGrid = true;
    private boolean showBoundaries = true;
    private GridOverlayInterface gridOverlay;
    private Timer zoomTimer;
    private List<CityBoundaryInterface> cityBoundaries;

    public MapPlotter() {
        loadMapImage();
        initializeLists();
        initializeCityBoundaries();
        readCoordinatesFromCSV("resources/addresses.csv");
        createGridOverlay();
        setupMouseListeners();
        setPreferredSize(new Dimension(mapImage.getWidth(), mapImage.getHeight()));
    }


    private void initializeCityBoundaries() {
        cityBoundaries = new ArrayList<>();

        // Define boundary for CityA using a polygon with vertices
        int[] xPointsA = {100, 200, 300, 250, 150};
        int[] yPointsA = {300, 250, 300, 400, 350};
        Polygon cityABoundary = new Polygon(xPointsA, yPointsA, xPointsA.length);
        cityBoundaries.add(new CityBoundary("CityA", cityABoundary));

        // Define boundary for CityB using another polygon with vertices
        int[] xPointsB = {400, 450, 500, 550, 600};
        int[] yPointsB = {300, 350, 400, 350, 300};
        Polygon cityBBoundary = new Polygon(xPointsB, yPointsB, xPointsB.length);
        cityBoundaries.add(new CityBoundary("CityB", cityBBoundary));

        // Add more city boundaries as needed
    }



    private void drawCityBoundaries(Graphics2D g2d) {
        for (CityBoundaryInterface boundary : cityBoundaries) {
            boundary.drawBoundary(g2d);
        }
    }

    private void loadMapImage() {
        try {
            mapImage = ImageIO.read(new File("resources/caviteMapCity.png"));
        } catch (IOException e) {
            System.out.println("Map image not found.");
        }
    }

    private void initializeLists() {
        coordinates = new ArrayList<>();
        names = new ArrayList<>();
        addresses = new ArrayList<>();
        cities = new ArrayList<>();  // Initialize the city names list
    }

    private Point getCoordinatesForCity(String cityName) {
        for (CityBoundaryInterface boundary : cityBoundaries) {
            if (boundary.getCityName().equalsIgnoreCase(cityName)) {
                return boundary.getRandomPoint();
            }
        }
        return new Point(0, 0);
    }

    private void readCoordinatesFromCSV(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length != 3) continue;
                names.add(data[0]);
                cities.add(data[1]);
                addresses.add(data[2]);
                coordinates.add(getCoordinatesForCity(data[1]));
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV file.");
            e.printStackTrace();
        }
    }


    private void createGridOverlay() {
        gridOverlay = new GridOverlay(mapImage.getWidth(), mapImage.getHeight());
    }

    private void setupMouseListeners() {
        addMouseWheelListener(e -> handleMouseWheelEvent(e));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragStart = e.getPoint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDrag(e);
            }
        });
    }

    private void handleMouseWheelEvent(MouseWheelEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        double previousScale = scale;

        if (e.getWheelRotation() < 0 && scale < maxScale) scale += 0.1;
        else if (e.getWheelRotation() > 0 && scale > minScale) scale -= 0.1;

        offsetX = (int) (mouseX - (mouseX - offsetX) * (scale / previousScale));
        offsetY = (int) (mouseY - (mouseY - offsetY) * (scale / previousScale));

        revalidate();
        repaint();
    }

    private void handleMouseDrag(MouseEvent e) {
        Point dragEnd = e.getPoint();
        offsetX += dragEnd.x - dragStart.x;
        offsetY += dragEnd.y - dragStart.y;
        dragStart = dragEnd;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(offsetX, offsetY);
        g2d.scale(scale, scale);

        // Draw the map image first
        if (mapImage != null) {
            g2d.drawImage(mapImage, 0, 0, null);
        }

        // Draw the grid overlay if it's enabled
        if (showGrid && gridOverlay != null) {
            gridOverlay.paintComponent(g2d); // Draw the grid overlay on top of the map
        }

        if (showPoints) {
            drawPlotPoints(g2d);
        }

        if (showNames) {
            drawNames(g2d);
        }

        if (showBoundaries) {
            drawCityBoundaries(g2d);
        }
    }

    private void drawPlotPoints(Graphics g) {
        g.setColor(Color.RED);
        for (Point point : coordinates) {
            g.fillOval(point.x - 5, point.y - 5, 10, 10);
        }
    }

    private void drawNames(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        for (int i = 0; i < coordinates.size(); i++) {
            Point point = coordinates.get(i);
            String name = names.get(i);
            g.drawString(name, point.x + 10, point.y);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(mapImage.getWidth(), mapImage.getHeight());
    }

    public JPanel createListPanel() {
        JPanel listPanel = new JPanel(new BorderLayout());

        String[] columnNames = {"Name", "City", "Address"};
        Object[][] data = new Object[names.size()][3];
        for (int i = 0; i < names.size(); i++) {
            data[i][0] = names.get(i);
            data[i][1] = cities.get(i); // City name
            data[i][2] = addresses.get(i);
        }

        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.BOLD, 16));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    int modelIndex = table.convertRowIndexToModel(row);
                    zoomToCoordinate(coordinates.get(modelIndex));
                }
            }
        });

        JPanel searchPanel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField();

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

        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BorderLayout());

        JSlider gridSizeSlider = new JSlider(JSlider.HORIZONTAL, 10, 100, 50);
        gridSizeSlider.setMajorTickSpacing(15);
        gridSizeSlider.setMinorTickSpacing(5);
        gridSizeSlider.setPaintTicks(true);
        gridSizeSlider.setPaintLabels(true);
        gridSizeSlider.setSnapToTicks(true);

        gridSizeSlider.addChangeListener(e -> {
            int newGridSize = gridSizeSlider.getValue();
            gridOverlay.setGridSize(newGridSize);
            repaint();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton toggleNamesButton = new JButton("Toggle Names");
        JButton togglePointsButton = new JButton("Toggle Points");
        JButton toggleGridButton = new JButton("Toggle Grid");
        JButton toggleCityBoundariesButton = new JButton("Toggle City Boundaries");

        toggleNamesButton.addActionListener(e -> toggleNames());
        togglePointsButton.addActionListener(e -> togglePoints());
        toggleGridButton.addActionListener(e -> toggleGrid());
        toggleCityBoundariesButton.addActionListener(e -> toggleBoundaries());

        buttonPanel.add(toggleNamesButton);
        buttonPanel.add(togglePointsButton);
        buttonPanel.add(toggleGridButton);
        buttonPanel.add(toggleCityBoundariesButton);

        controlsPanel.add(new JLabel("Adjust Grid Size:"), BorderLayout.NORTH);
        controlsPanel.add(gridSizeSlider, BorderLayout.CENTER);
        controlsPanel.add(buttonPanel, BorderLayout.SOUTH);

        listPanel.add(searchPanel, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(400, 400));
        scrollPane.setMaximumSize(new Dimension(600, 500));

        listPanel.add(scrollPane, BorderLayout.CENTER);
        listPanel.add(controlsPanel, BorderLayout.SOUTH);

        return listPanel;
    }

    private void zoomToCoordinate(Point point) {
        // Define the specific minimum zoom scale
        final double minZoomScale = 2.0; // Change this value to your desired minimum zoom scale

        // Set initial zoom scale
        final double startScale = scale;
        final double targetScale = Math.min(maxScale, minZoomScale);

        // Calculate the new offset to center the point after zoom
        final int startX = offsetX;
        final int startY = offsetY;
        final int targetX = (int) (-point.x * targetScale + getWidth() / 2);
        final int targetY = (int) (-point.y * targetScale + getHeight() / 2);

        // Define the number of animation steps
        final int animationSteps = 30;
        final double scaleStep = (targetScale - startScale) / animationSteps;
        final int xStep = (targetX - startX) / animationSteps;
        final int yStep = (targetY - startY) / animationSteps;

        // Create and start the timer for animation
        zoomTimer = new Timer(20, new ActionListener() {
            private int step = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (step >= animationSteps) {
                    ((Timer) e.getSource()).stop(); // Stop the timer when done
                    return;
                }

                // Update scale and offset
                scale = startScale + step * scaleStep;
                offsetX = startX + step * xStep;
                offsetY = startY + step * yStep;

                revalidate();
                repaint();
                step++;
            }
        });
        zoomTimer.start();
    }

    private void toggleNames() {
        showNames = !showNames;
        repaint();
    }

    private void togglePoints() {
        showPoints = !showPoints;
        repaint();
    }

    private void toggleGrid() {
        showGrid = !showGrid;
        repaint();
    }

    private void toggleBoundaries() {
        showBoundaries = !showBoundaries;
        repaint();
    }

}

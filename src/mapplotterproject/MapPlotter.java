package mapplotterproject;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapPlotter extends JPanel {
    private BufferedImage mapImage;
    private List<Point> coordinates;
    private List<String> names;
    private List<String> addresses;
    private double scale = 1.0;
    private double minScale = 0.5;
    private double maxScale = 3.0;
    private int offsetX = 0;
    private int offsetY = 0;
    private Point dragStart;
    private boolean showNames = true;
    private boolean showPoints = true;
    private boolean showGrid = true;
    private GridOverlay gridOverlay;
    private Timer zoomTimer; // Timer for zoom animation

    public MapPlotter() {
        loadMapImage();
        initializeLists();
        readCoordinatesFromCSV("resources/addresses.csv");
        createGridOverlay();
        setupMouseListeners();
        setPreferredSize(new Dimension(mapImage.getWidth(), mapImage.getHeight()));
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
    }

    private void readCoordinatesFromCSV(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length != 4) continue;
                names.add(data[0]);
                addresses.add(data[1]);
                coordinates.add(new Point(Integer.parseInt(data[2]), Integer.parseInt(data[3])));
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

        if (mapImage != null) {
            g2d.drawImage(mapImage, 0, 0, null);
        }

        if (showGrid) {
            gridOverlay.paintComponent(g);
        }

        if (showPoints) {
            drawPlotPoints(g);
        }

        if (showNames) {
            drawNames(g);
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
        int newWidth = (int) (mapImage.getWidth() * scale);
        int newHeight = (int) (mapImage.getHeight() * scale);
        return new Dimension(newWidth, newHeight);
    }

    public JPanel createListPanel() {
        JPanel listPanel = new JPanel(new BorderLayout());

        // Define column names for the table
        String[] columnNames = {"Name", "Address"};

        // Create a 2D array for the table data
        Object[][] data = new Object[names.size()][2];
        for (int i = 0; i < names.size(); i++) {
            data[i][0] = names.get(i);
            data[i][1] = addresses.get(i);
        }

        // Create the table model and table
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make all cells non-editable
                return false;
            }
        };

        // Create the table model and table
        JTable table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.BOLD, 16));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set up row sorter for the table
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // Add a selection listener to the table
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    // Map row to the actual index in coordinates list
                    int modelIndex = table.convertRowIndexToModel(row);
                    // Zoom to the coordinate of the selected row
                    zoomToCoordinate(coordinates.get(modelIndex));
                }
            }
        });

        // Create a search panel with a text field
        JPanel searchPanel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField();

        // Add a document listener to the search field for live filtering
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

        // Create a panel for the toggle buttons
        JPanel controlsPanel = new JPanel();
        JButton toggleNamesButton = new JButton("Toggle Names");
        JButton togglePointsButton = new JButton("Toggle Points");
        JButton toggleGridButton = new JButton("Toggle Grid");

        toggleNamesButton.addActionListener(e -> toggleNames());
        togglePointsButton.addActionListener(e -> togglePoints());
        toggleGridButton.addActionListener(e -> toggleGrid());

        controlsPanel.add(toggleNamesButton);
        controlsPanel.add(togglePointsButton);
        controlsPanel.add(toggleGridButton);

        // Add the search panel and table to the main list panel
        listPanel.add(searchPanel, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(400, 400));  // Adjust size as needed
        scrollPane.setMaximumSize(new Dimension(600, 500));    // Maximum size as needed

        listPanel.add(scrollPane, BorderLayout.CENTER);
        listPanel.add(controlsPanel, BorderLayout.SOUTH);  // Add controls panel to the bottom
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


    public void toggleNames() {
        showNames = !showNames;
        repaint();
    }

    public void togglePoints() {
        showPoints = !showPoints;
        repaint();
    }

    public void toggleGrid() {
        showGrid = !showGrid;
        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Cavite Map with Address Points");

        MapPlotter mapPanel = new MapPlotter();
        JPanel listPanel = mapPanel.createListPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mapPanel, listPanel);
        splitPane.setDividerLocation(1000);
        splitPane.setResizeWeight(1.0);

        frame.add(splitPane, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1400, 800);
        frame.setVisible(true);
    }
}
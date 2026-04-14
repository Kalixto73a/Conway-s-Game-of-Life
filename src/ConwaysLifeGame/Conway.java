package ConwaysLifeGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class Conway extends JPanel {

    private boolean[][] grid;
    private final int rows = 500;
    private final int cols = 500;
    private final int cellSize = 15;
    private double offsetX;
    private double offsetY;
    private double zoom = 1.0;
    private int lastMouseX, lastMouseY;

    public Conway() {
        grid = new boolean[rows][cols];

        spawnCenterRandom();

        offsetX = cols * cellSize / 2.0;
        offsetY = rows * cellSize / 2.0;

        Timer timer = new Timer(50, e -> {
            generateNewCells();
            repaint();
        });
        timer.start();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMouseX = e.getX();
                lastMouseY = e.getY();
                toggleCell(e);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - lastMouseX;
                int dy = e.getY() - lastMouseY;

                offsetX -= dx / zoom;
                offsetY -= dy / zoom;

                lastMouseX = e.getX();
                lastMouseY = e.getY();

                repaint();
            }
        });

        addMouseWheelListener(e -> {
            double zoomFactor = 1.1;

            if (e.getPreciseWheelRotation() < 0) {
                zoom *= zoomFactor;
            } else {
                zoom /= zoomFactor;
            }

            zoom = Math.max(0.2, Math.min(zoom, 5));
            repaint();
        });
    }

    private void spawnCenterRandom() {
        Random rand = new Random();

        int centerX = rows / 2;
        int centerY = cols / 2;

        int radius = 70;

        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = centerY - radius; y <= centerY + radius; y++) {

                if (x >= 0 && y >= 0) {
                    grid[x][y] = rand.nextDouble() < 0.3;
                }
            }
        }
    }

    public void randomizeGrid() {
        Random rand = new Random();

        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                grid[x][y] = rand.nextDouble() < 0.2;
            }
        }

        repaint();
    }

    private void toggleCell(MouseEvent e) {
        int worldX = (int)((e.getX() / zoom) + offsetX);
        int worldY = (int)((e.getY() / zoom) + offsetY);

        int col = worldX / cellSize;
        int row = worldY / cellSize;

        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            grid[row][col] = !grid[row][col];
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        Graphics2D g2 = (Graphics2D) g;
        g2.scale(zoom, zoom);

        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {

                int drawX = y * cellSize - (int)offsetX;
                int drawY = x * cellSize - (int)offsetY;

                if (drawX + cellSize < 0 || drawY + cellSize < 0 ||
                        drawX > getWidth() / zoom || drawY > getHeight() / zoom) continue;

                if (grid[x][y]) {
                    g2.setColor(Color.WHITE);
                    g2.fillRect(drawX, drawY, cellSize, cellSize);
                }
            }
        }
    }

    private int checkNeighbours(int x, int y) {
        int count = 0;

        for (int r = -1; r <= 1; r++) {
            for (int c = -1; c <= 1; c++) {
                if (r == 0 && c == 0) continue;

                int nx = (x + r + rows) % rows;
                int ny = (y + c + cols) % cols;

                if (grid[nx][ny]) count++;
            }
        }
        return count;
    }

    private void generateNewCells() {
        boolean[][] newGrid = new boolean[rows][cols];

        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                int count = checkNeighbours(x, y);

                if (grid[x][y]) {
                    newGrid[x][y] = (count == 2 || count == 3);
                } else {
                    newGrid[x][y] = (count == 3);
                }
            }
        }

        grid = newGrid;
    }

    public static void main(String[] args) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        JFrame frame = new JFrame();
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Conway panel = new Conway();
        frame.add(panel);

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_R) {
                    panel.randomizeGrid();
                }

                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                }
            }
        });
        gd.setFullScreenWindow(frame);
    }
}
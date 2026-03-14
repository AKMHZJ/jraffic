import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;
import java.util.List;

public class GamePanel extends JPanel {

    static final int W = 800, H = 700;
    static final int CX = 400, CY = 350;
    static final int ROAD = 100;
    static final int L = CX - ROAD / 2, R = CX + ROAD / 2; // 350, 450
    static final int T = CY - ROAD / 2, B = CY + ROAD / 2; // 300, 400
    static final int CAR_SIZE = 30;

    private final List<Car> cars = new ArrayList<>();
    private final TrafficLights lights = new TrafficLights();
    private Timer timer;

    public GamePanel(int w, int h) {
        setPreferredSize(new Dimension(w, h));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP     -> spawnCar(new Car(410, 700, Direction.Down,  Car.randomColor()), 610, null, Direction.Down);
                    case KeyEvent.VK_DOWN   -> spawnCar(new Car(360, -30, Direction.Top,   Car.randomColor()), 60,  null, Direction.Top);
                    case KeyEvent.VK_RIGHT  -> spawnCar(new Car(-30, 360, Direction.Right, Car.randomColor()), null, 60,  Direction.Right);
                    case KeyEvent.VK_LEFT   -> spawnCar(new Car(800, 310, Direction.Left,  Car.randomColor()), null, 740, Direction.Left);
                    case KeyEvent.VK_R      -> spawnRandom();
                    case KeyEvent.VK_ESCAPE -> System.exit(0);
                }
            }
        });
    }

    public void start() {
        timer = new Timer(16, e -> {
            TrafficLightsSystem.update(lights, cars);
            for (Car c : cars) {
                TrafficLightsSystem.applyToCar(c, lights);
                if (c.moving && !c.nextCar(cars)) c.moveCar();
                if (!c.turned) c.redirect();
            }
            cars.removeIf(c -> c.y > 740 || c.y < -40 || c.x > 840 || c.x < -40);
            repaint();
        });
        timer.start();
    }

    private void spawnRandom() {
        switch ((int)(Math.random() * 4)) {
            case 0 -> spawnCar(new Car(410, 700, Direction.Down,  Car.randomColor()), 610, null, Direction.Down);
            case 1 -> spawnCar(new Car(360, -30, Direction.Top,   Car.randomColor()), 60,  null, Direction.Top);
            case 2 -> spawnCar(new Car(-30, 360, Direction.Right, Car.randomColor()), null, 60,  Direction.Right);
            default-> spawnCar(new Car(800, 310, Direction.Left,  Car.randomColor()), null, 740, Direction.Left);
        }
    }

    private void spawnCar(Car car, Integer checkY, Integer checkX, Direction dir) {
        if (cars.size() >= 28) return;
        Car last = lastCarInDir(dir);
        if (last == null) { cars.add(car); return; }
        boolean safe = checkY != null
            ? (dir == Direction.Down ? last.y < checkY : last.y > checkY)
            : (dir == Direction.Right ? last.x > checkX : last.x < checkX);
        if (safe) cars.add(car);
    }

    private Car lastCarInDir(Direction dir) {
        for (int i = cars.size() - 1; i >= 0; i--)
            if (cars.get(i).dir == dir) return cars.get(i);
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawRoads(g2);
        drawLights(g2);
        drawCars(g2);
    }

    private void drawRoads(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));

        // Edge lines (skip intersection box)
        g2.drawLine(L, 0, L, T); g2.drawLine(L, B, L, H);
        g2.drawLine(R, 0, R, T); g2.drawLine(R, B, R, H);
        g2.drawLine(0, T, L, T); g2.drawLine(R, T, W, T);
        g2.drawLine(0, B, L, B); g2.drawLine(R, B, W, B);

        // Centre dashes
        float[] dash = {12f, 12f};
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dash, 0));
        g2.drawLine(CX, 0,  CX, T); g2.drawLine(CX, B,  CX, H);
        g2.drawLine(0,  CY, L,  CY); g2.drawLine(R,  CY, W,  CY);

        // Stop lines
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(L,  T,  CX, T);
        g2.drawLine(CX, B,  R,  B);
        g2.drawLine(L,  CY, L,  B);
        g2.drawLine(R,  T,  R,  CY);
    }

    private void drawLights(Graphics2D g2) {
        drawSquare(g2, L - 20, T - 20, lights.lights_top_left);   // NW
        drawSquare(g2, R + 20, T - 20, lights.lights_top_right);  // NE
        drawSquare(g2, L - 20, B + 20, lights.lights_down_left);  // SW
        drawSquare(g2, R + 20, B + 20, lights.lights_down_right); // SE
    }

    private void drawSquare(Graphics2D g2, int x, int y, boolean green) {
        g2.setColor(green ? Color.GREEN : Color.RED);
        g2.fillRect(x - 6, y - 6, 12, 12);
    }

    private void drawCars(Graphics2D g2) {
        for (Car c : cars) {
            g2.setColor(c.color);
            g2.fillRect(c.x, c.y, CAR_SIZE, CAR_SIZE);
        }
    }
}
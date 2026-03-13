import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel {
    private final int W, H;
    private final List<Car> cars = new ArrayList<>();
    private final TrafficLights lights = new TrafficLights();

    private final int roadW = 100;
    private final int hRoadY = 300;
    private final int vRoadX = 350;
    private final int centerX = 400;
    private final int centerY = 350;

    private static final int CAR_SIZE = 30;

    private Timer timer;

    public GamePanel(int w, int h) {
        this.W = w;
        this.H = h;
        setPreferredSize(new Dimension(W, H));
        setBackground(Color.gray);
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> keyUp();
                    case KeyEvent.VK_DOWN -> keyDown();
                    case KeyEvent.VK_LEFT -> keyLeft();
                    case KeyEvent.VK_RIGHT -> keyRight();
                    case KeyEvent.VK_R -> keyRandom();
                    case KeyEvent.VK_ESCAPE -> System.exit(0);
                }
            }
        });
    }

    void start() {
        timer = new Timer(16, e -> {
            TrafficLightsSystem.update(lights, cars);
            for (Car c : cars) {
                TrafficLightsSystem.applyToCar(c, lights);
                if (c.moving && !c.nextCar(cars))
                    c.moveCar();
                if (!c.turned)
                    c.redirect();
            }
            cars.removeIf(c -> !(c.y <= 740 && c.y >= -40 && c.x <= 840 && c.x >= -40));
            repaint();
        });
        timer.start();
    }

    private void keyUp() {
        Car car = new Car(410, 700, Direction.Down, Car.randomColor());
        pushCar(car, 610, null, Direction.Down);
    }

    private void keyDown() {
        Car car = new Car(360, -30, Direction.Top, Car.randomColor());
        pushCar(car, 60, null, Direction.Top);
    }

    private void keyRight() {
        Car car = new Car(-30, 360, Direction.Right, Car.randomColor());
        pushCar(car, null, 60, Direction.Right);
    }

    private void keyLeft() {
        Car car = new Car(800, 310, Direction.Left, Car.randomColor());
        pushCar(car, null, 740, Direction.Left);
    }

    private void keyRandom() {
        switch ((int) (Math.random() * 4)) {
            case 0 -> keyUp();
            case 1 -> keyDown();
            case 2 -> keyLeft();
            default -> keyRight();
        }
    }

    private void pushCar(Car car, Integer checkY, Integer checkX, Direction dir) {
        final int MAX_CARS = 28;
        boolean canPush;

        if (cars.isEmpty()) {
            canPush = true;
        } else {
            Car last = getLastCarDir(dir);
            if (last != null) {
                if (checkY != null && checkX == null) {
                    int yLimit = checkY;
                    if (car.dir == Direction.Down) {
                        canPush = (last.y < yLimit);
                    } else {
                        canPush = (last.y > yLimit);
                    }
                } else if (checkX != null && checkY == null) {
                    int xLimit = checkX;
                    if (car.dir == Direction.Right) {
                        canPush = (last.x > xLimit);
                    } else {
                        canPush = (last.x < xLimit);
                    }
                } else {
                    canPush = false; 
                }
            } else {
                canPush = true;
            }
        }
        if (canPush && cars.size() < MAX_CARS)
            cars.add(car);
    }

    private Car getLastCarDir(Direction dir) {
        for (int i = cars.size() - 1; i >= 0; i--) {
            if (cars.get(i).dir == dir)
                return cars.get(i);
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics gg) {
        super.paintComponent(gg);
        Graphics2D g = (Graphics2D) gg.create();
        g.setColor(Color.BLACK);
        g.fillRect(0, hRoadY, W, 2); 
        g.fillRect(0, hRoadY + roadW - 2, W, 2); 
        g.fillRect(vRoadX, 0, 2, H); 
        g.fillRect(vRoadX + roadW - 2, 0, 2, H); 

        
        int dash = 14, gap = 10, thick = 3;

        dashedX(g, hRoadY + roadW / 2, 0, vRoadX - 10, dash, gap, thick, Color.BLACK);
        dashedX(g, hRoadY + roadW / 2, vRoadX + roadW + 10, W, dash, gap, thick, Color.BLACK);

        dashedY(g, vRoadX + roadW / 2, 0, hRoadY - 10, dash, gap, thick, Color.BLACK);
        dashedY(g, vRoadX + roadW / 2, hRoadY + roadW + 10, H, dash, gap, thick, Color.BLACK);

        
        g.fillOval((centerX - 2), (centerY - 2), 4, 4);

        
        drawLight(g, 315 + 15, 265 + 15, lights.lights_top_left);
        drawLight(g, 315 + 15, 405 + 15, lights.lights_down_left);
        drawLight(g, 455 + 15, 405 + 15, lights.lights_down_right);
        drawLight(g, 455 + 15, 265 + 15, lights.lights_top_right);

        
        g.setColor(Color.BLACK);
        g.setFont(g.getFont().deriveFont(14f));
        g.drawString("Arrows spawn, R random, Esc quit", 10, 20);

        
        for (Car c : cars) {
            g.setColor(c.color);
            g.fillRect(c.x, c.y, CAR_SIZE, CAR_SIZE);
            g.setColor(Color.DARK_GRAY);
            g.drawRect(c.x, c.y, CAR_SIZE, CAR_SIZE);
        }

        
    }

    private static void drawLight(Graphics2D g, int cx, int cy, boolean green) {
        g.setColor(green ? new Color(0, 170, 0) : new Color(200, 0, 0));
        g.fillOval(cx - 10, cy - 10, 20, 20);
        g.setColor(Color.DARK_GRAY);
        g.drawOval(cx - 10, cy - 10, 20, 20);
    }

    private static void dashedX(Graphics2D g, int y, int x0, int x1, int dash, int gap, int thick, Color c) {
        g.setColor(c);
        Stroke old = g.getStroke();
        g.setStroke(new BasicStroke(thick, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        int x = x0;
        while (x < x1) {
            int x2 = Math.min(x + dash, x1);
            g.drawLine(x, y, x2, y);
            x += dash + gap;
        }
        g.setStroke(old);
    }

    private static void dashedY(Graphics2D g, int x, int y0, int y1, int dash, int gap, int thick, Color c) {
        g.setColor(c);
        Stroke old = g.getStroke();
        g.setStroke(new BasicStroke(thick, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        int y = y0;
        while (y < y1) {
            int y2 = Math.min(y + dash, y1);
            g.drawLine(x, y, x, y2);
            y += dash + gap;
        }
        g.setStroke(old);
    }
}
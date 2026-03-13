import java.awt.Color;

public class Car {
    public int x, y;
    public Direction dir, origin;
    public Color color;
    public boolean turned;
    public boolean moving;

    public static final Color PURPLE_RGB = new Color(160, 32, 240);

    public Car(int x, int y, Direction dir, Color color) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.origin = dir;
        this.color = color;
        this.turned = false;
        this.moving = false;
    }

    public void moveCar() {
        switch (dir) {
            case Top -> y += 2;
            case Down -> y -= 2;
            case Left -> x -= 2;
            case Right -> x += 2;
        }
    }

    public void redirect() {
        if (dir == Direction.Top) {
            if (x == 360 && y == 310 && color.equals(PURPLE_RGB)) {
                dir = Direction.Left;
                turned = true;
            } else if (x == 360 && y == 360 && color.equals(Color.YELLOW)) {
                dir = Direction.Right;
                turned = true;
            }
        } else if (dir == Direction.Down) {
            if (y == 310 && x == 410 && color.equals(Color.YELLOW)) {
                dir = Direction.Left;
                turned = true;
            } else if (y == 360 && x == 410 && color.equals(PURPLE_RGB)) {
                dir = Direction.Right;
                turned = true;
            }
        } else if (dir == Direction.Right) {
            if (x == 360 && y == 360 && color.equals(PURPLE_RGB)) {
                dir = Direction.Top;
                turned = true;
            } else if (x == 410 && y == 360 && color.equals(Color.YELLOW)) {
                dir = Direction.Down;
                turned = true;
            }
        } else if (dir == Direction.Left) {
            if (x == 360 && y == 310 && color.equals(Color.YELLOW)) {
                dir = Direction.Top;
                turned = true;
            } else if (x == 410 && y == 310 && color.equals(PURPLE_RGB)) {
                dir = Direction.Down;
                turned = true;
            }
        }
    }

    static Color randomColor() {
        int r = (int) (Math.random() * 4);
        return switch (r) {
            case 0 -> Color.BLUE;
            case 1 -> Color.YELLOW;
            case 2 -> Color.GREEN;
            default -> PURPLE_RGB;
        };
    }

    public boolean nextCar(java.util.List<Car> cars) {
        final int SAFE_DISTANCE = 65;
        for (Car other : cars) {
            if (other == this)
                continue;
            if (other.dir == this.dir) {
                switch (dir) {
                    case Top -> {
                        if (other.y > this.y && other.y - this.y <= SAFE_DISTANCE)
                            return true;
                    }
                    case Down -> {
                        if (other.y < this.y && this.y - other.y <= SAFE_DISTANCE)
                            return true;
                    }
                    case Left -> {
                        if (other.x < this.x && this.x - other.x <= SAFE_DISTANCE)
                            return true;
                    }
                    case Right -> {
                        if (other.x > this.x && other.x - this.x <= SAFE_DISTANCE)
                            return true;
                    }
                }
            }
        }
        return false;
    }
}

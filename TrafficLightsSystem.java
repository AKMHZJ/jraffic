import java.util.List;
 

public class TrafficLightsSystem {
    static final Lane[] LANES = new Lane[] {
            new Lane(Direction.Right, -30, 360, 300, 360),
            new Lane(Direction.Top, 360, -30, 360, 240),
            new Lane(Direction.Left, 800, 310, 470, 310),
            new Lane(Direction.Down, 410, 700, 410, 420),
    };

    static final long GREEN_MS = 2500;
    static final long CLEARANCE_MS = 1500;
    static final long CHECK_INTERVAL_MS = 1000;

    public static void update(TrafficLights l, List<Car> cars) {
        long now = nowMs();
        long elapsed = now - l.phaseStartMs;
        int curIdx = dirIdx(l.current_direction);

        if (l.state) {
            if (elapsed >= GREEN_MS) {
                l.state = false;
                allRed(l);
                l.phaseStartMs = now;
                return;
            }

            if (now - l.lastLaneCheckMs >= CHECK_INTERVAL_MS) {
                l.lastLaneCheckMs = now;
                Direction curDir = idxDir(curIdx);
                if (countWaitingCars(cars, curDir) == 0) {
                    l.state = false;
                    allRed(l);
                    l.phaseStartMs = now;
                }
            }
        } else if (elapsed >= CLEARANCE_MS) {
            int nextIdx = (curIdx + 1) % 4;
            setGreen(l, nextIdx);
            l.state = true;
            l.phaseStartMs = now;
            l.lastLaneCheckMs = now;
        }
    }

    public static int countWaitingCars(List<Car> cars, Direction direction) {
        return (int) cars.stream().filter(c -> {
            if (c.dir != direction) {
                return false;
            }

            return switch (direction) {
                case Down -> c.y >= 420;
                case Top -> c.y <= 240;
                case Right -> c.x <= 300;
                case Left -> c.x >= 470;
            };
        }).count();
    }

    public static void applyToCar(Car car, TrafficLights l) {
        boolean mustStop = (!l.lights_down_right && car.dir == Direction.Down && car.y == 420) ||
                (!l.lights_top_left && car.dir == Direction.Top && car.y == 240) ||
                (!l.lights_down_left && car.dir == Direction.Right && car.x == 300) ||
                (!l.lights_top_right && car.dir == Direction.Left && car.x == 470);
        car.moving = !mustStop;
    }

    public static int dirIdx(Direction d) {
        return switch (d) {
            case Right -> 0;
            case Top -> 1;
            case Left -> 2;
            default -> 3;
        };
    }

    public static Direction idxDir(int i) {
        return switch (i) {
            case 0 -> Direction.Right;
            case 1 -> Direction.Top;
            case 2 -> Direction.Left;
            default -> Direction.Down;
        };
    }

    public static void setGreen(TrafficLights l, int i) {
        l.lights_down_left = i == 0;
        l.lights_top_left = i == 1;
        l.lights_top_right = i == 2;
        l.lights_down_right = i == 3;
        l.current_direction = idxDir(i);
    }

    public static void allRed(TrafficLights l) {
        l.lights_down_left = false;
        l.lights_top_left = false;
        l.lights_top_right = false;
        l.lights_down_right = false;
    }

    public static int nextActiveIdx(int cur, boolean[] active) {
        for (int step = 1; step <= 4; step++) {
            int i = (cur + step) % 4;
            if (active[i])
                return i;
        }
        return cur;
    }

    public static long nowMs() {
        return System.currentTimeMillis();
    }
}
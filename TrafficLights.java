
public class TrafficLights {
    public boolean lights_top_left = false;
    public boolean lights_down_right = false;
    public boolean lights_top_right = false;
    public boolean lights_down_left = false;
    public Direction current_direction = Direction.Right;
    public boolean state = false;
    public long phaseStartMs = TrafficLightsSystem.nowMs();
    public long lastLaneCheckMs = phaseStartMs;
}
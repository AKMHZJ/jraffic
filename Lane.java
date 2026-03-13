public class Lane {
    public final Direction dir;
    public final int sx, sy, ex, ey;

    public Lane(Direction d, int sx, int sy, int ex, int ey) {
        this.dir = d;
        this.sx = sx;
        this.sy = sy;
        this.ex = ex;
        this.ey = ey;
    }
}
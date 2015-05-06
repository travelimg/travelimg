package at.ac.tuwien.qse.sepm.entities;

/**
 * Created by christoph on 06.05.15.
 */
public class Koordinate {

    private double x;
    private double y;

    Koordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}

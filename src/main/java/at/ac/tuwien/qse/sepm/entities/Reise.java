package at.ac.tuwien.qse.sepm.entities;

/**
 * Created by David on 18.05.2015.
 */
public class Reise {
    private double latCentroid;
    private double longCentroid;
    private String name;

    public Reise(double latCentroid, double longCentroid, String name) {
        this.latCentroid = latCentroid;
        this.longCentroid = longCentroid;
        this.name = name;
    }

    public double getLatCentroid() {
        return latCentroid;
    }

    public void setLatCentroid(double latCentroid) {
        this.latCentroid = latCentroid;
    }

    public double getLongCentroid() {
        return longCentroid;
    }

    public void setLongCentroid(double longCentroid) {
        this.longCentroid = longCentroid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

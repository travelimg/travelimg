package at.ac.tuwien.qse.sepm.entities;

public class Exif {
    private int id;
    private String exposure;
    private double aperture;
    private double focalLength;
    private int iso;
    private boolean flash;
    private String make;
    private String model;
    private double altitude;

    public Exif(int id, String exposure, double aperture, double focalLength, int iso, boolean flash, String make, String model, double altitude) {
        this.id = id;
        this.exposure = exposure;
        this.aperture = aperture;
        this.focalLength = focalLength;
        this.iso = iso;
        this.flash = flash;
        this.make = make;
        this.model = model;
        this.altitude = altitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExposure() {
        return exposure;
    }

    public void setExposure(String exposure) {
        this.exposure = exposure;
    }

    public double getAperture() {
        return aperture;
    }

    public void setAperture(double aperture) {
        this.aperture = aperture;
    }

    public double getFocalLength() {
        return focalLength;
    }

    public void setFocalLength(double focalLength) {
        this.focalLength = focalLength;
    }

    public int getIso() {
        return iso;
    }

    public void setIso(int iso) {
        this.iso = iso;
    }

    public boolean isFlash() {
        return flash;
    }

    public void setFlash(boolean flash) {
        this.flash = flash;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "Exif{" +
                "id=" + id +
                ", exposure='" + exposure + '\'' +
                ", aperture=" + aperture +
                ", focalLength=" + focalLength +
                ", iso=" + iso +
                ", flash=" + flash +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", altitude=" + altitude +
                '}';
    }
}

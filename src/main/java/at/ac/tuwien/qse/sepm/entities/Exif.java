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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Exif exif = (Exif) o;

        if (id != exif.id) return false;
        if (Double.compare(exif.aperture, aperture) != 0) return false;
        if (Double.compare(exif.focalLength, focalLength) != 0) return false;
        if (iso != exif.iso) return false;
        if (flash != exif.flash) return false;
        if (Double.compare(exif.altitude, altitude) != 0) return false;
        if (exposure != null ? !exposure.equals(exif.exposure) : exif.exposure != null) return false;
        if (make != null ? !make.equals(exif.make) : exif.make != null) return false;
        return !(model != null ? !model.equals(exif.model) : exif.model != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + (exposure != null ? exposure.hashCode() : 0);
        temp = Double.doubleToLongBits(aperture);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(focalLength);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + iso;
        result = 31 * result + (flash ? 1 : 0);
        result = 31 * result + (make != null ? make.hashCode() : 0);
        result = 31 * result + (model != null ? model.hashCode() : 0);
        temp = Double.doubleToLongBits(altitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}

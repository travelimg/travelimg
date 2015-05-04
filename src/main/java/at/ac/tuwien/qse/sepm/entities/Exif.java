package at.ac.tuwien.qse.sepm.entities;

import java.sql.Timestamp;

public class Exif {
    private int id;
    private Timestamp date;
    private double exposure;
    private double aperture;
    private double focalLength;
    private int iso;
    private boolean flash;
    private String cameraModel;
    private double longitude;
    private double latitude;
    private double altitude;

    public Exif(int id, Timestamp date, double exposure, double aperture, double focalLength, int iso, boolean flash, String cameraModel, double longitude, double latitude, double altitude) {
        this.id = id;
        this.date = date;
        this.exposure = exposure;
        this.aperture = aperture;
        this.focalLength = focalLength;
        this.iso = iso;
        this.flash = flash;
        this.cameraModel = cameraModel;
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public double getExposure() {
        return exposure;
    }

    public void setExposure(double exposure) {
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

    public String getCameraModel() {
        return cameraModel;
    }

    public void setCameraModel(String cameraModel) {
        this.cameraModel = cameraModel;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
}

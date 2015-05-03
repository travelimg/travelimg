package at.ac.tuwien.qse.sepm.entities;

import java.util.Date;

public class Exif {
    private Date date;
    private float exposure;
    private float aperture;
    private float focalLength;
    private int iso;
    private boolean flash;
    private String cameraModel;
    private float longitude;
    private float latitude;
    private float altitude;

    public Exif(Date date, float exposure, float aperture, float focalLength, int iso, boolean flash, String cameraModel, float longitude, float latitude, float altitude) {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getExposure() {
        return exposure;
    }

    public void setExposure(float exposure) {
        this.exposure = exposure;
    }

    public float getAperture() {
        return aperture;
    }

    public void setAperture(float aperture) {
        this.aperture = aperture;
    }

    public float getFocalLength() {
        return focalLength;
    }

    public void setFocalLength(float focalLength) {
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

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getAltitude() {
        return altitude;
    }

    public void setAltitude(float altitude) {
        this.altitude = altitude;
    }
}

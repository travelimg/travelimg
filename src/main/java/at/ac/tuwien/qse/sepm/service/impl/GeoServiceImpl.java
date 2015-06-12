package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.service.GeoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class GeoServiceImpl implements GeoService {
    private static final org.apache.logging.log4j.Logger logger = LogManager
            .getLogger(ClusterServiceImpl.class);

    public Place getPlaceByGeoData(double latitude, double longitude) throws ServiceException {
        logger.debug("getPlaceByGeoData() Latitude: " + latitude + " Longitude: " + longitude);
        Place p = new Place(1, "Unknown city", "Unknown country", latitude, longitude, null);
        String json = readUrl(
                "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + ","
                        + longitude);
        JSONObject obj = null;
        try{
            obj = new JSONObject(json);
        }
        catch(JSONException e){
            logger.error("Error in JSON file {}", e.getMessage());
            throw new ServiceException(e);
        }

        String status = obj.getString("status");
        if (!status.equals("OK")) {
            return p;
        }
        JSONArray results = obj.getJSONArray("results");
        JSONObject result = results.getJSONObject(0);
        JSONArray addressComponentsArray = result.getJSONArray("address_components");

        for (int i = 0; i < addressComponentsArray.length(); i++) {
            if (addressComponentsArray.getJSONObject(i).getJSONArray("types").get(0).equals(
                    "locality")) {
                p.setCity(addressComponentsArray.getJSONObject(i).getString("long_name"));
            }
            else if (addressComponentsArray.getJSONObject(i).getJSONArray("types").get(0).equals(
                    "administrative_area_level_1")) {
                p.setCity(addressComponentsArray.getJSONObject(i).getString("long_name"));
            }
            if (addressComponentsArray.getJSONObject(i).getJSONArray("types").get(0).equals(
                    "country")){
                p.setCountry(addressComponentsArray.getJSONObject(i).getString("long_name"));
            }
        }

        return p;
    }

    public String readUrl(String urlString) throws ServiceException {

        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException ex) {
            logger.error("Malformed url", ex);
            throw new ServiceException("Malformed url", ex);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            StringBuilder buffer = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            return buffer.toString();
        } catch (IOException ex) {
            logger.error("Failed to read url {}", url, ex);
            throw new ServiceException("Failed to read url", ex);
        }
    }
}

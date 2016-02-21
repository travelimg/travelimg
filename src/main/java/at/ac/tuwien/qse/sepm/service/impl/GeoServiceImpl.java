package at.ac.tuwien.qse.sepm.service.impl;

/*
 * Copyright (c) 2015 Lukas Eibensteiner
 * Copyright (c) 2015 Kristoffer Kleine
 * Copyright (c) 2015 Branko Majic
 * Copyright (c) 2015 Enri Miho
 * Copyright (c) 2015 David Peherstorfer
 * Copyright (c) 2015 Marian Stoschitzky
 * Copyright (c) 2015 Christoph Wasylewski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons
 * to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT
 * SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
        Place p = new Place(1, "Unknown city", "Unknown country", latitude, longitude);
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
            logger.debug("Status is {}",status);
            return p;
        }

        JSONArray results = obj.getJSONArray("results");
        logger.debug("Got {} address_components",results.length());
        for(int i = 0; i<results.length(); i++){
            JSONObject result = results.getJSONObject(i);
            JSONArray addressComponentsArray = result.getJSONArray("address_components");
            for (int j = 0; j < addressComponentsArray.length(); j++) {
                if(!p.getCity().equals("Unknown city") && !p.getCountry().equals("Unknown country")){
                    //stop here, as we found the city and country
                    break;
                }
                if (p.getCountry().equals("Unknown country")
                        && addressComponentsArray.getJSONObject(j).getJSONArray("types").length() > 0
                        && addressComponentsArray.getJSONObject(j).getJSONArray("types").get(0).equals(
                        "country")){
                    logger.debug("Found country {} at address_component[{}]",addressComponentsArray.getJSONObject(j).getString("long_name"),i);
                    p.setCountry(addressComponentsArray.getJSONObject(j).getString("long_name"));
                }
                if (p.getCity().equals("Unknown city")
                        && addressComponentsArray.getJSONObject(j).getJSONArray("types").length() > 0
                        && addressComponentsArray.getJSONObject(j).getJSONArray("types").get(0).equals(
                        "locality")) {
                    logger.debug("Found city {} at address_component[{}]",addressComponentsArray.getJSONObject(j).getString("long_name"),i);
                    p.setCity(addressComponentsArray.getJSONObject(j).getString("long_name"));
                }
                else if (p.getCity().equals("Unknown city") &&
                        addressComponentsArray.getJSONObject(j).getJSONArray("types").length() > 0
                        && addressComponentsArray.getJSONObject(j).getJSONArray("types").get(0).equals(
                        "administrative_area_level_1")) {
                    logger.debug("Found administrative level area {} at address_component[{}]",addressComponentsArray.getJSONObject(j).getString("long_name"),i);
                    p.setCity(addressComponentsArray.getJSONObject(j).getString("long_name"));
                }
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

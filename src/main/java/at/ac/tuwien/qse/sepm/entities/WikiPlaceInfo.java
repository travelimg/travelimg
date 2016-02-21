package at.ac.tuwien.qse.sepm.entities;

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

/**
 * A WikiPlaceInfo object contains Wikipedia-Information for a specific place (i.e. city)
 */
public class WikiPlaceInfo {

    private String placeName;
    private String countryName;
    private String description;
    private Double elevation;
    private Double area;
    private Integer population;
    private String utcOffset;
    private String currency;
    private String language;

    /**
     * Create a new WikiPlaceInfo object.
     *
     * @param placeName name of the place
     * @param countryName name of the place's country
     * @param description short text about the place
     * @param elevation height over sealevel in m
     * @param area size of the place in m2
     * @param population number of inhabitants
     * @param utcOffset time offset concerning Greenwich Mean Time
     * @param currency currency accepted in this place
     * @param language language spoken in this place
     */
    public WikiPlaceInfo(String placeName, String countryName, String description, Double elevation,
            Double area, Integer population, String utcOffset, String currency, String language) {
        this.placeName = placeName;
        this.countryName = countryName;
        this.description = description;
        this.elevation = elevation;
        this.area = area;
        this.population = population;
        this.utcOffset = utcOffset;
        this.currency = currency;
        this.language = language;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getElevation() {
        return elevation;
    }

    public void setElevation(Double elevation) {
        this.elevation = elevation;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public String getUtcOffset() {
        return utcOffset;
    }

    public void setUtcOffset(String utcOffset) {
        this.utcOffset = utcOffset;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Return place name and country name.
     * @return 'WikiPlaceInfo' + place name and country name
     */
    public String toString() {
        return "WikiPlaceInfo ["
                + getPlaceName() + ", "
                + getCountryName() + "]";

    }
}

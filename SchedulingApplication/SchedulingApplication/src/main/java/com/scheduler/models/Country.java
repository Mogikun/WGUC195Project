// Country.java
package com.scheduler.models;

/**
 * Represents a country in the scheduling system
 * @author Calvin Mogi
 */
public class Country {
    private int countryId;
    private String country;
    private String createDate;
    private String createdBy;
    private String lastUpdate;
    private String lastUpdatedBy;

    /**
     * Constructs a new Country
     * @param countryId The country's ID
     * @param country The country's name
     */
    public Country(int countryId, String country) {
        this.countryId = countryId;
        this.country = country;
    }

    // Getters and Setters
    public int getCountryId() { return countryId; }
    public void setCountryId(int countryId) { this.countryId = countryId; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}


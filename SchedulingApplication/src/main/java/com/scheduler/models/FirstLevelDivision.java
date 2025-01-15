// FirstLevelDivision.java
package com.scheduler.models;

/**
 * Represents a first-level division (state/province) in the scheduling system
 * @author Calvin Mogi
 */
public class FirstLevelDivision {
    private int divisionId;
    private String division;
    private int countryId;
    private String createDate;
    private String createdBy;
    private String lastUpdate;
    private String lastUpdatedBy;

    /**
     * Constructs a new FirstLevelDivision
     * @param divisionId The division's ID
     * @param division The division's name
     * @param countryId The associated country's ID
     */
    public FirstLevelDivision(int divisionId, String division, int countryId) {
        this.divisionId = divisionId;
        this.division = division;
        this.countryId = countryId;
    }

    // Getters and Setters
    public int getDivisionId() { return divisionId; }
    public void setDivisionId(int divisionId) { this.divisionId = divisionId; }
    public String getDivision() { return division; }
    public void setDivision(String division) { this.division = division; }
    public int getCountryId() { return countryId; }
    public void setCountryId(int countryId) { this.countryId = countryId; }
}
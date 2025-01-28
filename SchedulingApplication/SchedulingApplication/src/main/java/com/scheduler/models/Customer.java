package com.scheduler.models;

public class Customer {
    private int customerId;
    private String customerName;
    private String address;
    private String postalCode;
    private String phone;
    private int divisionId;
    private String divisionName;
    private String country;

    public Customer(int customerId, String customerName, String address,
                    String postalCode, String phone, int divisionId) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.divisionId = divisionId;
    }

    // Getters and Setters
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getDivisionId() { return divisionId; }
    public void setDivisionId(int divisionId) { this.divisionId = divisionId; }

    public String getDivisionName() { return divisionName; }
    public void setDivisionName(String divisionName) { this.divisionName = divisionName; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}
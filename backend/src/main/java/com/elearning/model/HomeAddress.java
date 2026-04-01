package com.elearning.model;

/**
 * Represents a physical home address with street, city, state, and zip code.
 */
public class HomeAddress {
    private String street;
    private String city;
    private String state;
    private String zipCode;

    public HomeAddress() {
    }

    /**
     * Constructs a HomeAddress with all fields.
     *
     * @param street  street address
     * @param city    city name
     * @param state   state abbreviation
     * @param zipCode postal zip code
     */
    public HomeAddress(String street, String city, String state, String zipCode) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
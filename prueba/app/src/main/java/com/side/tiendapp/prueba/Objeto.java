package com.side.tiendapp.prueba;


public class Objeto {
    String booking_id;
    String approx_address;
    String address;
    String neighborhood;
    Integer code;
    Double lat;
    Double lon;


    public String getBooking_id() {
        if(booking_id == null){
            booking_id ="";
        }
        return booking_id;
    }

    public void setBooking_id(String booking_id) {
        this.booking_id = booking_id;
    }

    public String getApprox_address() {
        if(approx_address == null){
            approx_address ="";
        }
        return approx_address;
    }

    public void setApprox_address(String approx_address) {
        this.approx_address = approx_address;
    }

    public String getAddress() {
        if(address == null){
            address ="";
        }
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNeighborhood() {
        if(neighborhood == null){
            neighborhood ="";
        }
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public Integer getCode() {
        if(code == null){
            code = 0;
        }
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Double getLat() {
        if(lat == null){
            lat = 0D;
        }
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        if(lon == null){
            lon = 0D;
        }
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }
}

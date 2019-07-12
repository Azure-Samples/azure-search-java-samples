package model;

import javax.json.JsonObject;

public class Address {
	private String streetAddress = "";
	private String city = "";
	private String stateProvince = "";
	private String postalCode = "";
	private String country = "";
	
	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStateProvince() {
		return stateProvince;
	}

	public void setStateProvince(String stateProvince) {
		this.stateProvince = stateProvince;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	
	public String getCountry() {
		return country;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}
	
	public String toString() {
		return streetAddress + '\n' + city + ", " + stateProvince + " " + postalCode + ", " + country;
	}
	
	public static Address FromJsonObject(JsonObject object) {
		Address a = new Address();
		a.setStreetAddress(object.getString("StreetAddress"));
		a.setCity(object.getString("City"));
		a.setStateProvince(object.getString("StateProvince"));
		a.setPostalCode(object.getString("PostalCode"));
		a.setCountry(object.getString("Country"));
		return a;
	}
}

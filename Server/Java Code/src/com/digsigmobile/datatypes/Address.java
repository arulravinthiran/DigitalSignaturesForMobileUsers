package com.digsigmobile.datatypes;

import java.io.Serializable;

import com.digsigmobile.exceptions.InvalidInputException;

public class Address implements Serializable
{
	/**
	 * serialVersionUID of Serializable interface
	 */
	private static final long serialVersionUID = 1L;
	
	private String street;
	private String city;
	private String state;
	private String country;
	private String zip;
	
	public Address(String country, String state, String city,
			String zip, String street) throws InvalidInputException {
		setAddress(street, city, state, country, zip);
	}
	
	public String getStreet() {
		return street;
	}
	public String getCity() {
		return city;
	}
	public String getState() {
		return state;
	}
	public String getCountry() {
		return country;
	}
	public String getZip() {
		return zip;
	}

	public String getAddress() {
		return this.toString();
	}

	private void setAddress(String street, String city, String state, 
			String country, String zip) throws InvalidInputException {
		this.street = street;
		this.city = city;
		this.state = state;
		this.country = country;
		this.zip = zip;
		if (!isValid()) throw new InvalidInputException("Not a valid address");
	}

	private boolean isValid() {
		if (this.city != null && !this.city.isEmpty() &&
			this.country != null && !this.country.isEmpty() &&
			this.state != null && !this.state.isEmpty() &&
			this.street != null && !this.street.isEmpty() &&
			this.zip != null && !this.zip.isEmpty()) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {  
		return street + "\n"
				+ city + ", " + state + " " + ", " + country + " " + zip;
	}

}

package com.digsigmobile.datatypes;

import java.io.Serializable;

import com.digsigmobile.datatypes.PhoneNumber;
import com.digsigmobile.exceptions.InvalidInputException;

public final class PhoneNumber implements Serializable
{
	/**
	 * serialVersionUID of Serializable interface
	 */
	private static final long serialVersionUID = 1L;
	
    private int area;   // area code (3 digits)
    private int exch;   // exchange  (3 digits)
    private int ext;    // extension (4 digits)

    public PhoneNumber(int area, int exch, int ext) throws InvalidInputException {
        setPhoneNumber(area, exch, ext);
    }

    public PhoneNumber(String phoneNumber)throws InvalidInputException {
    	setPhoneNumber(phoneNumber);
    }
    
    public String getPhoneNumber() {
    	return this.toString();
    }
    
    private void setPhoneNumber(String phoneNumber) throws InvalidInputException {
    	if(isValid(phoneNumber)) {
    		this.area = Integer.parseInt(phoneNumber.substring(0, 3));
    		this.exch = Integer.parseInt(phoneNumber.substring(3, 6));
    		this.ext = Integer.parseInt(phoneNumber.substring(6, 10));
    	} else {
    		throw new InvalidInputException("Not a valid phone number");
    	}
    }
    
    private void setPhoneNumber(int area, int exch, int ext) throws InvalidInputException{
    	this.area = area;
        this.exch = exch;
        this.ext  = ext;
        if (!isValid()) throw new InvalidInputException("Not a valid phone number");
    }
    
    private boolean isValid(String phone) {
		if (phone != null && !phone.isEmpty() &&
			phone.matches("\\d{10}")) {
			return true;
		}
		return false;
	}
    
    private boolean isValid() {
		if (this.area <= 0 ||
			this.exch < 0 ||
			this.ext < 0) {
			return false;
		}
		return true;
	}

	// how you're supposed to implement equals
    public boolean equals(Object y) {
        if (y == this) { return true;  }
        if (y == null) { return false; }
        if (y.getClass() != this.getClass()) { return false; }
        PhoneNumber a = this;
        PhoneNumber b = (PhoneNumber) y;
        return (a.area == b.area) && (a.exch == b.exch) && (a.ext == b.ext);
    }

    // 0 for padding with leading 0s
    public String toString() {
        return String.format("%03d%03d%04d", area, exch, ext);
    }

    // satisfies the hashCode contract
    public int hashCode() {
        return 10007 * (area + 1009 * exch) + ext;
    }

}
package com.example.appointmentmanager;

public class PatientInfo {
	private String Name;
	private String Address;
	private String Phone;
	
	public PatientInfo(String name, String address, String phone){
		Name=name;
		Address=address;
		Phone=phone;
	}

	public void setName(String name){
		Name=name;
	}
	public void setAddress(String address){
		Address=address;
	}
	public void setPhone(String phone){
		Phone=phone;
	}
	
	public String getName(){
		return Name;
	}
	public String getAddress(){
		return Address;
	}
	public String getPhone(){
		return Phone;
	}
}

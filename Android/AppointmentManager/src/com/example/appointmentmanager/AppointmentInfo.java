package com.example.appointmentmanager;

import java.util.Date;

public class AppointmentInfo {
	private PatientInfo Patient;
	private Date AppointmentTime;
	
	public AppointmentInfo(PatientInfo patient, Date time){
		Patient = patient;
		AppointmentTime=time;
	}
	
	public void setTime(Date time){
		AppointmentTime=time;
	}
	
	public PatientInfo getPatient(){
		return Patient;
	}
	
	public Date getTime(){
		return AppointmentTime;
	}
}

package com.um.util;

import com.um.model.EHealthRecord;

public class EhealthUtil {
	
	/**
	 *  encryption the record
	 * @param eHealthRecord
	 * @return
	 */
	public static EHealthRecord encryptionRecord(EHealthRecord eHealthRecord){
		
		// keep the information of patient privacy
		// remove the address, profession, phoneNumber, contact
		if (eHealthRecord == null || eHealthRecord.getPatientInfo() == null) {
			return eHealthRecord;
		}
		
		// remove profession
		eHealthRecord.getPatientInfo().setProfession("xxxxxxx");
		
		// remove phonenumber
		eHealthRecord.getPatientInfo().setPhoneNumber("xxxxxxxx");
		
		// remove contact
		eHealthRecord.getPatientInfo().setContact("xxxxxxx");
		
		// remove address
		eHealthRecord.getPatientInfo().setAddress("xxxxxxx");
		
		return eHealthRecord;
	}
	
}

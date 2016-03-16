package com.um.myapp.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.um.data.DataBaseSetting;
import com.um.model.EHealthRecord;
import com.um.util.EhealthUtil;
import com.um.util.MedicineByDescription;

@Controller
public class QueryController {
	
	/**
	 * Query all record based on the batch and patient's name
	 * @param batch
	 * @param pname
	 * @return
	 */
	@RequestMapping(value="recordquery",method=RequestMethod.GET)
	public ModelAndView queryRecord(String batch, String pname){
		if( pname == "" || pname.equals("")){
			return new ModelAndView("dquery");
		}
		
		// get batch records
		List<EHealthRecord> eHealthRecordsByBatch = MedicineByDescription.getRecordsByBatch(batch);
		// get records by name
		List<EHealthRecord> ehealthList = new ArrayList<EHealthRecord>();
		for (EHealthRecord e : eHealthRecordsByBatch) {
			if (e.getPatientInfo() == null) {
				continue;
			}
			if (e.getPatientInfo().getName().equals(pname.trim())) {
				ehealthList.add(e);
			}
		}
		
		ModelAndView mv = new ModelAndView("recordQuery");
		
		// Keep privacy
		for (EHealthRecord eHealthRecord : ehealthList) {
			eHealthRecord = EhealthUtil.encryptionRecord(eHealthRecord);
		}
        
		if(ehealthList != null && ehealthList.size() > 0){
    	   mv.addObject("ehealthrecrods", ehealthList);
		}
		return mv;
	}
	
	/**
	 * Get the detail information of one patient
	 * @param ehealthregno
	 * @return
	 */
	@RequestMapping(value="detailRecord",method=RequestMethod.GET)
	public ModelAndView detailRecrod(String ehealthregno){
		
		if ("".equals(ehealthregno)) {
        	return new ModelAndView("dquery");
		}
		
		EHealthRecord eHealthRecord = null;
        ModelAndView mv = null;
        
        // all record data
        List<EHealthRecord> allList = MedicineByDescription.getAllRecords();
     	
     	for (EHealthRecord e : allList) {
     		if (e.getRegistrationno().equals(ehealthregno)) {
     			
     			eHealthRecord = EhealthUtil.encryptionRecord(e);
     		}
     	}
     			
     	if( eHealthRecord != null){
     		mv =  new ModelAndView("detail");
                 	
            mv.addObject("ehealthrecordss",eHealthRecord);
            mv.addObject("allCnMedicines", eHealthRecord.getChineseMedicines());
            mv.addObject("allWeMedicines", eHealthRecord.getWesternMedicines());
     	}else {
     		mv = new ModelAndView("dquery");
     	}
     			
     	return mv;
	}
	
	
	/**
	 * Edit the record information of one patient
	 * @param ehealthregno
	 * @return
	 */
	@RequestMapping(value="editRecord",method=RequestMethod.GET)
	public ModelAndView editRecrod(String ehealthregno){
		
		EHealthRecord eHealthRecord = null;
		if("".equals(ehealthregno)){
        	return new ModelAndView("dquery");
		}else{
			
			List<EHealthRecord> allRecordsList = MedicineByDescription.getAllRecords();
			for (EHealthRecord record : allRecordsList) {
				if (ehealthregno.equals(record.getRegistrationno())) {
					eHealthRecord = record;
					break;
				}
			}
			
			if(eHealthRecord != null){
            	ModelAndView mv = new ModelAndView("editRecord");
            	mv.addObject("ehealthrecordss",eHealthRecord);
            	mv.addObject("allCnMedicines", eHealthRecord.getChineseMedicines());
            	mv.addObject("allWeMedicines", eHealthRecord.getWesternMedicines());
            	return mv;
            }else{
	        	return new ModelAndView("dquery");
            }
		}
	}
	
	/**
	 * save info
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="saveEditRecord",method=RequestMethod.POST)
	public ModelAndView saveEdit(HttpServletRequest request,HttpServletResponse response) throws IOException{
		
		String regnoString = request.getParameter("regno");
		String conditondesc = request.getParameter("conditondesc");
		String westrndiagString = request.getParameter("westerndiag");
		String chinesediagString = request.getParameter("chinesediag");
		
		if("".equals(regnoString)){
        	return new ModelAndView("dquery");
		}else{
			
			MongoClient client = new MongoClient(DataBaseSetting.host,DataBaseSetting.port);
			MongoDatabase db = client.getDatabase(DataBaseSetting.database);
			
			MongoCollection<Document> collection = db.getCollection(DataBaseSetting.ehealthcollection);
			
			DBObject updateCondition=new BasicDBObject();  
	         
	        //where name='fox'  
	        updateCondition.put("ehealthrecord.registrationno",regnoString);  
	          
	        DBObject updatedValue=new BasicDBObject();  
	        updatedValue.put("ehealthrecord.conditionsdescribed", conditondesc);
	        updatedValue.put("ehealthrecord.diagnostics.westerndiagnostics", westrndiagString);
	        updatedValue.put("ehealthrecord.diagnostics.chinesediagnostics", chinesediagString);
	          
	        DBObject updateSetValue=new BasicDBObject("$set",updatedValue);  
	        /** 
	         * update insert_test set headers=3 and legs=4 where name='fox' 
	         * updateCondition:更新条件 
	         * updateSetValue:设置的新值 
	         */  
	        if(collection.updateOne(updateCondition, updateSetValue) != null){
	        	response.sendRedirect("detailRecord?ehealthregno="+regnoString);
	        }
	        // Close client
	        client.close();
	        
        	return new ModelAndView("dquery");
		}
	}
}

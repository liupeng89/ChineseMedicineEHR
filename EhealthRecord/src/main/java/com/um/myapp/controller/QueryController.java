package com.um.myapp.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.springframework.expression.spel.ast.Elvis;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.um.dao.ConnectionDB;
import com.um.data.DataBaseSetting;
import com.um.model.EHealthRecord;
import com.um.mongodb.converter.EhealthRecordConverter;
import com.um.util.DiagMedicineProcess;

@Controller
public class QueryController {
	
	@RequestMapping(value="recordquery",method=RequestMethod.GET)
	public ModelAndView queryRecord(String batch, String pname){
		if( pname == "" || pname.equals("")){
			List<String> batchList = DiagMedicineProcess.getBatch();
        	return new ModelAndView("dquery").addObject("batchList", batchList);
		}
		if(batch != null){
			
			List<EHealthRecord> ehealthList = new ArrayList<EHealthRecord>(); //查询病例
			
			// 1. 查询批次
			MongoCollection<Document> collection = ConnectionDB.getCollection(DataBaseSetting.database, DataBaseSetting.ehealthcollection);      
				
	        MongoCursor<Document> cursor = collection.find(new BasicDBObject("ehealthrecord.batch",Integer.valueOf(batch.substring(0, 4)))).iterator();

	        while(cursor.hasNext()){

	            EHealthRecord eHealthRecord = EhealthRecordConverter.toEHealthRecord(cursor.next());

	            if(eHealthRecord != null){
	            	   // add the privacy
	                ehealthList.add(eHealthRecord);
	            }
	        }
//			System.out.println(batch + "size ： " + ehealthList.size());
	        // 2. 查询姓名
	        if(ehealthList.size() == 0){
	        	List<String> batchList = DiagMedicineProcess.getBatch();
	        	return new ModelAndView("dquery").addObject("batchList", batchList);
	        }
	        
	        List<EHealthRecord> eList = new ArrayList<EHealthRecord>();// 符合姓名条件的病历
	        for(EHealthRecord e : ehealthList){
	        	if(e.getPatientInfo() == null || e.getPatientInfo().getName() == ""){
	        		continue;
	        	}
	        	if(e.getPatientInfo().getName() == pname || e.getPatientInfo().getName().equals(pname)){
	        		   // add the privacy
	        		e = EhealthRecordConverter.protectPatientInfo(e);
	        		
	        		eList.add(e);
	        	}
	        }
//	        System.out.println(eList.size());
	        // 3. 返回结果
	        if(eList.size() > 0){
	        	ModelAndView mv = new ModelAndView("recordQuery");
	 	        mv.addObject("ehealthrecrods", eList);
	 	        
	 	        return mv;
	        }else{
	        	List<String> batchList = DiagMedicineProcess.getBatch();
	        	return new ModelAndView("dquery").addObject("batchList", batchList);
	        }
		}else{
//			System.out.println("batch is null");
			if(!pname.equals("")){
				
				List<EHealthRecord> ehealthList = new ArrayList<EHealthRecord>(); //查询病例
		        
	            MongoCollection<Document> collection = ConnectionDB.getCollection(DataBaseSetting.database, DataBaseSetting.ehealthcollection);      
			
	            MongoCursor<Document> cursor = collection.find(new BasicDBObject("ehealthrecord.patientinfo.name",pname)).iterator();

	            while(cursor.hasNext()){

	                EHealthRecord eHealthRecord = EhealthRecordConverter.toEHealthRecord(cursor.next());
	                
	                //add the privacy
	                eHealthRecord = EhealthRecordConverter.protectPatientInfo(eHealthRecord);

	                if(eHealthRecord != null){
	                    ehealthList.add(eHealthRecord);
	                }
	            }
	            
	            ModelAndView mv = new ModelAndView("recordQuery");
	            
	            if(ehealthList != null && ehealthList.size() > 0){
	                mv.addObject("ehealthrecrods", ehealthList);
	            }
	            return mv;
		            
			}else{
				List<String> batchList = DiagMedicineProcess.getBatch();
	        	return new ModelAndView("dquery").addObject("batchList", batchList);
			}
		}
		
	}
	
	// 详细信息
	@RequestMapping(value="detailRecord",method=RequestMethod.GET)
	public ModelAndView detailRecrod(String ehealthregno){
//		System.out.println("regno" + ehealthregno);
		EHealthRecord eHealthRecord = null;
        ModelAndView mv = null;
		if("".equals(ehealthregno)){
			List<String> batchList = DiagMedicineProcess.getBatch();
        	return new ModelAndView("dquery").addObject("batchList", batchList);
		}else{
            MongoCollection<Document> collection = ConnectionDB.getCollection(DataBaseSetting.database, DataBaseSetting.ehealthcollection);
            MongoCursor<Document> cursor = collection.find(new BasicDBObject("ehealthrecord.registrationno",Long.valueOf(ehealthregno))).iterator();

            while(cursor.hasNext()){
                eHealthRecord = EhealthRecordConverter.toEHealthRecord(cursor.next());
                //add the privacy
                eHealthRecord = EhealthRecordConverter.protectPatientInfo(eHealthRecord);
                break;
            }
            if(eHealthRecord != null){
            	mv =  new ModelAndView("detail");
            	
            	Map<String, HashMap<String, String>> conditionMap = DiagMedicineProcess.getConditionDescription(eHealthRecord);
//            	System.out.println(conditionMap);
            	mv.addObject("ehealthrecordss",eHealthRecord);
            	mv.addObject("allCnMedicines", eHealthRecord.getChineseMedicines());
            	mv.addObject("allWeMedicines", eHealthRecord.getWesternMedicines());
            	mv.addObject("conditions", conditionMap);
            }else{
//            	System.out.println("ehealth is null");
            	List<String> batchList = DiagMedicineProcess.getBatch();
	        	return new ModelAndView("dquery").addObject("batchList", batchList);
            }
            return mv;
		}
	}
	
	
	// 编辑信息
	@RequestMapping(value="editRecord",method=RequestMethod.GET)
	public ModelAndView editRecrod(String ehealthregno){
		
		EHealthRecord eHealthRecord = null;
		if("".equals(ehealthregno)){
			List<String> batchList = DiagMedicineProcess.getBatch();
        	return new ModelAndView("dquery").addObject("batchList", batchList);
		}else{
            MongoCollection<Document> collection = ConnectionDB.getCollection(DataBaseSetting.database, DataBaseSetting.ehealthcollection);
            MongoCursor<Document> cursor = collection.find(new BasicDBObject("ehealthrecord.registrationno",Long.valueOf(ehealthregno))).iterator();
            while(cursor.hasNext()){
                eHealthRecord = EhealthRecordConverter.toEHealthRecord(cursor.next());
                //add the privacy
                eHealthRecord = EhealthRecordConverter.protectPatientInfo(eHealthRecord);
                break;
            }
            if(eHealthRecord != null){
            	ModelAndView mv = new ModelAndView("editRecord");
            	mv.addObject("ehealthrecordss",eHealthRecord);
            	mv.addObject("allCnMedicines", eHealthRecord.getChineseMedicines());
            	mv.addObject("allWeMedicines", eHealthRecord.getWesternMedicines());
            	return mv;
            }else{
            	List<String> batchList = DiagMedicineProcess.getBatch();
	        	return new ModelAndView("dquery").addObject("batchList", batchList);
            }
		}
	}
	
	//保存信息
	@RequestMapping(value="saveEditRecord",method=RequestMethod.POST)
	public ModelAndView saveEdit(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String regnoString = "";
		String conditondesc = "";
		String westrndiagString = "";
		String chinesediagString = "";
		regnoString = request.getParameter("regno");
		conditondesc = request.getParameter("conditondesc");
		westrndiagString = request.getParameter("westerndiag");
		chinesediagString = request.getParameter("chinesediag");
		
		if("".equals(regnoString)){
			List<String> batchList = DiagMedicineProcess.getBatch();
        	return new ModelAndView("dquery").addObject("batchList", batchList);
		}else{
            MongoCollection<Document> collection = ConnectionDB.getCollection(DataBaseSetting.database, DataBaseSetting.ehealthcollection);
            
            DBObject updateCondition=new BasicDBObject();  
         
	        //where name='fox'  
	        updateCondition.put("ehealthrecord.registrationno", Long.valueOf(regnoString));  
	          
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
	        List<String> batchList = DiagMedicineProcess.getBatch();
        	return new ModelAndView("dquery").addObject("batchList", batchList);
		}
	}
}

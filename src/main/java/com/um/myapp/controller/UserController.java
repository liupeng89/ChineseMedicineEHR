package com.um.myapp.controller;

import org.bson.Document;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.um.dao.ConnectionDB;
import com.um.data.DataBaseSetting;

@Controller
public class UserController {
	
	@RequestMapping(value="login",method=RequestMethod.POST)
	public ModelAndView login(String username,String passwd){
		
		if(this.checkParams(new String[] {username,passwd})){
			
			ModelAndView mvAndView = null;
			
			// data operation
			
			MongoClient client = new MongoClient("localhost",27017);
			
			try {
				 MongoDatabase database = client.getDatabase(DataBaseSetting.database);
			        
			     MongoCollection<Document> collection = database.getCollection(DataBaseSetting.infocollection);
			     
			     MongoCursor<Document> cursor = collection.find(new BasicDBObject("doctorinfo.username",username)).iterator();
		            
		         String passString = "";
		            
		            while(cursor.hasNext()){
		        		Document document = (Document)cursor.next().get("doctorinfo");
		        		passString = document.getString("password");
		            }
					if(passString != "" && passwd.equals(passString)){
						mvAndView = new ModelAndView("succ");
						mvAndView.addObject("username",username);
						mvAndView.addObject("passwd",passwd);
						return mvAndView;
					}else{
						//密码错误
						mvAndView = new ModelAndView("home");
						mvAndView.addObject("isnull","name or password is wrong!");
						return mvAndView;
					}
			        
			} finally {
				// TODO: handle finally clause
				if(client != null){
					client.close();
				}
			}
			
		}else{
			return new ModelAndView("home").addObject("isnull","name or password is null!");
		}
	}
	
	/**
	 *  check for not null
	 * @param params
	 * @return
	 */
	private boolean checkParams(String[] params){
		for(String param:params){
			if(param == "" || param == null || param.isEmpty()){
				return false;
			}
		}
		return true;
	}
	
}

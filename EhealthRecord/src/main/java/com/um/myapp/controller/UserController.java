package com.um.myapp.controller;

import org.bson.Document;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.um.dao.ConnectionDB;
import com.um.data.DataBaseSetting;

@Controller
public class UserController {
	
	@RequestMapping(value="login",method=RequestMethod.POST)
	public ModelAndView login(String username,String passwd){
		
		if(this.checkParams(new String[] {username,passwd})){
			
			ModelAndView mvAndView = null;
			
            MongoCollection<Document> collection = ConnectionDB.getCollection(DataBaseSetting.database, DataBaseSetting.infocollection);
		
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

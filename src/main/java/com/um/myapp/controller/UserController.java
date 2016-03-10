package com.um.myapp.controller;

import org.bson.Document;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.um.data.DataBaseSetting;

@Controller
public class UserController {
	
	@RequestMapping(value="login",method=RequestMethod.POST)
	public ModelAndView login(String username,String passwd){
		
		ModelAndView mvAndView = null;
		
		if ("".equals(username) || "".equals(passwd)) {
			return new ModelAndView("home").addObject("isnull","name or password is null!");
		}
		// data base
		MongoClient client = new MongoClient(DataBaseSetting.host,DataBaseSetting.port);
		MongoDatabase db = client.getDatabase(DataBaseSetting.database);
		MongoCollection<Document> collection = db.getCollection(DataBaseSetting.infocollection);
	     
        String passString = "";
         
        FindIterable<Document> iterable = collection.find(new BasicDBObject("doctorinfo.username",username));
         
        Document document = null;
         
        if(iterable.first() != null){
       	 document = (Document) iterable.first().get("doctorinfo");
        }
         
        if(document != null){
        	 passString = document.getString("password");
        }
        
		if(passString != "" && passwd.equals(passString)){
			mvAndView = new ModelAndView("succ");
			mvAndView.addObject("username",username);
			mvAndView.addObject("passwd",passwd);
		}else{
			//密码错误
			mvAndView = new ModelAndView("home");
			mvAndView.addObject("isnull","name or password is wrong!");
		}
		//close database
		client.close();
		
		return mvAndView;
	}
}

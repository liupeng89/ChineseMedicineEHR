package com.um.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.mathworks.toolbox.javabuilder.MWException;

/**
 * Database conncetions bean 
 *  
 * @author heermaster
 *
 */
@Configuration
public class DaoConfig {
	
	
//	@Bean
//	public DataBaseBean dataBaseBean() {
//		System.out.println("data base bean");
//		return new DataBaseBean();
//	}
	
	@Bean
	public PredictumBean predictumBean() throws MWException {
		System.out.println("predict bean built!");
		return new PredictumBean();
	}
}

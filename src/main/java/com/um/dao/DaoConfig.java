package com.um.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Database conncetions bean 
 *  
 * @author heermaster
 *
 */
@Configuration
public class DaoConfig {
	
	@Bean
	public DaoConnectionBean daoConnectionBean(){
		return new DaoConnectionBean();
	}
}

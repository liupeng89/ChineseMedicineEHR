package com.um.data;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.um.dao.DaoConnectionBean;

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

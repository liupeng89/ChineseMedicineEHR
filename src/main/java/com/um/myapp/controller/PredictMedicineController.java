package com.um.myapp.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PredictMedicineController {
	
	/**
	 * Predict medicines by input description
	 * @param request
	 * @return
	 */
	@RequestMapping(value="predictMedicineByInput", method=RequestMethod.GET, produces="application/json")
	public @ResponseBody String predictMedicineByInput(HttpServletRequest request){
		System.out.println("request begin!");
		String resultString = "{\"success\":\"1\"}";
		System.out.println(request.getAttribute("batch"));
		return resultString;
	}
}

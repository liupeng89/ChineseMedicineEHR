package com.um.myapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FrameController {
	
	//left 
	@RequestMapping(value="left",method=RequestMethod.GET)
	public ModelAndView displayLeft(){
		ModelAndView mv = new ModelAndView("left");
		return mv;
	}
	
	// top
	@RequestMapping(value="top",method=RequestMethod.GET)
	public ModelAndView displayTop(){
		ModelAndView mv = new ModelAndView("top");
		return mv;
	}
	
	// main query
	@RequestMapping(value="mainquery",method=RequestMethod.GET)
	public ModelAndView displayDquery(){
		ModelAndView mv = new ModelAndView("dquery");
		// get the batch info
		return mv;
	}
	
	//main statis
	@RequestMapping(value="mainstatis",method=RequestMethod.GET)
	public ModelAndView displayDstatis(){
		ModelAndView mv = new ModelAndView("dstatis");
		return mv;
	}
	
	// predict medicine 
	@RequestMapping(value="maindiagmedic",method=RequestMethod.GET)
	public ModelAndView displayDiagMedicine(){
		ModelAndView mv = new ModelAndView("predictMedicine");
		return mv;
	}
	
	// predict medicines based on the case 
	@RequestMapping(value="casediagmedic",method=RequestMethod.GET)
	public ModelAndView displayDiagMedicineByCase(){
		ModelAndView mv = new ModelAndView("casePredictMedicine");
		return mv;
	}
	
	// statistics based on the ill
	@RequestMapping(value="statisByIll",method=RequestMethod.GET)
	public ModelAndView displayByILL(){
		ModelAndView mv = new ModelAndView("statisticsByILL");
		return mv;
	}
	
	// 中药出现概率统计
	@RequestMapping(value="maincnmedicine",method=RequestMethod.GET)
	public ModelAndView cnMedicineStatis(){
		ModelAndView mv = new ModelAndView("cnmedicproba");
		return mv;
	}
	
	//中药处方统计
	@RequestMapping(value="statisByCN",method=RequestMethod.GET)
	public ModelAndView displaystatisByCN(){
		ModelAndView mv = new ModelAndView("statisticsByCM");
		return mv;
	}
	
	//中西医诊断统计分类
	@RequestMapping(value="statisByCWCassify",method=RequestMethod.GET)
	public ModelAndView displaystatisByCWCassify(){
		ModelAndView mv = new ModelAndView("statisticsByCWClassify");
		return mv;
	}
	// 中医诊断处方统计
	@RequestMapping(value="statisByCNDiagnose",method=RequestMethod.GET)
	public ModelAndView displaystatisByCNDiagnose(){
		ModelAndView mv = new ModelAndView("statisticsByCNDiagnose");
		return mv;
	}
	
}

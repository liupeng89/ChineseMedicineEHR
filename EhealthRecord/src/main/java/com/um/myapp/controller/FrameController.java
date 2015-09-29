package com.um.myapp.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.um.classify.CWRelationMapping;
import com.um.data.DataBaseSetting;
import com.um.model.EHealthRecord;
import com.um.util.DiagMedicineProcess;

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
		//同时查询批次数据
		List<String> batchList = DiagMedicineProcess.getBatch();
//		System.out.println(batchList);
		mv.addObject("batchList", batchList);
		return mv;
	}
	
	//main statis
	@RequestMapping(value="mainstatis",method=RequestMethod.GET)
	public ModelAndView displayDstatis(){
		ModelAndView mv = new ModelAndView("dstatis");
		return mv;
	}
	
	// 输入处方预测
	@RequestMapping(value="maindiagmedic",method=RequestMethod.GET)
	public ModelAndView displayDiagMedicine(){
//		ModelAndView mv = new ModelAndView("diagmedicine");
		ModelAndView mv = new ModelAndView("predictMedicine");
		List<String> batchList = DiagMedicineProcess.getBatch();
//		System.out.println(batchList);
		CWRelationMapping cwRelationMapping = new CWRelationMapping();
		int allcount = cwRelationMapping.queryEhealthData().size();
		mv.addObject("allcount",allcount);
		mv.addObject("batchList", batchList);
		return mv;
	}
	
	// 输入处方预测
		@RequestMapping(value="casediagmedic",method=RequestMethod.GET)
		public ModelAndView displayDiagMedicineByCase(){
			ModelAndView mv = new ModelAndView("casePredictMedicine");
			List<String> batchList = DiagMedicineProcess.getBatch();
			System.out.println(batchList);
			
			CWRelationMapping cwRelationMapping = new CWRelationMapping();
			List<EHealthRecord> allEHealthRecords = cwRelationMapping.queryEhealthDataByCollection(DataBaseSetting.ehealthcollection); // 全部病例
			List<EHealthRecord> aList = new ArrayList<EHealthRecord>();
			
			for(EHealthRecord e:allEHealthRecords){
				String batchString = "";
				if(e.getBatchString().contains(".")){
					batchString = e.getBatchString().substring(0, 4).trim();
				}else{
					batchString = e.getBatchString().trim();
				}
				if(batchString.equals("2012") || batchString == "2012"){
					aList.add(e);
				}
			}
			
			int allcount = aList.size(); // 全部病例数量
			mv.addObject("allcount",allcount);
			mv.addObject("batchList", batchList);
			return mv;
		}
	
	// 中药出现概率统计
	@RequestMapping(value="maincnmedicine",method=RequestMethod.GET)
	public ModelAndView cnMedicineStatis(){
		ModelAndView mv = new ModelAndView("cnmedicproba");
		List<String> batchList = DiagMedicineProcess.getBatch();
//		System.out.println(batchList);
		mv.addObject("batchList", batchList);
		return mv;
	}
}

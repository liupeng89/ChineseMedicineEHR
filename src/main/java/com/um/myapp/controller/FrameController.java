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
		// get the batch info
		List<String> batchList = DiagMedicineProcess.getBatch();
		mv.addObject("batchList", batchList);
		return mv;
	}
	
	//main statis
	@RequestMapping(value="mainstatis",method=RequestMethod.GET)
	public ModelAndView displayDstatis(){
		ModelAndView mv = new ModelAndView("dstatis");
		List<String> batchList = DiagMedicineProcess.getBatch();
		mv.addObject("batchList", batchList);
		return mv;
	}
	
	// predict medicine 
	@RequestMapping(value="maindiagmedic",method=RequestMethod.GET)
	public ModelAndView displayDiagMedicine(){
		ModelAndView mv = new ModelAndView("predictMedicine");
		List<String> batchList = DiagMedicineProcess.getBatch();
		int allcount = CWRelationMapping.queryEhealthData().size();
		mv.addObject("allcount",allcount);
		mv.addObject("batchList", batchList);
		return mv;
	}
	
	// predict medicines based on the case 
		@RequestMapping(value="casediagmedic",method=RequestMethod.GET)
		public ModelAndView displayDiagMedicineByCase(){
			ModelAndView mv = new ModelAndView("casePredictMedicine");
			List<String> batchList = DiagMedicineProcess.getBatch();
			System.out.println(batchList);
			
			List<EHealthRecord> allEHealthRecords = CWRelationMapping.queryEhealthDataByCollection(DataBaseSetting.ehealthcollection); // get all records 
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
			
			int allcount = aList.size(); // the number of all records
			mv.addObject("allcount",allcount);
			mv.addObject("batchList", batchList);
			return mv;
		}
	
	// statistics based on the ill
	@RequestMapping(value="statisByIll",method=RequestMethod.GET)
	public ModelAndView displayByILL(){
		ModelAndView mv = new ModelAndView("statisticsByILL");
		List<String> batchList = DiagMedicineProcess.getBatch();
		mv.addObject("batchList", batchList);
		return mv;
	}
	
	// 中药出现概率统计
	@RequestMapping(value="maincnmedicine",method=RequestMethod.GET)
	public ModelAndView cnMedicineStatis(){
		ModelAndView mv = new ModelAndView("cnmedicproba");
		List<String> batchList = DiagMedicineProcess.getBatch();
		mv.addObject("batchList", batchList);
		return mv;
	}
	
	//中药处方统计
	@RequestMapping(value="statisByCN",method=RequestMethod.GET)
	public ModelAndView displaystatisByCN(){
		ModelAndView mv = new ModelAndView("statisticsByCM");
		List<String> batchList = DiagMedicineProcess.getBatch();
		mv.addObject("batchList", batchList);
		return mv;
	}
	
	//中西医诊断统计分类
	@RequestMapping(value="statisByCWCassify",method=RequestMethod.GET)
	public ModelAndView displaystatisByCWCassify(){
		ModelAndView mv = new ModelAndView("statisticsByCWClassify");
		List<String> batchList = DiagMedicineProcess.getBatch();
		mv.addObject("batchList", batchList);
		return mv;
	}
	// 中医诊断处方统计
	@RequestMapping(value="statisByCNDiagnose",method=RequestMethod.GET)
	public ModelAndView displaystatisByCNDiagnose(){
		ModelAndView mv = new ModelAndView("statisticsByCNDiagnose");
		List<String> batchList = DiagMedicineProcess.getBatch();
		mv.addObject("batchList", batchList);
		return mv;
	}
	
}

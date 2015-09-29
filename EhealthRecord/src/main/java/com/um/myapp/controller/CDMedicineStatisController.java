package com.um.myapp.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.um.classify.CWRelationMapping;
import com.um.classify.DiagnosticsClassify;
import com.um.data.DataBaseSetting;
import com.um.data.DiagClassifyData;
import com.um.model.ChineseMedicine;
import com.um.model.EHealthRecord;
import com.um.util.DiagMedicineProcess;

@Controller
public class CDMedicineStatisController {
	
	@RequestMapping(value="CDMedicineStatis",method=RequestMethod.GET)
	public ModelAndView cdMedicineStatis() throws IOException{
		
		CWRelationMapping cwRelationMapping = new CWRelationMapping();
		/**
		 * 1. 读取病历信息
		 */
//		List<EHealthRecord> eHealthList = cwRelationMapping.queryEhealthData();
		List<EHealthRecord> eHealthList = cwRelationMapping.queryEhealthDataByCollection(DataBaseSetting.ehealthcollection);
		int ehealthCount = eHealthList.size();
		/**
		 * 2.诊断分类
		 */
		List<DiagnosticsClassify> chineseDiagnostics = cwRelationMapping.createDiagnostics(DiagClassifyData.cnDiagClassify);
		
		/**
		 * 3. 对病历进行分类处理
		 * 		3.1 中医诊断分类
		 */
		
		cwRelationMapping.chineseDiagnosticsClassify(eHealthList,chineseDiagnostics);//中医诊断分类
		
		int numOfChineseDiag = chineseDiagnostics.size();
		Map<String, List<String>> cnMedicineOfcnDiag = new HashMap<String, List<String>>();
		for(int i = 0; i < numOfChineseDiag; i++ ){
			
			DiagnosticsClassify cndiag = chineseDiagnostics.get(i);
			List<String> cnMedicines = new ArrayList<String>();
			
			if(cndiag != null && cndiag.geteHealthRecords() != null && cndiag.geteHealthRecords().size() > 0){
				for(EHealthRecord eRecord:cndiag.geteHealthRecords()){
					if(eRecord != null && eRecord.getChineseMedicines() != null && eRecord.getChineseMedicines().size() > 0){
						for(ChineseMedicine cm:eRecord.getChineseMedicines()){
							if(cm == null){
								break;
							}
							cnMedicines.add(cm.getNameString());
						}
					}
				}
				cnMedicineOfcnDiag.put(cndiag.getDiagString() + "("+cndiag.getCodeStrings()+")", cnMedicines);
			}
			
		}
		Set<String> cnKeySets = cnMedicineOfcnDiag.keySet();
		List<String> cnlist = null;
		//对中医诊断---中药处方进行统计
		Map<String, HashMap<String, Integer>> cnClassifyStatistics = new HashMap<String, HashMap<String,Integer>>();
		for(String key:cnKeySets){
			cnlist = cwRelationMapping.copyList(cnMedicineOfcnDiag.get(key));
			if(cnlist != null && cnlist.size() > 0){
				// 对中药处方进行统计
				HashMap<String, Integer> cnStatistic = cwRelationMapping.cnMedicineStatistics(cnlist);
				cnStatistic = (HashMap<String, Integer>) DiagMedicineProcess.sortMapByValue(cnStatistic);
				cnClassifyStatistics.put(key, cnStatistic);
			}
		}
		
		ModelAndView mv = new ModelAndView("cdmedicine");
		mv.addObject("cnClassifyStatistics", cnClassifyStatistics);
		mv.addObject("ehealthCount", ehealthCount);
		return mv;
	}
}

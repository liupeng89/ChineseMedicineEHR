package com.um.myapp.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.mathworks.toolbox.javabuilder.MWException;
import com.um.classify.CWRelationMapping;
import com.um.data.DataBaseSetting;
import com.um.data.DiagClassifyData;
import com.um.model.ChineseMedicine;
import com.um.model.EHealthRecord;
import com.um.util.DiagMedicineProcess;
import com.um.util.MachineLearningPredict;
import com.um.util.MedicineByDescription;

@Controller
public class DiagMedicineController {

	/**
	 *  诊断处方: 根据诊断得出中医处方 + 相似病历
	 *  
	 * @param batch：批次
	 * @param diagnose：诊断
	 * @param description：描述
	 * @return 预测出 + 相似病历
	 * @throws IOException
	 */
	@RequestMapping(value="diagmedicine",method=RequestMethod.POST)
	public ModelAndView diagMedicineController(String batch,String diagnose,String description) throws IOException{
		if(diagnose == "" || description == ""){
			return null;
		}
		ModelAndView mv = new ModelAndView("diagmedicine"); // view
		
		List<String> medicieList = MedicineByDescription.getMedicineByDiagAndDesc(batch,diagnose,description); // 根据描述，得出处方
		
		// 相似病历
		List<EHealthRecord> similaryRecords = MedicineByDescription.getSimilaryEHealthRecords(batch, diagnose, description);
		
		List<String> batchList = DiagMedicineProcess.getBatch();
		mv.addObject("batchList", batchList);
		CWRelationMapping cwRelationMapping = new CWRelationMapping();
		int allcount = cwRelationMapping.queryEhealthData().size();
		mv.addObject("allcount",allcount);
		mv.addObject("medicines", medicieList);
		mv.addObject("diagnose", diagnose);
		mv.addObject("description", description);
		mv.addObject("similaryRecords",similaryRecords);
		return mv;
	}
	
	/**
	 *  统计学预测处方－－－－－处方预测
	 * @return
	 */
	@RequestMapping(value="predictmedicine",method=RequestMethod.POST)
	public ModelAndView predictMedicineController(HttpServletRequest request){
		
		ModelAndView mv = new ModelAndView("predictMedicine");
		// 1. 整理参数----------
		String batch = ""; // 批次
		String diagnose = ""; // 诊断
		String description = ""; // 描述
		// 1.1 解析请求参数
		Map<String, String> requestMap = MedicineByDescription.parseRequest(request);
		// 1.2 生成 诊断/描述
		diagnose = requestMap.get("diagnose");
		description = requestMap.get("description");
		batch = request.getParameter("batch"); // 批次
		
		// 格式化输出
		String descconvertString = MedicineByDescription.getFormatDescirption(description);
		
		// 2. 预测中药
		List<String> medicieList = MedicineByDescription.getMedicineByDiagAndDesc(batch,diagnose,description); // 根据描述，得出处方
		
		// 3. 提供相似病历
		List<EHealthRecord> similaryRecords = MedicineByDescription.getSimilaryEHealthRecords(batch, diagnose, description);
		
		// 4. 获取批次
		List<String> batchList = DiagMedicineProcess.getBatch();
		mv.addObject("batchList", batchList);
		// 病例数量
		CWRelationMapping cwRelationMapping = new CWRelationMapping();
		int allcount = cwRelationMapping.queryEhealthData().size();
		mv.addObject("allcount",allcount);
		
		mv.addObject("batch", batch);
		mv.addObject("medicineListByStatis", medicieList);
		mv.addObject("diagnose", diagnose);
		mv.addObject("description", descconvertString);
		mv.addObject("similaryRecords",similaryRecords);
		return mv;
	}
	
	/**
	 *  机器学习预测处方－－－－处方预测
	 * @return
	 * @throws MWException 
	 */
	@RequestMapping(value="predictByMachinelearning",method=RequestMethod.POST)
	public ModelAndView predictMachineController(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("predictMedicine");
		// 1. 整理参数----------
		String batch = ""; // 批次
		String diagnose = ""; // 诊断
		String description = ""; // 描述
		// 1.1 解析请求参数
		Map<String, String> requestMap = MedicineByDescription.parseRequest(request);
//		System.out.println("[request map] :" + requestMap);
		
		// 1.2 生成 诊断/描述
		diagnose = requestMap.get("diagnose");
		description = requestMap.get("description");
		
		// 格式化输出
		String descconvertString = MedicineByDescription.getFormatDescirption(description);
		// 初始化算法输入
		List<String> inputcode = MachineLearningPredict.parseDiagAndDesc(diagnose, description);
		// 机器学习预测
		List<String> medicieList = MachineLearningPredict.predict(inputcode);
		// 4. 获取批次
		List<String> batchList = DiagMedicineProcess.getBatch();
		mv.addObject("batchList", batchList);
		CWRelationMapping cwRelationMapping = new CWRelationMapping();
		int allcount = cwRelationMapping.queryEhealthData().size();
		mv.addObject("allcount",allcount);
		mv.addObject("batch", batch);
		mv.addObject("medicines", medicieList);
		mv.addObject("diagnose", diagnose);
		mv.addObject("description", descconvertString);
		return mv;
	}
	
	/**
	 *  根据用户的输入预测处方－－－－结合统计 ＋ 机器学习的方法
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value="predictByStatisticAndMachine",method=RequestMethod.POST)
	public ModelAndView predictByStatisAndMachine(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("predictMedicine");
		
		// 1. 整理参数----------
		String batch = ""; // 批次
		String diagnose = ""; // 诊断
		String description = ""; // 描述
		// 1.1 解析请求参数
		Map<String, String> requestMap = MedicineByDescription.parseRequest(request);
		
		// 1.2 生成 诊断/描述
		diagnose = requestMap.get("diagnose");
		description = requestMap.get("description");
		batch = requestMap.get("batch");
		
		// 1.3 格式化描述输出
		String descconvertString = MedicineByDescription.getFormatDescirption(description);

		// 2. 基于统计的方法预测中药
		List<String> medicineListByStatis = MedicineByDescription.getMedicineByDiagAndDesc(batch,diagnose,description); // 根据描述，得出处方
		
		List<String> medicineListByStatisticSorted = new ArrayList<String>();
		for( String s : DiagClassifyData.machineMedicine ){
			for( String o : medicineListByStatis ){
				if( s == o || s.equals(o) ){
					medicineListByStatisticSorted.add(s);
				}
			}
		}
		
		// 2.1 提供相似病历
		List<EHealthRecord> similaryRecords = MedicineByDescription.getSimilaryEHealthRecords(batch, diagnose, description);
		
		// 3. 基于机器学习的方法预测中药
		//  3.1 初始化输入参数
		List<String> inputcode = MachineLearningPredict.parseDiagAndDesc(diagnose, description); // 解析机器学习算法输入格式
		// 	3.2 机器学习预测   machine learning object
		List<String> medicineListByMachine = MachineLearningPredict.predict(inputcode); // 机器学习预测结果
		
		// 4. 获取批次
		List<String> batchList = DiagMedicineProcess.getBatch();
		mv.addObject("batchList", batchList);
		mv.addObject("batch", batch);
		mv.addObject("medicineListByStatis", medicineListByStatisticSorted);
		mv.addObject("medicineListByMachine",medicineListByMachine);
		mv.addObject("diagnose", diagnose);
		mv.addObject("description", descconvertString);
		mv.addObject("similaryRecords",similaryRecords);
		
		return mv;
	}
	
	/**
	 *  基于现有病例来处方预测 ----序号， 结合统计 ＋ 机器学习
	 * @return
	 * @throws MWException 
	 */
	@RequestMapping(value="predictByCount",method=RequestMethod.POST)
	public ModelAndView predictByCountController(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("casePredictMedicine");
		// 1. 获取参数
		String countString = request.getParameter("count");
		
		int count = Integer.valueOf(countString); // 病例序号
		// 2. 查找病例
		
		// 1.1 读取数据库种病例数据
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
		
		// 3. 目标病例
		EHealthRecord targetRecord = aList.get( count - 1 );
		if(targetRecord == null){
			mv.addObject("allcount",allcount);
		}
		
		//4 . 目标病例的诊断和描述
		String diag = targetRecord.getChinesediagnostics();
		String description = targetRecord.getConditionsdescribed();
		String diagnose = "";
		String[] diagKeywords = DiagClassifyData.diagKeywords;
		for( String k : diagKeywords ){
			if(diag.matches(".*" + k + ".*")){
				diagnose += k + " ";
			}
		}
		
		// 5. 原病例中药            
		List<String> orignMedicines = new ArrayList<String>();
		if( targetRecord.getChineseMedicines() != null && targetRecord.getChineseMedicines().size() > 0 ){
			for(ChineseMedicine c : targetRecord.getChineseMedicines()){
				orignMedicines.add(c.getNameString());
			}
		}
		
		// 原始中药进行排序
		List<String> sortedList = new ArrayList<String>();
		
		for( String s : DiagClassifyData.machineMedicine ){
			for( String o : orignMedicines ){
				if( s == o || s.equals(o) ){
					sortedList.add(s);
				}
			}
		}
		
		// 6 统计预测
		List<String> medicineListByStatistic = MedicineByDescription.getMedicineByDiagAndDesc("2012",diagnose,description); // 根据描述，得出处方
		List<String> medicineListByStatisticSorted = new ArrayList<String>();
		for( String s : DiagClassifyData.machineMedicine ){
			for( String o : medicineListByStatistic ){
				if( s == o || s.equals(o) ){
					medicineListByStatisticSorted.add(s);
				}
			}
		}
		// 7. 机器学习预测
		//  7.1 初始化算法输入
		List<String> inputcode = MachineLearningPredict.parseDiagAndDescByEhealthRecords(targetRecord);
		//  7.2 机器学习预测
		List<String> medicineListByMachine = MachineLearningPredict.predict(inputcode); // 机器学习预测结果
		
		// 8. 获取批次
		mv.addObject("allcount",allcount);
		mv.addObject("orignMedicines",sortedList);
		mv.addObject("medicineListByStatis",medicineListByStatisticSorted);
		mv.addObject("medicineListByMachine",medicineListByMachine);
		mv.addObject("diagnose", diagnose);
		mv.addObject("description", description);
		return mv;
	}
}

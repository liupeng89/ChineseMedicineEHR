package com.um.classify;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.um.classify.DiagnosticsClassify;
import com.um.dao.ConnectionDB;
import com.um.data.DataBaseSetting;
import com.um.data.DiagClassifyData;
import com.um.model.ChineseMedicine;
import com.um.model.EHealthRecord;
import com.um.mongodb.converter.EhealthRecordConverter;

public class CWRelationMapping {
	
	/**
	 *  分析并统计数据之间的关系：
	 *  
	 *  	1、 西医诊断的分类
	 *  	2、中医诊断的分类
	 *  	3、西医诊断 / 中医诊断 之间的关系
	 *  	4、 中医诊断大类中，根据病症描述，在分成多个小类
	 *  	5、各个小类之间与中药处方之间的关系
	 *  
	 * @param args
	 */
	
	/**
	 *  主要步骤：
	 *  	1、读取数据库中数据
	 *  	2、查找 关键字，并生成关键字表，用于进行分类
	 *  	3、 根据关键字表，对病历进行分类
	 *  	4、统计分类信息
	 * @param args
	 */
	
	/**
	 *  数据读取
	 *  	： 连接数据库，并进行数据的读取
	 * @return
	 */
	public static List<EHealthRecord> queryEhealthData(){
		
		final List<EHealthRecord> results =  new ArrayList<EHealthRecord>();
		
		MongoCollection<Document> collection = ConnectionDB.getCollections(DataBaseSetting.ehealthcollection);
		FindIterable<Document> iterable = collection.find();
		
		iterable.forEach(new Block<Document>() {

			@Override
			public void apply(Document document) {
				// TODO Auto-generated method stub
				EHealthRecord eHealthRecord = EhealthRecordConverter.toEHealthRecord(document);
	        	
	        	if(eHealthRecord != null){
	        		results.add(eHealthRecord);
	        	}
			}
		});
		
		return results;
	}
	
	/**
	 *  
	 * @param ehealthcollection
	 * @return
	 */
	public static List<EHealthRecord> queryEhealthDataByCollection(String collectionString){
		if(collectionString == "" || collectionString.equals("")){
			return null;
		}
		
		final List<EHealthRecord> results =  new ArrayList<EHealthRecord>();
		
		MongoCollection<Document> collection = ConnectionDB.getCollections(collectionString);
		FindIterable<Document> iterable = collection.find();
		
		iterable.forEach(new Block<Document>() {

			@Override
			public void apply(Document document) {
				// TODO Auto-generated method stub
				EHealthRecord eHealthRecord = EhealthRecordConverter.toEHealthRecord(document);
	        	
	        	if(eHealthRecord != null){
	        		results.add(eHealthRecord);
	        	}
			}
		});
		
		return results;
	}
	
	/**
	 *  生成中西医诊断关键字表
	 * @param file
	 * @return
	 * @throws IOException 
	 */
	public List<DiagnosticsClassify> createDiagnostics(File file) throws IOException{
		List<DiagnosticsClassify> results = null;
		if(file == null){
			return null;
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		
		results = new ArrayList<DiagnosticsClassify>();
		
		String data = "";
		String[] keywords = null;
		while((data = reader.readLine()) != null){
			// 1. 读取数据
			keywords = parseString(data);
			// 2. 解析
			String describeString = createDescribe(keywords);
			// 3. 编码
			String describeCode = createDescribeCode(keywords);
			// 4. 封装成对象
			DiagnosticsClassify diagnostics = new DiagnosticsClassify();
			diagnostics.setDiagString(describeString);
			diagnostics.setKeywrods(keywords);
			diagnostics.setCodeStrings(describeCode);
			results.add(diagnostics);
		}
		reader.close();
		return results;
	}	
	
	/**
	 * 生成中西医诊断关键字表(string[] )
	 * @param keywords
	 * @return
	 * @throws IOException
	 */
	public List<DiagnosticsClassify> createDiagnostics(String[] keywords) throws IOException{
		List<DiagnosticsClassify> results = null;
		if(keywords == null || keywords.length == 0){
			return null;
		}
		
		int length = keywords.length;
		
		results = new ArrayList<DiagnosticsClassify>(length);
		
		String[] keys = null;
		for(int i = 0; i < length ; i++){
			// 1. 读取数据
			keys = parseString(keywords[i].trim());
			// 1. 读取数据
			// 2. 解析
			String describeString = createDescribe(keys);
			// 3. 编码
			String describeCode = createDescribeCode(keys);
			// 4. 封装成对象
			DiagnosticsClassify diagnostics = new DiagnosticsClassify();
			diagnostics.setDiagString(describeString);
			diagnostics.setKeywrods(keys);
			diagnostics.setCodeStrings(describeCode);
			results.add(diagnostics);
		}
		
		return results;
	}	
	
	/**
	 *  中医诊断分类处理
	 * @param eHealthRecords
	 * @param chineseDiagnostics
	 */
	public void chineseDiagnosticsClassify(List<EHealthRecord> eHealthRecords,List<DiagnosticsClassify> chineseDiagnostics){
		if(eHealthRecords == null || eHealthRecords.size() == 0 || chineseDiagnostics == null || chineseDiagnostics.size() == 0){
			return ;
		}
		
		for(EHealthRecord eRecord : eHealthRecords){
			// 根据中医诊断，进行分类
			String cndiag = eRecord.getChinesediagnostics();
			// 判断中医诊断/诊断分类匹配
			DiagnosticsClassify dClassify = matchDiagnostics(cndiag,chineseDiagnostics);
			if(dClassify != null){
				dClassify.geteHealthRecords().add(eRecord);
			}
		}		
	}
	
	/**
	 *  西医诊断分类处理
	 * @param eHealthRecords
	 * @param westrenDiagnostics
	 */
	public void westernDiagnosticsClassify(List<EHealthRecord> eHealthRecords,List<DiagnosticsClassify> westrenDiagnostics){
		if(eHealthRecords == null || eHealthRecords.size() == 0 || westrenDiagnostics == null || westrenDiagnostics.size() == 0){
			return ;
		}
		
		for(EHealthRecord eRecord : eHealthRecords){
			// 根据西医诊断，进行分类
			String cndiag = eRecord.getWesterndiagnostics();
			// 判断西医诊断/诊断分类匹配
			DiagnosticsClassify dClassify = matchDiagnostics(cndiag,westrenDiagnostics);
			if(dClassify != null){
				dClassify.geteHealthRecords().add(eRecord);
			}
		}		
	}
	
	/**
	 *  判断诊断是否匹配
	 * @param diagString
	 * @param diagnosticsClassifies
	 * @return
	 */
	public DiagnosticsClassify matchDiagnostics(String diagString,List<DiagnosticsClassify> diagnosticsClassifies){
		
		if(diagnosticsClassifies == null || diagnosticsClassifies.size() == 0){
			return null;
		}
		
		int maxcount = 0; // 最大匹配长度		
		int index = -1;    // DiagnosticsClassify 索引		
		int length = diagnosticsClassifies.size();
		
		for(int i = 0; i < length; i++ ){
			
			int count = 0;    // 匹配长度			
			String[] keywords = diagnosticsClassifies.get(i).getKeywrods(); // 关键字
			
			for(String s : keywords ){
				if(diagString.matches(".*" + s + ".*")){
					count++;
				}				
			}
			if(count > maxcount){
				maxcount = count;
				index = i;
			}
		}
		
		if(index != -1){
			return diagnosticsClassifies.get(index);
		}else{
			return null;
		}
	}
	
	/**
	 *  中药处方统计：统计中药名称和出现的次数
	 * @param cnList
	 * @return
	 */
	public HashMap<String, Integer> cnMedicineStatistics(List<String> cnList){
		if(cnList == null || cnList.size() == 0){
			return null;
		}
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		
		for(int i = 0; i < cnList.size(); i++){
			int count = 1;
			for(int j=i+1; j<cnList.size();j++){
				if(cnList.get(j).equals(cnList.get(i))){
					//重复
					count++;
					cnList.remove(j);
					j--; // 去掉之后，从当前位置继续
				}
			}
			result.put(cnList.get(i), count);
		}
		return result;
	}
	
	/**
	 *  parse string
	 * @param string
	 * @return
	 */
	public String[]  parseString(String string){
		
		String[] results = null;
		if(string == ""){
			return results;
		}
		results = string.split("\\|");		
		return results;
	}
	
	/**
	 *  根据关键字list,生成分类描述
	 * @param strings
	 * @return
	 */
	public String createDescribe(String[] strings){
		String result = "";
		if(strings == null || strings.length == 0){
			return result;
		}
		int length = strings.length;
		
		for(int i = 0; i < length;i++){
			result += strings[i];
		}
		
		return result;
	}
	/**
	 *  根据关键字生成关键字编码
	 * @param keywords
	 * @return
	 */
	public String createDescribeCode(String[] keywords){
		String result = "";
		if(keywords == null || keywords.length == 0){
			return result;
		}
		int length = keywords.length;
		
		for(int i = 0; i < length; i++){
			for(String s : DiagClassifyData.cnDiagCodeStrings){
				if(s.matches(keywords[i] + ".*")){
					result += s.split("\\|")[1];
				}
			}
		}
		return result;
	}
	
	/**
	 *  copy list
	 * @param src
	 * @return
	 */
	public List<String> copyList(List<String> src){
		if(src == null || src.size() == 0){
			return null;			
		}
		int length = src.size();
		List<String> dst = new ArrayList<String>(length);
		for(String s:src){
			dst.add(s);
		}
		return dst;
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		CWRelationMapping cwRelationMapping = new CWRelationMapping();
		/**
		 * 1.病历信息
		 */
		List<EHealthRecord> eHealthList = CWRelationMapping.queryEhealthData();
		
		System.out.println(eHealthList.size());
		
		File chineseDiagFile = new File("D:\\project\\document\\chinesediagnostics.txt");
		File westernDiagFile = new File("D:\\project\\document\\westerndiagnostics.txt");
		/**
		 * 2.诊断分类
		 */
		List<DiagnosticsClassify> chineseDiagnostics = cwRelationMapping.createDiagnostics(chineseDiagFile);
		List<DiagnosticsClassify> westernDiagnostics = cwRelationMapping.createDiagnostics(westernDiagFile);
		/**
		 * 3. 对病历进行分类处理
		 * 		3.1 中医诊断分类
		 *      3.2 西医诊断分类
		 */
		
		cwRelationMapping.chineseDiagnosticsClassify(eHealthList,chineseDiagnostics);//中医诊断分类
		
		cwRelationMapping.westernDiagnosticsClassify(eHealthList, westernDiagnostics);//西医诊断分类
		/**
		 *  4.  统计每一种诊断类型中的病历的数量
		 */
//		int cncount = 0; 
//		int wncount = 0;
//		for(DiagnosticsClassify cn:chineseDiagnostics){
//			System.out.println(cn.geteHealthRecords().size());
//			cncount += cn.geteHealthRecords().size();
//		}
//		
//		System.out.println("-------------------------------------------");
//		
//		for(DiagnosticsClassify wn:westernDiagnostics){
//			System.out.println(wn.geteHealthRecords().size());
//			wncount += wn.geteHealthRecords().size();
//		}
		
//		System.out.println("----------------------");
//		System.out.println("[cn count]: " + cncount);
//		System.out.println("[wn count]: " + wncount);
		
		/**
		 * 5. 映射关系
		 */
		
//		int cnLen = chineseDiagnostics.size();
//		for(int i = 0; i < cnLen; i++){
//			DiagnosticsClassify cndiag = chineseDiagnostics.get(i);
//			if(cndiag == null){
//				continue;
//			}
//			if(cndiag.geteHealthRecords() != null && cndiag.geteHealthRecords().size() > 0){
//				int lenRecord = cndiag.geteHealthRecords().size();
//				for(int j = 0; j < lenRecord; j++){
//					EHealthRecord eRecord = cndiag.geteHealthRecords().get(j);
//					int wnLen = westernDiagnostics.size();
//					for(int k = 0; k < wnLen; k++){
//						DiagnosticsClassify wndiag = westernDiagnostics.get(k);
//						if(wndiag == null){
//							continue;
//						}
//						if(wndiag.geteHealthRecords() != null && wndiag.geteHealthRecords().size() > 0){
//							int lenWnR = wndiag.geteHealthRecords().size();
//							for(int m = 0; m < lenWnR; m++){
//								if(eRecord.getRegistrationno() == wndiag.geteHealthRecords().get(m).getRegistrationno()){
//									System.out.println("C"+ (i+1) + "  ---------------------> " + "W"+(k+1));
//								}
//							}
//						}
//					}
//				}
//			}
//		}
		
		/**
		 *  6.  病症描述
		 */
//		PrintWriter write = new PrintWriter(new File("D:\\condition.txt"),"UTF-8");
//		
//		for(EHealthRecord e:eHealthList){
//			write.write(e.getConditionsdescribed()+"\n");
//		}
//		write.close();
//		System.out.println("write file successed!");
		
		/**
		 *  7. 中医类型中的中药处方统计
		 *  	
		 *    主要统计：给类型的中医诊断中，共有多少种中药，分别是什么，以及各种中药出现的次数
		 */
		
		// 7.1 中医诊断类型统计
		
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
				cnMedicineOfcnDiag.put("C" + (i+1), cnMedicines);
			}
			
		}
		System.out.println("[中医诊断分类数目]: " + cnMedicineOfcnDiag.size());
		Set<String> cnKeySets = cnMedicineOfcnDiag.keySet();
		List<String> cnlist = null;
		//对中医诊断---中药处方进行统计
		Map<String, HashMap<String, Integer>> cnClassifyStatistics = new HashMap<String, HashMap<String,Integer>>();
		for(String key:cnKeySets){
//			System.out.println(cnMedicineOfcnDiag.get(key)); // 名称有重复，需要单独处理
			cnlist = cwRelationMapping.copyList(cnMedicineOfcnDiag.get(key));
			if(cnlist != null && cnlist.size() > 0){
				// 对中药处方进行统计
				HashMap<String, Integer> cnStatistic = cwRelationMapping.cnMedicineStatistics(cnlist);
				cnClassifyStatistics.put(key, cnStatistic);
			}
		}
		
		System.out.println(cnClassifyStatistics.toString());
		
		
		
		
	}

}

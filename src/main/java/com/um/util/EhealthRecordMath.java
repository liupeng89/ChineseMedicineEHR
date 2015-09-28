package com.um.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.um.classify.CWRelationMapping;
import com.um.model.ChineseMedicine;
import com.um.model.EHealthRecord;

/**
 *  针对系统中的数学运算
 * @author lp
 *
 */
public class EhealthRecordMath {

	/**
	 *  求和运算
	 *  	如： p(A) + p(B) + P(C) ....
	 *  	输出出现概率
	 * @param maps
	 * @param length
	 * @return
	 */
//	public static int getSum(List<String> medicineList,List<EHealthRecord> eList){
//		if(medicineList == null || medicineList.isEmpty() || eList == null || eList.isEmpty()){
//			return 0;
//		}
//		int result = 0;
//		
//		Set<String> medicineSet = new HashSet<String>();
//		Set<EHealthRecord> ehealSet = new HashSet<EHealthRecord>();
//		for(String s : medicineList){
//			medicineSet.add(s); // 判断是否包含该味中药
//		}
//		System.out.println(medicineSet.size());
//		for(EHealthRecord e : eList){
//			if(e.getChineseMedicines() == null || e.getChineseMedicines().isEmpty()){
//				continue;
//			}
//			
//			for(ChineseMedicine c : e.getChineseMedicines()){
//				if(medicineSet.contains(c.getNameString()) && !ehealSet.contains(e)){
//					result++;
//					ehealSet.add(e);
//					break;
//				}
//			}
//		}
////		System.out.println(ehealSet.size());
////		for(EHealthRecord e : ehealSet){
////			System.out.println(e.getRegistrationno());
////		}
//		return result ;
//	}
	
	/**
	 *  交集运算
	 * @param maps
	 * @return
	 */
	public static int getMix(String[] meidicines,List<EHealthRecord> eList){
		if(meidicines == null || meidicines.length == 0 || eList == null || eList.isEmpty()){
			return 0;
		}
		List<EHealthRecord> results = new ArrayList<EHealthRecord>();
		for(EHealthRecord e : eList){
			if(DiagMedicineProcess.hasThisMedicine(e, meidicines)){
				//同时出现在同一病历中
				results.add(e);
			}
		}
		
		return results.size();
	}
	/**
	 *  并集运算 
	 * @param maps
	 * @return
	 */
	public static int getUnion(List<String> medicineList,List<EHealthRecord> eList){
		if (medicineList == null || medicineList.isEmpty() || eList == null || eList.isEmpty()) {
			return 0;
		}
		
		Set<String> medicineNameSet = new HashSet<String>(); // 中药名称set
		Set<EHealthRecord> ehealSet = new HashSet<EHealthRecord>(); // 满足并集条件的eheahl set
		int union = 0;
		
		for(String s : medicineList){
			medicineNameSet.add(s); // 构建中药名称set
		}
//		System.out.println(medicineNameSet);
		for(EHealthRecord e : eList){
			if(e.getChineseMedicines() == null || e.getChineseMedicines().isEmpty()){
				continue;
			}
			for(ChineseMedicine c : e.getChineseMedicines()){
				if(medicineNameSet.contains(c.getNameString())){
					ehealSet.add(e);
				}
			}
		}
//		System.out.println(medicineNameSet);
		union = ehealSet.size();		
		return union;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		CWRelationMapping c = new CWRelationMapping();
		List<EHealthRecord> eList = c.queryEhealthData();
		List<String> medicine = new ArrayList<String>();
		
		
		String[] strings = {"白术","炙黄芪","党参","太子参","莪术","蛇莓","猫爪草","望江南子","山慈菇"};
		
		for(int i=0;i<9;i++){
			medicine.add(strings[i]);
		}
		
		int union = getUnion(medicine, eList); // 894
//		int union1 = getSum(medicine, eList);  // 970
		System.out.println(union);
//		System.out.println(union1);
	}

}

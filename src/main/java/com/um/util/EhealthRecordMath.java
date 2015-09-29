package com.um.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.um.model.ChineseMedicine;
import com.um.model.EHealthRecord;

/**
 *  针对系统中的数学运算
 * @author lp
 *
 */
public class EhealthRecordMath {

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
		union = ehealSet.size();		
		return union;
	}
}

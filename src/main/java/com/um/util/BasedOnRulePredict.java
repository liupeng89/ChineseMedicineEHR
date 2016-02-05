package com.um.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.um.data.DiagClassifyData;

public class BasedOnRulePredict {
	
	
	/**
	 *  Predict medicines based on rules.
	 * @param description
	 * @return
	 */
	public static List<String> predictBasedOnRules(String description){
		if ( description.equals("") ) {
			return null;
		}
		List<String> medicine = new ArrayList<String>();
		Set<String> medicineList = new HashSet<String>();
		
		// 1. 4石见穿,白花蛇舌草,炒薏仁,龙葵 全选
		medicineList.add("石见穿");
		medicineList.add("白花蛇舌草");
		medicineList.add("炒薏苡仁");
		medicineList.add("龙葵");
		medicineList.add("茅莓根");
		
		// 2. 化疗后｜纯中药治疗 －－ 莪术,山慈菇,红豆杉,蛇沧簕 4选2，若有血痰则不选莪术
		if (description.contains("单纯中医药治疗") || description.contains("化疗后")||
				description.contains("术前")||description.contains("术后")||
				description.contains("放疗中")||description.contains("放疗后")||
				description.contains("化疗中")||description.contains("分子靶向药物")||
				description.contains("免疫治疗")) {
			String[] medines = {"莪术","山慈菇","红豆杉","蛇泡勒"};
			Random r = new Random();
			if (description.contains("红血痰")) {
				int low = 1;
				int high = 4;
				int Result = r.nextInt(high-low) + low;
				medicineList.add(medines[Result]);
				Result = r.nextInt(high-low) + low;
				medicineList.add(medines[Result]);
			}else {
				int low = 0;
				int high = 4;
				int Result = r.nextInt(high-low) + low;
				medicineList.add(medines[Result]);
				Result = r.nextInt(high-low) + low;
				medicineList.add(medines[Result]);
			}
		}
		
		//3. 气虚－－－太子参 or 党参, 白朮, 黄芪 ＋ 甘草
		if (description.contains("气虚")) {
			Random random = new Random();
			String[] medicines = {"太子参","党参"};
			int low = 0;
			int high = 2;
			int result = random.nextInt(high - low) + low;
			medicineList.add(medicines[result]);
			medicineList.add("白术");
			medicineList.add("炙黄芪");
			medicineList.add("甘草");
		}
		
		//4. 胸肋痛----延胡索，，，牛蒡子OR 木蝴蝶
		if (description.contains("胸肋痛")) {
			medicineList.add("延胡索");
			Random random = new Random();
			String[] medicines = {"牛蒡子","木蝴蝶"};
			int low = 0;
			int high = 2;
			int result = random.nextInt(high - low) + low;
			medicineList.add(medicines[result]);
		}
		
		//5. 厌食 or 食欲减退or纳差--> 炒谷芽 ＋ 山楂
		if (description.contains("厌食")||description.contains("食欲减退")||description.contains("纳差")) {
			medicineList.add("炒稻芽");
			medicineList.add("山楂");
		}
		
		// 6. 阴虚 －－》 沙参 ＋ 麦冬
		if (description.contains("气阴两虚")) {
			medicineList.add("沙参");
			medicineList.add("麦冬");
		}
		// 7. 腹胀 ＋ 便秘 －－》 轻：厚朴 ＋ 枳壳 ；重： 厚朴 ＋ 生大黄
		if (description.contains("便秘（重）")) {
			medicineList.add("厚朴");
			medicineList.add("生大黄");
		}
		if (description.contains("便秘（轻）")) {
			medicineList.add("厚朴");
			medicineList.add("枳壳");
		}
		// 8. 睡眠差 －－ 》 酸枣仁 ＋ 磁石
		if (description.contains("失眠（重）")) {
			medicineList.add("酸枣仁");
			medicineList.add("磁石");
		}
		// 9. 红血痰 －－－》 紫珠草 ＋ 三七
		if (description.contains("红血痰")) {
			medicineList.add("紫珠草");
			medicineList.add("三七");
		}
		// 10. 口渴多饮 －－－》 石斛 ＋ 天仁粉
		if (description.contains("口渴多饮")) {
			medicineList.add("石斛");
			medicineList.add("天仁粉");
		}
		
		// 11. 腹泻 泄泻－－ 》 石榴皮，五味子，补骨脂， 淮山药
		if (description.contains("泄泻")) {
			medicineList.add("石榴皮");
			medicineList.add("五味子");
			medicineList.add("补骨脂");
			medicineList.add("淮山药");
		}
		medicine.addAll(medicineList);
		
		List<String> medicineListByStatisticSorted = new ArrayList<String>();
		for( String s : DiagClassifyData.machineMedicine ){
			for( String o : medicine ){
				if( s == o || s.equals(o) ){
					medicineListByStatisticSorted.add(s);
				}
			}
		}
		
		return medicineListByStatisticSorted;
	}
}

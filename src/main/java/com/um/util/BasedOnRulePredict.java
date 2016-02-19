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
		List<String> medicineList = new ArrayList<String>();
		Set<String> medicineSet = new HashSet<String>();
		
		// 1. 4 石见穿,白花蛇舌草,炒薏仁,龙葵－－必然出现
		medicineSet.add("石见穿");
		medicineSet.add("白花蛇舌草");
		medicineSet.add("炒薏苡仁");
		medicineSet.add("龙葵");
		medicineSet.add("茅莓根");
		
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
				int frist = r.nextInt(high-low) + low;
				medicineSet.add(medines[frist]);
				// choice 2 from 3
				int second = r.nextInt(high-low) + low;
				while (second == frist) {
					second = r.nextInt(high-low) + low;
				}
				medicineSet.add(medines[second]);
			}else {
				int low = 0;
				int high = 4;
				int first = r.nextInt(high-low) + low;
				medicineSet.add(medines[first]);
				int second = r.nextInt(high-low) + low;
				// choice 2 from 4
				while (second == first) {
					second = r.nextInt(high-low) + low;
				}
				medicineSet.add(medines[second]);
			}
		}
		
		//3. 气虚－－－太子参 or 党参, 白朮, 黄芪 ＋ 甘草
		if (description.contains("气虚")) {
			Random random = new Random();
			String[] medicines = {"太子参","党参"};
			int low = 0;
			int high = 2;
			int result = random.nextInt(high - low) + low;
			medicineSet.add(medicines[result]);
			medicineSet.add("白术");
			medicineSet.add("炙黄芪");
			medicineSet.add("甘草");
		}
		
		//4. 胸肋痛----延胡索，，，牛蒡子OR 木蝴蝶
		if (description.contains("胸肋痛")) {
			medicineSet.add("延胡索");
			Random random = new Random();
			String[] medicines = {"牛蒡子","木蝴蝶"};
			int low = 0;
			int high = 2;
			int result = random.nextInt(high - low) + low;
			medicineSet.add(medicines[result]);
		}
		
		//5. 厌食 or 食欲减退or纳差--> 炒谷芽 ＋ 山楂
		if (description.contains("厌食")||description.contains("食欲减退")||description.contains("纳差")) {
			medicineSet.add("炒稻芽");
			medicineSet.add("山楂");
		}
		
		// 6. 阴虚 －－》 沙参 ＋ 麦冬
		if (description.contains("气阴两虚")) {
			medicineSet.add("沙参");
			medicineSet.add("麦冬");
		}
		// 7. 腹胀 ＋ 便秘 －－》 轻：厚朴 ＋ 枳壳 ；重： 厚朴 ＋ 生大黄
		if (description.contains("便秘") && (description.contains("重") || description.contains("中"))) {
			medicineSet.add("厚朴");
			medicineSet.add("生大黄");
		}
		if (description.contains("便秘") && description.contains("轻")) {
			medicineSet.add("厚朴");
			medicineSet.add("枳壳");
		}
		// 8. 睡眠差 －－ 》 酸枣仁 ＋ 磁石
		if (description.contains("失眠") && (description.contains("中") || description.contains("重"))) {
			medicineSet.add("酸枣仁");
			medicineSet.add("磁石");
		}
		// 9. 红血痰 －－－》 紫珠草 ＋ 三七
		if (description.contains("红血痰")) {
			medicineSet.add("紫珠草");
			medicineSet.add("三七");
		}
		// 10. 口渴多饮 －－－》 石斛 ＋ 天仁粉
		if (description.contains("口渴多饮")) {
			medicineSet.add("石斛");
			medicineSet.add("天仁粉");
		}
		
		// 11. 腹泻 泄泻－－ 》 石榴皮，五味子，补骨脂， 淮山药
		if (description.contains("泄泻")) {
			medicineSet.add("石榴皮");
			medicineSet.add("五味子");
			medicineSet.add("补骨脂");
			medicineSet.add("淮山药");
		}
		medicineList.addAll(medicineSet);
		
		List<String> medicineListByStatisticSorted = new ArrayList<String>();
		for( String s : DiagClassifyData.machineMedicine ){
			for( String o : medicineList ){
				if( s == o || s.equals(o) ){
					medicineListByStatisticSorted.add(s);
				}
			}
		}
		
		return medicineListByStatisticSorted;
	}
}

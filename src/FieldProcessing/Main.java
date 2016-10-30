package FieldProcessing;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {

	public static void main(String[] args) {

		String sourceDirectory = "./raw/";
		
		Map<Integer, String> map_processedData=new RawData(new File(sourceDirectory), setFieldNumbersOfServer()).preProcess();
		
		new ThroughputCounter(map_processedData).throughputCountAndOutput();
		
	}


	public static Map<Integer, ArrayList<Integer>> setFieldNumbersOfServer() {
		
		ArrayList<Integer> fieldNumbers_189=new ArrayList<Integer>();
		fieldNumbers_189.add(9); fieldNumbers_189.add(12); fieldNumbers_189.add(13);
		fieldNumbers_189.add(14); fieldNumbers_189.add(15); 
		
		ArrayList<Integer> fieldNumbers_196=new ArrayList<Integer>();
		fieldNumbers_196.add(10); fieldNumbers_196.add(11);
		fieldNumbers_196.add(12); fieldNumbers_196.add(13); fieldNumbers_196.add(14);
		
		ArrayList<Integer> fieldNumbers_197=new ArrayList<Integer>();
		fieldNumbers_197.add(10); fieldNumbers_197.add(11);
		fieldNumbers_197.add(12); fieldNumbers_197.add(13); fieldNumbers_197.add(14); 
		
		ArrayList<Integer> fieldNumbers_198=new ArrayList<Integer>();
		fieldNumbers_198.add(9); fieldNumbers_198.add(12); fieldNumbers_198.add(13);
		fieldNumbers_198.add(14); fieldNumbers_198.add(15); 
		
		//int[] fieldNumbers_189={9,12,13,14,15,16,7};
		//int[] fieldNumbers_196={10,11,12,13,14,16,5};
		//int[] fieldNumbers_197={10,11,12,13,14,16,5};
		//int[] fieldNumbers_198={9,12,13,14,15,16,7};
		
		
		Map<Integer, ArrayList<Integer>> map = new HashMap<Integer, ArrayList<Integer>>();
		map.put(189, fieldNumbers_189);
		map.put(196, fieldNumbers_196);
		map.put(197, fieldNumbers_197);
		map.put(198, fieldNumbers_198);
		
		return map;
	}
}

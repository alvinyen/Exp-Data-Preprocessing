package FieldProcessing;

import static java.lang.System.out;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;


public class ThroughputCounter {
	
	private Map<Integer, String> map_processedData;
	private int defaultYear = 2016;
	
	public ThroughputCounter(Map<Integer, String> map_processedData){
		this.map_processedData=map_processedData;
	}
	
	public void throughputCountAndOutput(){
		for(int serverNumber : map_processedData.keySet()){
			this.throughputCount(map_processedData.get(serverNumber), serverNumber);
		}
	} 
	
	private void throughputCount(String convertedStringOfServer, int serverNumber){
		Map<Long, Integer> map_toWriteInFile = new HashMap<Long, Integer>();
		DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); //for the content of file
																									
		StringBuilder tempSb = new StringBuilder();
		int fileNumber=1;
		
		String[] sourceLines=convertedStringOfServer.split("\n");
		for(String aLine : sourceLines){
			for(Long start = Long.parseLong(aLine.split(",")[1]) +1; start<=Long.parseLong(aLine.split(",")[2]) ;start++ ){
				if (map_toWriteInFile.containsKey(start)) {
					map_toWriteInFile.put(start, map_toWriteInFile.get(start) + 1);
				} else if (!map_toWriteInFile.containsKey(start)) {
					map_toWriteInFile.put(start, 1);
				} else {
					out.println("wrong... in map_toWriteInFile");
				}
			}
		}
			
		// sort
		SortedMap<Long, Integer> sortedMapByKey = new TreeMap<Long, Integer>();
		sortedMapByKey.putAll(map_toWriteInFile);
		map_toWriteInFile.clear();

		// print in a file
		Set<Long> set=sortedMapByKey.keySet();
		Iterator iterator=set.iterator();
			
		while (iterator.hasNext()) {
			Long tempLong=(Long)iterator.next();
			tempSb.append(formatter2.format(new Date(tempLong))).append(",").append(sortedMapByKey.get(tempLong)).append("\n");
		}
			
		this.output(serverNumber, fileNumber++, tempSb.toString());
		
		
	}
	
	private void output(int serverNumber, int fileNumber, String contentToOutput){
		String outputRoot = "./output/";
		String fileName = serverNumber + "_" + String.valueOf(fileNumber) + ".csv";
		//.../output/196/196_1.csv
		
		String pathOfOutputFile = outputRoot + serverNumber + "/" + fileName;
		
		BufferedWriter bw = null;	
		
		File theDirectoryNamedServerNumber = new File(outputRoot+serverNumber); 
		if( !theDirectoryNamedServerNumber.exists() ){
			theDirectoryNamedServerNumber.mkdirs();
		}
			
		try {
				
			bw=new BufferedWriter(new FileWriter(new File(pathOfOutputFile)));
			bw.write(contentToOutput);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
			out.println("IOException when output.." + serverNumber + ":" + fileNumber);
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
				out.println("IOException when close bw.." + serverNumber + ":" + fileNumber);
			}
		}
		
	}
	
//	for test
//	private Long getBeginInMs(Long dateInMs){
//		DateFormat formatter2 = new SimpleDateFormat("MM-dd"); 
//		String month_day=formatter2.format(new Date(dateInMs));
//		Calendar time=Calendar.getInstance();
//		time.clear();
//		time.set(2016, Integer.parseInt(month_day.split("-")[0]), Integer.parseInt(month_day.split("-")[1]));
//		return time.getTimeInMillis();
//	}
	
}

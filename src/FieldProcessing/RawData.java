package FieldProcessing;

import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



public class RawData {
	
	private File directory ;
	private Map<Integer, ArrayList<Integer>> fieldNumbersOfServer ;
	private Map<Integer, String> tempServerStringsToConvert =new HashMap<Integer, String>(); 
	private Map<Integer, String> converted =new HashMap<Integer, String>(); 
	private boolean test = true;
	
	/** 
	  * 
	  * @param directory     			the directory of source files 
	  * @param fieldNumbersOfServer      the key-value pair that contains the number of server 
	  * 					     			and the series of numbers correspond to selected field 
	  */ 
	public RawData(File directory, Map<Integer, ArrayList<Integer>> fieldNumbersOfServer){
		if( directory.isDirectory() && fieldNumbersOfServer.size()!=0 ){
			this.directory = directory ;
			this.fieldNumbersOfServer = fieldNumbersOfServer ; 
		}else if( fieldNumbersOfServer.size() == 0 ){ //把這些用exception包起來
			this.stop("the map is empty");
		}else if( !directory.isDirectory() ){
			this.stop("not a directory");
		}else{
			this.stop("other reasons kill the process");
		}
	}
	
	public Map<Integer, String> preProcess(){
		this.selectFieldsInOrder();
		this.convert();
//		this.output();
//		return null;
		return this.convert();
	}
	
	private void selectFieldsInOrder(){
		File[] directorysOfServerFiles = directory.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		});
	
		for( File directoryOfServerFiles : directorysOfServerFiles){
			int serverNumber = Integer.parseInt(directoryOfServerFiles.getName()) ;
			this.selectFields(directoryOfServerFiles, fieldNumbersOfServer.get(serverNumber));
			
		}
		
//		if(test)
//		for( File directoryOfServerFiles : directorysOfServerFiles ){
//			int ServerNumber = Integer.parseInt(directoryOfServerFiles.getName()) ;
//			out.println(tempServerStringsToConvert.get(ServerNumber).split("\n")[0]);
//		}
		
		//this.convert();
		
//		if(test)
//			for( int serverNumber : converted.keySet() ){
//				out.println(converted.get(serverNumber));
//			}
		
		//this.output();
		
		
	}
	
	private void selectFields(File directoryOfServerFiles, ArrayList<Integer> seriesOfFieldNumber){
		int serverNumber = Integer.parseInt(directoryOfServerFiles.getName());
		File[] files = directoryOfServerFiles.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(".csv");
			}
		});
		
		for(File fileToSelectFields: files){
			String str = this.select(fileToSelectFields, seriesOfFieldNumber);
			if( str!=null && !str.equals("")){
				if(tempServerStringsToConvert.get(serverNumber)!=null){
					str = tempServerStringsToConvert.get(serverNumber) + str;
				}
				tempServerStringsToConvert.put(serverNumber, str);
			}
		}
		
		
	}
	
	private String select(File fileToSelectFields, ArrayList<Integer> seriesOfFieldNumber){
		BufferedReader br=null;  String line;	
		StringBuilder sb=new StringBuilder();
		try {
			br=new BufferedReader(new FileReader(fileToSelectFields));		
			while((line=br.readLine())!=null){
				String[] temp=line.split(",");
				
				for(int i=0;i<seriesOfFieldNumber.size();i++){
					sb.append(temp[seriesOfFieldNumber.get(i)]);
					if(i!=(seriesOfFieldNumber.size()-1)){
						sb.append(",");
					}else{
						sb.append("\n");
					}		
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.print("fileNotFound exception");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.print("io exception when readLine");
		} finally{
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.print("io exception when br.close()");
			}
		}
		return sb.toString();
	}
	
	private Map<Integer, String> convert(){
		DateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		for(Integer serverNumber: tempServerStringsToConvert.keySet() ){
			String tempServerString = tempServerStringsToConvert.get(serverNumber);
			String[] lines = tempServerString.split("\n");
			StringBuilder sb = new StringBuilder();
			for(String line : lines){
				if( !line.trim().equals("") ){
					String[] lineChunks = line.split(",");
					
                     String beginDateTime =lineChunks[1]+" "+lineChunks[2];
                     
                     long beginDateTimeInMs=0;
                     long endDateTimeInMs=0;
                     
                     try {
                    	 	beginDateTimeInMs = formatter.parse(beginDateTime).getTime();
                     } catch (ParseException e) {
                    	 	e.printStackTrace();
                    	 	this.stop("ParseException when parse beginDateTime to ms format");
                     }
                     
                     String endDateTime =lineChunks[3]+" "+lineChunks[4];
                     try {
                 	 	endDateTimeInMs = formatter.parse(endDateTime).getTime();
                     } catch (ParseException e) {
                 	 	e.printStackTrace();
                 	 	this.stop("ParseException when parse endDateTime to ms format");
                     }
                     
                     sb.append(lineChunks[0]).append(",").
                     	append(beginDateTimeInMs).append(",").
                     	append(endDateTimeInMs).append("\n") ;
				}
			}
			converted.put(serverNumber, sb.toString());
		} 
		return this.converted;
	}
	
	//for test only
	private void output(){
		//./clean/server#
		//converted=null
		String outputRoot="/Users/KJ-Yen/Documents/ServerSideDataPreProcessing/temp/";
		String fileNameExtension=".csv";
		BufferedWriter bw=null;
		try {
			for(Integer serverNumber:converted.keySet()){
				
				
				bw=new BufferedWriter(new FileWriter(new File(outputRoot+serverNumber+fileNameExtension)));
				
				Long start=Long.parseLong(converted.get(serverNumber).split("\n")[0].split(",")[1]);
				
				DateFormat formatter2 = new SimpleDateFormat("MM-dd"); 
				String month_day=formatter2.format(new Date(start));
				Calendar time=Calendar.getInstance();
				time.clear();
				time.set(2016, Integer.parseInt(month_day.split("-")[0]), Integer.parseInt(month_day.split("-")[1]));
				
				out.println(time.getTimeInMillis());
				

				bw.write(converted.get(serverNumber));
				bw.flush();
				bw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			out.print("io exception when buffered write");
		}finally{
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
				out.print("io exception when close bw");
			}
		}
	}
	
	private void stop(String reason){
		out.println("the map is empty");
		System.exit(0);
	}

}

package chatlength;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import com.opencsv.CSVReader;

public class ChatLengthCount {

	public static void main(String args[]) throws IOException{
		String rootpath = "/Users/xuwang/Desktop/234_logs/";
		String pathtologs = rootpath+"agent";
		File folder = new File(pathtologs);
		BufferedWriter outputwriter = new BufferedWriter(new FileWriter(rootpath+"/new_chatlength"));   
        outputwriter.write("groupname,chatlength,numofwords,bazaarprompt");
        outputwriter.newLine();
		for(File fileEntry: folder.listFiles()){
	//	File fileEntry = new File("logs/latest_logs_160711/BazaarAgent_mturk1607110_message_annotations.csv");
		String filename = fileEntry.getName();
		//		   System.out.println(filename);
		
		if(!filename.equals(".DS_Store")){
		   CSVReader csvreader = new CSVReader(new FileReader(fileEntry));
		//   BufferedReader reader = new BufferedReader(new FileReader(fileEntry));
		   
		   List<String[]> documentlist = new ArrayList<String[]>();
		   documentlist = csvreader.readAll();
		  // String line = reader.readLine();
		   int length = documentlist.size();
		   
		   int chatlength =0;
		   int numofwords =0;
		   int bazaarprompt = 0;
		   for(int i = 0 ; i < length ; i ++){
		   
           String[] nextline = documentlist.get(i);
           if(!nextline[3].equals("BazaarAgent")){
        	   
           String sentence = nextline[5];           
           chatlength++;
           
           
           boolean prevCharWasSpace=true;
           for (int j = 0; j < sentence.length(); j++) 
           {
               if (sentence.charAt(j) == ' ') {
                   prevCharWasSpace=true;
               }
           else{
                   if(prevCharWasSpace) numofwords++;
                   prevCharWasSpace = false;

               }
           }
           }
           
           else{
           String sentence = nextline[5];
           String arr[] = sentence.split(" ",2);
           String firstword = arr[0];
           if(!firstword.equals("Welcome,") && !firstword.equals("Thank") && !firstword.equals("Use")){
        	bazaarprompt++;   
           }
           }
           }
		 
		   String groupname = filename.substring(filename.indexOf("mturk"), filename.indexOf("_message"));
		   
           outputwriter.write(groupname+","+String.valueOf(chatlength)+","+String.valueOf(numofwords)+","+String.valueOf(bazaarprompt));
           outputwriter.newLine();
		}
		}
		   outputwriter.flush();
		   outputwriter.close();
		}
}

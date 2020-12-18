package chatlength;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.opencsv.CSVReader;

public class ChatLengthCountNoAgent {

	public static void main(String args[]) throws IOException{
		String rootpath = "/Users/xuwang/Desktop/234_logs/";
		String pathtologs = rootpath+"noagent";
		File folder = new File(pathtologs);
		BufferedWriter outputwriter = new BufferedWriter(new FileWriter(rootpath+"/noagent_chatlength"));   
		outputwriter.write("groupname,chatlength,numofwords");
	    outputwriter.newLine();
		for(File fileEntry: folder.listFiles()){
			HashMap<String, Integer> group2chatlength = new HashMap<String, Integer>();
		    HashMap<String, Integer> group2numofwords = new HashMap<String, Integer>();
	        
		   CSVReader csvreader = new CSVReader(new FileReader(fileEntry));
		   
		   List<String[]> documentlist = new ArrayList<String[]>();
		   documentlist = csvreader.readAll();
		   int length = documentlist.size();
		   		   
		   for(int i = 1 ; i < length ; i ++){
		   
           String[] nextline = documentlist.get(i);
           if(!nextline[1].equals("BazaarAgent")){
        	   
           String sentence = nextline[6];
           String roomname = nextline[5];
           
           if(!sentence.equals("leave") && !sentence.equals("join")){
        	   
           if(!group2chatlength.containsKey(roomname)){
        	   group2chatlength.put(roomname, 0);
           }
           
           int current = group2chatlength.get(roomname);
           current++;
           group2chatlength.put(roomname, current);
           
           if(!group2numofwords.containsKey(roomname)){
        	   group2numofwords.put(roomname, 0);
           }
           
           int words = 0;
           boolean prevCharWasSpace=true;
           for (int j = 0; j < sentence.length(); j++) 
           {
               if (sentence.charAt(j) == ' ') {
                   prevCharWasSpace=true;
               }
           else{
                   if(prevCharWasSpace) words++;
                   prevCharWasSpace = false;

               }
           }
           int currentwords = group2numofwords.get(roomname);
           currentwords += words;
           group2numofwords.put(roomname, currentwords);
           }
           }
        
		   }
		   for(String roomname: group2chatlength.keySet()){
			   int chatlength = group2chatlength.get(roomname);
			   int numofwords = group2numofwords.get(roomname);
	           outputwriter.write(roomname+","+String.valueOf(chatlength)+","+String.valueOf(numofwords));
	           outputwriter.newLine();  
		   }
		   
		}
		outputwriter.flush();
		   outputwriter.close();  
		}
	
}

package reasoning;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.opencsv.CSVReader;

public class ReasoningPredictionNoAgent {
	String pathToLightSide ="LightSide_2.3.1_20141107";
	    String pathToModel = "saved/gst_reasoning_model.model.xml";
	    String predictionCommand = "scripts/predict.sh";
	    
	    OutputStream stdin;
	    InputStream stderr;
	    InputStream stdout;

	    BufferedReader reader;
	    BufferedWriter writer;

	    Process process = null;
	public ReasoningPredictionNoAgent(){
		
		 
        pathToLightSide = System.getProperties().getProperty("pathToLightSide", pathToLightSide);
        pathToLightSide = System.getProperty("user.dir") + "/LightSide_2.3.1_20141107";
        pathToModel = System.getProperties().getProperty("pathToModel", pathToModel);
        predictionCommand = System.getProperties().getProperty("predictionCommand", predictionCommand);
        //System.out.println(pathToLightSide);
        //System.out.println(pathToModel);
        try
        {
            File lightSideLocation = new File(pathToLightSide);
            process = Runtime.getRuntime().exec(new String[] { predictionCommand, pathToModel }, null, lightSideLocation);

        }

        catch (IOException e)
        {
            e.printStackTrace();
        }
	}
	
    public String annotateText(String text)
    {
        String label = null;
    //    System.out.println("Working Directory = " + System.getProperty("user.dir"));
   //   System.out.println("Text " + text);
       

        stdin = process.getOutputStream();
        stderr = process.getErrorStream();
        stdout = process.getInputStream();

        reader = new BufferedReader(new InputStreamReader(stdout));
        writer = new BufferedWriter(new OutputStreamWriter(stdin));
        
        
        
        try
        {
            writer.write(text + "\n");
            writer.flush();

            String line = reader.readLine();
            if (line != null)
            {
                System.out.println("Line " + line);
                String[] response = line.split("\\s+");
                
                label = response[0];
                System.out.println("Label is " + label);
            }
            else
            {
                System.err.println("response from LightSide is null!");
            }

        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return label;
    }
	
	public static void main(String args[]) throws IOException{
		String pathtologs = "logs/latest_logs_noagent";
		File folder = new File(pathtologs);
		BufferedWriter outputwriter = new BufferedWriter(new FileWriter("noagent_1"));   

		for(File fileEntry: folder.listFiles()){
			HashMap<String, Integer> group2reasoning = new HashMap<String, Integer>();
		
	//	File fileEntry = new File("logs/latest_logs_160711/BazaarAgent_mturk1607110_message_annotations.csv");
		    String filename = fileEntry.getName();
	
        int total =0;
		//		   System.out.println(filename);
		   CSVReader csvreader = new CSVReader(new FileReader(fileEntry));
		//   BufferedReader reader = new BufferedReader(new FileReader(fileEntry));
		   
		   List<String[]> documentlist = new ArrayList<String[]>();
		   documentlist = csvreader.readAll();
		  // String line = reader.readLine();
		   int length = documentlist.size();
		   
		   ReasoningPredictionNoAgent prediction = new ReasoningPredictionNoAgent();
		   
		   for(int i = 1 ; i < length ; i ++){
		   
           String[] nextline = documentlist.get(i);
           if(!nextline[1].equals("BazaarAgent")){
        	   
           String sentence = nextline[6];
           String roomname = nextline[5];
           if(!group2reasoning.containsKey(roomname)){
        	   group2reasoning.put(roomname, 0);
           }
           String label = prediction.annotateText(sentence);
           System.out.println("text"+sentence);
           System.out.println("Label is " + label);
           if (label.equals("pos")){
        	total = group2reasoning.get(roomname);
        	total++;
        	group2reasoning.put(roomname, total);
           }
           }
		   }
          // System.out.println(label);
          // System.exit(-1);
		   for(String roomname: group2reasoning.keySet()){
		//	   String groupname = roomname.substring(filename.indexOf("mturkno")+7);
			   int result = group2reasoning.get(roomname);
	           outputwriter.write(roomname+","+String.valueOf(result));
	           outputwriter.newLine();  
		   }
		   
		}
		outputwriter.flush();
		   outputwriter.close();  
		}
	}



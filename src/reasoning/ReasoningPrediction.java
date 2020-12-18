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
import java.util.List;

import com.opencsv.CSVReader;

public class ReasoningPrediction {
		String pathToLightSide ="LightSide_2.3.1_20141107";
	    String pathToModel = "saved/gst_reasoning_model.model.xml";
	    String predictionCommand = "scripts/predict.sh";
	    
	    OutputStream stdin;
	    InputStream stderr;
	    InputStream stdout;

	    BufferedReader reader;
	    BufferedWriter writer;

	    Process process = null;
	public ReasoningPrediction(){
		
		 
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
		String rootpath = "/Users/xuwang/Desktop/234_logs/";
		String pathtologs = rootpath+"agent";
		File folder = new File(pathtologs);
		BufferedWriter outputwriter = new BufferedWriter(new FileWriter(rootpath+"new_reasoning"));   

		for(File fileEntry: folder.listFiles()){
		String filename = fileEntry.getName();
        if(!filename.equals(".DS_Store")){
		int total =0;
		//		   System.out.println(filename);
		   CSVReader csvreader = new CSVReader(new FileReader(fileEntry));
		//   BufferedReader reader = new BufferedReader(new FileReader(fileEntry));
		   
		   List<String[]> documentlist = new ArrayList<String[]>();
		   documentlist = csvreader.readAll();
		  // String line = reader.readLine();
		   int length = documentlist.size();
		   
		   ReasoningPrediction prediction = new ReasoningPrediction();
		   
		   for(int i = 0 ; i < length ; i ++){
		   
           String[] nextline = documentlist.get(i);
           if(!nextline[3].equals("BazaarAgent")){
        	   
           String sentence = nextline[5];           
           String label = prediction.annotateText(sentence);
           
           System.out.println("Label is " + label);
           if (label.equals("pos")){
        	total++; 
           }
           }
		   }
          // System.out.println(label);
          // System.exit(-1);
		   String groupname = filename.substring(filename.indexOf("mturk"), filename.indexOf("_message"));
		   
           outputwriter.write(groupname+","+String.valueOf(total));
           outputwriter.newLine();
		}
		}
		   outputwriter.flush();
		   outputwriter.close();
		}
	}



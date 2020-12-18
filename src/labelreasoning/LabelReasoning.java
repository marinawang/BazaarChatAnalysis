package labelreasoning;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import reasoning.ReasoningPrediction;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class LabelReasoning {
	String pathToLightSide ="LightSide_2.3.1_20141107";
    String pathToModel = "saved/gst_reasoning_model.model.xml";
    String predictionCommand = "scripts/predict.sh";
    
    OutputStream stdin;
    InputStream stderr;
    InputStream stdout;

    BufferedReader reader;
    BufferedWriter writer;

    Process process = null;
public LabelReasoning(){
	
	 
    pathToLightSide = System.getProperties().getProperty("pathToLightSide", pathToLightSide);
    pathToLightSide = System.getProperty("user.dir") + "/LightSide_2.3.1_20141107";
    pathToModel = System.getProperties().getProperty("pathToModel", pathToModel);
    predictionCommand = System.getProperties().getProperty("predictionCommand", predictionCommand);
  
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
//            System.out.println("Line " + line);
            String[] response = line.split("\\s+");
            
            label = response[0];
//            System.out.println("Label is " + label);
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
    int count = 0;
	for(File fileEntry: folder.listFiles()){
		
		count++;
	String filename = fileEntry.getName();
    int total =0;
	if (!filename.equals(".DS_Store")){   
    System.out.println(filename);
	   
	   CSVReader csvreader = new CSVReader(new FileReader(fileEntry));
	   String groupname = filename.substring(filename.indexOf("mturk"), filename.indexOf("_message"));

	   CSVWriter csvwriter = new CSVWriter(new FileWriter(rootpath+groupname+".csv"));
	   List<String[]> documentlist = new ArrayList<String[]>();
	   documentlist = csvreader.readAll();
	  
	   int length = documentlist.size();
	   
	   LabelReasoning labelreasoning = new LabelReasoning();
	   
	   for(int i = 0 ; i < length ; i ++){
		   String label;
       String[] nextline = documentlist.get(i);
       if(!nextline[3].equals("BazaarAgent")){
    	   
       String sentence = nextline[5];           
       label = labelreasoning.annotateText(sentence);
  
       }
       else{
       label = "bazaar";
       }
	   
       String[] newnextline = new String[nextline.length+1];
       
       for (int j =0; j< nextline.length; j++){
    	   newnextline[j]= nextline[j];
       }
       newnextline[newnextline.length-1]=label;
       
       csvwriter.writeNext(newnextline);
	   }
	   csvwriter.flush();
	   csvwriter.close();
	}
	}
	}
}

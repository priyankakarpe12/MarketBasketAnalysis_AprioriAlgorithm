package algorithm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PreProcess {

	public static void preProcess() {
		
		String line = null;
		try {
            
			FileReader fileReader = 
                new FileReader("E:\\Sample_Data_Aprori\\retail.txt");

            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);
            
            
            FileWriter fileWriter = new FileWriter("E:\\Sample_Data_Aprori\\processed_retail.txt");

            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            int count = 0;
            while((line = bufferedReader.readLine()) != null) {
                
            	if(line=="")
            		continue;
            	if(count > 1000)
            		break;
            	String[] splitted_ip = line.split("\\s+");
            	for(String temp : splitted_ip) {
            		
            		System.out.println(temp);
            		if(Integer.parseInt(temp) > 500) {
            			
            			line.replaceFirst(temp+" ", "");
            		}
            	}
            	
            	splitted_ip = line.split("\\s+");
            	for(String temp : splitted_ip) {
            		
            		bufferedWriter.write(temp + " ");
            	}
            	if(splitted_ip.length > 0)
            		bufferedWriter.newLine();
            	count++;
            }

            //close files.
            bufferedReader.close();
            bufferedWriter.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file");                
        }
        catch(IOException ex) {
            System.out.println("Error reading file");                  
        }
	}
	
	public static void main(String[] args) {

		preProcess();
	}

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Abhishek
 */
import java.io.*;
import java.util.*;
import java.lang.*;
import javax.xml.parsers.*;
public class Main {
    public static void main(String[]args){
       
        System.out.println(new Date().toString());
        System.out.println("Har Har Mahadev");
        try {
			buildIndex("C:\\Users\\Abhishek\\Documents\\NetBeansProjects\\TextSearchEngine\\Documents\\2000pages.dat",
					"C:\\Users\\Abhishek\\Documents\\NetBeansProjects\\TextSearchEngine\\Documents\\output\\");
			
		}
        catch (Exception e) {
			e.printStackTrace();
		}
        
    }
    private static void buildIndex(String corpusFile,String indexDirectory) throws IOException, ParserConfigurationException, SAXException{
                  loadStopWords();
		  SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		  SAXParser saxParser = saxParserFactory.newSAXParser();
		  WikiConstant.IndexFileDir = indexDirectory;
		  saxParser.parse(corpusFile, new WikiSAXHandler());
                  System.out.println(new Date().toString() );
        
    }
    public static void loadStopWords() throws IOException{

		BufferedReader br=new BufferedReader(new FileReader(new File(StopWordConfiguration.STOP_WORD_FILE)));
		
		String line;
		PageParser.stopWords = new HashSet<String>();

		while( (line = br.readLine()) != null){
			  String tokens[] = line.toLowerCase().split(StopWordConfiguration.STOP_WORD_DELIMITER);
			  for(String token:tokens){
				  PageParser.stopWords.add(token);
			  }
		}
		if(br != null)
			br.close();
                
	}
    
}

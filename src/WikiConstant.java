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
import java.text.DecimalFormat;
import java.util.*;
public class WikiConstant {
   public static final String ELE_TITLE="title";
    public static final String ELE_ID="id";
    public static final String ELE_TEXT="text"; 
    
    public static String IndexFileDir;
    public static final String File_Sufix="_index.txt";
    public static final int File_prefix=26;
    
    public static BufferedWriter TitleIndexWriter;
    	public static File TitleFile;
        
        public static final int NUM_OF_INDEXFILES = 26;
	public static final List<String> IndexFiles = new ArrayList<String>(NUM_OF_INDEXFILES);
        
        public static String wordDelimiter = "#";
        
        public static final String DOC_COUNT_DELIMITER = "-";
        public static final char CHAR_DOC_COUNT_DELIMITER = '-';
        public static final char CHAR_DOC_DELIMITER = ';';
        
     public static final int NUM_OF_PAGES_PER_CHUNK = 5000;
     public static int NumOfPagesInMap = 0;
     public static long dump=0;
     public static long lastDump;
     public static long startTime;
     public static final String WORD_DELIMITER = "=";
     public static Writer prevWriter=null;
	public static long indexCount=0;
        public static String indexFileDir;
        public static List<String> subIndexFiles=new ArrayList<String>();
	
	public static int lastSubIndexFile=1000;
        
        public static final String WEIGHT_DELIMITER = ":";
        public static DecimalFormat decimalFormat=new DecimalFormat("#.##");
        public static final int MAX_MERGE_LINE_LENGTH = 100000;
        	public static final char WORD_IDF_DELIMITER = '#';
                public static final List<String> indexFiles = new ArrayList<String>(NUM_OF_INDEXFILES);
                public static final String INDEX_SUFFIX="_index.txt";
        
	public static final String DOC_PARSIGN_REGEX = "[^a-z]";
     
     
	public static BufferedWriter dynamicStopwordsWriter;
	public static String ALL_WORDS_FILE="allWords.txt";
     
}

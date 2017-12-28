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
import java.util.concurrent.TimeUnit;
public class PageParser {
   public static Set<String> stopWords; 
    public static int totalNumberOfDoc=0;

    public static double maxWeight=0;

    private static Map<String,StringBuilder> allWords=new TreeMap<String,StringBuilder>();
    private Stemmer stemmer=new Stemmer();
     public static enum Fields{
        TITLE('t',10000,4), INFOBOX('i',20,3), LINKS('l',10,2), CATAGORY('c',50,1), BODY('b',1,0);
        private char shortForm;
        private int weight;
        private int setbit;
        private Fields(char shortForm,int weight,int setbit){
            this.shortForm=shortForm;
            this.weight=weight;
            this.setbit= 1 << setbit;
        }
   
     
      public char getShortForm() {
            return shortForm;
        }
        public int getWeight(){
            return weight;
        }
        public int getSetbit(){
            return setbit;
        }
         public static Fields getField(Character shortForm){
            switch(shortForm){
                case 't': return TITLE;
                case 'i' : return INFOBOX;
                case 'l' :return LINKS;
                case 'c' :return CATAGORY;
                case 'b' :return BODY;
            }
            return null;
        }}
          public void parse(WikiPage page) throws IOException {
               if(page.getTitle().indexOf("Wikipedia:") == 0 || page.getTitle().indexOf("File:") == 0){
            return;
               }
               totalNumberOfDoc++;
                page.setId(String.valueOf(totalNumberOfDoc));
        Map<String,Integer[]> wordCount=new HashMap<String, Integer[]>(256);
          
            String aux = page.getTitle().toString();
            
            WikiConstant.TitleIndexWriter.write(page.getId());
        WikiConstant.TitleIndexWriter.write(WikiConstant.wordDelimiter);
        WikiConstant.TitleIndexWriter.write(aux);
        WikiConstant.TitleIndexWriter.write('\n');
        
        parseText(aux.toLowerCase(), wordCount,Fields.TITLE);

        
        aux = page.getText().toString().toLowerCase();
        parseText(aux,wordCount, Fields.BODY);

        aux = page.getinfoBox().toString().toLowerCase();
        parseText(aux,wordCount, Fields.INFOBOX);

        aux=page.getExternalLinks().toString().toLowerCase();
        parseText(aux,wordCount, Fields.LINKS);

        aux=page.getCategory().toString().toLowerCase();
        parseText(aux,wordCount, Fields.CATAGORY);
        insertToAllWords(page, wordCount);
        
          }
      
          public void insertToAllWords(WikiPage page, Map<String,Integer[]> wordCount)

            throws IOException{

        Iterator<Map.Entry<String,Integer[]> > entries = wordCount.entrySet().iterator();

        StringBuilder docList=null;
        while(entries.hasNext()){
            Map.Entry<String, Integer[]> entry = entries.next();
            docList = allWords.get(entry.getKey());
            if(docList == null){
                docList=new StringBuilder();
                docList.append(page.getId())
                        .append( WikiConstant.DOC_COUNT_DELIMITER)
                        .append(getFieldsString(entry.getValue())
                                .append( WikiConstant.CHAR_DOC_DELIMITER));
                allWords.put(entry.getKey(), docList);
            }else{
                docList.append(page.getId())
                        .append( WikiConstant.DOC_COUNT_DELIMITER)
                        .append(getFieldsString(entry.getValue())
                                .append( WikiConstant.CHAR_DOC_DELIMITER));
            }
            //System.out.println(entry.getKey()+ ":"+page.getId()+"-"+entry.getValue());
        }

        if( WikiConstant.NUM_OF_PAGES_PER_CHUNK == ++WikiConstant.NumOfPagesInMap ){
            System.out.println("dump number "+ ++WikiConstant.dump
                    +" last dump dur:(s) " + TimeUnit.SECONDS.
                    convert(System.currentTimeMillis()- WikiConstant.lastDump, TimeUnit.MILLISECONDS)
                    +" total time(m) " + TimeUnit.MINUTES.
                    convert(System.currentTimeMillis()- WikiConstant.startTime, TimeUnit.MILLISECONDS));
            WikiConstant.lastDump = System.currentTimeMillis();
            dumpAllWords();
            WikiConstant.NumOfPagesInMap = 0;
            allWords = new TreeMap<>();
            System.gc();
        }
    }
          
          
           public void dumpAllWords() throws IOException{

        if(allWords.size() == 0)
            return;

        Writer writer = getWriterForDump();
        Iterator<Map.Entry<String, StringBuilder>> entries = allWords.entrySet().iterator();
        StringBuffer blockOfData;
        Map.Entry<String,StringBuilder> entry=null;

        while( entries.hasNext()){
            blockOfData = new StringBuffer(2048);
            entry = entries.next();
            blockOfData.append(entry.getKey())
                    .append(WikiConstant.WORD_DELIMITER)
                    .append(entry.getValue())
                    .append("\n");
            writer.write(blockOfData.toString());
            //}
        }
        try {
            if(writer != null)
                writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

           
           
           public void writeData(String blockOfData, Writer writer ){
        try {
            if(WikiConstant.prevWriter != writer){
                WikiConstant.prevWriter=writer;
                System.out.println("file number "+ ++WikiConstant.indexCount
                        +" last file dur:(s) " +TimeUnit.SECONDS.
                        convert(System.currentTimeMillis()- WikiConstant.lastDump, TimeUnit.MILLISECONDS)
                        +" total time(m) " + TimeUnit.MINUTES.
                        convert(System.currentTimeMillis()- WikiConstant.startTime, TimeUnit.MILLISECONDS));
                WikiConstant.lastDump=System.currentTimeMillis();
            }
            writer.write(blockOfData);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

           public Writer getWriterForDump() {

        File dumpFile=new File(WikiConstant.indexFileDir,"" +
                WikiConstant.lastSubIndexFile++ );
        WikiConstant.subIndexFiles.add(dumpFile.getAbsolutePath());
        try {
            return new BufferedWriter(new FileWriter(dumpFile,false));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
           
           public void parseText(String text, Map<String, Integer[]> wordCount, Fields type){
        String []tokens = text.split(WikiConstant.DOC_PARSIGN_REGEX);
        Integer[] count;
        for(String token:tokens){

            if(token.isEmpty()){
                continue;
            }

            if( stopWords.contains(token))
                continue;

            token = stemmer.stemWord(token);
            count = wordCount.get(token);
            if(count == null){
                count = new Integer[]{0,0,0,0,0};
                count[type.ordinal()]++;
                wordCount.put(token, count);
            }else{
                count[type.ordinal()]++;
                wordCount.put(token, count);
            }
        }
    }
           
           
            public void mergeSubIndexFiles() throws IOException {


        List<BufferedReader> readers = getReaderOfSubIndexFiles();
        boolean reachedEOF[] = new boolean[readers.size()];

        PriorityQueue<MergeLine> pq=new PriorityQueue<MergeLine>
                (WikiConstant.subIndexFiles.size());
        List<BufferedWriter> indexFileWriters = new ArrayList<BufferedWriter>
                (WikiConstant. NUM_OF_INDEXFILES);
        BufferedWriter allWordsWriter = new BufferedWriter(new FileWriter
                (new File(WikiConstant.indexFileDir, WikiConstant.ALL_WORDS_FILE)));
        File auxIndexFile=null;
        for(int i = 0; i< WikiConstant. NUM_OF_INDEXFILES; i++){
            auxIndexFile = new File(WikiConstant.indexFileDir,
                    i + WikiConstant.INDEX_SUFFIX);
            WikiConstant.indexFiles.add(auxIndexFile.getAbsolutePath());
            indexFileWriters.add(new BufferedWriter(new FileWriter(auxIndexFile,false)));
        }

        for(int i=0; i < readers.size(); i++){
            nextMergeLine(pq, readers, i,reachedEOF);
        }
         List<MergeLine> sameWords = new ArrayList<MergeLine>();;
        MergeLine mergeLine=null;
        String currentWord=null;
        int numOfDocsWordPresent=0;
        String auxDocIds;
        String idf;
        // get least line from PQ and set current word
        mergeLine = pq.poll();
        nextMergeLine(pq, readers,mergeLine.getFileNum(),reachedEOF);
        currentWord=mergeLine.getWord();
        Writer currentWriter=indexFileWriters.get(currentWord.charAt(0) - 'a');
        sameWords.add(mergeLine);
         while(!pq.isEmpty()){
            mergeLine = pq.poll();
            // if same word then we have to append
            if(currentWord.equals(mergeLine.getWord())){
                sameWords.add(mergeLine);
            }else{  // append all docIds of currentWord
                currentWriter=indexFileWriters.get(currentWord.charAt(0) - 'a');
                //cal IDF
                numOfDocsWordPresent = 0;
                for(MergeLine sameWord:sameWords){
                    auxDocIds = sameWord.getDocIds();
                    for(int i=0;i<auxDocIds.length();i++){
                        if(WikiConstant.CHAR_DOC_DELIMITER == auxDocIds.charAt(i))
                            numOfDocsWordPresent++;
                    }
                }
                  allWordsWriter.write(currentWord + WikiConstant.WORD_IDF_DELIMITER +
                        numOfDocsWordPresent +"\n");
                idf = invertedDocumentFreq(numOfDocsWordPresent);
                StringBuffer wholeLine = new StringBuffer();
                //Add word
                wholeLine.append(currentWord).append(WikiConstant.WORD_IDF_DELIMITER)
                        .append(idf).append(WikiConstant.WORD_DELIMITER);
                //append docIds
                for(MergeLine sameWord:sameWords){
                    wholeLine.append(sameWord.getDocIds());
                    if(wholeLine.length()> WikiConstant.MAX_MERGE_LINE_LENGTH){
                        writeData(wholeLine.toString(), currentWriter);
                        wholeLine=new StringBuffer();
                    }
                }

                wholeLine.append("\n");

                writeData(wholeLine.toString(), currentWriter);
                sameWords=new ArrayList<MergeLine>();
                sameWords.add(mergeLine);
                currentWord=mergeLine.getWord();
                //		printLineToIndexFile(sameWords, currentWord, indexFileWriter, mergeLine);
            }
            nextMergeLine(pq, readers, mergeLine.getFileNum(), reachedEOF);
        }
         numOfDocsWordPresent=0;
        for(MergeLine sameWord:sameWords){
            auxDocIds=sameWord.getDocIds();
            for(int i=0;i<auxDocIds.length();i++){
                if(';' == auxDocIds.charAt(i))
                    numOfDocsWordPresent++;
            }
        }
        allWordsWriter.write(currentWord+ WikiConstant.WORD_IDF_DELIMITER+numOfDocsWordPresent+"\n");
        idf=invertedDocumentFreq(numOfDocsWordPresent);
        StringBuffer wholeLine=new StringBuffer();
        //Add word
        currentWriter=indexFileWriters.get(currentWord.charAt(0) - 'a');
        wholeLine.append(currentWord).append(WikiConstant.WORD_IDF_DELIMITER)
                .append(idf).append(WikiConstant.WORD_DELIMITER);
        //append docIds
        for(MergeLine sameWord:sameWords){
            wholeLine.append(sameWord.getDocIds());
            if(wholeLine.length()> WikiConstant.MAX_MERGE_LINE_LENGTH){
                writeData(wholeLine.toString(), currentWriter);
                wholeLine=new StringBuffer();
            }
        }
         wholeLine.append("\n");
        writeData(wholeLine.toString(), currentWriter);
        //printLineToIndexFile(sameWords, currentWord, indexFileWriter, mergeLine);



        for(BufferedReader reader:readers){
            if(reader != null)
                reader.close();
        }
        for(BufferedWriter indexFileWriter:indexFileWriters){
            if(indexFileWriter != null)
                indexFileWriter.close();
        }
        if(allWordsWriter!=null)
            allWordsWriter.close();
        deleteFiles(WikiConstant.subIndexFiles);
    }
            
             public void deleteFiles(List<String> filePaths){
        for(String filePath:filePaths){
            new File(filePath).deleteOnExit();
        }
    }
             
             
    public void nextMergeLine(PriorityQueue<MergeLine> pq ,List<BufferedReader> readers,
                              int readerNum, boolean[] reachedEOF) throws IOException{

        String line = null, word = null, docIds = null;
        int delimiterIndex=0;
        int numberOfFileReachedEnd=0;
        int numberOfReaders = readers.size();
        while( numberOfFileReachedEnd != numberOfReaders && reachedEOF[readerNum] ){
            readerNum = (readerNum+1) % numberOfReaders;
            numberOfFileReachedEnd++;
        }

        if( numberOfFileReachedEnd == numberOfReaders)
            return;

        if ( (line = readers.get(readerNum).readLine() ) != null){
            delimiterIndex = line.indexOf(WikiConstant.WORD_DELIMITER);
            word=line.substring(0,delimiterIndex);
            docIds=line.substring(delimiterIndex+1);
            pq.add(new MergeLine(readerNum, word, docIds));
        }else{
            reachedEOF[readerNum]=true;
            nextMergeLine(pq, readers, (readerNum+1) % numberOfReaders, reachedEOF);
        }
    }
    
     public List<BufferedReader> getReaderOfSubIndexFiles(){
        List<BufferedReader> readers=new ArrayList<BufferedReader>(WikiConstant.subIndexFiles.size());
        for(String file: WikiConstant.subIndexFiles){
            try {
                readers.add(new BufferedReader(new FileReader(new File(file))));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return readers;
    }
      private StringBuilder getFieldsString(Integer[] values){

        StringBuilder valueString = new StringBuilder();
        int weight=0;
        int setBit=0;
        for(Fields field:Fields.values()){
            if(values[field.ordinal()] == 0)
                continue;
            weight = weight + (values[field.ordinal()].intValue() * field.getWeight());
            setBit = setBit | field.getSetbit();
            //valueString.append(field.getShortForm());
        }
        valueString.append(setBit).append(WikiConstant.WEIGHT_DELIMITER)
                .append( (termFrequence(weight)) );
        return valueString;
    }
private int getWeight(Integer[] values){

        int weight=0;
        for(Fields field:Fields.values()){
            if(values[field.ordinal()] == 0)
                continue;
            weight = weight + (values[field.ordinal()].intValue() * field.getWeight());
            //valueString.append(field.getShortForm());
        }
        return weight;
    }

 private String termFrequence(int weight){
        double result=0;
        if(weight == 0)
            return "0";
        else{
            result= 1 + Math.log10(weight);
            //result= weight/maxWeight;
        }
        return WikiConstant.decimalFormat.format(result);
    }
 
  private String invertedDocumentFreq(int numOfDocsWordPresent){
        double result=0;
        if(numOfDocsWordPresent == 0)
            return "0";
        else{
            result=  Math.log10( ((double)PageParser.totalNumberOfDoc) / numOfDocsWordPresent);
        }
        return WikiConstant.decimalFormat.format(result);
    }
           
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Abhishek
 */
import javax.xml.parsers.*;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;
import java.io.*;
import java.util.*;
public class WikiSAXHandler extends DefaultHandler{
    
    private enum CurrentElement{
        TITLE,ID,TEXT
    }
    
    private Map<String,Boolean> requiredElements;
    private boolean parse=false;
    
    private WikiPage page;
    private CurrentElement currentElement;
    
    private enum TextFeilds{
        INFOBOX("{{infobox"),
        EXTERNAL_LINKS("external_links"),
        TEXT("text"),
        CATEGORY("[[category:");
        private String pattern;
        TextFeilds(String pattern){
            this.pattern=pattern;
        }
        
    }
     
   // private PageParser pageParser=new PageParser();
    WikiSAXHandler(){
        requiredElements=new HashMap<String,Boolean>();
        requiredElements.put(WikiConstant.ELE_TITLE,true);
        requiredElements.put(WikiConstant.ELE_ID,false);
        requiredElements.put(WikiConstant.ELE_TEXT,true);
       
    }
    public void startDocument() throws SAXException{
       WikiConstant.TitleFile=new File(WikiConstant.IndexFileDir,WikiConstant.File_prefix+WikiConstant.File_Sufix);
       try{
       WikiConstant.TitleIndexWriter=new BufferedWriter(new FileWriter(WikiConstant.TitleFile));
    }catch(IOException e){
       e.printStackTrace(); 
    }
    }
    public void endDocuments()throws SAXException{
        try {
            WikiConstant.TitleIndexWriter.close();
             WikiConstant.IndexFiles.add(WikiConstant.TitleFile.getAbsolutePath());
             
        }
        catch (IOException e) {
			e.printStackTrace();
		}
    }
     
         private int countOfIBCurl=0;
	private TextFeilds prevFields;
	private TextFeilds curFields;
	private boolean infoboxDone;
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException{
        qName = qName.toLowerCase();
        if(requiredElements.get(qName) != null && requiredElements.get(qName) ) {
            switch (qName) {
                case XMLPageConfiguration.ELEMENT_TITLE:
                    requiredElements.put(XMLPageConfiguration.ELEMENT_ID, true);
                    currentElement = CurrentElement.TITLE;
                    page = new WikiPage();
                     countOfIBCurl = 0;
                    prevFields = TextFeilds.TEXT;
                    curFields = TextFeilds.TEXT;
                    infoboxDone = false;
                    break;
                     case WikiConstant.ELE_ID:
                    currentElement = CurrentElement.ID;
                    break;
                     default:
                    currentElement = CurrentElement.TEXT;
                    break;
        }
            parse=true;
    }
    }     
    
    public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
		qName = qName.toLowerCase();
		if(parse){
			if(qName.equals(WikiConstant.ELE_ID)){
				requiredElements.put(WikiConstant.ELE_ID, false);
			}else if(qName.equals(WikiConstant.ELE_TEXT)){
//				try {
//					pageParser.parse(page);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
			}
			parse=false;
		}
	}
    
   public void characters(char[] ch, int start, int length)
			throws SAXException {
        if (parse) {
            switch (currentElement) {
                case TEXT:
                    divideFields(ch, start, length);
                    break;
                case TITLE:
                    page.getTitle().append(ch, start, length);
                    break;
                case ID:
                    String id = new String(ch, start, length);
                    page.setId(id);
                    break;
            }
        }
	}
   	
   
   
   private void divideFields(char[] ch, int start, int length){
		int i=start;
		boolean match=false;
		int matchIndex=0;
		
		if(curFields == TextFeilds.INFOBOX || curFields == TextFeilds.CATEGORY){
			if(curFields == TextFeilds.INFOBOX){ //match curl braces for infobox
				for(;i<start+length;i++){
					if( ch[i] == '{')
						countOfIBCurl++;
					else if(ch[i] == '}')
						countOfIBCurl--;
					if(countOfIBCurl == 0){
						addStringToPrevField(ch, start, i-start+1, curFields);
						curFields= TextFeilds.TEXT;
						infoboxDone=true;
						divideFields(ch, i , length-(i - start));
						return;
					}
				}
			}
            else if(curFields == TextFeilds.CATEGORY){
				for(;i<start+length;i++){
					if( ch[i] == '[')
						countOfIBCurl++;
					else if(ch[i] == ']')
						countOfIBCurl--;
					if(countOfIBCurl == 0){
						addStringToPrevField(ch, start, i-start+1, curFields);
						addStringToPrevField(ch, start, i-start+1, curFields);
						curFields= TextFeilds.TEXT;
						divideFields(ch, i , length-(i - start));
						return;
					}
				}
			}
			addStringToPrevField(ch, start, length, curFields);
			return;
		}
		for(; i< start+length ; i++ ){
			matchIndex=i;
			if(  !infoboxDone && ch[i] == '{'){
				match=isMatch(ch, start, length,i, TextFeilds.INFOBOX.pattern);
				if(match){
					prevFields=curFields;
					curFields= TextFeilds.INFOBOX;
					countOfIBCurl=0;
				}
			} else if(curFields != TextFeilds.EXTERNAL_LINKS &&ch[i] == '='){
				match=isExternalLink(ch, start, length, i, TextFeilds.EXTERNAL_LINKS.pattern);
				if(match){
					prevFields=curFields;
					curFields= TextFeilds.EXTERNAL_LINKS;
				}
				
			} else if( ch[i] == '['){
				match = isMatch(ch, start, length,i, TextFeilds.CATEGORY.pattern);
				if(match){
					prevFields=curFields;
					curFields= TextFeilds.CATEGORY;
					countOfIBCurl=0;
				}
			}
			if(match){
				break;
			}
		}
		if(match){
			addStringToPrevField(ch, start, matchIndex-start, prevFields);
			divideFields(ch, matchIndex, length-(matchIndex - start));
		}else {
			addStringToPrevField(ch, start, length, curFields);
		}
	}

   private void addStringToPrevField(char[] ch,int start,int length,TextFeilds field ){
		switch(field){
			case TEXT:
                page.getText().append(ch, start, length);
                break;
			case INFOBOX:
                page.getinfoBox().append(ch,start,length);
                break;
			case CATEGORY:
                page.getCategory().append(ch,start,length);
                break;
			case EXTERNAL_LINKS:
                page.getExternalLinks().append(ch,start,length);
                break;
		}
		
	}
    private boolean isMatch(char ch[], int start, int length ,int firstCharPos, String  pattern) {
		
			int index = 0;
			while( firstCharPos + index < start + length  &&
					index < pattern.length() &&
					Character.toLowerCase(ch[firstCharPos + index]) == pattern.charAt(index)) {
				index++;
			}
			if( index == pattern.length())
				return true;
			return false;
	}
    
    private boolean isExternalLink(char ch[], int start, int length ,int firstCharPos, String  pattern) {

		int auxFirst = firstCharPos;
		//only two equals should be there
		while( auxFirst < start + length && ch[auxFirst] == '=' ) {
			auxFirst++;
		}
		if(auxFirst == start + length || auxFirst - firstCharPos != 2)
			return false;
		while( auxFirst < start + length && ch[auxFirst] == ' ')
            auxFirst++;
		return isMatch(ch, start, length, auxFirst, pattern);
	}
    
    
    
    
}

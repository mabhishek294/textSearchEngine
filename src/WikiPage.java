/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Abhishek
 */
public class WikiPage {

    private StringBuilder title;
    private String id;
    private StringBuilder text;
    private StringBuilder infoBox;
    private StringBuilder category;
    private StringBuilder externalLinks;

    public WikiPage() {
        title = new StringBuilder(32);
        text = new StringBuilder(4096);
        infoBox = new StringBuilder(64);
        category = new StringBuilder(256);
        category = new StringBuilder(128);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public StringBuilder getTitle() {
        return title;
    }

    public void setTitle(StringBuilder title) {
        this.title = title;
    }

    public StringBuilder getText() {
        return text;
    }

    public void setText(StringBuilder text) {
        this.text = text;
    }

    public StringBuilder getinfoBox() {
        return infoBox;
    }

    public void setInfoBox(StringBuilder InfoBox) {
        this.infoBox = InfoBox;
    }

    public StringBuilder getExternalLinks() {
        return externalLinks;
    }

    public void setExternalLinks(StringBuilder externalLinks) {
        this.externalLinks = externalLinks;
    }

    public StringBuilder getCategory() {
        return category;
    }

    public void setCategory(StringBuilder category) {
        this.category = category;
    }
}

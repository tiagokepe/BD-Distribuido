package com.ufpr.gdd;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rice.p2p.commonapi.Id;
import rice.p2p.past.ContentHashPastContent;

public class Catalogo extends ContentHashPastContent {

	private List<Id> segments;
	private String title, description, subject, nameFile;
	private Date date;
	
	public Catalogo(Id cId, String nameFile, String title, String subject, String description, Date date )
	{
		super(cId);
		this.title = title;
		this.subject = subject;
		this.description = description;
		this.date = date;
		this.nameFile = nameFile;
		segments = new ArrayList<Id>();
	}
	
	public void addSegment(Id fragId)
	{
		segments.add(fragId);
	}
	
	public void printFileInfo() {
		//System.out.print("Informação deste arquivo:\n\tTítulo: "+this.title+"\n\tAssunto:"+this.subject+"\n\tDescrição:"+this.description+"\n\tData:"+this.date.toString()+"\n");
		System.out.print("Informação deste arquivo:\n\tTítulo: " + this.title
				+ "\n\tAssunto:" + this.subject + "\n\tDescrição:"
				+ this.description + "\n\tData:" + ((this.date != null)? date.toString(): null) + "\n");
	}
	
	public List<Id> getSegments(){
		return this.segments;
	}

	public String getNameFile() {
		return nameFile;
	}

	public void setNameFile(String nameFile) {
		this.nameFile = nameFile;
	}

}

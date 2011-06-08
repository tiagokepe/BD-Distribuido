package com.ufpr.gdd;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rice.p2p.commonapi.Id;
import rice.p2p.past.ContentHashPastContent;

public class Catalogo extends ContentHashPastContent {

	private List<Id> segments;
	private String title, description, subject;
	private Date date;
	
	public Catalogo(Id cId, String title, String subject, String description, Date date )
	{
		super(cId);
		this.title = title;
		this.subject = subject;
		this.description = description;
		this.date = date;
		segments = new ArrayList<Id>();
	}
	
	public void addSegment(Id fragId)
	{
		segments.add(fragId);
	}

}

package com.ufpr.gdd;

import java.util.ArrayList;
import java.util.List;

import rice.p2p.commonapi.Id;
import rice.p2p.past.ContentHashPastContent;

public class Catalogo extends ContentHashPastContent {

	private List<Id> segments;
	public Catalogo(Id cId)
	{
		super(cId);
		segments = new ArrayList<Id>();
	}
	
	public void addSegment(Id fragId)
	{
		segments.add(fragId);
	}

}

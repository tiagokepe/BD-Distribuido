package com.ufpr.gdd;

import java.util.ArrayList;
import java.util.List;
import rice.p2p.commonapi.Id;
import rice.p2p.past.ContentHashPastContent;

public class Referencia extends ContentHashPastContent {

	private java.util.List<Id> catId;
	
	public Referencia(Id myId) {
		super(myId);
		catId = new ArrayList<Id>();
	}
	
	public void addCatalog(Id newEntry) {
		catId.add(newEntry);
	}
	
	public List<Id> returnCatalogos(){
		return catId;
	}

}

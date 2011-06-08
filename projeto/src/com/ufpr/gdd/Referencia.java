package com.ufpr.gdd;

import java.util.ArrayList;
import java.util.List;
import rice.p2p.commonapi.Id;
import rice.p2p.past.ContentHashPastContent;

public class Referencia extends ContentHashPastContent {

	private java.util.List<Catalogo> files;
	
	public Referencia(Id myId) {
		super(myId);
		files = new ArrayList<Catalogo>();
	}
	
	public void addCatalog(Catalogo newEntry) {
		files.add(newEntry);
	}
	
	public List<Catalogo> returnCatalogos(){
		return files;
	}

}

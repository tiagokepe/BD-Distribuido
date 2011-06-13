package com.ufpr.gdd;

import java.util.ArrayList;
import java.util.List;
import rice.p2p.commonapi.Id;
import rice.p2p.past.ContentHashPastContent;
import rice.p2p.past.PastContent;
import rice.p2p.past.PastException;

public class Referencia extends ContentHashPastContent {

	private java.util.List<Id> catId;
	private boolean modified;

	@Override
	public PastContent checkInsert(Id id, PastContent existingContent) throws PastException {
		if (( existingContent != null) && !(this.modified) ) {
			throw new PastException("ContentHashPastContent: can't insert, object already exists");
		}

		// only allow correct content hash key
		if (!id.equals(getId())) {
			throw new PastException("ContentHashPastContent: can't insert, content hash incorrect");
		}
		modified = false;
		return this;

	}

	public Referencia(Id myId) {
		super(myId);
		catId = new ArrayList<Id>();
		modified = false;
	}

	public void addCatalog(Id newEntry) {
		catId.add(newEntry);
		modified = true;
	}

	public List<Id> returnCatalogos(){
		return catId;
	}

}

package com.ufpr.gdd;

import rice.Continuation;
import rice.p2p.past.PastContent;

public class LookupContinuation implements Continuation<PastContent, Exception>{

	PastContent returnVal;
    public void receiveResult(PastContent result) {
    	synchronized (this) {
    		
	        System.out.println("Successfully looked up " + result + " for key.");
	        this.returnVal = result;
	        this.notify();
		}
    }

    public void receiveException(Exception result) {
         System.out.println("Error looking up key ");
         result.printStackTrace();
    }
    public PastContent getVal(){
    	return returnVal;
    }
}

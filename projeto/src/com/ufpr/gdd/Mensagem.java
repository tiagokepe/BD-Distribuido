package com.ufpr.gdd;
import rice.p2p.commonapi.Message;



public class Mensagem implements Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int getPriority() {
		return Message.LOW_PRIORITY;
	}

}

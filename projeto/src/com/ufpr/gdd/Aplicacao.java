package com.ufpr.gdd;
import rice.p2p.commonapi.Application;
import rice.p2p.commonapi.Endpoint;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.commonapi.RouteMessage;
import rice.pastry.PastryNode;


public class Aplicacao implements Application{

	protected Endpoint endpoint;
	
	public Aplicacao(PastryNode node) {
		this.endpoint = node.buildEndpoint(this, "myInstance");		
		this.endpoint.accept(new AplicacaoSocket());
		
		this.endpoint.register();
	}
	@Override
	public void deliver(Id arg0, Message arg1) {
		System.out.println(this+"received "+arg1);
	}

	@Override
	public boolean forward(RouteMessage arg0) {
		return true;
	}

	@Override
	public void update(NodeHandle arg0, boolean arg1) {		
	}
	

}

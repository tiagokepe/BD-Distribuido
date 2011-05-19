package com.ufpr.gdd;

import org.mpisws.p2p.filetransfer.FileTransfer;

import rice.p2p.commonapi.Application;
import rice.p2p.commonapi.Endpoint;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.IdFactory;
import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.Node;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.commonapi.RouteMessage;
import rice.pastry.PastryNode;

/**
 * 
 * @author trk08
 * 
 */
public class Aplicacao implements Application {
	private static Endpoint endpoint;
	private static Node node;
	private static FileTransfer fileTransfer;

	public static Endpoint getEndpoint() {
		return endpoint;
	}

	public static void setEndpoint(Endpoint endpoint) {
		Aplicacao.endpoint = endpoint;
	}

	public static Node getNode() {
		return node;
	}

	public static void setNode(Node node) {
		Aplicacao.node = node;
	}

	public static FileTransfer getFileTransfer() {
		return fileTransfer;
	}

	public static void setFileTransfer(FileTransfer fileTransfer) {
		Aplicacao.fileTransfer = fileTransfer;
	}

	public Aplicacao(PastryNode node,  final IdFactory factory) {
		Aplicacao.endpoint = node.buildEndpoint(this, "myInstance");
		Aplicacao.node = node;
		
		Aplicacao.endpoint.accept(new AppSocketReceiverImpl());
		Aplicacao.endpoint.register();
	}
	
	public Aplicacao(PastryNode node) {
		Aplicacao.endpoint = node.buildEndpoint(this, "myInstance");
		Aplicacao.node = node;
		
		Aplicacao.endpoint.accept(new AppSocketReceiverImpl());
		Aplicacao.endpoint.register();
	}
	

	@Override
	public void deliver(Id arg0, Message arg1) {
		System.out.println(this + "received " + arg1);
	}

	@Override
	public boolean forward(RouteMessage arg0) {
		return true;
	}

	@Override
	public void update(NodeHandle arg0, boolean arg1) {
	}
}

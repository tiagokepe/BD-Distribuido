package com.ufpr.gdd;


import rice.p2p.commonapi.Id;
import rice.p2p.past.ContentHashPastContent;

// Classe que determina o formato do conteúdo inserido na DHT
public class Conteudo extends ContentHashPastContent {

	// Definição, em bytes, do tamanho máximo que um segmento pode ter na DHT.
	public static final int CONTENT_SIZE = 262144;

	private byte[] value = new byte[CONTENT_SIZE];
	
	public Conteudo(Id cId, byte[] buffer) {
		super(cId);
		
		value = buffer;
	}
	
	public byte[] getBuffer() {
		return value;
	}

}

package com.ufpr.gdd;

import java.util.Iterator;

import com.sun.corba.se.impl.orbutil.graph.Node;
import com.ufpr.gdd.file.FileManager;
import com.ufpr.gdd.file.FileManagerException;

import rice.Continuation;
import rice.environment.Environment;
import rice.p2p.commonapi.Id;
import rice.p2p.past.Past;
import rice.pastry.PastryNode;
import rice.pastry.commonapi.PastryIdFactory;

public class Manipulacao {
	
	
	private Past pst;
	private Environment env;
	private PastryIdFactory localFactory;
	private PastryNode node;
	public Manipulacao(Past pst, Environment env, PastryIdFactory pif, PastryNode node) {
		this.pst = pst;
		this.env = env;
	    this.localFactory = pif;
	    this.node = node;
	}
	
	public boolean armazenar(String path, String name, String descricao, String date) throws Exception
	{
		Id cId = null;
		FileManager fm;
		Iterator<String> segsIter;
		byte buffer[] = new byte[Conteudo.CONTENT_SIZE];
		//  Aqui tentamos quebrar o arquivo.
		try {
			fm = new FileManager(path,Conteudo.CONTENT_SIZE);
			fm.breakFile();
			segsIter = fm.getSegments().iterator();
			fm.finalize();
		} catch (FileManagerException e) {
			e.printStackTrace();
			return false;
		}
			
		// Inserimos cada segmento na DHT.
		while ( segsIter.hasNext() ){
			  final Conteudo cont;
			  // Cria o conteúdo que será colocado na DHT, com uma ID.
			  try {
				  String segname = segsIter.next();
				  buffer = fm.getContent(segname);
			  }catch( FileManagerException e){
					e.printStackTrace();
					return false;
			  }
			  
			  // A função buildId(byte[]) não está funcionando. 
			  // Ela gera Ids iguais para buffers distintos. Verificar a razão.
			  cId = localFactory.buildId(buffer);
			  // Criação do conteúdo a ser inserido na DHT.
			  cont = new Conteudo(cId, buffer);
				  
			  // Tenta inserir o conteúdo gerado na DHT. A sua execução é assíncrona.
			  pst.insert(cont, new Continuation<Boolean[], Exception>() {
			        // the result is an Array of Booleans for each insert
			        public void receiveResult(Boolean[] results) {          
			          int numSuccessfulStores = 0;
			          for (int ctr = 0; ctr < results.length; ctr++) {
			            if (results[ctr].booleanValue()) 
			              numSuccessfulStores++;
			          }
			          System.out.println(cont + " successfully stored at " + 
			              numSuccessfulStores + " locations.");
			        }
			  
			        public void receiveException(Exception result) {
			          System.out.println("Error storing "+cont);
			          result.printStackTrace();
			        }
			      });
			}
		return true;
	}
}

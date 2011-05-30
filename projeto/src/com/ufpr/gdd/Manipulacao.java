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
		byte[] oldbuffer = null;
		  Id teste = null;
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
			  if ( teste != null && buffer != null )
			  {
				  System.out.println(localFactory.buildId(buffer).hashCode() == teste.hashCode());
				  System.out.println("++"+ (buffer == oldbuffer)); 

			  }
			 cont = new Conteudo( teste = localFactory.buildId(buffer), buffer);
			 oldbuffer = buffer;
				  
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

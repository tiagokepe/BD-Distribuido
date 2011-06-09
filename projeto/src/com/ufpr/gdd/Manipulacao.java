package com.ufpr.gdd;

import java.util.Date;
import java.util.Iterator;
import java.util.Random;

import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;
import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.ufpr.gdd.file.FileManager;
import com.ufpr.gdd.file.FileManagerException;

import rice.Continuation;
import rice.environment.Environment;
import rice.p2p.commonapi.Id;
import rice.p2p.past.ContentHashPastContent;
import rice.p2p.past.Past;
import rice.p2p.past.PastContent;
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
	
	// Recebe a descrição de um arquivo, cataloga e armazena ele. */
	public boolean armazenar(String path, String title, String subject, String description, Date date ) throws Exception
	{
		Id cId = null;
		FileManager fm;
		Iterator<String> segsIter;
		//Random rng = new Random();
		
		// O catalogo terá o array de Ids do arquivo
		
		// Geramos uma id única para este catálogo
		Id catId = localFactory.buildId(title+subject+description+date.toString());
		final Catalogo arquivo = new Catalogo(catId, title, subject, description, date); 
		
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
			  cId = localFactory.buildId(buffer.toString());
			  // Criação do conteúdo a ser inserido na DHT.
			  cont = new Conteudo(cId, buffer);
			  
			  // Inserção deste conteúdo no nosso catálogo
			  arquivo.addSegment(cId);
			  
			  // Tenta inserir o conteúdo gerado na DHT.
			  storeObject(cont);
			}
		
		// Tentamos inserir então o catálogo na DHT
		  storeObject(arquivo);
		  
		 // Precisamos então lidar com as referências a este catálogo
		// Referência de títulos
		Referencia ref = handleReference(title);
		ref.addCatalog(catId);
		storeObject(ref);
		
		
		// Referência de assunto
		ref = handleReference(subject);
		ref.addCatalog(catId);
		storeObject(ref);
		
		// Referência de descrição
		ref = handleReference(description);
		ref.addCatalog(catId);
		storeObject(ref);
		
		
		return true;
	}
	// Lida com referẽncias
	// Se a referência for achada na DHT, será retornada, senão uma nova será criada.
	private Referencia handleReference(String refStr) {
		Referencia ref;
		
		Id refId = localFactory.buildId(refStr);
		
		ref = (Referencia) getObject(refId);
		
		if ( ref == null) {
			ref = new Referencia(refId);
		}
		
		return ref;
	}

	/* Armazena um objeto na DHT. A sua execução é assíncrona. */
	private void storeObject( final ContentHashPastContent obj) {
		 pst.insert(obj, new Continuation<Boolean[], Exception>() {
		        // the result is an Array of Booleans for each insert
		        public void receiveResult(Boolean[] results) {          
		          int numSuccessfulStores = 0;
		          for (int ctr = 0; ctr < results.length; ctr++) {
		            if (results[ctr].booleanValue()) 
		              numSuccessfulStores++;
		          }
		          System.out.println(obj + " successfully stored at " + 
		              numSuccessfulStores + " locations.");
		        }
		  
		        public void receiveException(Exception result) {
		          System.out.println("Error storing "+obj);
		          result.printStackTrace();
		        }
		      });
	}
	private PastContent getObject(final Id lookupKey){
		
		PastContent lookupContent;
		
	    LookupContinuation lck = new LookupContinuation();
		pst.lookup(lookupKey,lck);
		
		synchronized (lck) {
			try {
				lck.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			lookupContent = lck.getVal();
		}
		
		return lookupContent;
	}
}

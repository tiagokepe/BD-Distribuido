package com.ufpr.gdd;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.mpisws.p2p.transport.util.FileOutputBuffer;

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
	private String baseDir = new String("/tmp");
	
	public Manipulacao(Past pst, Environment env, PastryIdFactory pif, PastryNode node) {
		this.pst = pst;
		this.env = env;
	    this.localFactory = pif;
	    this.node = node;
	}
	
	// Recebe a descrição de um arquivo, cataloga e armazena ele. */
	public void armazenar(String path, String title, String subject, String description, Date date ) throws Exception
	{
		Id cId = null;
		FileManager fm;
		Iterator<String> segsIter;
		//Random rng = new Random();
		
		// O catalogo terá o array de Ids do arquivo
		
		// Geramos uma id única para este catálogo
		//Id catId = localFactory.buildId(title+subject+description);
        Id catId = localFactory.buildId(String.valueOf(System.currentTimeMillis()));
		final Catalogo arquivo = new Catalogo(catId, path.substring(path.lastIndexOf("/") + 1), title, subject, description, date); 
		
		byte buffer[] = new byte[Conteudo.CONTENT_SIZE];
		//  Aqui tentamos quebrar o arquivo.
		try {
			fm = new FileManager(path,Conteudo.CONTENT_SIZE);
			fm.breakFile();
			segsIter = fm.getSegments().iterator();
			fm.finalize();
		} catch (FileManagerException e) {
			throw new Exception(e.getMessage());
		}
			
		// Inserimos cada segmento na DHT.
		while ( segsIter.hasNext() ){
			  final Conteudo cont;
			  // Cria o conteúdo que será colocado na DHT, com uma ID.
			  try {
				  String segname = segsIter.next();
				  buffer = fm.getContent(segname);
			  }catch( FileManagerException e){
				  throw new Exception(e.getMessage());
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
		Referencia ref;
		
		if ( title != null ) {
			ref = handleReference(title);
			ref.addCatalog(catId);
			storeObject(ref);
		}
		
		
		// Referência de assunto
		if ( subject != null ) {
			ref = handleReference(subject);
			ref.addCatalog(catId);
			storeObject(ref);
		}
		
		// Referência de descrição
		if ( description != null ) {
			ref = handleReference(description);
			ref.addCatalog(catId);
			storeObject(ref);
		}
		
		// Referência de data
		if ( date != null ) {
			ref = handleReference(date.toString());
			ref.addCatalog(catId);
			storeObject(ref);
		}
	}
 
	/**
	 * Lida com referẽncias
	 * Se a referência for achada na DHT, será retornada, senão uma nova será criada.
	 */
	private Referencia handleReference(String refStr) {
		Referencia ref = null;
		
		
		if ( refStr == null ) return null;
		
		Id refId = localFactory.buildId(refStr);
		
		ref = (Referencia) getObject(refId);
		
		if ( ref == null) {
			ref = new Referencia(refId);
		}
		
		return ref;
	}

	/* Armazena um objeto na DHT. A sua execução é assíncrona. */
	private void storeObject( final ContentHashPastContent obj) {
		
		if ( obj == null) return;
		
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
	// Busca um objeto na DHT com base em uma chave
	private PastContent getObject(final Id lookupKey){
		
		PastContent lookupContent;
		
	    LookupContinuation lck = new LookupContinuation();
		pst.lookup(lookupKey,lck);
		
		synchronized (lck) {
			if( ! lck.isReady() ) {
				try {
					lck.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			lookupContent = lck.getVal();
		}
		
		return lookupContent;
	}

	/**
	 * Retorna uma lista de catálogos que satisfazem a uma busca
	 */
	public boolean buscar(String term) throws ManipulacaoException{
		
		
		
		// Buscamos a referência correspondente ao nosso termo
		Referencia ref;
		
		Id refId = localFactory.buildId(term);
		ref = (Referencia) getObject(refId);
		
		// Se não há nenhuma referência,  então este arquivo não existe
		if ( ref == null ) {
			throw new ManipulacaoException("A sua busca não retornou resultados");
		}
		
		
		// Buscamos todos os catálogos referenciados,
		// por exemplo publicações de mesma data, ou com mesmo título
		List<Id> catIds = ref.returnCatalogos();
		Iterator<Id> iterator = catIds.iterator();
		
		System.out.println("Sua busca retornou "+ catIds.size() +" resultado(s).");
		
		while ( iterator.hasNext() ){
			Id arqId = iterator.next();
			Catalogo arq = (Catalogo) getObject(arqId);
			arq.printFileInfo();
			
			System.out.println("Este é o arquivo desejado? (S/N) ");
			if ( confirmFile() ){
				downloadFile(arq);
				break;
			}
		}
		
		return true;
	}
	
	/**
	 * Faz uma cópia no diretório da máquina local do arquivo referenciado pelo catálogo
	 * 
	 * @param cat
	 * 		Referencia para as partes do arquivo na DHT
	 * @throws ManipulacaoException
	 * 		
	 */
	private void downloadFile(Catalogo cat) throws ManipulacaoException{
		
		List<Id> segIds = cat.getSegments();
		Iterator<Id> iterator = segIds.iterator();
		byte[] buffer = new byte[Conteudo.CONTENT_SIZE];
		BufferedOutputStream output = null;

		try {
			output = new BufferedOutputStream(new FileOutputBuffer(this.baseDir + "/" + cat.getNameFile()));
			
			while (iterator.hasNext()) {

				Id segId = iterator.next();

				Conteudo seg = (Conteudo) getObject(segId);

				if (seg == null)
					throw new ManipulacaoException(
							"O arquivo contém segmentos corrompidos.");

				output.write(seg.getBuffer(), 0, seg.getBuffer().length);
				output.flush();
			}
		} catch (Exception e) {
			throw new ManipulacaoException(
					"Não foi possível escrever o arquivo no em " + this.baseDir
							+ ". " + e.getMessage());
		} finally {
			try {
				output.flush();
				if (output != null)
					output.close();
			} catch (Exception e) {
				
			}
		}
	}

	private boolean confirmFile() {
		Scanner sc = new Scanner(System.in);
		
		String str = sc.nextLine();
		
		if ( str.equalsIgnoreCase("Y") || str.equalsIgnoreCase("S") ) return true;
		
		return false;
	}
}

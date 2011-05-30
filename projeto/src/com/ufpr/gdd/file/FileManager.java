package com.ufpr.gdd.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Recebe um arquivo e quebra o mesmo em vários pedaço de 256 KB.
 * Cada parte, é jogada no /tmp da máquina.
 * 
 * @author roger
 *
 */
public class FileManager
{
	private File file;
	private BufferedInputStream input;
	private String fileName;
	private String baseDir = "/tmp/";
	
	private List<String> segments;
	public static int SEG_SIZE;

	/**
	 * Instancia a classe
	 * 
	 * @param path
	 * @throws FileManagerException
	 */
	public FileManager(String path, int seg_size) throws FileManagerException {
		file = new File(path);
		fileName = path.substring(path.lastIndexOf("/")+1);
		SEG_SIZE = seg_size;
		segments = new ArrayList<String>();

		if (file.isFile() && file.canRead()) {
			try {
				input = new BufferedInputStream(new FileInputStream(file));
			} catch (Exception e) {
				e.printStackTrace();
				throw new FileManagerException("Erro ao instanciar a classe file manager. " + e.getMessage());
			}
		} else {
			throw new FileManagerException(
			        "Arquivo não existe e/ou você não possui permissão de leitura");
		}
	}
	
	/**
	 * Quebra o arquivo em vários pedaços de 256Kb
	 * 
	 * @throws FileManagerException
	 */
	public void breakFile() throws FileManagerException {
		int bytesRead = -1, i = 0;
		byte buffer[] = new byte[SEG_SIZE];
		BufferedOutputStream output = null;
		File file;
		
		try {
			bytesRead = input.read(buffer);
			
			while (bytesRead != -1) {
				file = new File(baseDir + "/" + fileName + ".part" + i);
				file.createNewFile();
				
				output = new BufferedOutputStream(new FileOutputStream(file));
				output.write(buffer, 0, bytesRead);
				output.close();
				
				bytesRead = input.read(buffer);
				segments.add(file.getAbsolutePath());
				i++;
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new FileManagerException(e.getMessage());
		}
		
		buffer = null;
	}
	
	/**
	 * Finaliza a classe
	 */
	public void finalize() {
		try {
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public byte[] getContent(String path) throws FileManagerException {
		
		File f = new File(path);
		byte buffer[] = new byte[SEG_SIZE];
		
		try {
			BufferedInputStream bf = new BufferedInputStream(new FileInputStream(file));
			bf.read(buffer);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new FileManagerException("Erro ao tentar abir um segmento. " + e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new FileManagerException("Você não tem permissão de leitura." + e.getMessage());
		}
		
		return buffer;

	}
	
	public List<String> getSegments() {
		return this.segments;
	}
}

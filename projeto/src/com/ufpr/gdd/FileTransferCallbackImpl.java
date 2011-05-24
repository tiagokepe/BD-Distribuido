package com.ufpr.gdd;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.mpisws.p2p.filetransfer.FileTransferCallback;
import rice.p2p.util.rawserialization.SimpleInputBuffer;

/**
 * 
 * 
 * @author trk08
 *
 */
public class FileTransferCallbackImpl implements FileTransferCallback 
{
	public void messageReceived(ByteBuffer bb) {
		System.out.println("Message received: " + bb);
	}

	public void fileReceived(File f, ByteBuffer metadata) {
		try {
			String originalFileName = new SimpleInputBuffer(metadata).readUTF();
			File dest = new File("delme2.txt");
			System.out.println("Moving " + f + " to " + dest + " original:"
					+ originalFileName);
			System.out.println(f.renameTo(dest));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void receiveException(Exception e) {
		e.printStackTrace();
	}
}



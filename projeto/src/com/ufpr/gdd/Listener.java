package com.ufpr.gdd;

import org.mpisws.p2p.filetransfer.BBReceipt;
import org.mpisws.p2p.filetransfer.FileReceipt;
import org.mpisws.p2p.filetransfer.FileTransferListener;
import org.mpisws.p2p.filetransfer.Receipt;

/**
 * This listener just prints every time a method is called. It uses the incoming
 * flag to specify Downloaded/Uploaded.
 * 
 * @author trk08
 * 
 */
public class Listener implements FileTransferListener {
	public void fileTransferred(FileReceipt receipt, long bytesTransferred, long total, boolean incoming) {
		String s;
		
		if (incoming)
			s = " Downloaded ";
		else
			s = " Uploaded ";
		
		double percent = 100.0 * bytesTransferred / total;

		System.out.println(MyApp.this + s + percent + "% of " + receipt);
	}

	public void msgTransferred(BBReceipt receipt, int bytesTransferred,
			int total, boolean incoming) {
		String s;
		if (incoming) {
			s = " Downloaded ";
		} else {
			s = " Uploaded ";
		}
		double percent = 100.0 * bytesTransferred / total;
		System.out.println(MyApp.this + s + percent + "% of " + receipt);
	}

	public void transferCancelled(Receipt receipt, boolean incoming) {
		String s;
		if (incoming) {
			s = "download";
		} else {
			s = "upload";
		}
		System.out.println(MyApp.this + ": Cancelled " + s + " of " + receipt);
	}

	public void transferFailed(Receipt receipt, boolean incoming) {
		String s;
		if (incoming) {
			s = "download";
		} else {
			s = "upload";
		}
		System.out.println(MyApp.this + ": Transfer Failed " + s + " of "
				+ receipt);
	}

}

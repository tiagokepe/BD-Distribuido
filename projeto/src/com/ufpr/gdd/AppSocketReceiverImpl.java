package com.ufpr.gdd;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.mpisws.p2p.filetransfer.BBReceipt;
import org.mpisws.p2p.filetransfer.FileReceipt;
import org.mpisws.p2p.filetransfer.FileTransfer;
import org.mpisws.p2p.filetransfer.FileTransferCallback;
import org.mpisws.p2p.filetransfer.FileTransferImpl;
import org.mpisws.p2p.filetransfer.FileTransferListener;
import org.mpisws.p2p.filetransfer.Receipt;

import rice.p2p.commonapi.Node;
import rice.p2p.commonapi.appsocket.AppSocket;
import rice.p2p.commonapi.appsocket.AppSocketReceiver;

/**
 * 
 * @author trk08
 * 
 */
public class AppSocketReceiverImpl implements AppSocketReceiver 
{
	@Override
	public void receiveException(AppSocket arg0, Exception arg1) {

	}

	@Override
	public void receiveSelectResult(AppSocket arg0, boolean arg1, boolean arg2)
			throws IOException {

	}

	@Override
	public void receiveSocket(AppSocket socket) {
		Aplicacao.setFileTransfer(new FileTransferImpl(socket, new FileTransferCallbackImpl(), Aplicacao.getNode().getEnvironment()));

		Aplicacao.getFileTransfer().addListener(new MyFileListener());

		// it's critical to call this to be able to accept multiple times
		Aplicacao.getEndpoint().accept(this);
	}

 
}

/*******************************************************************************

"FreePastry" Peer-to-Peer Application Development Substrate

Copyright 2002-2007, Rice University. Copyright 2006-2007, Max Planck Institute 
for Software Systems.  All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

- Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.

- Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.

- Neither the name of Rice  University (RICE), Max Planck Institute for Software 
Systems (MPI-SWS) nor the names of its contributors may be used to endorse or 
promote products derived from this software without specific prior written 
permission.

This software is provided by RICE, MPI-SWS and the contributors on an "as is" 
basis, without any representations or warranties of any kind, express or implied 
including, but not limited to, representations or warranties of 
non-infringement, merchantability or fitness for a particular purpose. In no 
event shall RICE, MPI-SWS or contributors be liable for any direct, indirect, 
incidental, special, exemplary, or consequential damages (including, but not 
limited to, procurement of substitute goods or services; loss of use, data, or 
profits; or business interruption) however caused and on any theory of 
liability, whether in contract, strict liability, or tort (including negligence
or otherwise) arising in any way out of the use of this software, even if 
advised of the possibility of such damage.

 *******************************************************************************/
package com.ufpr.gdd;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import rice.Continuation;
import rice.environment.Environment;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.past.*;
import rice.pastry.*;
import rice.pastry.commonapi.PastryIdFactory;
import rice.pastry.socket.SocketPastryNodeFactory;
import rice.pastry.standard.RandomNodeIdFactory;
import rice.persistence.*;

/**
 * This Principal shows how to setup a FreePastry node using the Socket
 * Protocol.
 * 
 * @author Jeff Hoye
 */
public class Principal {

	// this will keep track of our applications
	Aplicacao apps;

	/**
	 * This constructor launches numNodes PastryNodes. They will bootstrap to an
	 * existing ring if one exists at the specified location, otherwise it will
	 * start a new ring.
	 * 
	 * @param bindport
	 *            the local port to bind to
	 * @param bootaddress
	 *            the IP:port of the node to boot from
	 * @param numNodes
	 *            the number of nodes to create in this JVM
	 * @param env
	 *            the environment for these nodes
	 * @param useDirect
	 *            true for the simulator, false for the socket protocol
	 */
	public Principal(int bindport, InetSocketAddress bootaddress,
			Environment env) throws Exception {

		// Generate the NodeIds Randomly
		NodeIdFactory nidFactory = new RandomNodeIdFactory(env);

		// construct the PastryNodeFactory
		PastryNodeFactory factory = new SocketPastryNodeFactory(nidFactory,
				bindport, env);

		PastryNode node = factory.newNode();

		PastryIdFactory idFactory = new rice.pastry.commonapi.PastryIdFactory(env);

//		Aplicacao app = new Aplicacao(node, idFactory);
		
		
		Storage stor = new MemoryStorage(idFactory);
		Past pst = new PastImpl(node, new StorageManagerImpl(idFactory, stor, new LRUCache( new MemoryStorage(idFactory), 512 * 1024, node.getEnvironment())), 0, "");

		// Cria clase que manipulará requisições na DHT.
		Manipulacao mp = new Manipulacao(pst, env, idFactory,node);
		
		node.boot(bootaddress);

		// the node may require sending several messages to fully boot into the
		// ring
		synchronized (node) {
			while (!node.isReady() && !node.joinFailed()) {
				// delay so we don't busy-wait
				node.wait(500);

				// abort if can't join
				if (node.joinFailed()) {
					throw new IOException("Could not join the FreePastry ring.  Reason:" + node.joinFailedReason());
				}
			}
		}

		System.out.println("Finished creating new node " + node);
		
		mp.armazenar("/home/antonio/100_8367.jpg", null, null, null);
		
		
	}

	/**
	 * Usage: java [-cp FreePastry-<version>.jar]
	 * rice.Principal.appsocket.Principal localbindport bootIP bootPort numNodes
	 * or java [-cp FreePastry-<version>.jar] rice.Principal.appsocket.Principal
	 * -direct numNodes
	 * 
	 * example java rice.Principal.DistPrincipal 9001 pokey.cs.almamater.edu
	 * 9001 10 example java rice.Principal.DistPrincipal -direct 10
	 */
	public static void main(String[] args) throws Exception {
		try {

			// Loads pastry settings
			Environment env;

			env = new Environment();

			// disable the UPnP setting (in case you are testing this on a
			// NATted LAN)
			// env.getParameters().setString("nat_search_policy", "never");

			int bindport = 0;
			InetSocketAddress bootaddress = null;

			// the port to use locally
			bindport = Integer.parseInt(args[0]);

			// build the bootaddress from the command line args
			InetAddress bootaddr = InetAddress.getByName(args[1]);
			int bootport = Integer.parseInt(args[2]);
			bootaddress = new InetSocketAddress(bootaddr, bootport);

			// launch our node!
			new Principal(bindport, bootaddress, env);
		} catch (Exception e) {
			// remind user how to use
			System.out.println("Usage:");
			System.out
					.println("java [-cp FreePastry-<version>.jar] rice.Principal.appsocket.Principal localbindport bootIP bootPort numNodes");
			System.out.println("  or");
			System.out
					.println("java [-cp FreePastry-<version>.jar] rice.Principal.appsocket.Principal -direct numNodes");
			System.out.println();
			System.out
					.println("example java rice.Principal.DistPrincipal 9001 pokey.cs.almamater.edu 9001 10");
			System.out
					.println("example java rice.Principal.DistPrincipal -direct 10");
			throw e;
		}
	}
}
// Copyright (c) 2013, Been Shi (powerstudio1st@163.com)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//		* Redistributions of source code must retain the above copyright
//			notice, this list of conditions and the following disclaimer.
//		* Redistributions in binary form must reproduce the above copyright
//			notice, this list of conditions and the following disclaimer in the
//			documentation and/or other materials provided with the distribution.
//		* Neither the name of the <organization> nor the
//			names of its contributors may be used to endorse or promote products
//			derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package whiley.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.io.*;
import java.net.*;
import java.util.*;

import wyjc.runtime.WyObject;
import wyjc.runtime.WyList;
import wyjc.runtime.WyRecord;

public class Socket$native {
	public static WyObject ClientSocket(String host, BigInteger port) {
		WyRecord r = new WyRecord();
		try {
			Socket s = new Socket(host, port.intValue());
			DataInputStream sin = new DataInputStream(s.getInputStream());
			DataOutputStream sout = new DataOutputStream(s.getOutputStream());
			InetAddress iad = InetAddress.getLocalHost();
			r.put("remoteHost", host);
			r.put("remotePort", port);
			r.put("localHost", iad.getHostAddress());
			r.put("localPort", new BigInteger("-1"));
			r.put("$sin", sin);
			r.put("$sout", sout);
			r.put("$csock", s);
			WyObject p = new WyObject(r);
			p.start();
			return p;
		} catch(IOException ioe) {
			//ioe.printStack();
		}
		return null;
	}

	public static boolean isConnected(WyObject p) {
		if (p == null) {
			return false;
		} else {
			Socket s = (Socket) ((HashMap) p.state()).get("$csock");
			return s.isConnected();
		}
	}

	public static WyObject ServerSocket(BigInteger port) {
		WyRecord r = new WyRecord();
		try {
			ServerSocket s = new ServerSocket(port.intValue());
			InetAddress iad = InetAddress.getLocalHost();
			r.put("$ssock", s);
			r.put("localPort", port);
			r.put("localHost", iad.getHostAddress());
			WyObject p = new WyObject(r);
			p.start();
			return p;
		} catch(IOException ioe) {
			//ioe.printStack();
		}
		return null;
	}

	public static WyObject accept(WyObject p) {
		if (p == null)
			return null;
		WyRecord r = new WyRecord();
		ServerSocket ss = (ServerSocket) ((HashMap) p.state()).get("$ssock");
		try {
			Socket s = ss.accept();
			DataInputStream sin = new DataInputStream(s.getInputStream());
			DataOutputStream sout = new DataOutputStream(s.getOutputStream());
			InetAddress iad = InetAddress.getLocalHost();
			r.put("remoteHost", new String(""));
			r.put("remotePort", new BigInteger("-1"));
			r.put("localHost", iad.getHostAddress());
			r.put("localPort", new BigInteger("-1"));
			r.put("$sin", sin);
			r.put("$sout", sout);
			r.put("$csock", s);
			WyObject ps = new WyObject(r);
			ps.start();
			return ps;
		} catch(IOException ioe) {
			//ioe.printStack();
		}
		return null;
	}

	public static void close(WyObject p) {
		if (!isConnected(p))
			return;
		Socket s = (Socket) ((HashMap) p.state()).get("$csock");
		DataInputStream sin = (DataInputStream) ((HashMap) p.state()).get("$sin");
		DataOutputStream sout = (DataOutputStream) ((HashMap) p.state()).get("$sout");
		try {
			sin.close();
			sout.close();
			s.close();
		} catch (IOException ioe) {
			//ioe.printStack();
		}
	}

	public static WyList read(WyObject p, BigInteger max) {
		if (!isConnected(p))
			return new WyList();
		DataInputStream sin = (DataInputStream) ((HashMap) p.state()).get("$sin");	
		WyList r = new WyList();
		byte[] bytes = new byte[max.intValue()];		
		try {
			int nbytes = sin.read(bytes);
			for(int i = 0; i != nbytes; ++i) {				
				r.add(bytes[i]);
			}
		} catch (IOException ioe) {
			//ioe.printStack();
		}
		return r;	
	}
	
	public static void write(WyObject p, WyList bytes) {
		if (!isConnected(p))
			return;
		DataOutputStream sout = (DataOutputStream) ((HashMap) p.state()).get("$sout");
		try {
			byte[] bs = new byte[bytes.size()];
			for(int i = 0; i != bs.length; ++i) {
				Byte r = (Byte) bytes.get(i); 
				bs[i] = r.byteValue();
			}
			sout.write(bs);			
		} catch (IOException ioe) {
			//ioe.printStack();
		}
	}
}

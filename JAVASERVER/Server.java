package TheServer;

import java.io.*;
import java.net.*;
import java.lang.Thread;
import java.util.*;
import static java.lang.System.out;
 
public class Server {
	ArrayList<Socket> SocketList = new ArrayList<Socket>();
	public static void main(String args[]) {
		Server onServer = new Server();
		try {
			ServerSocket server = new ServerSocket(5555);
			while (true) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {

				}
				Socket s1 = server.accept();
				onServer.SocketList.add(s1);
				Thread t1 = new MyThread1(s1, onServer);
				t1.start();
				System.out.println("Get IP:" + s1.getInetAddress());
			}
		} catch (IOException e) {
			System.out.println("Error");
		}
	}
}


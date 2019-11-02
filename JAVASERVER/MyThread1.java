package TheServer;

import java.io.*;
import java.net.*;
import java.lang.Thread;
import java.util.*;
import static java.lang.System.out;

public class MyThread1 extends Thread {
	Socket a;
	Server b;
	boolean life = true;

	public MyThread1(Socket x, Server y) {
		a = x;
		b = y;
	}

	public void run() {

		while (life) {
			try {
				sleep(100);
			} catch (InterruptedException e) {

			}
			DataInputStream dini = null;
			String tmp = null;
			try {
				dini = new DataInputStream(a.getInputStream());
				tmp = new String(dini.readUTF());
			} catch (Exception e) {
				try {
					a.close();
				}
				catch (Exception ee)
				{

				}
				life = false;
				break;
			}
			if (tmp.indexOf(',') == -1) {
				try {
					a.close();
				}
				catch (Exception e)
				{

				}
				for (int i = 0; i < b.SocketList.size(); i++) {
					if (b.SocketList.get(i) == a) {
						b.SocketList.remove(i);
						break;
					}
				}
				life = false;
			} else {
				String[] tokens = tmp.split(",");
				if (tokens.length != 2) {
					try {
						a.close();
					}
					catch (Exception e)
					{

					}
					for (int i = 0; i < b.SocketList.size(); i++) {
						if (b.SocketList.get(i) == a) {
							b.SocketList.remove(i);
							break;
						}
					}
					life = false;
				} else {
					for (int i = 0; i < b.SocketList.size(); i++) {
						try {
							DataOutputStream out = new DataOutputStream(b.SocketList.get(i).getOutputStream());
							out.writeUTF(tmp);
							System.out.println(tmp);
						} catch (Exception e) {
							try {
								b.SocketList.get(i).close();
							}
							catch (Exception ee)
							{

							}
							b.SocketList.remove(i);
						}
					}
				}
			}
		}
	}
}
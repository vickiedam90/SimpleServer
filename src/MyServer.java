import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

public class MyServer {

	// InetAddress infoip;
	private String message = "";
	private ServerSocket serverSocket = null;
	private Set<String> list = new HashSet<>();
	
	public MyServer() {
		/*
		 * try { infoip = InetAddress.getLocalHost(); } catch
		 * (UnknownHostException e) { e.printStackTrace(); }
		 */
		Thread socketServerThread = new Thread(new SocketServerThread());
		socketServerThread.start();
	}

	protected class SocketServerThread extends Thread {

		static final int SocketServerPORT = 8080;

		public void run() {
			ObjectInputStream in = null;
			Socket socket = null;
			serverSocket = null;
			InputStream is = null;

			while (true) {
				try {

					in = null;
					socket = null;
					serverSocket = null;
					is = null;

					serverSocket = new ServerSocket(SocketServerPORT);
					System.out.println("Server waiting..");

					socket = serverSocket.accept();
					System.out.println("Client detected");

					is = socket.getInputStream();
					
					in = new ObjectInputStream(is);
					
					list.addAll((Collection<? extends String>) in.readObject());
					Thread.sleep(1000);
					message += "From " + socket.getInetAddress() + ":"
								+ socket.getPort() + "\n";
					System.out.println(message);
					System.out.println(list);
					SocketServerReply socketServerReply = new SocketServerReply(
								socket);
					socketServerReply.run();
					
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					if (is != null) {
						try {
							is.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (serverSocket != null) {
						try {
							serverSocket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (socket != null) {
						try {
							socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				}
			}
		}

	}

	private class SocketServerReply {

		private Socket hostThreadSocket;
		String msgReply = "";

		SocketServerReply(Socket socket) {
			hostThreadSocket = socket;
		}

		public void run() {
			OutputStream out = null;
			msgReply = "Synchronization complete";
			try {

				out = hostThreadSocket.getOutputStream();
				PrintStream printStream = new PrintStream(out);
				//out.writeUTF(msgReply);
				printStream.print(msgReply);
				message += "replayed: " + msgReply + "\n";
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
				message += "Something wrong! " + e.toString() + "\n";
			} finally {
				msgReply = "";
				message = "";

				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (hostThreadSocket != null) {
					try {
						hostThreadSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		}

	}

	public static void main(String[] args) {
		new MyServer();
	}

}

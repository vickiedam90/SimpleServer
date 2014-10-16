import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class MyServer {

	//InetAddress infoip;
	private String message = "";
	private ServerSocket serverSocket = null;
	private List<Object> list = new ArrayList<>();

	public MyServer() {
	/*	try {
			infoip = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}*/
		Thread socketServerThread = new Thread(new SocketServerThread());
		socketServerThread.start();
	}

	protected class SocketServerThread extends Thread {
		ObjectInputStream in = null;
		static final int SocketServerPORT = 8080;
		
		public void run() {
			Socket socket = null;
			try {
				serverSocket = new ServerSocket(SocketServerPORT);
				System.out.println("Server waiting..");
				
				while (true) {
					socket = serverSocket.accept();
					System.out.println("Client detected");
					//in = new ObjectInputStream(socket.getInputStream());
					//list.add(in.readObject());
					message += "From " + socket.getInetAddress() + ":"
							+ socket.getPort() + "\n";
					System.out.println(message);
					SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
							socket);
					socketServerReplyThread.run();

				}
			} catch (IOException e) {
				e.printStackTrace();
		//	} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (serverSocket != null) {
					try {
						serverSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(socket != null){
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(in != null){
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	private class SocketServerReplyThread extends Thread {

		private Socket hostThreadSocket;

		SocketServerReplyThread(Socket socket) {
			hostThreadSocket = socket;
		}

		public void run() {
			DataOutputStream out = null;
			String msgReply = "Synchronization complete";
			try {
				out = new DataOutputStream(hostThreadSocket.getOutputStream());
				out.writeUTF(msgReply);
				message += "replayed: " + msgReply + "\n";

			} catch (IOException e) {
				e.printStackTrace();
				message += "Something wrong! " + e.toString() + "\n";
			} finally {
				if (hostThreadSocket != null) {
					try {
						hostThreadSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (out != null) {
					try {
						out.close();
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


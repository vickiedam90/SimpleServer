import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MyServer {

	//InetAddress infoip;
	String message = "";
	ServerSocket serverSocket = null;

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

		static final int SocketServerPORT = 8080;
		int count = 0;

		public void run() {
			Socket socket = null;
			try {
				serverSocket = new ServerSocket(SocketServerPORT);
				System.out.println("Server waiting..");

				while (true) {
					socket = serverSocket.accept();
					/*
					 * count++; message += "#" + count + " from " +
					 * socket.getInetAddress() + ":" + socket.getPort() + "\n";
					 */
					message += "From " + socket.getInetAddress() + ":"
							+ socket.getPort() + "\n";

					SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
							socket, count);
					socketServerReplyThread.run();

				}
			} catch (IOException e) {

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
			}
		}

	}

	private class SocketServerReplyThread extends Thread {

		private Socket hostThreadSocket;
		//int cnt;

		SocketServerReplyThread(Socket socket, int c) {
			hostThreadSocket = socket;
			//cnt = c;
		}

		public void run() {
			// OutputStream outputStream;
			DataOutputStream out = null;
			// String msgReply = "Hello from Android, you are #" + cnt;
			String msgReply = "Synchronization complete";
			try {
				out = new DataOutputStream(hostThreadSocket.getOutputStream());
				out.writeUTF(msgReply);
				/*
				 * outputStream = hostThreadSocket.getOutputStream();
				 * PrintStream printStream = new PrintStream(outputStream);
				 * printStream.print(msgReply); printStream.close();
				 */
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
/*
 * private String getIpAddress() { String ip = ""; try {
 * Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
 * .getNetworkInterfaces(); while (enumNetworkInterfaces.hasMoreElements()) {
 * NetworkInterface networkInterface = enumNetworkInterfaces .nextElement();
 * Enumeration<InetAddress> enumInetAddress = networkInterface
 * .getInetAddresses(); while (enumInetAddress.hasMoreElements()) { InetAddress
 * inetAddress = enumInetAddress.nextElement();
 * 
 * if (inetAddress.isSiteLocalAddress()) { ip += "SiteLocalAddress: " +
 * inetAddress.getHostAddress() + "\n"; }
 * 
 * }
 * 
 * }
 * 
 * } catch (SocketException e) { // TODO Auto-generated catch block
 * e.printStackTrace(); ip += "Something Wrong! " + e.toString() + "\n"; }
 * 
 * return ip; }
 */

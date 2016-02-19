import java.io.*;
import java.net.*;

public class Server implements Runnable {
	
	private final static int DEFAULT_PORT = 7652;
	private static int port;
	private static ServerSocket sock;
	private Socket socket;
	
	public Server(Socket socket){
		this.socket = socket;
	}

	@Override
	public void run() {
		try{
			BufferedReader in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), 
					true);
			while(true){
				String input = in.readLine();
				if(input.equalsIgnoreCase("Exit")){
					out.println("Client shutting down.");
					try{
						socket.close();
					} catch (IOException e) {
						System.out.println("Error closing socket.");
					}
					Thread.currentThread().interrupt();
					return;
				}
				System.out.println(input);
				out.println("I got your message.");
			}
		} catch (IOException e) {
			System.out.println("Read failed");
			System.exit(-1);
		}
	}
	
	protected void finalize(){
		try{
			sock.close();
		} catch (IOException e) {
			System.out.println("Error closing socket.");
		}
	}
	
	public static void main(String[] args){
		if(args.length == 2 && args[0].equals("-p")){
			try{
				port = Integer.parseInt((args[1]));
			} catch(NumberFormatException e){
				System.out.println("Port is not valid.");
				return;
			}
		} else if(args.length == 0){
			port = DEFAULT_PORT;
		} else {
			System.out.println("Usage: Server [-p port]");
			return;
		}
		try{
			sock = new ServerSocket(port);
			System.out.println("Server is ready for connections " +
					"on port " + port + ".");
			while(true){
				Socket socket = sock.accept();
				new Thread(new Server(socket)).start();
			}
			
		} catch (IOException e){
			System.out.println("Error creating socket.");
		}
	}
}

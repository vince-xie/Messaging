import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class Client {
	
	private final static int DEFAULT_PORT = 7652;
	private static Socket socket;
	
	protected void finalize(){
		try{
			socket.close();
		} catch (IOException e) {
			System.out.println("Error closing socket.");
		}
	}
	
	public static void main(String[] args){
		try{
			socket = new Socket("localhost", DEFAULT_PORT);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), 
					true);
			BufferedReader br = 
                    new BufferedReader(new InputStreamReader(System.in));
			while(true){
				System.out.print("Enter a message: ");
				String message = br.readLine();
				out.println(message);
				String input = in.readLine();
				System.out.println(input);
				if(message.equalsIgnoreCase("Exit")){
					return;
				}
			}
		} catch (IOException e){
			System.out.println("Error creating socket.");
		}
	}
}

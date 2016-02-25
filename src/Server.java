import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server implements Runnable {

	private final static int DEFAULT_PORT = 7652;
	private static int port;
	private static ServerSocket sock;
	private Socket socket;
	public Vector<Group> Groups;

	public Server(){
		Groups = new Vector<Group>();
	}

	public Server(Socket socket){
		this.socket = socket;
		Groups = new Vector<Group>();
	}

	public void setSocket(Socket sock){
		socket = sock;
	}

	private boolean isControlCharLegal(String string){
		for(char c:string.toCharArray()){
			if(!Character.isISOControl(c)){
				return false;
			}
		}
		return true;
	}
	@Override
	public void run() {

		try{
			BufferedReader in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), 
					true);
			String input = "";
			while(true){
				input = in.readLine();
				if(input.contains("post")){
					String groupName = input.substring(5);
					if(!isControlCharLegal(groupName)){
						out.println("Error: invalid group name");
						break;
					}
					else{
						out.println("ok");
						input = in.readLine();
						if(input.contains("id")){
							String userID = input.substring(3);
							out.println("ok");
							String header = in.readLine();
							String message = "";
							while(!(input = in.readLine()).equals(post.endOfMessageChar)){
								message = message + "\n"+input;
							}
							message = message.substring(1);
							addMessage(groupName, new Message(userID, message, header));

						}
						else{
							out.println("Error: invalid command");
						}
					}
					out.println("exit");
					break;
				}



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

	public synchronized void addMessage(String groupName, Message message){
		for(Group g: Groups){
			if(g.groupName.equalsIgnoreCase(groupName)){
				g.addMessage(message);
				return;
			}
		}
		Group g = new Group(groupName);
		g.addMessage(message);
		Groups.add(g);

	}


	public static void main(String[] args){
		args = new String[2];
		args[0] = "-p";
		args[1] = "7652";
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
			Server server = new Server();
			while(true){
				Socket socket = sock.accept();
				server.setSocket(socket);
				new Thread(server).start();
			}

		} catch (IOException e){
			System.out.println("Error creating socket.");
		}
	}
}

class Group{

	String groupName;
	Vector<Message> messages;


	public Group(String name){
		groupName = name;
		messages = new Vector<Message>();
	}

	public  synchronized void addMessage(Message m){
		messages.add(m);
	}

}

class Message{

	String posterName;
	String header;
	String message;


	public Message(String posterName, String message, String header){
		this.posterName = posterName;
		this.message = message;
		this.header = header;
	}

	public String toString(){
		return header+"\n\n"+message;
	}

}

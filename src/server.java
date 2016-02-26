import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;


// Group Members: Bharath Kannan, Vincent Xie, Augustus Chang
public class server implements Runnable {

	private final static int DEFAULT_PORT = 7652;
	private static int port;
	private static ServerSocket sock;
	private Socket socket;
	public Vector<Group> Groups;
	public static String endOfMessageChar = "./end";


	public server(){
		Groups = new Vector<Group>();
	}

	public server(Socket socket){
		this.socket = socket;
		Groups = new Vector<Group>();
	}
	
	//Sets the server's socket
	public void setSocket(Socket sock){
		socket = sock;
	}

	@Override
	public void run() {
		synchronized(this){
			PrintWriter out = null;
			try{
				BufferedReader in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), 
						true);
				String input = "";
				while(true){
					input = in.readLine();
					String[] cmd = input.split(" +");
						if(cmd[0].equalsIgnoreCase("post")&&cmd.length>=2){
						
						String groupName = cmd[1];
						if(cmd.length!=2){
							out.println("Error: invalid group name");
							break;
						}
						else{
							out.println("ok");
							input = in.readLine();
							String[] idarr = input.split(" +");
							if(idarr[0].equalsIgnoreCase("id")&&idarr.length>=2){
								String userID = idarr[1];
								out.println("ok");
								String message = "";
								while(!(input = in.readLine()).contains(endOfMessageChar)){
									message = message + "\n"+input;
								}
								if(message.length()>=2){
									message = message.substring(1);
								}
								else{
									message = "";
								}
								addMessage(groupName, new Message(userID, message, formatHeader(socket, userID)));

							}
							else{
								out.println("Error: invalid command");
							}
						}
						out.println("exit");
						break;
					}
					
					
					if(cmd[0].equalsIgnoreCase("get")&&cmd.length>=2){
						
						String groupName = cmd[1];
						Group group = getGroup(groupName);
						if(cmd.length!=2||group==null){
							out.println("Error: invalid group name");
							break;
						}
						else{
							out.println("ok");
							out.println(group.messages.size()+" messages");
							for(int i = 0;i<group.messages.size()-1;i++){
								String send = group.messages.get(i)+"";
								out.println(send);
								out.println();
							}
							out.println(group.messages.get(group.messages.size()-1));
							out.println(endOfMessageChar);
						}
						break;
					}
					
					
					else{
						out.println("Error: invalid command");
						break;
					}
				}
			} catch (Exception e) {
				System.out.println("Read failed");
				if(out!=null){
					out.println("exit");
				}
			}
		}
	}

	//Formats the header of a message.
	private static String formatHeader(Socket s, String id){
		SimpleDateFormat f = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());	
		String m = "From "+id+" "+s.getRemoteSocketAddress().toString()+" "+f.format(c.getTime());
		return m;
	}
	
	protected void finalize(){
		try{
			sock.close();
		} catch (IOException e) {
			System.out.println("Error closing socket.");
		}
	}
	
	//Adds a message to the specified group.
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

	//Returns the specified group.
	public synchronized Group getGroup(String groupName){
		for(Group g : Groups){
			if(g.groupName.equals(groupName)){
				return g;
			}
		}
		return null;
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
			System.out.println("Format Error: Server [-p port]");
			return;
		}
		try{
			sock = new ServerSocket(port);
			System.out.println("Server is ready for connections " +
					"on port " + port + ".");
			server server = new server();
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class post{
	private final static int DEFAULT_PORT = 7652;
	private static  Socket socket;
	public static String endOfMessageChar = "./end";
	
	private static int  contains(String[] arr, String s){
		for(int i = 0; i<arr.length;i++){
			String c = arr[i];
			if(c.equalsIgnoreCase(s)){
				return i;
			}
		}
		return -1;
	}
	
	private  static void talkToServer(Socket s, String groupName, String message){
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					s.getInputStream()));
			PrintWriter out = new PrintWriter(s.getOutputStream(), 
					true);
			
			out.println("post "+groupName);
			String status = in.readLine();
			if(status.toLowerCase().contains("error")){
				System.out.println(status);
				System.exit(1);
			}
			else if(status.equalsIgnoreCase("ok")){
				out.println("id "+System.getProperty("user.name"));
				status = in.readLine();
				if(status.contains("Error")){
					System.out.println(status);
					System.exit(1);
				}
				else if(status.equalsIgnoreCase("ok")){
					out.println(formatMessage(s, System.getProperty("user.name"), message));
					status = in.readLine();
					if(status.equals("exit")){
						System.exit(0);
					}
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private static String formatMessage(Socket s, String id, String message){
		SimpleDateFormat f = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());	
		String m = "From "+id+" "+s.getLocalSocketAddress().toString()+" "+f.format(c.getTime())+"\n";
		m = m+message;
		return m;
	}
	public static void main(String... args){
		// set default conditions
		String hostname = "localhost";
		int port = DEFAULT_PORT;
		String groupName = "";

		// arguments can't exceed 5, be under 1 and cannot be even
		if(args.length > 5 || args.length < 1 || args.length == 2 || args.length == 4){
			System.out.println("Format Error: post [-h hostname] [-p port] groupname");
			return;
		}

		// if only 1 argument
		// we default condition. First argument is the hostname
		if(args.length == 1){
			groupName = args[0];
		}

		// We know that the arg.length is 3 or 5. Check accordingly
		
		try{

			String firstCommand = args[0];
			if(args.length == 3){
			
				if(firstCommand.equals("-h")){
					hostname = args[1];
					groupName = args[2];
				}else if(firstCommand.equals("-p")){
					port = Integer.parseInt(args[1]);
					groupName = args[2];
				}else{
					System.out.println("Format Error: post [-h hostname] [-p port] groupname");
					return;
				}
			}

			if(args.length == 5){
				String secondCommand = args[2];

				if(firstCommand.equals("-h") && secondCommand.equals("-p")){
					hostname = args[1];
					port = Integer.parseInt(args[3]);
					groupName = args[4];
				}else{
					System.out.println("Format Error: post [-h hostname] [-p port] groupname");
					return;
				}
			}

		} catch (NumberFormatException e){
			System.out.println("Format Error: post [-h hostname] [-p port] groupname");
			System.out.println("Hint: Port number must be a #");
			return;
		} catch (Exception e){
			System.out.println("Format Error: post [-h hostname] [-p port] groupname");
			return;
		}

		try{
			socket = new Socket(hostname, port);
			BufferedReader br = 
                    new BufferedReader(new InputStreamReader(System.in));
			String message = "";
			while(true){
				String nextLine = br.readLine();
				if(nextLine.equals(endOfMessageChar)){
					talkToServer(socket, groupName, message+endOfMessageChar);
					return;
				}
				message = message + nextLine+"\n";
				if(message.equalsIgnoreCase("Exit")){
					return;
				}
			}
		} catch (IOException e){
			System.out.println("Error creating socket.");
		}

	}

}

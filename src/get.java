import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class get{
	private final static int DEFAULT_PORT = 7652;
	private static  Socket socket;
	public static String endOfMessageChar = "./end";
	

	
	private  static void talkToServer(Socket s, String groupName){
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					s.getInputStream()));
			PrintWriter out = new PrintWriter(s.getOutputStream(), 
					true);
			
			out.println("get "+groupName);
			String status = in.readLine();
			if(status.toLowerCase().contains("error")){
				System.out.println(status);
				System.exit(1);
			}
			else if(status.equalsIgnoreCase("ok")){
//				Scanner sc = new Scanner(new InputStreamReader(s.getInputStream()));
//				sc.useDelimiter(endOfMessageChar);
				while(!(status = in.readLine()).equals(endOfMessageChar)){
					System.out.println(status);
//					if(sc.hasNext()){
//						System.out.println();
//					}
				}


			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	

	public static void main(String... args){
		// set default conditions
		args = new String[1];
		args[0] = "test";
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
			talkToServer(socket, groupName);
		} catch (IOException e){
			System.out.println("Error creating socket.");
		}

	}

}

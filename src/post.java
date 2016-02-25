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
	public static String endOfMessageChar = "!";
	
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
			if(status.contains("Error")){
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
		String hostname = "localhost";
		int port = DEFAULT_PORT;
		String groupName = "";
		int h = contains(args,"-h");
		int p = contains(args, "-p");
		int groupIndex = 0;
		try{
			if(h!=-1){
				hostname = args[h+1];
				groupIndex+=2;
			}
			if(p!=-1){
				try{
					port = Integer.parseInt((args[p+1]));
					groupIndex+=2;
				} catch(NumberFormatException e){
					System.out.println("Port is not valid.");
					System.exit(1);;
				}
			}
			if(args.length!=groupIndex+1){
				throw new Exception("Invalid format");
			}
			
			groupName = args[groupIndex];
			
		}catch(Exception e){
			System.out.println("Error: Invalid Command.");
			System.exit(1);
		}

		try{
			socket = new Socket(hostname, port);
			BufferedReader br = 
                    new BufferedReader(new InputStreamReader(System.in));
			String message = "";
			while(true){
				String nextLine = br.readLine();
				if(nextLine.equals(endOfMessageChar)){
					talkToServer(socket, groupName, message+"!");
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

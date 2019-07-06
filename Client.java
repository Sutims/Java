/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author GusevAlexandr
 * @author DvorjanchikovEvginiy
 */

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client  {
	
	private String notif = " *** ";

	private ObjectInputStream sInput;		
	private ObjectOutputStream sOutput;		
	private Socket socket;					
	
	private String server, username;	
	private int port;					

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	
	Client(String server, int port, String username) {
		this.server = server;
		this.port = port;
		this.username = username;
	}
	
	public boolean start() {
		try {
			socket = new Socket(server, port);
		} 
		catch(Exception ec) {
			display("Error connectiong to server:" + ec);
			return false;
		}
		
		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);
	
		try
		{
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}

		new ListenFromServer().start();
		try
		{
			sOutput.writeObject(username);
		}
		catch (IOException eIO) {
			display("Exception doing login : " + eIO);
			disconnect();
			return false;
		}
		return true;
	}

	private void display(String msg) {

		System.out.println(msg);
		
	}
	
	void sendMessage(ChatMessage msg) {
		try {
			sOutput.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception writing to server: " + e);
		}
	}

	private void disconnect() {
		try { 
			if(sInput != null) sInput.close();
		}
		catch(Exception e) {}
		try {
			if(sOutput != null) sOutput.close();
		}
		catch(Exception e) {}
        try{
			if(socket != null) socket.close();
		}
		catch(Exception e) {}
			
	}

	public static void main(String[] args) {
		int portNumber = 1500;
		String serverAddress = "localhost";
		String userName = "Anonymous";
		Scanner scan = new Scanner(System.in);
		
		System.out.println("Enter the username: ");
		userName = scan.nextLine();

		switch(args.length) {
			case 3:
				serverAddress = args[2];
			case 2:
				try {
					portNumber = Integer.parseInt(args[1]);
				}
				catch(Exception e) {
					System.out.println("Invalid port number.");
					System.out.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
					return;
				}
			case 1: 
				userName = args[0];
			case 0:
				break;
			default:
				System.out.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
			return;
		}
		Client client = new Client(serverAddress, portNumber, userName);
		if(!client.start())
			return;
		
		System.out.println("\nHello.! Welcome to the chatroom.");
		
		while(true) {
			System.out.print("> ");
                         Thread myThready = new Thread(new Runnable()
                         {
                            @Override
                            public void run()
                            {
                                System.out.println("im here");
                                try {
                                    Thread.sleep(10000);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                client.sendMessage(new ChatMessage(ChatMessage.PUSH, ""));
                            }
                         }
                         );
                         myThready.start();
			String msg = scan.nextLine();
			if(msg.equalsIgnoreCase("LOGOUT")) {
				client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
				break;
			}
			else if(msg.equalsIgnoreCase("WHOISIN")) {
				client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));				
			}
			else if(msg.equalsIgnoreCase("PUSH"))
                        {
				client.sendMessage(new ChatMessage(ChatMessage.PUSH, ""));
			}
                        else if(msg.equalsIgnoreCase("PUSH_AND_OUT"))
                        {
                         client.sendMessage(new ChatMessage(ChatMessage.PUSH_AND_OUT, ""));   
                        }
                        else
                        {
                            client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msg));
                        }
		}
		scan.close();
		client.disconnect();	
	}

	class ListenFromServer extends Thread {

		public void run() {
			while(true) {
				try {
                                        Object tmp =  sInput.readObject();
                                        if(tmp.getClass() == String.class)
                                        {
					 String msg = tmp.toString();
                                         System.out.println(msg);
					 System.out.print("> ");
                                        }
                                        else
                                        {
                                            ArrayList<String> as = (ArrayList<String>)tmp;
                                            for(int i = 0; i < as.size(); ++i) {
                                                System.out.println(as.get(i));
					        System.out.print("> ");
					}
                                        }
				}
				catch(IOException e) {
					display(notif + "Server has closed the connection: " + e + notif);
					break;
				}
				catch(ClassNotFoundException e2) {
				}
			}
		}
	}
}


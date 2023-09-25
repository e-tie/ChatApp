package com.fdmgroup.multithreading;

import java.io.*;
import java.net.*;

public class ChatClient {

	// A main method that takes two arguments: host name and port number
	public static void main(String[] args) throws IOException {
		// Check the number of arguments
		if (args.length != 2) {
			System.out.println("Usage: java ChatClient <host> <port>");
			System.exit(1);
		}

		// Parse the host name and port number from the arguments
		String host = args[0];
		int port = Integer.parseInt(args[1]);

		// Create a socket to connect to the server
		Socket socket = new Socket(host, port);
		System.out.println("Connected to the chat server: " + socket);

		// Create input and output streams for the socket
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

		// Create a reader for the standard input
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

		// Create a new thread for receiving messages from the server
		Thread receiver = new Thread(new Runnable() {
			public void run() {
				try {
					// Loop forever to read messages from the server
					while (true) {
						// Read a message from the server
						String message = in.readLine();

						// If the message is null, break the loop
						if (message == null) {
							break;
						}

						// Print the message to the console
						System.out.println(message);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		// Start the receiver thread
		receiver.start();

		// Loop forever to send messages to the server
		while (true) {
			// Read a message from the standard input
			String message = stdIn.readLine();

			// If the message is "quit", break the loop
			if (message.equals("quit")) {
				break;
			}

			// Send the message to the server
			out.println(message);
		}

		// Close the input and output streams
		in.close();
		out.close();
		stdIn.close();

		// Close the socket
		socket.close();

		System.out.println("Disconnected from the chat server: " + socket);
	}
}
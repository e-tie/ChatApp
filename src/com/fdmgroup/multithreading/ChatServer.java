package com.fdmgroup.multithreading;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {

	// A list of sockets for connected clients
	private static List<Socket> clients = new ArrayList<>();

	// A main method to start the server
	public static void main(String[] args) throws IOException {
		try (// Create a server socket on port 8080
				ServerSocket serverSocket = new ServerSocket(8080)) {
			System.out.println("Chat server started on port 8080");

			// Loop forever to accept new connections
			while (true) {
				// Wait for a client to connect
				Socket clientSocket = serverSocket.accept();
				System.out.println("New client connected: " + clientSocket);

				// Add the client socket to the list
				clients.add(clientSocket);

				// Create a new thread for the client
				ServerThread serverThread = new ServerThread(clientSocket);

				// Start the thread
				serverThread.start();
			}
		}
	}

	// A nested class that extends Thread and handles communication with a client
	private static class ServerThread extends Thread {

		// A socket for the client
		private Socket clientSocket;

		// A constructor that takes a socket as a parameter
		public ServerThread(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}

		// A run method that overrides the one in Thread class
		public void run() {
			try {
				// Create input and output streams for the client socket
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

				// Send a welcome message to the client
				out.println("Welcome to the chat server!");

				// Loop forever to receive and broadcast messages from the client
				while (true) {
					// Read a message from the client
					String message = in.readLine();

					// If the message is null or "quit", break the loop
					if (message == null || message.equals("quit")) {
						break;
					}

					// Print the message to the console
					System.out.println("Message from " + clientSocket + ": " + message);

					// Broadcast the message to all other clients
					for (Socket socket : clients) {
						if (socket != clientSocket) {
							PrintWriter out2 = new PrintWriter(socket.getOutputStream(), true);
							out2.println(message);
						}
					}
				}

				// Close the input and output streams
				in.close();
				out.close();

				// Remove the client socket from the list
				clients.remove(clientSocket);

				// Close the client socket
				clientSocket.close();

				System.out.println("Client disconnected: " + clientSocket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
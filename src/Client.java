import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import service.RoomHandler;
import service.UserHandler;


public class Client {
    private Socket clientSocket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    private boolean running;

    public Client(Socket clientSocket) {
        try {
            this.clientSocket = clientSocket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        } catch (IOException e) {
            System.err.println("Error: Failed to start the server on port " + clientSocket.getPort());
            closeEverything(clientSocket, bufferedReader, bufferedWriter);
            e.printStackTrace();
        }
    }

    public void clientFunction() {
        try {
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);

            String messageToSend;

            UserHandler clientHandler = new UserHandler(this.bufferedWriter);
            RoomHandler roomHandler = new RoomHandler(this.bufferedWriter);

            while (clientSocket.isConnected()) {
                System.out.println("Select Operation: ");
                System.out.println("[ 1 ] Register User");
                System.out.println("[ 2 ] Create New Chat Room");
                System.out.println("[ 3 ] Enter New Chat Room");
                System.out.println("[ 4 ] List All Chat Rooms");
                System.out.println("[ 5 ] Send Messages");
                System.out.println("[ 6 ] Exit Chat Room");
                System.out.println("[ 7 ] Close Chat Room");
                System.out.println("[ 8 ] Ban User");
                System.out.println("[ 9 ] Exit");
                messageToSend = scanner.nextLine();
                switch (messageToSend) {
                    case "1":
                        clientHandler.registerUser();
                        this.username = clientHandler.getUsername();
                        break;

                    case "2":
                        roomHandler.createNew();
                        break;

                    case "3":
                        roomHandler.enterChatRoom();
                        break;

                    case "4":
                        roomHandler.listAllChatRooms();
                        break;

                    case "5":
                        roomHandler.sendMessage();
                        break;

                    case "6":
                        roomHandler.exitChatRoom();
                        break;

                    case "7":
                        roomHandler.closeChatRoom();
                        break;

                    case "8":
                        roomHandler.banUser();
                        break;
                        
                    case "9":
                        closeEverything(clientSocket, bufferedReader, bufferedWriter);
                        return;

                    default:
                        System.out.println("Invalid option. Please try again.");
                        break;
                }
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
            }
            scanner.close();

        } catch (IOException e) {
            closeEverything(clientSocket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromGroupChat;
                while (clientSocket.isConnected()) {
                    try {
                        messageFromGroupChat = bufferedReader.readLine();
                        System.out.println(messageFromGroupChat);
                    } catch (IOException e) {
                        closeEverything(clientSocket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket clientSocket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws IOException {
        Socket clientSocket = new Socket("localhost", 8080);
        Client client = new Client(clientSocket);
        client.listenForMessage();
        client.clientFunction();
    }
}

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
            // this.username = username;
            // System.out.println("Username:" + username);

        } catch (IOException e) {
            System.err.println("Error: Failed to start the server on port " + clientSocket.getPort());
            closeEverything(clientSocket, bufferedReader, bufferedWriter);
            e.printStackTrace();
        }
    }

    public void clientFunction() {
        try {
            // bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            Scanner scanner = new Scanner(System.in);
            System.out.println("okoko");
            System.out.println("Welcome " + this.username);
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

    public String getUsername() {
        return username;
    }
    
    public static void main(String[] args) throws IOException {
        Socket clientSocket = new Socket("localhost", 8080);
        Client client = new Client(clientSocket);
        client.listenForMessage();
        client.clientFunction();
    }
}

// public static void main(String[] args) throws IOException {
//     Scanner scanner = new Scanner(System.in);

//     Socket clientSocket = new Socket("localhost", 8080);
//     Client client;
//     while(true){
//         String username;
//         do {
//             System.out.print("Enter a valid username: ");
//             username = scanner.nextLine();

//             if (username.isEmpty() || username.contains(" ")) {
//                 System.out.println("Invalid username. Username cannot be empty or contain spaces.");
//             }
//         } while (username.isEmpty() || username.contains(" "));
//         client = new Client(clientSocket, username);
//         client.listenForMessage();
//         String messageFromServer = client.bufferedReader.readLine();
//         String[] message = messageFromServer.split(" ");
//         if(message[0].equals("ERRO")){
//             System.out.println(message[1]);
//         }
//         if(message[0].equals("REGISTRO_OK")){
//             System.out.println("Registered successfully");
//             break;
//         }
//     }
//     client.listenForMessage();
//     client.clientFunction();
// }


// public void start() {
// System.out.println("Client started. Port : " + this.clientSocket.getPort());
// ObjectOutputStream output;
// ObjectInputStream input;
// Scanner scanner = new Scanner(System.in);
// String messageTest = "";

// try {
// System.out.println("Conectado ao servidor");
// System.out.println("Digite: SAIR para encerrar conexão");

// output = new ObjectOutputStream(this.clientSocket.getOutputStream());
// output.flush();
// input = new ObjectInputStream(this.clientSocket.getInputStream());

// System.out.println("kk");
// System.out.println("Server>> messageTest" + messageTest);

// do {
// System.out.print("..:");
// messageTest = scanner.nextLine();
// output.writeObject(messageTest);
// output.flush();

// } while (!messageTest.equals("SAIR"));
// output.close();
// input.close();
// this.clientSocket.close();

// } catch (Exception e) {
// System.err.println("Error 1: " + e);
// }
// }
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client {
    private Socket clientSocket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    private boolean running;

    public Client(Socket clientSocket, String username) {
        try {
            this.clientSocket = clientSocket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.username = username;
            System.out.println("Username:" + username);
            
        } catch (IOException e) {
            System.err.println("Error: Failed to start the server on port " + clientSocket.getPort());
            closeEverything(clientSocket, bufferedReader, bufferedWriter);
            e.printStackTrace();
        }
    }

    public void clientFunction() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            Scanner scanner = new Scanner(System.in);
            System.out.println("okoko");
            System.out.println("Welcome " + this.username);
            while (clientSocket.isConnected()) {
                System.out.println("Select Operation: ");
                System.out.println("[ 1 ] Create New Chat Room");
                System.out.println("[ 2 ] Enter New Chat Room");
                System.out.println("[ 3 ] List All Chat Rooms");
                System.out.println("[ 4 ] Send Messages");
                System.out.println("[ 5 ] Exit Chat Room");
                System.out.println("[ 6 ] Close Chat Room");
                System.out.println("[ 7 ] Ban User");
                System.out.println("[ 8 ] Exit");
                String messageToSend = scanner.nextLine();
                switch (messageToSend) {
                    case "1":
                        createNew();
                        break;
                    case "2":
                        enterChatRoom();
                        break;
                    case "3":
                        listAllChatRooms();
                        break;
                    case "4":
                        sendMessage();
                        break;
                    case "5":
                        exitChatRoom();
                        break;
                    case "6":
                        closeChatRoom();
                        break;
                    case "7":
                        banUser();
                        break;
                    case "8":
                        closeEverything(clientSocket, bufferedReader, bufferedWriter);
                        return;

                    default:
                        System.out.println("Invalid option. Please try again.");
                        break;
                }

                bufferedWriter.write(username + ": " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }

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

    public void createNew() {
        // if (!(username.equals())) {
        //     System.out.println("User not Registered ");
        //     return;
        // }

        try {

            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter Chat Room Name: ");
            String chatRoomName = scanner.nextLine();
            System.out.println("Enter Chat Room Type");
            System.out.println("[ 1 ] Public");
            System.out.println("[ 2 ] Private");
            String roomType;
            switch (roomType = scanner.nextLine()) {
                case "1":
                    bufferedWriter.write("CRIAR_SALA " + "PUBLICA " + chatRoomName);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    break;

                case "2":
                    System.out.print("Enter Private Chat Password: ");
                    String password = scanner.nextLine();
                    bufferedWriter.write("CRIAR_SALA " + "PRIVADA " + chatRoomName + " " + password);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enterChatRoom() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Chat Room...");
        System.out.print("Enter Chat Room Name: ");
        String chatRoomName = scanner.nextLine();
        System.out.print("Enter Chat Room Password(optional), if public press enter: ");
        String password = scanner.nextLine();
        bufferedWriter.write("ENTRAR_SALA " + chatRoomName + " " + password);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    public void listAllChatRooms() throws IOException {
        bufferedWriter.write("LISTAR_SALAS ");
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    public void sendMessage() {

    }

    public void exitChatRoom() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Exit Chat Room...");
        System.out.print("Enter Chat Room Name: ");
        String chatRoomName = scanner.nextLine();
        bufferedWriter.write("SAIR_SALA " + chatRoomName);
        bufferedWriter.newLine();
        bufferedWriter.flush();
        
    }
    
    public void closeChatRoom() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Close Chat Room...(You need to be admin to close it)");
        System.out.print("Enter Chat Room Name: ");
        String chatRoomName = scanner.nextLine();
        bufferedWriter.write("FECHAR_SALA " + chatRoomName);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }
    
    public void banUser() {
        
    }
    
    public String getUsername() {
        return username;
    }
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

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String username;
        do {
            System.out.print("Enter a valid username: ");
            username = scanner.nextLine();

            if (username.isEmpty() || username.contains(" ")) {
                System.out.println("Invalid username. Username cannot be empty or contain spaces.");
            }
        } while (username.isEmpty() || username.contains(" "));
        Socket clientSocket = new Socket("localhost", 8080);
        Client client = new Client(clientSocket, username);
        client.listenForMessage();
        client.clientFunction();
    }

}
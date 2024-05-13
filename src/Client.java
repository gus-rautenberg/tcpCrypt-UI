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
                        registerUser();
                        break;
                    case "2":
                        createNew();
                        break;
                    case "3":
                        enterChatRoom();
                        break;
                    case "4":
                        listAllChatRooms();
                        break;
                    case "5":
                        sendMessage();
                        break;
                    case "6":
                        exitChatRoom();
                        break;
                    case "7":
                        closeChatRoom();
                        break;
                    case "8":
                        banUser();
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
    public void registerUser() throws IOException {
        System.out.println("Register User");
        String username = handleUsername();

        String messageToServer = "REGISTRO " + username;
        sendMessageToServer(messageToServer);
        this.username = username;
        // bufferedWriter.write("AUTENTICACAO " + username);
        // bufferedWriter.newLine();
        // bufferedWriter.flush();
    }
    public String handleUsername() {
        Scanner scanner = new Scanner(System.in);
        String username;
        do {
            System.out.print("Enter a valid username: ");
            username = scanner.nextLine();

            if (username.isEmpty() || username.contains(" ")) {
                System.out.println("Invalid username. Username cannot be empty or contain spaces.");
            }
        } while (username.isEmpty() || username.contains(" "));
        return username;
    }

    public void sendMessageToServer(String message) throws IOException {
        this.bufferedWriter.write(message);
        this.bufferedWriter.newLine();
        this.bufferedWriter.flush();
    }

    public void createNew() throws IOException {
        // if (!(username.equals())) {
        // System.out.println("User not Registered ");
        // return;
        // }
            Scanner scanner = new Scanner(System.in);
            String chatRoomName = handleChatRoomName();   
            System.out.println("Creating New Chat Room");
            
            System.out.println("Enter Chat Room Type");
            System.out.println("[ 1 ] Public");
            System.out.println("[ 2 ] Private");
            String roomType;
            String messageToServer;
            switch (roomType = scanner.nextLine()) {
                case "1":
                    messageToServer = "CRIAR_SALA " + "PUBLICA " + chatRoomName;
                    sendMessageToServer(messageToServer);
                    break;

                case "2":
                    String password = handlePassword();
                    password = handlePasswordSHA256(password);
                    messageToServer = "CRIAR_SALA " + "PRIVADA " + chatRoomName + " " + password;
                    sendMessageToServer(messageToServer);

                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }

    }

    public String handlePasswordSHA256(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] digest = md.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : digest) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            password = hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return password;
    }

    public String handlePassword() {
        Scanner scanner = new Scanner(System.in);
        String password;
        do {
            System.out.print("Enter Chat Room Password: ");
            password = scanner.nextLine();

            if (password.contains(" ") || password.isEmpty()) {
                System.out.println("Invalid Password. Password cannot contain spaces OR be empty.");
            }
        } while (password.contains(" ") || password.isEmpty());
        return password;
    }
    public String handleChatRoomName() {
        Scanner scanner = new Scanner(System.in);
        String chatRoomName;
        do {
            System.out.print("Enter Chat Room Name: ");
            chatRoomName = scanner.nextLine();

            if (chatRoomName.isEmpty() || chatRoomName.contains(" ")) {
                System.out.println("Invalid Chat Room Name. Chat Room Name cannot be empty or contain spaces.");
            }
        } while (chatRoomName.isEmpty() || chatRoomName.contains(" "));
        return chatRoomName;
    }

    public void enterChatRoom() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Entering Room...");
        String chatRoomName = handleChatRoomName();

        String password = handlePasswordEnterRoom();

        password = handlePasswordSHA256(password);

        String messageToServer = "ENTRAR_SALA " + chatRoomName + " " + password;
        sendMessageToServer(messageToServer);
    }

    public String handlePasswordEnterRoom() {
        Scanner scanner = new Scanner(System.in);
        String password;
        do {
            System.out.print("Enter Chat Room Password(optional), if public press enter: ");
            password = scanner.nextLine();

            if (password.contains(" ")) {
                System.out.println("Invalid Password. Password cannot contain spaces.");
            }
        } while (password.contains(" "));
        return password;
    }

    public void listAllChatRooms() throws IOException {
        String messageToServer = "LISTAR_SALAS ";
        sendMessageToServer(messageToServer);
    }

    public void sendMessage() throws IOException {
        System.out.println("Choose Chat Room:"); // checar se nao ta vazio o mesmo pra mensagem
        String chatRoomName = handleChatRoomName();

        String message = handleMessage();

        String messageToServer = "ENVIAR_MENSAGEM " + chatRoomName + " " + message;
        sendMessageToServer(messageToServer);
    }

    public String handleMessage() {
        Scanner scanner = new Scanner(System.in);
        String message;
        do {
            System.out.print("Enter Message: ");
            message = scanner.nextLine();
            if (message.isEmpty()) {
                System.out.println("Invalid Message. Message cannot be empty.");
            }
        } while (message.isEmpty());
        return message;
    }

    public void exitChatRoom() throws IOException {
        System.out.println("Exit Chat Room...");
        String chatRoomName = handleChatRoomName();
        
        String messageToServer = "SAIR_SALA " + chatRoomName;
        sendMessageToServer(messageToServer);
    }

    public void closeChatRoom() throws IOException {
        System.out.println("Close Chat Room...(You need to be admin to close it)");
        String chatRoomName = handleChatRoomName();
        String messageToServer = "FECHAR_SALA " + chatRoomName;
        sendMessageToServer(messageToServer);
    }

    public void banUser() throws IOException {
        System.out.println("Ban User...(You need to be admin to close it)");
        String chatRoomName = handleChatRoomName();
        String username = handleUsername();

        String messageToServer = "BANIR_USUARIO " + chatRoomName + " " + username;
        sendMessageToServer(messageToServer);
    }

    public String getUsername() {
        return username;
    }
    
    public static void main(String[] args) throws IOException {
        // Scanner scanner = new Scanner(System.in);
        // String username;
        // do {
        //     System.out.print("Enter a valid username: ");
        //     username = scanner.nextLine();

        //     if (username.isEmpty() || username.contains(" ")) {
        //         System.out.println("Invalid username. Username cannot be empty or contain spaces.");
        //     }
        // } while (username.isEmpty() || username.contains(" "));
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
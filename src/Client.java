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

import service.AuthenticationHandler;
import service.RoomHandler;
import service.UserHandler;

import utils.Utils;

public class Client {
    private Socket clientSocket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    public boolean crypto = false;
    public AuthenticationHandler authHandler;

    public Client(Socket clientSocket) {
        try {
            this.clientSocket = clientSocket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.authHandler = new AuthenticationHandler(this.bufferedWriter);

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

            UserHandler clientHandler = new UserHandler(this.bufferedWriter, this.bufferedReader);
            RoomHandler roomHandler = new RoomHandler(this.bufferedWriter);
            System.out.println();

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
                System.out.println();
                System.out.print("Select Option: ");
                messageToSend = scanner.nextLine();
                switch (messageToSend) {
                    case "1":
                        if (crypto) {
                            System.out.println("ERRO: User already registered");
                            break;
                        }
                        clientHandler.registerUser(authHandler);
                        this.username = clientHandler.getUsername();

                        break;

                    case "2":
                        if (!crypto) {
                            System.out.println("ERRO: User not registered");
                            break;
                        }
                        roomHandler.createNew(authHandler);
                        break;

                    case "3":
                        if (!crypto) {
                            System.out.println("ERRO: User not registered");
                            break;
                        }
                        roomHandler.enterChatRoom(authHandler);
                        break;

                    case "4":
                        if (!crypto) {
                            System.out.println("ERRO: User not registered");
                            break;
                        }
                        roomHandler.listAllChatRooms(authHandler);
                        break;

                    case "5":
                        if (!crypto) {
                            System.out.println("ERRO: User not registered");
                            break;
                        }
                        roomHandler.sendMessage(authHandler);
                        break;

                    case "6":
                        if (!crypto) {
                            System.out.println("ERRO: User not registered");
                            break;
                        }
                        roomHandler.exitChatRoom(authHandler);
                        break;

                    case "7":
                        if (!crypto) {
                            System.out.println("ERRO: User not registered");
                            break;
                        }

                        roomHandler.closeChatRoom(authHandler);
                        break;

                    case "8":
                        if (!crypto) {
                            System.out.println("ERRO: User not registered");
                            break;
                        }

                        roomHandler.banUser(authHandler);
                        break;

                    default:
                        System.out.println("Invalid option. Please try again.");
                        break;
                }
                System.out.println();
                System.out.println("Press (Enter) to continue...");

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
                Utils utils = new Utils(bufferedWriter);
                while (clientSocket.isConnected()) {
                    try {
                        // messageFromGroupChat = bufferedReader.readLine();
                        String messageFromServer;
                        messageFromServer = bufferedReader.readLine();
                        String[] words = messageFromServer.split(" ");
                        // for (int i = 0; i < words.length; i++) {

                        // System.out.println("words[" + i + "]: " + words[i]);
                        // }
                        if (crypto == false) {
                            // System.out.println("nao esntra aqui no crypto");
                            if (words[0].equals("REGISTRO_OK")) {
                                // System.out.println("ta na noia esse cara");
                                String messageToServer = "AUTENTICACAO " + username;
                                // System.out.println("username: " + username);
                                // System.out.println("messageToServer: " + messageToServer);

                                utils.sendMessageToServer(messageToServer);
                                // System.out.println("REGISTRO_OK");
                            } else if (words[0].equals("CHAVE_PUBLICA")) {
                                // System.out.println("Entrou aqui: ");
                                // pq nao entra aqui??
                                authHandler.setSimetricKey(words[1]);
                                authHandler.sendSimetricKeyToServer();
                                crypto = true;
                            }
                        } else {
                            try {
                                messageFromServer = authHandler.decryptMessageFromClient(messageFromServer);
                                // System.out.println("pulou ban");

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (!words[0].equals("CHAVE_PUBLICA")) {
                            System.out.println(messageFromServer);

                        }

                    } catch (IOException e) {
                        closeEverything(clientSocket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket clientSocket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            RoomHandler roomHandler = new RoomHandler(bufferedWriter);
            roomHandler.interrupt(this.authHandler, this.username);
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

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

        } catch (IOException e) {
            System.err.println("Error: Failed to start the server on port " + clientSocket.getPort());
            closeEverything(clientSocket, bufferedReader, bufferedWriter);
            e.printStackTrace();
        }
    }

    public void sendMessage() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            Scanner scanner = new Scanner(System.in);
            while (clientSocket.isConnected()) {
                String messageToSend = scanner.nextLine();
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
        System.out.println("Enter username: ");
        String username = scanner.nextLine();
        Socket clientSocket = new Socket("localhost", 8080);
        Client client = new Client(clientSocket, username);
        client.listenForMessage();
        client.sendMessage();
    }
}
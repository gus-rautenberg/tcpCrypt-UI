import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.io.IOException;

public class Client {
    private Socket clientSocket;
    private boolean running;

    public Client(String endereco, int porta) {
        try {
            this.clientSocket = new Socket(endereco, porta);

        } catch (IOException e) {
            System.err.println("Error: Failed to start the server on port " + porta);
            e.printStackTrace();
        }
    }

    public void start() {
        System.out.println("Client started. Port : " + this.clientSocket.getPort());
        ObjectOutputStream output;
        ObjectInputStream input;
        Scanner scanner = new Scanner(System.in);
        String messageTest = "";

        try {
            System.out.println("Conectado ao servidor");
            System.out.println("Digite: SAIR para encerrar conexão");

            output = new ObjectOutputStream(this.clientSocket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(this.clientSocket.getInputStream());

            System.out.println("kk");
            System.out.println("Server>> messageTest" + messageTest);

            do {
                System.out.print("..:");
                messageTest = scanner.nextLine();
                output.writeObject(messageTest);
                output.flush();

                // messageTest = (String) input.readObject();
                // System.out.println("Server>> " + messageTest);

            } while (!messageTest.equals("SAIR"));
            output.close();
            input.close();
            this.clientSocket.close();

        } catch (Exception e) {
            System.err.println("Error 1: " + e);
        }
    }

    public static void main(String[] args) {
        Client client = new Client("localhost", 8080);
        client.start();
    }
}

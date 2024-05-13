package service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Scanner;
import utils.Utils;

public class RoomHandler {
        private Utils utils;

        public RoomHandler(BufferedWriter bufferedWriter) {
            this.utils = new Utils(bufferedWriter);
        }
        
        public void createNew() throws IOException {
            Scanner scanner = new Scanner(System.in);
            String chatRoomName = utils.handleChatRoomName();   
            System.out.println("Creating New Chat Room");
            
            System.out.println("Enter Chat Room Type");
            System.out.println("[ 1 ] Public");
            System.out.println("[ 2 ] Private");
            String roomType;
            String messageToServer;
            switch (roomType = scanner.nextLine()) {
                case "1":
                    messageToServer = "CRIAR_SALA " + "PUBLICA " + chatRoomName;
                    utils.sendMessageToServer(messageToServer);
                    break;

                case "2":
                    String password = utils.handlePassword();
                    password = utils.handlePasswordSHA256(password);
                    messageToServer = "CRIAR_SALA " + "PRIVADA " + chatRoomName + " " + password;
                    utils.sendMessageToServer(messageToServer);

                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }

    }

    public void enterChatRoom() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Entering Room...");
        String chatRoomName = utils.handleChatRoomName();

        String password = utils.handlePasswordEnterRoom();

        password = utils.handlePasswordSHA256(password);

        String messageToServer = "ENTRAR_SALA " + chatRoomName + " " + password;
        utils.sendMessageToServer(messageToServer);
    }

    public void listAllChatRooms() throws IOException {
        String messageToServer = "LISTAR_SALAS ";
        utils.sendMessageToServer(messageToServer);
    }

    public void exitChatRoom() throws IOException {
        System.out.println("Exit Chat Room...");
        String chatRoomName = utils.handleChatRoomName();
        
        String messageToServer = "SAIR_SALA " + chatRoomName;
        utils.sendMessageToServer(messageToServer);
    }

    public void closeChatRoom() throws IOException {
        System.out.println("Close Chat Room...(You need to be admin to close it)");
        String chatRoomName = utils.handleChatRoomName();
        String messageToServer = "FECHAR_SALA " + chatRoomName;
        utils.sendMessageToServer(messageToServer);
    }

    public void banUser() throws IOException {
        System.out.println("Ban User...(You need to be admin to close it)");
        String chatRoomName = utils.handleChatRoomName();
        String username = utils.handleUsername();

        String messageToServer = "BANIR_USUARIO " + chatRoomName + " " + username;
        utils.sendMessageToServer(messageToServer);
    }

    public void sendMessage() throws IOException {
        System.out.println("Choose Chat Room:"); // checar se nao ta vazio o mesmo pra mensagem
        String chatRoomName = utils.handleChatRoomName();

        String message = utils.handleMessage();

        String messageToServer = "ENVIAR_MENSAGEM " + chatRoomName + " " + message;
        utils.sendMessageToServer(messageToServer);
    }
}

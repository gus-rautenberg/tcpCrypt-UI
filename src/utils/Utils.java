package utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Scanner;

public class Utils {
    private BufferedWriter bufferedWriter;

    public Utils(BufferedWriter bufferedWriter) {
        this.bufferedWriter = bufferedWriter;
    
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
        bufferedWriter.write(message);
        bufferedWriter.newLine();
        bufferedWriter.flush();
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
}

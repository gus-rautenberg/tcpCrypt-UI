package service;

import java.io.IOException;
import utils.Utils;
import java.io.BufferedWriter;


public class UserHandler {
        private Utils utils;
        private String username;

    public UserHandler(BufferedWriter bufferedWriter) {
        this.utils = new Utils(bufferedWriter);
    }

    public void registerUser() throws IOException {
        System.out.println("Register User");
        String username = utils.handleUsername();

        String messageToServer = "REGISTRO " + username;
        utils.sendMessageToServer(messageToServer);
        this.username = username;
        // bufferedWriter.write("AUTENTICACAO " + username);
        // bufferedWriter.newLine();
        // bufferedWriter.flush();
    }

    public String getUsername() {
        return username;
    }
}

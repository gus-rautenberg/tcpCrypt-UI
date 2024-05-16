package service;

import java.io.IOException;
import utils.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;


public class UserHandler {
        private Utils utils;
        private String username;
        private BufferedReader bufferedReader;

    public UserHandler(BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        this.utils = new Utils(bufferedWriter);
        this.bufferedReader = bufferedReader;
    }

    public void registerUser(AuthenticationHandler authHandler) throws IOException {
        System.out.println("Register User");
        String username = utils.handleUsername();

        String messageToServer = "REGISTRO " + username;
        utils.sendMessageToServer(messageToServer);
        this.username = username;

    }

    

    public String getUsername() {
        return username;
    }
}

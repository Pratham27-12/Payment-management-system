package org.example.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    public static void main(String[] args) {
        if(checkPassword("Pratham$1982", "$2a$10$KEZDmw/eH/dQonC/VFH1tuH6SVffU7hPenEuI3ha262C7qGDhV96G"))
            System.out.println("Password is correct");
        else
            System.out.println("Password is incorrect");
    }
}

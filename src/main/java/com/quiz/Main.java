
package com.quiz;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            String[] choices = {
                    "Student Login",
                    "Admin Login"
            };

            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Welcome to Java Quiz System",
                    "Main Menu",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    choices,
                    choices[0]
            );

            if (choice == 0) {

                StudentLogin login = new StudentLogin();
                login.setVisible(true);

            } else if (choice == 1) {

                AdminLogin admin = new AdminLogin();
                admin.setVisible(true);

            }
        });
    }
}

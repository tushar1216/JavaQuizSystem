
package com.quiz;

import javax.swing.*;
import java.awt.*;

public class AdminLogin extends JFrame {

    // Change this password to your preferred admin password.
    private static final String ADMIN_PASSWORD = "admin123";

    private JPasswordField passwordField;

    public AdminLogin() {

        setTitle("Administrator Login");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JLabel heading = new JLabel(
                "ADMINISTRATOR LOGIN",
                SwingConstants.CENTER
        );
        heading.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel passwordLabel = new JLabel("Enter Admin Password:");

        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");

        loginButton.addActionListener(e -> checkPassword());

        passwordField.addActionListener(e -> checkPassword());

        panel.add(heading);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);

        add(panel);
    }

    private void checkPassword() {

        String enteredPassword =
                new String(passwordField.getPassword());

        if (ADMIN_PASSWORD.equals(enteredPassword)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Admin login successful!"
            );

            dispose();

            AdminDashboard dashboard = new AdminDashboard();
            dashboard.setVisible(true);

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Incorrect password. Please try again.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );

            passwordField.setText("");
        }
    }
}

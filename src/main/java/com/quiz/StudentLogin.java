
package com.quiz;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class StudentLogin extends JFrame {

    private JTextField registrationField;
    private JButton startButton;

    public StudentLogin() {

        setTitle("Student Login - Quiz System");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(240, 245, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Heading
        JLabel heading = new JLabel(
                "JAVA QUIZ SYSTEM",
                SwingConstants.CENTER
        );

        heading.setFont(new Font("Arial", Font.BOLD, 24));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(heading, gbc);

        // Registration number label
        JLabel regLabel = new JLabel("Registration Number:");

        regLabel.setFont(new Font("Arial", Font.PLAIN, 15));

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(regLabel, gbc);

        // Registration number input
        registrationField = new JTextField(18);

        registrationField.setFont(
                new Font("Arial", Font.PLAIN, 16)
        );

        gbc.gridx = 1;
        panel.add(registrationField, gbc);

        // Start quiz button
        startButton = new JButton("Start Quiz");

        startButton.setFont(
                new Font("Arial", Font.BOLD, 16)
        );

        startButton.setBackground(new Color(40, 100, 200));
        startButton.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;

        panel.add(startButton, gbc);

        // Information label
        JLabel info = new JLabel(
                "Each registration number is allowed one attempt.",
                SwingConstants.CENTER
        );

        info.setFont(new Font("Arial", Font.ITALIC, 11));

        gbc.gridy = 3;
        panel.add(info, gbc);

        add(panel);

        // Button action
        startButton.addActionListener(e -> startQuiz());

    }


    // Validate registration number and reserve an attempt
    private void startQuiz() {

        String regNo = registrationField.getText().trim();

        // Check empty input
        if (regNo.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter your registration number."
            );

            return;
        }

        startButton.setEnabled(false);

        try {

            boolean attemptStarted =
                    DatabaseConnection.startAttempt(regNo);

            if (attemptStarted) {

                JOptionPane.showMessageDialog(
                        this,
                        "Registration successful!\n"
                                + "You can now proceed to the quiz."
                );

                // Quiz window will be connected in the next step.
                dispose();
                QuizApp quiz = new QuizApp(regNo);
                quiz.setVisible(true);

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "You have already used your quiz attempt."
                );

            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database error: " + ex.getMessage()
            );

            ex.printStackTrace();

        } finally {

            startButton.setEnabled(true);
        }
    }

}

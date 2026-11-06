
package com.quiz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class QuizApp extends JFrame {

    private final String regNo;

    private int currentQuestion = 0;
    private int score = 0;
    private int timeLeft = 15;

    private Timer timer;

    private JLabel questionNumberLabel;
    private JLabel timerLabel;
    private JLabel questionLabel;

    private JRadioButton[] options;
    private ButtonGroup optionGroup;

    private JButton nextButton;

    private boolean quizFinished = false;

    private final String[][] questions = {
            {
                    "Which method is used to start a thread in Java?",
                    "run()", "start()", "execute()", "init()"
            },
            {
                    "Which package contains AWT classes?",
                    "java.swing", "java.awt", "java.io", "java.sql"
            },
            {
                    "Which of the following is a lightweight Swing component?",
                    "Frame", "JFrame", "JButton", "Window"
            },
            {
                    "Which method is used to establish a JDBC connection?",
                    "createConnection()", "connect()",
                    "getConnection()", "openConnection()"
            },
            {
                    "Which keyword is used to implement an interface in Java?",
                    "extends", "inherits", "implements", "interface"
            },
            {
                    "Which method executes a SQL SELECT query using JDBC?",
                    "executeUpdate()", "executeQuery()",
                    "executeInsert()", "executeBatch()"
            },
            {
                    "Which keyword is used to inherit a class in Java?",
                    "implements", "extends", "super", "this"
            },
            {
                    "Which interface is commonly used to create a thread?",
                    "Threadable", "Runnable", "Executable", "Process"
            },
            {
                    "Which structure is used to handle exceptions in Java?",
                    "throw", "throws", "try-catch", "try-finally"
            },
            {
                    "Which collection allows duplicates and preserves insertion order?",
                    "HashSet", "TreeSet", "ArrayList", "HashMap"
            }
    };

    // Correct answer indexes: A=0, B=1, C=2, D=3
    private final int[] correctAnswers = {
            1, 1, 2, 2, 2, 1, 1, 1, 2, 2
    };

    public QuizApp(String registrationNumber) {

        this.regNo = registrationNumber;

        setTitle("Java Programming Quiz");
        setSize(750, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                finishQuiz();
            }
        });

        createInterface();
        showQuestion();
    }

    private void createInterface() {

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(Color.WHITE);

        questionNumberLabel = new JLabel();
        questionNumberLabel.setBounds(40, 20, 300, 30);
        questionNumberLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(questionNumberLabel);

        timerLabel = new JLabel();
        timerLabel.setBounds(570, 20, 140, 30);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        timerLabel.setForeground(Color.RED);
        mainPanel.add(timerLabel);

        questionLabel = new JLabel();
        questionLabel.setBounds(40, 80, 660, 60);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 17));
        mainPanel.add(questionLabel);

        options = new JRadioButton[4];

        optionGroup = new ButtonGroup();

        for (int i = 0; i < 4; i++) {

            options[i] = new JRadioButton();

            options[i].setBounds(60, 170 + (i * 45), 600, 35);
            options[i].setFont(new Font("Arial", Font.PLAIN, 16));
            options[i].setBackground(Color.WHITE);

            optionGroup.add(options[i]);
            mainPanel.add(options[i]);
        }

        nextButton = new JButton("Next Question");

        nextButton.setBounds(280, 380, 180, 40);
        nextButton.setFont(new Font("Arial", Font.BOLD, 15));

        nextButton.addActionListener(e -> submitAnswer());

        mainPanel.add(nextButton);

        add(mainPanel);
    }

    private void showQuestion() {

        if (currentQuestion >= questions.length) {
            finishQuiz();
            return;
        }

        questionNumberLabel.setText(
                "Question " + (currentQuestion + 1)
                        + " of " + questions.length
        );

        questionLabel.setText(
                "<html>" + questions[currentQuestion][0] + "</html>"
        );

        for (int i = 0; i < 4; i++) {
            options[i].setText(
                    (char) ('A' + i) + ". "
                            + questions[currentQuestion][i + 1]
            );
        }

        optionGroup.clearSelection();

        nextButton.setText(
                currentQuestion == questions.length - 1
                        ? "Submit Quiz" : "Next Question"
        );

        startTimer();
    }

    private void startTimer() {

        if (timer != null) {
            timer.stop();
        }

        timeLeft = 15;
        updateTimerLabel();

        timer = new Timer(1000, e -> {

            timeLeft--;
            updateTimerLabel();

            if (timeLeft <= 0) {
                timer.stop();
                moveToNextQuestion();
            }
        });

        timer.start();
    }

    private void updateTimerLabel() {
        timerLabel.setText("Time: " + timeLeft + " seconds");
    }

    private void submitAnswer() {

        if (timer != null) {
            timer.stop();
        }

        int selectedAnswer = -1;

        for (int i = 0; i < 4; i++) {
            if (options[i].isSelected()) {
                selectedAnswer = i;
                break;
            }
        }

        if (selectedAnswer == correctAnswers[currentQuestion]) {
            score++;
        }

        moveToNextQuestion();
    }

    private void moveToNextQuestion() {

        if (quizFinished) {
            return;
        }

        currentQuestion++;

        if (currentQuestion < questions.length) {
            showQuestion();
        } else {
            finishQuiz();
        }
    }

    private void finishQuiz() {

        if (quizFinished) {
            return;
        }

        quizFinished = true;

        if (timer != null) {
            timer.stop();
        }

        nextButton.setEnabled(false);

        try {

            boolean saved = DatabaseConnection.saveResult(
                    regNo, score, questions.length
            );

            if (saved) {
                JOptionPane.showMessageDialog(
                        this,
                        "Your quiz has been submitted successfully.",
                        "Quiz Completed",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Unable to save your result. Please contact the administrator.",
                        "Submission Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database error while saving your result.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();
        }

        dispose();
    }
}

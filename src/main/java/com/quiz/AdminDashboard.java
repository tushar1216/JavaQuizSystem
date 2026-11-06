
package com.quiz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDashboard extends JFrame {

    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public AdminDashboard() {

        setTitle("Quiz System - Admin Dashboard");
        setSize(850, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        createInterface();
        loadResults("");
    }

    private void createInterface() {

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JLabel heading = new JLabel(
                "ADMIN DASHBOARD - STUDENT RESULTS",
                SwingConstants.CENTER
        );

        heading.setFont(new Font("Arial", Font.BOLD, 22));

        mainPanel.add(heading, BorderLayout.NORTH);

        // Table columns
        String[] columns = {
                "ID",
                "Registration Number",
                "Score",
                "Total Questions",
                "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        resultsTable = new JTable(tableModel);
        resultsTable.setRowHeight(28);
        resultsTable.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(resultsTable);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        searchField = new JTextField(18);

        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");
        JButton resetButton = new JButton("Reset Selected Attempt");
        JButton exportButton = new JButton("Export to Excel");

        searchButton.addActionListener(e ->
                loadResults(searchField.getText().trim())
        );

        searchField.addActionListener(e ->
                loadResults(searchField.getText().trim())
        );

        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadResults("");
        });

        resetButton.addActionListener(e -> resetSelectedAttempt());
        exportButton.addActionListener(e -> exportResults());

        searchPanel.add(new JLabel("Registration Number:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);
        searchPanel.add(resetButton);
        searchPanel.add(exportButton);

        mainPanel.add(searchPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // Load student results from MySQL
    private void loadResults(String search) {

        tableModel.setRowCount(0);

        String sql = """
                SELECT id, reg_no, score, total_questions, status
                FROM quiz_attempts
                WHERE reg_no LIKE ?
                ORDER BY id DESC
                """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + search + "%");

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Object[] row = {
                            rs.getInt("id"),
                            rs.getString("reg_no"),
                            rs.getObject("score"),
                            rs.getObject("total_questions"),
                            rs.getString("status")
                    };

                    tableModel.addRow(row);
                }
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load results: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();
        }
    }

    // Reset a student's attempt
    private void resetSelectedAttempt() {

        int selectedRow = resultsTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a student first."
            );

            return;
        }

        String regNo = tableModel.getValueAt(
                selectedRow, 1
        ).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to reset the attempt for "
                        + regNo + "?\n"
                        + "This will delete their existing result "
                        + "and allow them to take the quiz again.",
                "Confirm Reset",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM quiz_attempts WHERE reg_no = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, regNo);

            int deleted = ps.executeUpdate();

            if (deleted > 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Attempt reset successfully for " + regNo
                );

                loadResults(searchField.getText().trim());

            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to reset attempt: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();
        }
    }

    // Export currently displayed student results to a CSV file
    private void exportResults() {

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "There are no results to export."
            );
            return;
        }

        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setDialogTitle("Save Student Results");

        fileChooser.setSelectedFile(
                new File("Quiz_Results.csv")
        );

        int choice = fileChooser.showSaveDialog(this);

        if (choice != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = fileChooser.getSelectedFile();

        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getAbsolutePath() + ".csv");
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                file.toPath(),
                StandardCharsets.UTF_8
        )) {

            // Write column headings
            writer.write(
                    "ID,Registration Number,Score,Total Questions,Status"
            );
            writer.newLine();

            // Write student records
            for (int row = 0; row < tableModel.getRowCount(); row++) {

                for (int column = 0;
                     column < tableModel.getColumnCount();
                     column++) {

                    Object value = tableModel.getValueAt(row, column);

                    String text = value == null
                            ? ""
                            : value.toString();

                    // Escape quotation marks for CSV
                    text = text.replace("\"", "\"\"");

                    writer.write("\"" + text + "\"");

                    if (column < tableModel.getColumnCount() - 1) {
                        writer.write(",");
                    }
                }

                writer.newLine();
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Results exported successfully!\n"
                            + file.getAbsolutePath()
            );

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error exporting results: " + e.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();
        }
    }

}

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class CollaborativeClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 5000;
    private boolean isSendingUpdate = false;  // Flag to prevent self-updating
    private JTextArea textArea;
    private PrintWriter out;
    private JFrame frame;
    private JLabel notificationLabel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new CollaborativeClient().start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void start() throws IOException {
        Socket socket = new Socket(SERVER_ADDRESS, PORT);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        frame = new JFrame("Collaborative Document Editor");
        textArea = new JTextArea(20, 50);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        // Document listener to send updates to the server
        textArea.getDocument().addDocumentListener(new SimpleDocumentListener() {
            @Override
            public void update() {
                if (!isSendingUpdate) {
                    String text = textArea.getText();
                    // (new) not sure if this works
                    out.println(text.replace("\n", "__NEWLINE__"));  // Replace newlines with a special marker
                    // out.println(text);  // Send the entire content including newlines (old)
                }
            }
        });

        // Create a toolbar with New, Open, Save, and Save As buttons
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton newButton = new JButton("New");
        // JButton openButton = new JButton("Open");
        JButton saveButton = new JButton("Save");
        JButton saveAsButton = new JButton("Save As");

        // Add action listeners to toolbar buttons
        newButton.addActionListener(e -> createNewDocument());
        // openButton.addActionListener(e -> openDocument());
        saveButton.addActionListener(e -> saveDocument());
        saveAsButton.addActionListener(e -> saveDocumentAs());

        toolBar.add(newButton);
        // toolBar.add(openButton);
        toolBar.add(saveButton);
        toolBar.add(saveAsButton);

        // Notification Label
        notificationLabel = new JLabel(" ");
        notificationLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Start a new thread to listen for updates from the server
        new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("Server Response: " + line);  // Debug: Log server response
                    String finalLine = line; // Make a final copy for lambda
                    SwingUtilities.invokeLater(() -> {
                        System.out.println("Updating text area with content: " + finalLine);
                        updateDocument(finalLine, "Document updated by another client");
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();  // Close the socket to prevent resource leaks
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // Frame setup
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(toolBar, BorderLayout.NORTH);  // Add toolbar to the top
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        panel.add(notificationLabel, BorderLayout.SOUTH);

        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // Create a new document
    private void createNewDocument() {
        textArea.setText("");  // Clear the text area
        JOptionPane.showMessageDialog(frame, "New document created.");
    }


private File currentFile = null;  // Track the currently saved file

    // Save the current document to the existing file or prompt "Save As" if no file is set
    private void saveDocument() {
        if (currentFile == null) {
            saveDocumentAs();  // If no file is selected, fallback to "Save As"
        } else {
            writeToFile(currentFile);  // Save directly to the current file
        }
    }

    // Save the current document as a new file
    private void saveDocumentAs() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(frame);

        if (option == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();

            // Ensure the file has a .txt extension
            if (!currentFile.getName().endsWith(".txt")) {
                currentFile = new File(currentFile.getAbsolutePath() + ".txt");
            }

            writeToFile(currentFile);  // Write to the selected file
        }
    }

    // Write the content to a specified file
    private void writeToFile(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(textArea.getText());
            JOptionPane.showMessageDialog(frame, "Document saved successfully.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error saving document: " + e.getMessage());
        }
    }


    // Update the document content and notification
    private void updateDocument(String content, String notification) {
        isSendingUpdate = true;
        textArea.setText(content);  // Updates the text area
        notificationLabel.setText(notification);  // Displays a notification
        new Timer(3000, e -> notificationLabel.setText(" ")).start(); // Clears notification after 3 seconds
        isSendingUpdate = false;
    }
}

// Simple DocumentListener to detect text changes
abstract class SimpleDocumentListener implements javax.swing.event.DocumentListener {
    public abstract void update();

    @Override
    public void insertUpdate(javax.swing.event.DocumentEvent e) {
        update();
    }

    @Override
    public void removeUpdate(javax.swing.event.DocumentEvent e) {
        update();
    }

    @Override
    public void changedUpdate(javax.swing.event.DocumentEvent e) {
        update();
    }
}

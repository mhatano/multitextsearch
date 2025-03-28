package jp.hatano.textsearch.gui;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class FileListPanel extends JList<File> {
    private DefaultListModel<File> fileListModel;
    private File currentDirectory;
    private MainFrame mainFrame;

    public FileListPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        fileListModel = new DefaultListModel<>();
        setModel(fileListModel);

        addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = getSelectedIndex();
                if (selectedIndex != -1) {
                    File selectedFile = fileListModel.getElementAt(selectedIndex);
                    loadFile(selectedFile, -1); // Load file without highlighting any line
                }
            }
        });
    }

    public void loadDirectory(File directory) {
        fileListModel.clear();
        currentDirectory = directory;
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    fileListModel.addElement(file);
                }
            }
        }
    }

    public File getCurrentDirectory() {
        return currentDirectory;
    }

    public void loadFile(File file, int highlightLineNumber) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            JTextArea textArea = mainFrame.getTextArea();
            textArea.setText("");
            String line;
            int lineCount = 0;
            while ((line = reader.readLine()) != null) {
                textArea.append(String.format("%5d: ", lineCount + 1) + line + "\n");
                lineCount++;
            }
            // Check if highlightLineNumber is valid
            if (highlightLineNumber >= 0 && highlightLineNumber < lineCount) {
                highlightLine(highlightLineNumber);
            } else if (highlightLineNumber != -1) {
                System.out.println("The target line " + (highlightLineNumber + 1) + " is out of bounds in the file.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Error loading file: " + ex.getMessage());
        }
    }

    private void highlightLine(int lineNumber) {
        try {
            JTextArea textArea = mainFrame.getTextArea();
            if (textArea != null) {
                int lineCount = textArea.getLineCount();
                if (lineNumber >= 0 && lineNumber < lineCount) {
                    int start = textArea.getLineStartOffset(lineNumber);
                    int end = textArea.getLineEndOffset(lineNumber);
                    textArea.setCaretPosition(start);
                    textArea.moveCaretPosition(end);
                    textArea.getCaret().setSelectionVisible(true);
                } else {
                    System.out.println("Error highlighting line. Line number: " + (lineNumber + 1));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
package jp.hatano.textsearch.gui;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

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

    public void loadDirectory(File directory, boolean includeSubdirectories) {
        fileListModel.clear();
        currentDirectory = directory;
        addDirectory(directory,includeSubdirectories);
    }

    private void addDirectory(File directory, boolean includeSubdirectories) {
        File[] files = directory.listFiles();
        if ( files != null ) {
            for ( File file : files ) {
                if ( file.isDirectory() && includeSubdirectories ) {
                    addDirectory(file,true);
                } else if ( file.isFile() ) {
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
                JOptionPane.showMessageDialog(mainFrame, 
                    "The target line " + (highlightLineNumber + 1) + " is out of bounds in the file.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String stackTrace = sw.toString();

            JOptionPane.showMessageDialog(mainFrame, 
                "Error loading file: " + ex.getMessage() + "\n\nStack Trace:\n" + stackTrace, 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
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
                    JOptionPane.showMessageDialog(mainFrame, 
                        "Error highlighting line. Line number: " + (lineNumber + 1), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String stackTrace = sw.toString();

            JOptionPane.showMessageDialog(mainFrame, 
                "Error highlighting line: " + ex.getMessage() + "\n\nStack Trace:\n" + stackTrace, 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public List<File> getFileList() {
        List<File> fileList = new ArrayList<File>();
        for ( Object object : fileListModel.toArray() ) {
            File file = (File)object;
            fileList.add(file);
        }
        return fileList;
    }
}
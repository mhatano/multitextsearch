package jp.hatano.textsearch.gui;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public class ResultListPanel extends JList<String> {
    private DefaultListModel<String> listModel;
    private MainFrame mainFrame;

    public ResultListPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        listModel = new DefaultListModel<>();
        setModel(listModel);

        addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = getSelectedIndex();
                if (selectedIndex != -1) {
                    String selectedValue = listModel.getElementAt(selectedIndex);
                    int lineIndex = selectedValue.lastIndexOf(": Line ");
                    if (lineIndex != -1) {
                        try {
                            String filePath = selectedValue.substring(0, lineIndex);
                            String lineNumberPart = selectedValue.substring(lineIndex + 7).split(":")[0].trim();
                            int lineNumber = Integer.parseInt(lineNumberPart) - 1;
                            File file = new File(filePath);
                            this.mainFrame.getFileListPanel().loadFile(file, lineNumber);
                            highlightFileInList(file);
                            highlightLineInTextArea(lineNumber);
                        } catch (NumberFormatException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void highlightFileInList(File file) {
        FileListPanel fileListPanel = mainFrame.getFileListPanel();
        DefaultListModel<File> fileListModel = (DefaultListModel<File>) fileListPanel.getModel();
        for (int i = 0; i < fileListModel.size(); i++) {
            if (fileListModel.getElementAt(i).equals(file)) {
                fileListPanel.setSelectedIndex(i);
                fileListPanel.ensureIndexIsVisible(i);
                break;
            }
        }
    }

    private void highlightLineInTextArea(int lineNumber) {
        JTextArea textArea = mainFrame.getTextArea();
        try {
            int start = textArea.getLineStartOffset(lineNumber);
            int end = textArea.getLineEndOffset(lineNumber);
            textArea.setCaretPosition(start);
            textArea.moveCaretPosition(end);
            textArea.getCaret().setSelectionVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void clearResults() {
        listModel.clear();
    }

    public void searchFiles(FileListPanel fileListPanel, String searchText) {
        List<File> fileList = fileListPanel.getFileList();
        File[] files = fileList.toArray(new File[0]);
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    searchFile(file, searchText);
                }
            }
        }
    }

    private void searchFile(File file, String searchText) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.contains(searchText)) {
                    listModel.addElement(file.getAbsolutePath() + ": Line " + lineNumber + ": " + line);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public File getCurrentDirectory() {
        return mainFrame.getFileListPanel().getCurrentDirectory();
    }
}

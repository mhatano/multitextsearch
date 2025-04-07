package jp.hatano.textsearch.gui;

import jp.hatano.textsearch.util.DialogUtils;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Locale;

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
                            DialogUtils.showErrorDialog(mainFrame,"Error parsing line number", ex);
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
            textArea.setEditable(true);
            textArea.setCaretPosition(start);
            textArea.moveCaretPosition(end);
            textArea.getCaret().setSelectionVisible(true);
            textArea.setEditable(false);
        } catch (Exception ex) {
            DialogUtils.showErrorDialog(mainFrame, "Error highlighting line in text area", ex);
        }
    }

    public void clearResults() {
        listModel.clear();
    }

    public void searchFiles(FileListPanel fileListPanel, String searchText, boolean ignoreCase) {
        List<File> fileList = fileListPanel.getFileList();
        File[] files = fileList.toArray(new File[0]);
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    searchFile(file, searchText, ignoreCase);
                }
            }
        }
    }

    private void searchFile(File file, String searchText, boolean ignoreCase) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.contains(searchText) || (ignoreCase && line.toUpperCase(Locale.ROOT).contains(searchText.toUpperCase(Locale.ROOT)))) {
                    listModel.addElement(file.getAbsolutePath() + ": Line " + lineNumber + ": " + line);
                }
            }
        } catch (Exception ex) {
            DialogUtils.showErrorDialog(mainFrame,"Error searching file: " + file.getAbsolutePath(), ex);
        }
    }

    public File getCurrentDirectory() {
        return mainFrame.getFileListPanel().getCurrentDirectory();
    }
}

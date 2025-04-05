package jp.hatano.textsearch.gui;

import javax.swing.*;
import java.util.List;
import java.io.File;

public class SearchTermListPanel extends JList<String> {
    private DefaultListModel<String> listModel;
    private MainFrame mainFrame;

    public SearchTermListPanel(MainFrame mainFrame0) {
        mainFrame = mainFrame0;
        listModel = new DefaultListModel<>();
        setModel(listModel);

        addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = getSelectedIndex();
                if (selectedIndex != -1) {
                    String selectedTerm = listModel.getElementAt(selectedIndex);
                    mainFrame.getResultListPanel().clearResults();
                    File currentDirectory = mainFrame.getResultListPanel().getCurrentDirectory();
                    if (currentDirectory != null) {
                        mainFrame.getResultListPanel().searchFiles(mainFrame.getFileListPanel(), selectedTerm);
                    }
                }
            }
        });
    }

    public void setSearchTerms(List<String> searchTerms) {
        listModel.clear();
        for (String term : searchTerms) {
            listModel.addElement(term);
        }
    }
}

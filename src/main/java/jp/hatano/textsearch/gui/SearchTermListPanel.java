package jp.hatano.textsearch.gui;

import javax.swing.*;
import java.util.List;
import java.io.File;

public class SearchTermListPanel extends JPanel {
    JList<String> searchTermList;
    JCheckBox ignoreCaseCheckBox;
    private DefaultListModel<String> listModel;
    private MainFrame mainFrame;
    private boolean ignoreCase = false;

    public SearchTermListPanel(MainFrame mainFrame0) {
        mainFrame = mainFrame0;
        searchTermList = getSearchTermList(mainFrame);
        ignoreCase = mainFrame.getIgnoreCase();
        ignoreCaseCheckBox = new JCheckBox("Ignore Case", ignoreCase);
        ignoreCaseCheckBox.addActionListener(e -> {
            ignoreCase = ignoreCaseCheckBox.isSelected();
            mainFrame.setIgnoreCase(ignoreCase);
            mainFrame.getResultListPanel().clearResults();
            File currentDirectory = mainFrame.getResultListPanel().getCurrentDirectory();
            if (currentDirectory != null) {
                mainFrame.getResultListPanel().searchFiles(mainFrame.getFileListPanel(), searchTermList.getSelectedValue(), ignoreCase);
            }
        });
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(new JScrollPane(searchTermList));
        add(ignoreCaseCheckBox);
    }

    public JList<String> getSearchTermList(MainFrame mainFrame) {
        JList<String> list = new JList<String>();
        listModel = new DefaultListModel<>();
        list.setModel(listModel);

        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = list.getSelectedIndex();
                if (selectedIndex != -1) {
                    String selectedTerm = listModel.getElementAt(selectedIndex);
                    mainFrame.getResultListPanel().clearResults();
                    File currentDirectory = mainFrame.getResultListPanel().getCurrentDirectory();
                    if (currentDirectory != null) {
                        mainFrame.getResultListPanel().searchFiles(mainFrame.getFileListPanel(), selectedTerm,ignoreCase);
                    }
                }
            }
        });
        return list;
    }

    public void setSearchTerms(List<String> searchTerms) {
        listModel.clear();
        for (String term : searchTerms) {
            listModel.addElement(term);
        }
    }
}

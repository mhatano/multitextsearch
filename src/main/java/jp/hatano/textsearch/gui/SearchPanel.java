package jp.hatano.textsearch.gui;

import jp.hatano.textsearch.util.DialogUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class SearchPanel extends JPanel {
    private MainFrame mainFrame;

    public SearchPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public void loadSearchTerms(File file) {
        List<String> searchTerms = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                searchTerms.add(line);
            }
        } catch (Exception ex) {
            DialogUtils.showErrorDialog(mainFrame, "Error loading search terms from file: " + file.getAbsolutePath(), ex);
        }
        mainFrame.getSearchTermListPanel().setSearchTerms(searchTerms);
    }
}
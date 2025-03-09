package jp.hatano.textsearch.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class SearchPanel extends JPanel {
    private JButton loadButton;
    private JButton loadSearchTermsButton;
    private MainFrame mainFrame;

    public SearchPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        loadSearchTermsButton = new JButton("Load Search Terms");
        loadButton = new JButton("Load Directory");

        add(loadSearchTermsButton);
        add(loadButton);

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(mainFrame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File currentDirectory = fileChooser.getSelectedFile();
                    mainFrame.getFileListPanel().loadDirectory(currentDirectory);
                }
            }
        });

        loadSearchTermsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(mainFrame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File searchTermsFile = fileChooser.getSelectedFile();
                    loadSearchTerms(searchTermsFile);
                }
            }
        });
    }

    private void loadSearchTerms(File file) {
        List<String> searchTerms = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                searchTerms.add(line);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Error loading search terms: " + ex.getMessage());
        }
        mainFrame.getSearchTermListPanel().setSearchTerms(searchTerms);
    }
}
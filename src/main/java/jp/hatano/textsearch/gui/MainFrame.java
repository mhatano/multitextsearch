package jp.hatano.textsearch.gui;

import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

public class MainFrame extends JFrame {
    private SearchPanel searchPanel;
    private FileListPanel fileListPanel;
    private ResultListPanel resultListPanel;
    private SearchTermListPanel searchTermListPanel;
    private JTextArea textArea;
    private JSplitPane verticalSplitPane;
    private JSplitPane horizontalSplitPane;
    private JSplitPane leftSplitPane;
    private JSplitPane mainSplitPane;
    private Preferences prefs;

    public MainFrame() {
        setTitle("Text Search Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        prefs = Preferences.userNodeForPackage(MainFrame.class);

        searchPanel = new SearchPanel(this);
        fileListPanel = new FileListPanel(this);
        resultListPanel = new ResultListPanel(this);
        searchTermListPanel = new SearchTermListPanel(this);
        textArea = new JTextArea();

        verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(fileListPanel), new JScrollPane(textArea));
        horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(resultListPanel), verticalSplitPane);
        leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(searchTermListPanel), new JScrollPane(resultListPanel));
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplitPane, verticalSplitPane);

        add(searchPanel, BorderLayout.NORTH);
        add(mainSplitPane, BorderLayout.CENTER);

        loadPreferences();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                savePreferences();
            }
        });
    }

    private void loadPreferences() {
        int width = prefs.getInt("windowWidth", 1000);
        int height = prefs.getInt("windowHeight", 600);
        setSize(width, height);

        int mainDividerLocation = prefs.getInt("mainDividerLocation", 200);
        mainSplitPane.setDividerLocation(mainDividerLocation);

        int leftDividerLocation = prefs.getInt("leftDividerLocation", 200);
        leftSplitPane.setDividerLocation(leftDividerLocation);

        int verticalDividerLocation = prefs.getInt("verticalDividerLocation", 200);
        verticalSplitPane.setDividerLocation(verticalDividerLocation);

        int horizontalDividerLocation = prefs.getInt("horizontalDividerLocation", 200);
        horizontalSplitPane.setDividerLocation(horizontalDividerLocation);
    }

    private void savePreferences() {
        prefs.putInt("windowWidth", getWidth());
        prefs.putInt("windowHeight", getHeight());

        prefs.putInt("mainDividerLocation", mainSplitPane.getDividerLocation());
        prefs.putInt("leftDividerLocation", leftSplitPane.getDividerLocation());
        prefs.putInt("verticalDividerLocation", verticalSplitPane.getDividerLocation());
        prefs.putInt("horizontalDividerLocation", horizontalSplitPane.getDividerLocation());
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    public FileListPanel getFileListPanel() {
        return fileListPanel;
    }

    public ResultListPanel getResultListPanel() {
        return resultListPanel;
    }

    public SearchTermListPanel getSearchTermListPanel() {
        return searchTermListPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }
}
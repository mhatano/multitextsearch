package jp.hatano.textsearch.gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
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
        setTitle("Multi-Text Search");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        prefs = Preferences.userNodeForPackage(MainFrame.class);

        searchPanel = new SearchPanel(this);
        fileListPanel = new FileListPanel(this);
        resultListPanel = new ResultListPanel(this);
        searchTermListPanel = new SearchTermListPanel(this);
        textArea = new JTextArea();
        Font textAreaFont = new Font("Courier New", Font.PLAIN, 12);
        if (!Arrays.stream(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()).anyMatch(name -> name.equals("Courier New")) ) {
            textAreaFont = new Font("Monospaced", Font.PLAIN, 12);
        }
        textArea.setFont(textAreaFont);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(false);
        textArea.setEditable(false);

        verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(fileListPanel), new JScrollPane(textArea));
        horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(resultListPanel), verticalSplitPane);
        leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(searchTermListPanel), new JScrollPane(resultListPanel));
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplitPane, verticalSplitPane);

        add(searchPanel, BorderLayout.NORTH);
        add(mainSplitPane, BorderLayout.CENTER);

        // Create menu bar
        JMenuBar menuBar = new JMenuBar();

        // Create "File" menu
        JMenu fileMenu = new JMenu("File");

        // Add "Open Search Term List File" menu item
        JMenuItem openSearchTermListMenuItem = new JMenuItem("Open Search Term List File");
        fileMenu.add(openSearchTermListMenuItem);

        // Add "Open Search Target Folder" menu item
        JMenuItem openSearchTargetFolderMenuItem = new JMenuItem("Open Search Target Folder");
        fileMenu.add(openSearchTargetFolderMenuItem);

        // Add "Exit" menu item
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        fileMenu.add(exitMenuItem);

        // Add action listener for "Open Search Term List File" menu item
        openSearchTermListMenuItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Open Search Term List File");

            // Restore last directory from preferences
            String lastSearchTermDir = prefs.get("lastSearchTermDir", null);
            if (lastSearchTermDir != null) {
                fileChooser.setCurrentDirectory(new File(lastSearchTermDir));
            }

            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                searchPanel.loadSearchTerms(selectedFile);

                // Save the directory to preferences
                prefs.put("lastSearchTermDir", selectedFile.getParent());
            }
        });

        // Add action listener for "Open Search Target Folder" menu item
        openSearchTargetFolderMenuItem.addActionListener(e -> {
            JFileChooser folderChooser = new JFileChooser();
            folderChooser.setDialogTitle("Open Search Target Folder");
            folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            // Add "Include Subdirectories" checkbox to the file chooser
            JCheckBox includeSubdirectoriesCheckBox = new JCheckBox("Include Subdirectories");

            // Restore last directory and checkbox state from preferences
            String lastSearchTargetDir = prefs.get("lastSearchTargetDir", null);
            if (lastSearchTargetDir != null) {
                folderChooser.setCurrentDirectory(new File(lastSearchTargetDir));
            }
            boolean includeSubdirectories = prefs.getBoolean("includeSubdirectories", false);
            includeSubdirectoriesCheckBox.setSelected(includeSubdirectories);

            folderChooser.setAccessory(includeSubdirectoriesCheckBox);

            int result = folderChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFolder = folderChooser.getSelectedFile();
                boolean includeSubdirs = includeSubdirectoriesCheckBox.isSelected();
                fileListPanel.loadDirectory(selectedFolder, includeSubdirs);

                prefs.put("lastSearchTargetDir", selectedFolder.getAbsolutePath());
                prefs.putBoolean("includeSubdirectories", includeSubdirs);
            }
        });

        // Add action listener for "Exit" menu item
        exitMenuItem.addActionListener(e -> System.exit(0));

        // Add "File" menu to the menu bar
        menuBar.add(fileMenu);

        // Set the menu bar for the frame
        setJMenuBar(menuBar);

        loadPreferences();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                savePreferences();
            }
        });
    }

    private void loadPreferences() {
        int x = prefs.getInt("windowX", 100);
        int y = prefs.getInt("windowY", 100);
        setLocation(x, y);

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
        prefs.putInt("windowX", getLocationOnScreen().x);
        prefs.putInt("windowY", getLocationOnScreen().y);

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
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
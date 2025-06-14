package jp.hatano.textsearch.gui;

import javax.swing.*;

import jp.hatano.textsearch.util.DialogUtils;

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
    private boolean ignoreCase;

    public MainFrame() {
        try {
            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
            boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {  
                if ((isWindows && "Windows".equals(info.getName())) 
                    || (isMac && "Mac OS X".equals(info.getName()))
                    || (!isWindows && !isMac && "Nimbums".equals(info.getName())) ) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        setTitle("Multi-Text Search");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        prefs = Preferences.userNodeForPackage(MainFrame.class);
        ignoreCase = prefs.getBoolean("searchIgnoreCase", false);

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
                if ( selectedFile.exists() && !selectedFile.isDirectory() ) {
                    searchPanel.loadSearchTerms(selectedFile);

                    // Save the directory to preferences
                    prefs.put("lastSearchTermDir", selectedFile.getParent());
                } else {
                    DialogUtils.showErrorDialog(this, "Selected file does not exist or is not a file.");
                }
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
                if ( selectedFolder.exists() && selectedFolder.isDirectory() ) {
                    fileListPanel.loadDirectory(selectedFolder, includeSubdirs);

                    prefs.put("lastSearchTargetDir", selectedFolder.getAbsolutePath());
                    prefs.putBoolean("includeSubdirectories", includeSubdirs);
                } else {
                    DialogUtils.showErrorDialog(this, "The selected folder does not exist or is not a directory.");
                }
            }
        });

        // Add action listener for "Exit" menu item
        exitMenuItem.addActionListener(e -> System.exit(0));

        // Add "File" menu to the menu bar
        menuBar.add(fileMenu);

        // Create "Help" menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutMenuItem = new JMenuItem("About Multi-Text Search");
        helpMenu.add(aboutMenuItem);

        // Add action listener for "About" menu item
        aboutMenuItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                this,
                "Multi-Text Search\n"+
                "Version 1.0\n\n"+
                "A powerful text search tool that allows you to\n"+
                "search multiple files for multiple search terms\n"+
                "simultaneously.\n\n"+
                "Developed by: Manami Hatano\n\n"+
                "https://github.com/mhatano/multitextsearch\n\n"+
                "Copyright (c) 2025, M.Hatano", 
                "About Multi-Text Search",
                JOptionPane.INFORMATION_MESSAGE
            );
        });

        // Add "Help" menu to the menu bar
        menuBar.add(helpMenu);

        // Set the menu bar for the frame
        setJMenuBar(menuBar);

        // Add components to content pane
        getContentPane().add(searchPanel, BorderLayout.NORTH);
        getContentPane().add(mainSplitPane, BorderLayout.CENTER);

        loadPreferences();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                savePreferences();
            }
        });
    }

    public boolean getIgnoreCase() {
        return ignoreCase;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
        prefs.putBoolean("searchIgnoreCase",ignoreCase);
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
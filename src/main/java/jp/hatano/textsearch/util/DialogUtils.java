package jp.hatano.textsearch.util;

import javax.swing.*;

import java.io.PrintWriter;
import java.io.StringWriter;

public class DialogUtils {

    public static void showErrorDialog(JFrame parent, String message, Exception ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        String stackTrace = sw.toString();

        JOptionPane.showMessageDialog(parent,
            message + "\n\nStack Trace:\n" + stackTrace,
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }

    public static void showErrorDialog(JFrame parent, String message) {
        JOptionPane.showMessageDialog(parent,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}
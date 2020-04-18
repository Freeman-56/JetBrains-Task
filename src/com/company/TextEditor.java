package com.company;

import com.company.AstTree.AstNode;
import com.company.Parser.LangInterpreter;
import com.company.Parser.Parser;
import com.company.Parser.IfChecker;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Arrays;

public class TextEditor extends JFrame implements ActionListener {
    private AstNode program;
    private Parser parser = new Parser();
    private IfChecker ifChecker = new IfChecker();

    // Text component
    private JTextArea textArea;

    // Frame
    private JFrame frame;

    // Constructor
    TextEditor() {
        // Create a frame
        frame = new JFrame("Editor");
        DocumentListener documentListener = new DocumentListener() {
            boolean watch = false;
            boolean ready = true;
            @Override
            public void insertUpdate(DocumentEvent e) {
                parser.setSource(textArea.getText());
                try {
                    if(textArea.getText().charAt(textArea.getCaretPosition()) == 'i' && !watch)
                        ready = false;
                    if (textArea.getText().charAt(textArea.getCaretPosition()) == '{' && !watch)
                        watch = true;
                    if(watch)
                        if(textArea.getText().charAt(textArea.getCaretPosition()) == '}'){
                            ready = true;
                            watch = false;
                        }
                    if(textArea.getText().charAt(textArea.getCaretPosition()) == ';' && !watch) {
                        ready = true;
                        watch = false;
                    }
                    if(ready) {
                        program = parser.parse();
                        if (ifChecker.isRightIfAdded(program)) {
                            JOptionPane.showMessageDialog(frame, "If-statement added");
                        }
                    }
                } catch (Exception ex) {
                    ex.getStackTrace();
                }
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        };

        try {
            // Set metl look and feel
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");

            // Set theme to ocean
            MetalLookAndFeel.setCurrentTheme(new OceanTheme());
        }
        catch (Exception ex) {
            ex.getStackTrace();
        }

        // Text component
        textArea = new JTextArea();
        textArea.getDocument().addDocumentListener(documentListener);
        // Create a menubar
        JMenuBar mb = new JMenuBar();

        // Create amenu for menu
        JMenu m1 = new JMenu("File");

        // Create menu items
        JMenuItem mi1 = new JMenuItem("New");
        JMenuItem mi2 = new JMenuItem("Open");
        JMenuItem mi3 = new JMenuItem("Save");

        // Add action listener
        mi1.addActionListener(this);
        mi2.addActionListener(this);
        mi3.addActionListener(this);

        m1.add(mi1);
        m1.add(mi2);
        m1.add(mi3);

        // Create amenu for menu
        JMenu m2 = new JMenu("Edit");

        // Create menu items
        JMenuItem mi4 = new JMenuItem("cut");
        JMenuItem mi5 = new JMenuItem("copy");
        JMenuItem mi6 = new JMenuItem("paste");

        // Add action listener
        mi4.addActionListener(this);
        mi5.addActionListener(this);
        mi6.addActionListener(this);

        m2.add(mi4);
        m2.add(mi5);
        m2.add(mi6);

        JMenuItem mc = new JMenuItem("Run!");

        mc.addActionListener(this);

        mb.add(m1);
        mb.add(m2);
        mb.add(mc);

        frame.setJMenuBar(mb);
        frame.add(textArea);
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    // If a button is pressed
    public void actionPerformed(ActionEvent e)
    {
        String s = e.getActionCommand();

        switch (s) {
            case "cut":
                textArea.cut();
                break;
            case "copy":
                textArea.copy();
                break;
            case "paste":
                textArea.paste();
                break;
            case "Save": {
                // Create an object of JFileChooser class
                JFileChooser j = new JFileChooser("f:");

                // Invoke the showsSaveDialog function to show the save dialog
                int r = j.showSaveDialog(null);

                if (r == JFileChooser.APPROVE_OPTION) {

                    // Set the label to the path of the selected directory
                    File fi = new File(j.getSelectedFile().getAbsolutePath());

                    try {
                        // Create a file writer
                        FileWriter wr = new FileWriter(fi, false);

                        // Create buffered writer to write
                        BufferedWriter w = new BufferedWriter(wr);

                        // Write
                        w.write(textArea.getText());

                        w.flush();
                        w.close();
                    } catch (Exception evt) {
                        JOptionPane.showMessageDialog(frame, evt.getMessage());
                    }
                }
                // If the user cancelled the operation
                else
                    JOptionPane.showMessageDialog(frame, "the user cancelled the operation");
                break;
            }
            case "Open": {
                // Create an object of JFileChooser class
                JFileChooser j = new JFileChooser("f:");

                // Invoke the showsOpenDialog function to show the save dialog
                int r = j.showOpenDialog(null);

                // If the user selects a file
                if (r == JFileChooser.APPROVE_OPTION) {
                    // Set the label to the path of the selected directory
                    File fi = new File(j.getSelectedFile().getAbsolutePath());

                    try {
                        // String
                        String s1;
                        StringBuilder sl;

                        // File reader
                        FileReader fr = new FileReader(fi);

                        // Buffered reader
                        BufferedReader br = new BufferedReader(fr);

                        // Initilize sl
                        sl = new StringBuilder(br.readLine());

                        // Take the input from the file
                        while ((s1 = br.readLine()) != null) {
                            sl.append("\n").append(s1);
                        }

                        // Set the text
                        textArea.setText(sl.toString());
                    } catch (Exception evt) {
                        JOptionPane.showMessageDialog(frame, evt.getMessage());
                    }
                }
                // If the user cancelled the operation
                else
                    JOptionPane.showMessageDialog(frame, "the user cancelled the operation");
                break;
            }
            case "New":
                textArea.setText("");
                break;
            case "Run!":
                try {
                    parser.setSource(textArea.getText());
                    program = parser.parse();
                    LangInterpreter interpreter = new LangInterpreter(program);
                    interpreter.execute();
                    JOptionPane.showMessageDialog(frame, interpreter.getOutput());
                } catch (Exception ex) {
                    System.out.println(Arrays.toString(ex.getStackTrace()));
                }
                break;
        }
    }

}

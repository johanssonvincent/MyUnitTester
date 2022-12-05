/**
 * @author Vincent Johansson (dv14vjn@cs.umu.se]
 * @since 2022-11-21
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * ProgramGUI class
 * The graphical interface of the program
 */
public class ProgramGUI extends JFrame{

    /* Variable declarations */
    public JTextField textInput;
    public JTextArea textOutput;
    public JFrame window;
    public JButton clearOutput;
    public JButton runInput;
    public enum ButtonType {RunButton, ClearButton};

    /**
     * ProgramGUI constructor
     */
    public ProgramGUI() {
        window = setupMainWindow();

        /* Add input box and run button */
        JPanel inputPanel = createTopPanel();
        window.add(inputPanel, BorderLayout.NORTH);

        /* Add textarea showing program output */
        textOutput = new JTextArea(20,20);
        textOutput.setLineWrap(true);
        JScrollPane scrollOutput = new JScrollPane(textOutput,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        window.add(scrollOutput, BorderLayout.CENTER);

        /* Add button to clear output text */
        JPanel bottomPanel = createBottomPanel();
        window.add(bottomPanel, BorderLayout.SOUTH);
        window.pack();

        window.setVisible(true);
    }

    /**
     * Setup for the main window JFrame
     * @return  window JFrame
     */
    protected JFrame setupMainWindow() {
        JFrame window = new JFrame("MyUnitTester");
        /* Window setup */
        window.setSize(500, 500);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBackground(Color.WHITE);

        return window;
    }

    /**
     * Setup for the top JPanel
     * @return  topPanel JPanel
     */
    protected JPanel createTopPanel() {
        JPanel topPanel = new JPanel();
        /* Panel setup */
        topPanel.setLayout(new FlowLayout());
        textInput = new JTextField(20);
        topPanel.add(textInput);
        runInput = new JButton("Run test");
        topPanel.add(runInput);

        /* Setup listener */
        ActionListener runButton = new ButtonListener(ButtonType.RunButton);
        runInput.addActionListener(runButton);

        return topPanel;
    }

    /**
     * Setup for the bottom JPanel
     * @return  bottomPanel JPanel
     */
    protected JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel();
        /* Panel setup */
        bottomPanel.setLayout(new FlowLayout());
        clearOutput = new JButton("Clear");
        bottomPanel.add(clearOutput);

        ActionListener clearButton = new ButtonListener(ButtonType.ClearButton);
        clearOutput.addActionListener(clearButton);

        return bottomPanel;
    }

    /**
     * ButtonListener class
     * Local class for the ProgramGUI, implements ActionListener and
     * Overrides actionPerformed to customize action on different button clicks
     */
    class ButtonListener implements ActionListener {

        private final ButtonType type;
        private List<String> results;

        public ButtonListener(ButtonType r) {
            this.type = r;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            switch (type) {
                case RunButton -> {
                    TestRunner testRunner = new TestRunner(textInput, textOutput);
                    testRunner.execute();
                    try {
                        results = testRunner.get();
                    } catch (ExecutionException | InterruptedException e) {
                        SwingUtilities.invokeLater(()->{
                            textOutput.append(e.getCause().getMessage());
                        });
                    }
                    for (int i = 0; i < results.size(); i++) {
                        int finalI = i;
                        SwingUtilities.invokeLater(()->textOutput.append(results.get(finalI)+"\n"));
                    }
                }
                case ClearButton -> {
                    SwingUtilities.invokeLater(()->textOutput.setText(""));
                }
            }
        }
    }
}

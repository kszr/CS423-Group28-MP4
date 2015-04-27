package Interface;

import javax.swing.*;
import java.awt.*;

/**
 * Created by abhishekchatterjee on 4/27/15.
 */
public class StatusWindow extends JFrame {
    private JPanel controlPanel;
    private JTextArea throttleTextArea;
    private JTextArea numJobsTextArea;
    private JTextArea transferTextArea;

    public StatusWindow() {
        initUI();
    }

    private void initUI() {
        setTitle("Status");
        setSize(400, 300);
        setLocationRelativeTo(null);

        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        this.add(controlPanel);

        createThrottleTextArea();
        createNumJobsTextArea();
        createTransferTextArea();

        this.setVisible(true);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void createThrottleTextArea() {
        JLabel throttleLabel = new JLabel("Throttle value");
        controlPanel.add(throttleLabel);

        throttleTextArea = new JTextArea(1, 30);
        throttleTextArea.setEditable(false);
        controlPanel.add(throttleTextArea);

        JScrollPane scroll = new JScrollPane (transferTextArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        controlPanel.add(scroll);
    }

    public void setThrottleText(String text) {
        throttleTextArea.setText(text);
    }

    public String getThrottleText() {
        return throttleTextArea.getText();
    }

    private void createNumJobsTextArea() {
        JLabel numJobsLabel = new JLabel("Progress on jobs");
        controlPanel.add(numJobsLabel);

        numJobsTextArea = new JTextArea(1, 30);
        numJobsTextArea.setEditable(false);
        controlPanel.add(numJobsTextArea);


        JScrollPane scroll = new JScrollPane (transferTextArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        controlPanel.add(scroll);
    }

    public void setNumJobsText(String text) {
        numJobsTextArea.setText(text);
    }

    public String getNumJobsText() {
        return numJobsTextArea.getText();
    }

    private void createTransferTextArea() {
        JLabel transferLabel = new JLabel("Status messages");
        controlPanel.add(transferLabel);

        transferTextArea = new JTextArea(10, 30);
        transferTextArea.setText("");
        transferTextArea.setEditable(false);
        controlPanel.add(transferTextArea);

        JScrollPane scroll = new JScrollPane (transferTextArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        controlPanel.add(scroll);
    }

    public void appendTransferText(String text) {
        transferTextArea.append(text+"\n");
    }

    public String getTransferText() {
        return transferTextArea.getText();
    }
}

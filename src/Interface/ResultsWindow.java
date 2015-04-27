package Interface;

import javax.swing.*;

/**
 * Created by abhishekchatterjee on 4/27/15.
 */
public class ResultsWindow extends JFrame {
    private JPanel controlPanel;
    private JTextArea resultTextArea;

    public ResultsWindow() {
        initUI();
    }

    private void initUI() {
        setTitle("Results");
        setSize(300, 200);
        setLocationRelativeTo(null);

        controlPanel = new JPanel();
        this.add(controlPanel);

        this.setVisible(true);

        createNoneditableTextArea();
    }

    private void createNoneditableTextArea() {
        resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);
        resultTextArea.setVisible(true);
        controlPanel.add(resultTextArea);
    }

    public void setResultText(String result) {
        resultTextArea.setText(result);
    }
}

package Interface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Created by abhishekchatterjee on 4/26/15.
 */
public class Window extends JFrame {
    private JPanel controlPanel;
    private JLabel infoLabel;
    private JLabel throtLabel;
    private JButton button;
    private JTextField throtText;

    public Window() {
        initUI();
    }

    private void initUI() {
        setTitle("Set Throttling Value");
        setSize(300, 200);
        setLocationRelativeTo(null);

        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        this.add(controlPanel);

        createNoneditableTextField();
        createEditableTextField();
        addButton();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void addButton() {
        button = new JButton();
        button.setText("Submit");
        controlPanel.add(button);
    }

    public String getTextFieldText() {
        return throtText.getText();
    }

    private void createNoneditableTextField() {
        infoLabel = new JLabel("Current Throttling Value:", JLabel.LEFT);
        controlPanel.add(infoLabel);
    }

    public void updateThrottlingValue(double throttle) {
        infoLabel.setText("Current Throttling Value: " + throttle);
    }

    private void createEditableTextField() {
        throtLabel = new JLabel("New Throttling Value: ", JLabel.LEFT);
        throtText = new JTextField(6);

        controlPanel.add(throtLabel);
        controlPanel.add(throtText);
    }

    public void addSubmitActionListener(ActionListener listener) {
        button.addActionListener(listener);
    }
}

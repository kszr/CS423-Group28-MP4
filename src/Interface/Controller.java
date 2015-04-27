package Interface;

import Hardware.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by abhishekchatterjee on 4/26/15.
 */
public class Controller {
    private HardwareWindow window;
    private HardwareMonitor hardwareMonitor;

    public Controller(HardwareMonitor hardwareMonitor, HardwareWindow window) {
        this.hardwareMonitor = hardwareMonitor;
        this.window = window;

        setUpActionListener();
    }

    private void setUpActionListener() {
        window.addSubmitActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String text = window.getTextFieldText();
                    double throttle = Double.parseDouble(text);
                    if(throttle > 1.0)
                        throw new NumberFormatException("Value out of range.");
                    hardwareMonitor.setThrottlingValue(throttle);
                    window.updateThrottlingValue(throttle);
                } catch(NumberFormatException nfe) {
                    nfe.printStackTrace();
                } catch (NullPointerException ne) {
                    ne.printStackTrace();
                }
            }
        });
    }
}

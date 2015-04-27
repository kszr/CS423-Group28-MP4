package Adapter;

import Hardware.HardwareMonitor;
import State.StateManager;

/**
 * Created by abhishekchatterjee on 4/26/15.
 */
public class Adapter {
    private HardwareMonitor hardwareMonitor;
    private StateManager stateManager;

    int portNumber;
    int jobCount;

    public Adapter() {
        hardwareMonitor = new HardwareMonitor();

        //initialize portNumber to something.
        portNumber = 0;

        stateManager = new StateManager(portNumber);
    }

    private void sendState() {
        stateManager.updateState(jobCount, hardwareMonitor.getThrottlingValue(), hardwareMonitor.getHardwareInfo());
    }

    public void openHardwareMonitorInterface() {
        hardwareMonitor.openInterface();
    }
}

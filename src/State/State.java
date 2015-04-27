package State;

import Hardware.HardwareInfo;

/**
 * Created by abhishekchatterjee on 4/26/15.
 * Stores the system state, including:
 *      - Number of jobs pending in the queue
 *      - Current local throttling values
 *      - Information collected by the Hardware Monitor
 */
public class State {
    public int jobCount;
    public double throttle;
    public HardwareInfo hardwareInfo;

    public String toString() {
        return "Job count: " + jobCount +
                "\nThrottling value: " + throttle +
                "\nHardware info: " + hardwareInfo.toString();
    }
}

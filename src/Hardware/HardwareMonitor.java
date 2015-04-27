package Hardware;

import javax.management.*;
import java.lang.management.ManagementFactory;

import Interface.*;

/**
 * Created by abhishekchatterjee on 4/26/15.
 */
public class HardwareMonitor {
    private double throttle;

    /**
     * Throttling value is set to 100% by default.
     */
    public HardwareMonitor() {
        throttle = 1.0;
    }

    public double getThrottlingValue() {
        return throttle;
    }

    public HardwareInfo getHardwareInfo() {
        HardwareInfo info = new HardwareInfo();

        try {
            info.cpuUtil = getCpuLoad();
        } catch(Exception e) {
            return null;
        }

        return info;
    }

    public void setThrottlingValue(double throttle) {
        this.throttle = throttle;
    }

    /**
     * Gets the CPU utilization
     * Source: http://stackoverflow.com/a/21962037/1843968
     * @return
     * @throws MalformedObjectNameException
     * @throws ReflectionException
     * @throws InstanceNotFoundException
     */
    private static double getCpuLoad() throws MalformedObjectNameException, ReflectionException, InstanceNotFoundException {

        MBeanServer mbs    = ManagementFactory.getPlatformMBeanServer();
        ObjectName name    = ObjectName.getInstance("java.lang:type=OperatingSystem");
        AttributeList list = mbs.getAttributes(name, new String[]{ "ProcessCpuLoad" });

        if (list.isEmpty())     return Double.NaN;

        Attribute att = (Attribute)list.get(0);
        Double value  = (Double)att.getValue();

        if (value == -1.0)      return Double.NaN;  // usually takes a couple of seconds before we get real values

        return ((int)(value * 1000) / 10.0);        // returns a percentage value with 1 decimal point precision
    }

    /**
     * The interface component of the Hardware Monitor that allows
     * the user to set throttling values.
     */
    public void openInterface() {
        Window window = new Window();
        window.setVisible(true);
        window.updateThrottlingValue(throttle);
        Controller controller = new Controller(this, window);
    }

    public static void main(String[] args) {
        HardwareMonitor hm = new HardwareMonitor();
        hm.openInterface();
    }
}

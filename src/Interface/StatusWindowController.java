package Interface;

import Aggregation.JobAggregator;
import Balancer.JobServer;
import Balancer.QueueWatcher;
import Hardware.HardwareMonitor;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by abhishekchatterjee on 4/27/15.
 */
public class StatusWindowController implements Observer {
    private StatusWindow statusWindow;

    public StatusWindowController() {
        openStatusWindow();
    }

    private void openStatusWindow() {
        statusWindow = new StatusWindow();
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof HardwareMonitor) {
            if(arg instanceof Double) {
                statusWindow.setCPUUtilText(Double.toString((double) arg));
            } else {
                statusWindow.setThrottleText(Double.toString(((HardwareMonitor) o).getThrottlingValue()));
                statusWindow.appendTransferText("Throttle value updated.");
            }
        } else if(o instanceof JobServer) {
            statusWindow.appendTransferText("Initiating a transfer.");
        } else if(o instanceof QueueWatcher) {
            statusWindow.setNumJobsText(Integer.toString((int) arg));
        }
    }
}

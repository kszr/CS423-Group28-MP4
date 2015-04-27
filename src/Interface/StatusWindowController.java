package Interface;

import Aggregation.JobAggregator;
import Balancer.JobServer;
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
            statusWindow.setThrottleText(Double.toString(((HardwareMonitor) o).getThrottlingValue()));
            statusWindow.appendTransferText("Throttle value updated.");
        } else if(o instanceof JobAggregator) {
            statusWindow.setNumJobsText(((JobAggregator) o).getCompletedCount() +
                    " jobs completed out of " + ((JobAggregator) o).getTotalCount());
        } else if(o instanceof JobServer) {
            statusWindow.appendTransferText("Initiating a transfer.");
        }
    }
}

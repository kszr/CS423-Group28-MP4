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
    private JobServer jobServer;
    private JobAggregator jobAggregator;
    private HardwareMonitor hardwareMonitor;

    public StatusWindowController(JobAggregator jobAggregator,
                                  JobServer jobServer,
                                  HardwareMonitor hardwareMonitor) {
        this.jobAggregator = jobAggregator;
        this.jobServer = jobServer;

        openStatusWindow();
    }

    private void openStatusWindow() {
        statusWindow = new StatusWindow();
        statusWindow.setThrottleText(Double.toString(hardwareMonitor.getThrottlingValue()));
        statusWindow.setNumJobsText(jobAggregator.getCompletedCount() +
                " jobs completed out of " + jobAggregator.getTotalCount());
    }

    @Override
    public void update(Observable o, Object arg) {
        if(arg instanceof HardwareMonitor) {
            statusWindow.setThrottleText(Double.toString(((HardwareMonitor) arg).getThrottlingValue()));
            statusWindow.appendTransferText("Throttle value updated.");
        } else if(arg instanceof JobAggregator) {
            statusWindow.setNumJobsText(((JobAggregator) arg).getCompletedCount() +
                    " jobs completed out of " + ((JobAggregator) arg).getTotalCount());
        } else if(arg instanceof JobServer) {
            statusWindow.appendTransferText("Initiating a transfer.");
        }
    }
}

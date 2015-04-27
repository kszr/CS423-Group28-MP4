package Balancer;

import Jobs.JobQueue;
import Utilities.AutoMutex;

/**
 * Created by Matthew on 4/26/2015.
 */
public class QueueWatcher {

    private long waitPeriod = 10000;
    private JobRequester requester;
    private JobQueue queue;

    public QueueWatcher(JobQueue queue, JobRequester requester) {
        this.queue = queue;
        this.requester = requester;

        Runnable run = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    loop();
                }
            }
        };

        new Thread(run).start();
    }

    public void loop() {

        boolean shouldRequest = false;

        try (AutoMutex mutex = queue.holdMutex()) {
            shouldRequest = queue.numJobs() == 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (shouldRequest) {
            requester.request();
        }

        try {
            Thread.sleep(waitPeriod);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



}

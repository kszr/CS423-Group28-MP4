package Balancer;

import Jobs.JobQueue;
import Utilities.AutoMutex;
import java.util.Observable;

/**
 * Created by Matthew on 4/26/2015.
 */
public class QueueWatcher extends Observable{

    private long waitPeriod = 10000;
    private JobRequester requester;
    private JobQueue queue;

    private int lastJobCount = 0;

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
        int newJobCount = lastJobCount;

        try (AutoMutex mutex = queue.holdMutex()) {
            newJobCount = queue.numJobs();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (newJobCount == 0) {
            requester.request();
        }

        if (newJobCount != lastJobCount) {
            lastJobCount = newJobCount;
            setChanged();
            notifyObservers();
        }

        try {
            Thread.sleep(waitPeriod);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getLastJobCount() {
        return lastJobCount;
    }



}

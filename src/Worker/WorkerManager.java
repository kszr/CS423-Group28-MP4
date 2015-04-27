package Worker;

import Aggregation.AggregatorSender;
import Jobs.Job;
import Jobs.JobQueue;
import Utilities.AutoMutex;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Created by Matthew on 4/26/2015.
 */
public class WorkerManager {

    /**
     * Change based on desired usage; e.g. 2.5f with 5 workers will result in 5 threads each sleeping half the time
     */
    private float allowedCPUUsage = 1.0f;
    private JobQueue queue;
    private AggregatorSender sender;

    private List<Worker> allWorkers;
    private Queue<Worker> nappingWorkers;
    private Semaphore nappingSemaphore = new Semaphore(1);

    public WorkerManager(JobQueue queue, AggregatorSender sender, int numWorkers) {

        this.sender = sender;

        allWorkers = new ArrayList<>(numWorkers);
        nappingWorkers = new LinkedList<>();

        for (int i = 0; i < numWorkers; i++) {
            Worker newWorker = new Worker(this);
            allWorkers.add(newWorker);
            nappingWorkers.add(newWorker);
        }

        try (AutoMutex mutex = queue.holdMutex()) {
            this.queue = queue;
            queue.watcher = this;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * A new job has arrived. This checks if there is a worker who would immediately like it.
     * @param job The new job
     * @return true iff the job has been taken
     */
    public boolean incomingJob(Job job) {
        try (AutoMutex mutex = new AutoMutex(nappingSemaphore)) {

            if (nappingWorkers.size() == 0) {
                return false;
            }

            Worker worker = nappingWorkers.poll();
            worker.giveJob(job);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Sends the job to the other completed jobs, either returns a new job for the worker or marks it as napping.
     * @param worker
     * @param job
     */
    public Job reportCompletion(Worker worker, Job job) {
        Job toReturn = null;

        try (AutoMutex queueMutex = queue.holdMutex()) {

            if (queue.numJobs() > 0) {
                toReturn = queue.popJob();
            } else {
                try (AutoMutex nappingMutex = new AutoMutex(nappingSemaphore)) {
                    nappingWorkers.add(worker);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        sender.sendJob(job);

        return toReturn;
    }

    public float getAllowedCPUUsage() {
        return allowedCPUUsage;
    }

    public void setAllowedCPUUsage(float allowedCPUUsage) {
        this.allowedCPUUsage = allowedCPUUsage;
    }

    public float getPerWorkerCPUUsage() {
        return allowedCPUUsage / allWorkers.size();
    }

    public static void main(String[] args) {

        final int numJobs = 1024;
        int dataLength = 1000;
        Job[] jobs = new Job[numJobs];

        for (int i = 0; i < numJobs; i++) {
            float[] data = new float[dataLength];
            for (int j = 0; j < dataLength; j++) {
                data[j] = 1.111111f;
            }
            Job job = new Job();
            job.completed = false;
            job.data = data;
            job.index = i*dataLength;

            jobs[i] = job;
        }

        JobQueue queue = new JobQueue();

        AggregatorSender sender = new AggregatorSender() {
            private long startTime = System.currentTimeMillis();
            private int jobsDone = 0;
            private Semaphore counterSemaphore = new Semaphore(1);

            @Override
            public void sendJob(Job job) {
                try {
                    counterSemaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                jobsDone++;
                System.out.println("Job aggregated: " + job.index + " Total: " + jobsDone);


                if (jobsDone == numJobs) {
                    long endTime = System.currentTimeMillis();
                    System.out.println("Start time: " + startTime +
                            "\nEnd time: " + endTime +
                            "\nTime taken: " + (endTime - startTime));

                }

                counterSemaphore.release();

            }
        };

        WorkerManager manager = new WorkerManager(queue, sender, 8);

        for (Job job : jobs) {
            queue.addJob(job);
        }

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

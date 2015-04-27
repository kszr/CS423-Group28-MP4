package Adapter;

import Aggregation.AggregatorSender;
import Aggregation.JobAggregator;
import Balancer.JobRequester;
import Balancer.JobServer;
import Balancer.QueueWatcher;
import Hardware.HardwareMonitor;
import Jobs.Job;
import Jobs.JobQueue;
import Jobs.JobSplitter;
import State.StateManager;
import Worker.WorkerManager;

/**
 * Created by abhishekchatterjee on 4/26/15.
 */
public class Adapter {

    // Set these before calling Setup()
    public int numElements = 1024 * 1024 * 32;
    public int numJobs = 512;

    public int numWorkerThreads = 8;

    public int ownJobServerPort = 9834;

    public String otherAddress;
    public int otherJobServerPort = 9834;

    public boolean isAggregator;
    public String aggregatorAddress;
    public int aggregatorPort = 9835;


    // Created during Setup();
    private JobQueue jobQueue;

    private JobRequester jobRequester;
    private JobServer jobServer;

    private WorkerManager workerManager;

    private QueueWatcher queueWatcher;

    private HardwareMonitor hardwareMonitor;

    private AggregatorSender aggregatorSender;
    private JobAggregator jobAggregator; // null iff isAggregator == false


    public Adapter(String otherAddress, String aggregatorAddress, boolean isAggregator) {
        this.otherAddress = otherAddress;
        this.aggregatorAddress = aggregatorAddress;
        this.isAggregator = isAggregator;
    }

    public void setup() {
        jobQueue = new JobQueue();

        jobServer = new JobServer(ownJobServerPort, jobQueue);
        jobRequester = new JobRequester(otherAddress, otherJobServerPort, jobQueue);

        if (isAggregator) {
            jobAggregator = new JobAggregator(aggregatorPort, numJobs);
        }
        aggregatorSender = new AggregatorSender(aggregatorAddress, aggregatorPort);

        workerManager = new WorkerManager(jobQueue, aggregatorSender, numWorkerThreads);

        hardwareMonitor = new HardwareMonitor(workerManager);

        JobSplitter splitter = new JobSplitter(numElements, numJobs);
        for (Job job : splitter) {
            jobQueue.addJob(job);
        }

        queueWatcher = new QueueWatcher(jobQueue, jobRequester);


    }


    public void openHardwareMonitorInterface() {
        hardwareMonitor.openInterface();
    }
}

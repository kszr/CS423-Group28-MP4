package Adapter;

import Aggregation.AggregatorSender;
import Aggregation.JobAggregator;
import Balancer.JobRequester;
import Balancer.JobServer;
import Balancer.QueueWatcher;
import Hardware.HardwareMonitor;
import Interface.StatusWindow;
import Interface.StatusWindowController;
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
    public boolean startsJobs;
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


    public Adapter(String otherAddress, String aggregatorAddress, boolean isAggregator, boolean startsJobs) {
        this.otherAddress = otherAddress;
        this.aggregatorAddress = aggregatorAddress;
        this.isAggregator = isAggregator;
        this.startsJobs = startsJobs;
    }

    public void setup() {
        jobQueue = new JobQueue();
        System.out.println("Queue created.");

        jobServer = new JobServer(ownJobServerPort, jobQueue);
        System.out.println("Job Server created");
        jobRequester = new JobRequester(otherAddress, otherJobServerPort, jobQueue);
        System.out.println("Job Requester created.");

        workerManager = new WorkerManager(jobQueue, aggregatorSender, numWorkerThreads);

        hardwareMonitor = new HardwareMonitor(workerManager);
        if (isAggregator) {
            System.out.println("This is the aggregator.");

            jobAggregator = new JobAggregator(aggregatorPort, numJobs);
            openStatusWindow();
        }
        aggregatorSender = new AggregatorSender(aggregatorAddress, aggregatorPort);

        if (startsJobs) {
            System.out.println("This is the job producer.");
            JobSplitter splitter = new JobSplitter(numElements, numJobs);
            for (Job job : splitter) {
                jobQueue.addJob(job);
            }
        }

        queueWatcher = new QueueWatcher(jobQueue, jobRequester);


    }

    /**
     * Opens a window that displays statuses.
     */
    public void openStatusWindow() {
        StatusWindowController statusWindowController = new StatusWindowController();

        System.out.println("OpenStatusWindow");

        hardwareMonitor.addObserver(statusWindowController);
        jobAggregator.addObserver(statusWindowController);
        jobServer.addObserver(statusWindowController);
    }

    /**
     * Opens the GUI for the Hardware Monitor.
     */
    public void openHardwareMonitorInterface() {
        hardwareMonitor.openInterface();
    }

    /**
     * Args:
     * [0] Partner's address
     * [1] Aggregator's address
     * [2] 1 if this is the results aggregator, 0 if it is the job generator
     * @param args
     */
    public static void main(String[] args) {

        boolean isAggregator = 1 == Integer.parseInt(args[2]);
        Adapter adapter = new Adapter(args[0], args[1], isAggregator, !isAggregator);
        adapter.setup();

    }
}

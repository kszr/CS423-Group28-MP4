package Balancer;

import Jobs.Job;
import Jobs.JobQueue;
import Utilities.AutoMutex;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.zip.GZIPInputStream;

/**
 * Requests jobs from a remote Balancer.JobServer
 *
 * Created by Matthew on 4/25/2015.
 */
public class JobRequester {

    private String serverAddress;
    private int serverPort;
    private JobQueue queue;

    public JobRequester(String serverAddress, int serverPort, JobQueue queue) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.queue = queue;
    }

    public void Request() {

        System.out.println("Balancer.JobRequester opening connection to " + serverAddress + " at port " + serverPort);

        try (Socket socket = new Socket(serverAddress, serverPort);
             GZIPInputStream gzipped = new GZIPInputStream(socket.getInputStream());
             ObjectInputStream stream = new ObjectInputStream(gzipped)) {



            System.out.println("Balancer.JobRequester made connection");

            while (true) {

                Job job = (Job) stream.readObject();

                try (AutoMutex mutex = queue.holdMutex()) {
                    queue.addJob(job);
                    System.out.println("Jobs.Job received:" + job.toString());
                }

                System.out.println("Balancer.JobRequester received job");

            }


        } catch (EOFException e) {
            System.out.println("Balancer.JobRequester has no more jobs to read.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Balancer.JobRequester's connection has been closed");

    }

    public static void main(String[] args) {

        final JobQueue requesterQueue = new JobQueue();
        final JobQueue serverQueue = new JobQueue();

        final int port = 9898;

        int numJobs = 8;
        int dataLength = 100;
        for (int i = 0; i < numJobs; i++) {
            float[] data = new float[dataLength];
            for (int j = 0; j < data.length; j++) {
                data[j] = i * 1.111f;
            }

            Job job = new Job();
            job.completed = false;
            job.data = data;
            job.index = i*dataLength;

            serverQueue.addJob(job);
        }

        Runnable requesterRunnable = new Runnable() {
            @Override
            public void run() {
                JobRequester requester = new JobRequester("localhost", port, requesterQueue);
                requester.Request();
                requester.Request();
            }
        };

        Runnable serverRunnable = new Runnable() {
            @Override
            public void run() {
                JobServer server = new JobServer(port, serverQueue);
                server.serve();
            }
        };

        Thread requesterThread = new Thread(requesterRunnable);
        Thread serverThread = new Thread(serverRunnable);

        serverThread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        requesterThread.start();


    }

}

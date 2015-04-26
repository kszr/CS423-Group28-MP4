import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Requests jobs from a remote JobServer
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

        System.out.println("JobRequester opening connection to " + serverAddress + " at port " + serverPort);

        try (Socket socket = new Socket(serverAddress, serverPort);
             ObjectInputStream stream = new ObjectInputStream(socket.getInputStream())) {

            System.out.println("JobRequester made connection");

            while (true) {
                Job job = (Job) stream.readObject();

                try (JobQueueAccess access = queue.getAccess()) {
                    queue.addJob(job);
                }

                System.out.println("JobRequester received job");

            }


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("JobRequester's connection has been closed");

    }

    public static void main(String[] args) {

        final JobQueue requesterQueue = new JobQueue();
        final JobQueue serverQueue = new JobQueue();

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
            job.length = dataLength;

            serverQueue.addJob(job);
        }

        Runnable requesterRunnable = new Runnable() {
            @Override
            public void run() {
                JobRequester requester = new JobRequester("172.0.0.1", 1567, requesterQueue);
                requester.Request();
                requester.Request();
            }
        };

        Runnable serverRunnable = new Runnable() {
            @Override
            public void run() {
                JobServer server = new JobServer(1567, serverQueue);
                server.serve();
            }
        };

        Thread requesterThread = new Thread(requesterRunnable);
        Thread serverThread = new Thread(serverRunnable);

        serverThread.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        requesterThread.start();


    }

}

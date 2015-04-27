package Aggregation;

import Jobs.Job;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.GZIPInputStream;

/**
 * Collects completed jobs
 *
 * Created by Matthew on 4/26/2015.
 */
public class JobAggregator {

    private int port;
    private Job[] completedJobs;

    private int jobsReceived = 0;

    public JobAggregator(int port, int jobCount) {
        this.port = port;
        completedJobs = new Job[jobCount];
    }

    public void serve() {

        try (ServerSocket listener = new ServerSocket(port)) {

            while (true) {
                talk(listener);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void talk(ServerSocket listener) {

        try (Socket socket = listener.accept();
             GZIPInputStream gzipped = new GZIPInputStream(socket.getInputStream());
             ObjectInputStream stream = new ObjectInputStream(gzipped)) {

            Job received = (Job) stream.readObject();

            int place = received.index;
            if (completedJobs[place] == null) {
                completedJobs[place] = received;
                jobsReceived++;
                completionCheck();
            } else {
                throw new Exception("Received duplicate jobs with index " + place + "!");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void completionCheck() {
        if (jobsReceived != completedJobs.length)
            return;

        System.out.println("All jobs received!");

        float minVal = Float.MAX_VALUE;
        float maxVal = -Float.MAX_VALUE;

        for (int i = 0; i < completedJobs.length; i++) {
            Job job = completedJobs[i];

            for (int j = 0; j < job.data.length; j++) {

                float val = job.data[j];

                minVal = Float.min(minVal, val);
                maxVal = Float.max(maxVal, val);

            }
        }

        System.out.println("Minimum value is " + minVal + " and maximum value is " + maxVal +
                "\nDifference is " + (maxVal-minVal));

    }




}

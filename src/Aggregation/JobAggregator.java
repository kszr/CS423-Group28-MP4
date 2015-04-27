package Aggregation;

import Interface.ResultsWindow;
import Jobs.Job;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;
import java.util.zip.GZIPInputStream;

/**
 * Collects completed jobs
 *
 * Created by Matthew on 4/26/2015.
 */
public class JobAggregator extends Observable {

    private int port;
    private Job[] completedJobs;

    private int jobsReceived = 0;

    public JobAggregator(int port, int jobCount) {
        this.port = port;
        completedJobs = new Job[jobCount];

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                serve();
            }
        };

        new Thread(runnable).start();

    }

    public int getCompletedCount() {
        return jobsReceived;
    }

    public int getTotalCount() {
        return completedJobs.length;
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

            System.out.println("Received completed job: " + place);

            if (completedJobs[place] == null) {
                completedJobs[place] = received;
                jobsReceived++;
                setChanged();
                notifyObservers();
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

        /*
        System.out.println("Minimum value is " + minVal + " and maximum value is " + maxVal +
                "\nDifference is " + (maxVal-minVal));*/

        double difference = maxVal-minVal;
        displayResult(difference);
    }

    /**
     * Displays the result in a window.
     * @param difference
     */
    private void displayResult(double difference) {
        ResultsWindow resultsWindow = new ResultsWindow();

        String resultText;
        if(difference < 0.00001)
            resultText = "Successful";
        else resultText = "Unsuccessful";

        resultsWindow.setResultText(resultText);
    }
}

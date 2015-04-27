package Balancer;

import Jobs.Job;
import Jobs.JobQueue;
import Utilities.AutoMutex;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;
import java.util.zip.GZIPOutputStream;

/**
 * When asked for jobs, the Jobs.Job Server will send back a fraction of jobs in the local queue
 *
 * Created by Matthew on 4/25/2015.
 */
public class JobServer extends Observable {

    private int portNumber;
    private JobQueue queue;

    public JobServer(int portNumber, JobQueue queue) {
        this.portNumber = portNumber;
        this.queue = queue;


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                serve();
            }
        };

        new Thread(runnable).start();

    }

    public void serve() {

        try (ServerSocket listener = new ServerSocket(portNumber)) {

            while (true) {
                talk(listener);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void talk(ServerSocket listener) {

        System.out.println("Balancer.JobServer ready to serve on port " + portNumber);

        try (Socket socket = listener.accept();
             GZIPOutputStream gzipped = new GZIPOutputStream(socket.getOutputStream());
             ObjectOutputStream objStream = new ObjectOutputStream(gzipped)) {

            System.out.println("Incoming connection to Balancer.JobServer");


            int jobsTaken = 0;
            while (sendJob(jobsTaken, objStream)) {
                System.out.println("Sent job");

                setChanged();
                notifyObservers();

                jobsTaken++;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Closed connection that came to Balancer.JobServer");

    }


    private boolean sendJob(int currentNumber, ObjectOutputStream stream) throws Exception {
        Job gotten;
        try (AutoMutex mutex = queue.holdMutex()) {
            if ((queue.numJobs() + currentNumber) / 2 <= currentNumber) {
                return false;
            }

            gotten = queue.popJob();
        }

        stream.writeObject(gotten);
        stream.flush();
        return true;
    }







}

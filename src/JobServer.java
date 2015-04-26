import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Semaphore;

/**
 * When asked for jobs, the Job Server will send back a fraction of jobs in the local queue
 *
 * Created by Matthew on 4/25/2015.
 */
public class JobServer {

    private int portNumber;
    private JobQueue queue;

    public JobServer(int portNumber, JobQueue queue) {
        this.portNumber = portNumber;
        this.queue = queue;
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

        System.out.println("JobServer ready to serve on port " + portNumber);

        try (Socket socket = listener.accept();
             ObjectOutputStream objStream = new ObjectOutputStream(socket.getOutputStream())) {

            System.out.println("Incoming connection to JobServer");


            int jobsTaken = 0;
            while (sendJob(jobsTaken, objStream)) {
                System.out.println("Sent job");
                jobsTaken++;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Closed connection that came to JobServer");

    }


    private boolean sendJob(int currentNumber, ObjectOutputStream stream) throws IOException {
        Job gotten;
        try (JobQueueAccess access = queue.getAccess()) {
            if (queue.numJobs() / 2 <= currentNumber) {
                return false;
            }

            gotten = queue.popJob();
        }

        stream.writeObject(gotten);
        return true;
    }







}

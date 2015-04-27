package Aggregation;

import Jobs.Job;
import Jobs.JobQueue;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.zip.GZIPOutputStream;

/**
 * Sends completed jobs to the aggregator
 *
 * Created by Matthew on 4/26/2015.
 */
public class AggregatorSender {

    private String serverAddress;
    private int serverPort;


    public AggregatorSender(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void sendJob(Job job){

        try (Socket socket = new Socket(serverAddress, serverPort);
             GZIPOutputStream gzipped = new GZIPOutputStream(socket.getOutputStream());
             ObjectOutputStream stream = new ObjectOutputStream(gzipped)){

            stream.writeObject(job);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

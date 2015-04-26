import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;

/**
 * A JobQueue holds jobs.
 * You (should) hold the semaphore when you want to do something with it.
 * Use JobQueueAccess to do so in an automatic way.
 *
 * Created by Matthew on 4/25/2015.
 */
public class JobQueue {

    public final Semaphore semaphore = new Semaphore(1);

    private Queue<Job> jobs = new LinkedList<>();

    public Job popJob() {
        return jobs.poll();
    }

    public int numJobs() {
        return jobs.size();
    }

    public void addJob(Job job) {
        jobs.add(job);
    }

    public JobQueueAccess getAccess() {
        return new JobQueueAccess(this);
    }

}

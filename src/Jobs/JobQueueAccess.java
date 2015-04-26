package Jobs;

import java.io.Closeable;
import java.io.IOException;

/**
 * An individual access to a Jobs.JobQueue object. Use to maintain mutual exclusion of access.
 *
 * Created by Matthew on 4/25/2015.
 */
public class JobQueueAccess implements Closeable {

    private JobQueue queue;

    public JobQueueAccess(JobQueue queue) {
        this.queue = queue;
        boolean holdsLock = false;
        while (!holdsLock) {
            try {
                queue.semaphore.acquire();
                holdsLock = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() throws IOException {
        queue.semaphore.release();
    }

}

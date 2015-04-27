package Utilities;

import java.util.concurrent.Semaphore;

/**
 * Created by Matthew on 4/26/2015.
 */
public class AutoMutex implements AutoCloseable {

    private Semaphore semaphore;

    public AutoMutex(Semaphore semaphore) {
        this.semaphore = semaphore;
        boolean acquired = false;
        while (!acquired) {
            try {
                semaphore.acquire();
                acquired = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() throws Exception {
        semaphore.release();
    }
}

package Worker;

import Jobs.Job;

/**
 * Does work. The only class that should interact with these is the WorkerManager.
 * I should totally be doing this with a thread pool or something instead.
 *
 * Created by Matthew on 4/26/2015.
 */
public class Worker {

    private final WorkerManager manager;

    private long lastRest;
    private int numPerRest = 1000000;
    private int numSinceRest = 0;

    /**
     * The worker is in 'napping' state when first created
     * @param manager
     */
    public Worker(WorkerManager manager) {
        this.manager = manager;
    }

    /**
     * Starts a thread running the given job. It will ask for more jobs once completed.
     * @param job
     */
    public void giveJob(final Job job) {

//        System.out.println("Starting thread with job: " + job.index);

        Runnable run = new Runnable() {
            @Override
            public void run() {
//                System.out.println("Thread started with job:" + job.index);

                lastRest = System.currentTimeMillis();
                Job toDo = job;
                do {
                    doJob(toDo);
                    toDo = manager.reportCompletion(Worker.this, toDo);
                } while (toDo != null);

//                System.out.println("Thread ended that began with job " + job.index);
            }
        };


        Thread thread = new Thread(run);
        thread.start();

    }

    private void doJob(Job job) {

        // Throttling is based roughly on http://stackoverflow.com/a/1205300

//        System.out.println("Starting job: " + job.index);

        for (int i = 0; i < job.data.length; i++) {
            job.data[i] += 1.111111f;
            restCheck();
        }

        job.completed = true;
//        System.out.println("Finished job: " + job.index);

    }

    private void restCheck() {
        numSinceRest++;
        if (numSinceRest == numPerRest) {
            numSinceRest = 0;

            float percentage = manager.getPerWorkerCPUUsage();
            if (percentage >= 1.0f) {
                // never sleep
                return;
            }



            long currentTime = System.currentTimeMillis();

            long runTime = currentTime - lastRest;

            long sleepTime = (long) ((1f-percentage)*runTime/percentage);

//            System.out.println("Run time: " + runTime + " Sleep time: " + sleepTime);

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            lastRest = System.currentTimeMillis();

        }
    }


}

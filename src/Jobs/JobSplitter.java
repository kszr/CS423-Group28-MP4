package Jobs;

import java.util.Iterator;

/**
 * Created by Matthew on 4/26/2015.
 */
public class JobSplitter implements Iterable<Job>{

    private int elementCount;
    private int jobCount;

    public JobSplitter(int elementCount, int jobCount) {
        this.elementCount = elementCount;
        this.jobCount = jobCount;
    }

    public Job getJob(int i) {

        Job job = new Job();

        job.index = i;
        job.completed = false;

        int numElements = (elementCount / jobCount) + ((elementCount % jobCount) > i ? 1 : 0);

        job.data = new float[numElements];
        for (int j = 0; j < numElements; j++) {
            job.data[j] = 1.111111f;
        }

        return job;
    }


    @Override
    public Iterator<Job> iterator() {

        return new Iterator<Job>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < jobCount;
            }

            @Override
            public Job next() {
                return getJob(i++);
            }
        };

    }


    public static void main(String[] args) {

        JobSplitter splitter = new JobSplitter(9, 4);

        for (Job job : splitter) {
            System.out.println(job.toString());
        }


    }
}

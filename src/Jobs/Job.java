package Jobs;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Matthew on 4/25/2015.
 */
public class Job implements Serializable{

    public boolean completed;

    /**
     * Where this job needs to be stored at.
     */
    public int index;
    public float[] data;

    @Override
    public String toString() {
        return "Jobs.Job{" +
                "completed=" + completed +
                ", index=" + index +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}

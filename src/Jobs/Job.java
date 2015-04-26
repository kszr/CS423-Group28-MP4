package Jobs;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Matthew on 4/25/2015.
 */
public class Job implements Serializable{

    public boolean completed;
    public int length;
    public float[] data;

    @Override
    public String toString() {
        return "Jobs.Job{" +
                "completed=" + completed +
                ", length=" + length +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}

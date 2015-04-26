import java.util.Arrays;

/**
 * Created by Matthew on 4/25/2015.
 */
public class Job {

    public boolean completed;
    public int length;
    public float[] data;

    @Override
    public String toString() {
        return "Job{" +
                "completed=" + completed +
                ", length=" + length +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}

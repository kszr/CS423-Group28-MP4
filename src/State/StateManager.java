package State;

import Hardware.HardwareInfo;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by abhishekchatterjee on 4/26/15.
 * Sends the current State of the system to the remote node.
 * How often it does this is decided by the information policy,
 * which is "never".
 */
public class StateManager {
    private State currState;
    private int portNumber;

    /**
     * Needs to be initialized with the port number for the remote node.
     * Initializes a State object to keep track of the state of the system.
     * @param portNumber
     */
    public StateManager(int portNumber) {
        currState = new State();
        this.portNumber = portNumber;
    }

    /**
     * Needs to be supplied with:
     *      - jobcount by whatever is handling jobs,
     *      - hardwareInfo by HardwareMonitor
     *      - throttle by hardwareMonitor
     * This is intended to be operated by the adapter (WorkerManager).
     * @param jobcount
     * @param throttle
     * @param hardwareInfo
     */
    public void updateState(int jobcount, double throttle, HardwareInfo hardwareInfo) {
        currState.jobCount = jobcount;
        currState.throttle = throttle;
        currState.hardwareInfo = hardwareInfo;
    }

    /**
     * Send the current state of the system to the remote node.
     */
    private void sendState() {
        try (ServerSocket listener = new ServerSocket(portNumber)) {

            while (true) {
                talk(listener);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Does things.
     * @param listener
     */
    private void talk(ServerSocket listener) {
        try (Socket socket = listener.accept();
             ObjectOutputStream objStream = new ObjectOutputStream(socket.getOutputStream())) {

            sendState(objStream);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Does things.
     * @param stream
     * @throws IOException
     */
    private void sendState(ObjectOutputStream stream) throws IOException {
        stream.writeObject(currState);
    }
}

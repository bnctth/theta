package hu.bme.mit.theta.xta.local_analysis;

import java.util.Collection;

public interface ProcessIdProvider {
    int newIdForBasicAction(int locProcess, int previousId);

    int newIdForBinaryAction(int emitProcess, int recvProcess, int previousId);

    int newIdForBroadcastAction(int emitProcess, Collection<Integer> recvProcesses, int systemProcessNumber, int previousId);

}

import java.util.LinkedList;
import java.util.Queue;

public class SchedulerFCFS extends SchedulerBase implements Scheduler {

    private Platform platform;
    private LinkedList<Process> processes = new LinkedList<Process>();

    public SchedulerFCFS(Platform platform) {
        this.platform = platform;
    }
    @Override
    public int getNumberOfContextSwitches() {
        return contextSwitches;
    }

    @Override
    public void notifyNewProcess(Process p) {
        processes.add(p);
        update(p);
    }

    @Override
    public Process update(Process cpu) {
        if (cpu == null) {
            cpu = processes.pop();
            platform.log("Scheduled: "+cpu.getName());
            contextSwitches++;
        }
        if (cpu.getRemainingBurst() != 0) {
            return cpu;
        } else {
            platform.log("Process " + cpu.getName() + " burst complete");
            contextSwitches ++;
            if (cpu.isExecutionComplete()) {
                platform.log("Process " + cpu.getName() + " execution complete");
            } else {
                processes.add(cpu);
            }
            if (!processes.isEmpty()) {
                Process nextProcess = processes.pop();
                platform.log("Scheduled: " + nextProcess.getName());
                contextSwitches ++;
                return nextProcess;
            }
            return null;
        }
    }
}

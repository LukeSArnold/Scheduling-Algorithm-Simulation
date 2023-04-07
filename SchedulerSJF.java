import java.util.Comparator;
import java.util.PriorityQueue;

public class SchedulerSJF extends SchedulerBase implements Scheduler {

    PriorityQueue<Process> processes = new PriorityQueue<>(new SortByTime());
    Platform platform;
    public SchedulerSJF(Platform platform) {
        this.platform = platform;
    }

    @Override
    public int getNumberOfContextSwitches() {
        return contextSwitches;
    }

    @Override
    public void notifyNewProcess(Process p) {
        processes.add(p);
    }

    @Override
    public Process update(Process cpu) {
        if (cpu == null) {
            cpu = processes.remove();
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
                Process nextProcess = processes.remove();
                platform.log("Scheduled: " + nextProcess.getName());
                contextSwitches ++;
                return nextProcess;
            }
            return null;
        }
    }
}

class SortByTime implements Comparator<Process> {

    @Override
    public int compare(Process p1, Process p2) {
        return Integer.compare(p1.getBurstTime(), p2.getBurstTime());
    }
}

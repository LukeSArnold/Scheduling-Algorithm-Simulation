import java.util.Comparator;
import java.util.PriorityQueue;

public class SchedulerSRTF extends SchedulerBase implements Scheduler {

    PriorityQueue<Process> processes = new PriorityQueue<>(new SortByTimeRemaining());
    Platform platform;
    public SchedulerSRTF(Platform platform) {
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
        if (processes.peek()!=null) {
            if (cpu.getRemainingBurst() > processes.peek().getRemainingBurst()) {
                platform.log("Preemptively removed: " + cpu.getName());
                contextSwitches++;
                processes.add(cpu);
                platform.log("Scheduled: "+processes.peek().getName());
                contextSwitches++;
                return processes.remove();
            }
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

class SortByTimeRemaining implements Comparator<Process> {

    @Override
    public int compare(Process p1, Process p2) {
        return Integer.compare(p1.getRemainingBurst(), p2.getRemainingBurst());
    }
}

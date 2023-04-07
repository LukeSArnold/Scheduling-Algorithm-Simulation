import java.util.LinkedList;

public class SchedulerRR extends SchedulerBase implements Scheduler {
    private Platform platform;
    private int timeQuantum;
    private int quantumElapsed;
    private LinkedList<Process> processes = new LinkedList<Process>();

    public SchedulerRR(Platform platform, int i) {
        this.platform = platform;
        this.timeQuantum = i;
    }
    @Override
    public int getNumberOfContextSwitches() {
        return contextSwitches;
    }

    @Override
    public void notifyNewProcess(Process p) {
        processes.add(p);
        contextSwitches++;
        update(p);
    }

    @Override
    public Process update(Process cpu) {
        if (cpu == null) {
            cpu = processes.pop();
            platform.log("Scheduled: "+cpu.getName());
            contextSwitches++;
        }
        if (cpu.isExecutionComplete()) {
            platform.log("Process " + cpu.getName() + " execution complete");
            quantumElapsed = 0;
            if (!processes.isEmpty()) {
                Process nextProcess = processes.pop();
                platform.log("Scheduled: " + nextProcess.getName());
                contextSwitches++;
                return nextProcess;
            } else {
                return null;
            }
        }
        if ((cpu.getElapsedBurst() != 0) && (cpu.getElapsedBurst() % timeQuantum == 0)){
            platform.log("Time quantum complete for "+cpu.getName());
            quantumElapsed = 0;
            contextSwitches++;
            processes.add(cpu);
            Process nextProcess = processes.pop();
            platform.log("Scheduled: " + nextProcess.getName());
            contextSwitches ++;
            return nextProcess;
        }
        return cpu;

    }
}

import java.util.*;

/*
 * Unique Concept:
 * Distributed Event Causality Detection using Vector Clocks
 * No external libraries used.
 */

class VectorClock {
    private final int[] clock;

    public VectorClock(int size) {
        this.clock = new int[size];
    }

    public void tick(int processId) {
        clock[processId]++;
    }

    public void merge(VectorClock other) {
        for (int i = 0; i < clock.length; i++) {
            clock[i] = Math.max(clock[i], other.clock[i]);
        }
    }

    public int[] snapshot() {
        return Arrays.copyOf(clock, clock.length);
    }

    public boolean happensBefore(VectorClock other) {
        boolean strictlyLess = false;
        for (int i = 0; i < clock.length; i++) {
            if (this.clock[i] > other.clock[i]) return false;
            if (this.clock[i] < other.clock[i]) strictlyLess = true;
        }
        return strictlyLess;
    }

    @Override
    public String toString() {
        return Arrays.toString(clock);
    }
}

class DistributedEvent {
    String name;
    int processId;
    VectorClock timestamp;

    public DistributedEvent(String name, int processId, VectorClock clock) {
        this.name = name;
        this.processId = processId;
        this.timestamp = clock;
    }
}

public class DistributedCausalityAnalyzer {

    public static void analyze(DistributedEvent a, DistributedEvent b) {
        System.out.println("\nComparing Events: " + a.name + " & " + b.name);

        if (a.timestamp.happensBefore(b.timestamp)) {
            System.out.println(a.name + " → happens BEFORE → " + b.name);
        } else if (b.timestamp.happensBefore(a.timestamp)) {
            System.out.println(b.name + " → happens BEFORE → " + a.name);
        } else {
            System.out.println("⚡ Events are CONCURRENT (No causal relationship)");
        }
    }

    public static void main(String[] args) {
        int processes = 3;

        VectorClock vc1 = new VectorClock(processes);
        VectorClock vc2 = new VectorClock(processes);

        vc1.tick(0); // P0 event
        DistributedEvent e1 = new DistributedEvent("E1", 0, vc1);

        vc2.tick(1); // P1 event
        DistributedEvent e2 = new DistributedEvent("E2", 1, vc2);

        vc1.merge(vc2);
        vc1.tick(0); // Communication event
        DistributedEvent e3 = new DistributedEvent("E3", 0, vc1);

        System.out.println("Event Timestamps:");
        System.out.println("E1: " + e1.timestamp);
        System.out.println("E2: " + e2.timestamp);
        System.out.println("E3: " + e3.timestamp);

        analyze(e1, e2);
        analyze(e2, e3);
        analyze(e1, e3);
    }
}
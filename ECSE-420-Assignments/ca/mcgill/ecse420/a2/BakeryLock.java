package ca.mcgill.ecse420.a2;


import java.util.concurrent.atomic.AtomicIntegerArray;

public class BakeryLock {
    private AtomicIntegerArray flag;
    private AtomicIntegerArray label;
    private int n;

    public BakeryLock(int n) {
        this.n = n;
        flag = new AtomicIntegerArray(n);
        label = new AtomicIntegerArray(n);
    }
    public void lock(int id) {
        flag.set(id, 1);
        int max = 0;
        for (int i = 0; i < n; i++) {
            max = Math.max(max, label.get(i));
        }
        label.set(id, max + 1);

        for (int k = 0; k < n; k++) {
            if (k == id) continue;
            while (flag.get(k) == 1 && (label.get(k) < label.get(id) || (label.get(k) == label.get(id) && k < id))) {
                // Wait
            }
        }
    }

    public void unlock(int id) {
        flag.set(id, 0);
    }
}

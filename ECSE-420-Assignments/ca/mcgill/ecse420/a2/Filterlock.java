package ca.mcgill.ecse420.a2;

import java.util.concurrent.atomic.AtomicIntegerArray;

public class Filterlock {

    private final int n; // num threads
    private final AtomicIntegerArray level;
    private final AtomicIntegerArray victim;
    
    public Filterlock(int n) {
        this.n = n;
        this.level = new AtomicIntegerArray(n);
        this.victim = new AtomicIntegerArray(n);
    }


    public void lock(int me) {
        // Loop through all waiting rooms (1 to n-1)
        for (int L = 1; L < n; L++) {  
            level.set(me, L);      // Check into the room
            victim.set(L, me);     
        
            // The Spin Loop: Check every other thread
            for (int k = 0; k < n; k++) {
                while ((k != me) && (level.get(k) >= L) && (victim.get(L) == me)) {
                    //wait
                }
            }
        }
    }
     public void unlock(int me) {
        level.set(me, 0);
     }
    

    }
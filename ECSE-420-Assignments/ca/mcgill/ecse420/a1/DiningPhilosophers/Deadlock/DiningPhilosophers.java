package ca.mcgill.ecse420.a1.diningPhilosophers.deadlock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophers {
	
	public static void main(String[] args) {

		int numberOfPhilosophers = 5;
		Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
		Object[] chopsticks = new Object[numberOfPhilosophers];

		for (int i = 0; i < numberOfPhilosophers; i++) {
			chopsticks[i] = new Object();
		}

		for (int i = 0; i < numberOfPhilosophers; i++) {
			philosophers[i] = new Philosopher(i, chopsticks[i], chopsticks[(i + 1) % numberOfPhilosophers]);
		}

		ExecutorService executor = Executors.newFixedThreadPool(numberOfPhilosophers);

        for (int i = 0; i < numberOfPhilosophers; i++) {
            executor.execute(philosophers[i]);
        }

	}

	public static class Philosopher implements Runnable {

		private int id;
		private Object leftChopstick;
		private Object rightChopstick;
		private int count;

		public Philosopher(int id, Object left, Object right) {
			this.id = id;
			this.leftChopstick = left;
			this.rightChopstick = right;
			this.count = 0;
		}

		@Override
		public void run() {
			while (true) {
				try {
					think();
					synchronized (leftChopstick) {
						synchronized (rightChopstick) {
							eat();
						}
					}
				} catch (InterruptedException e) {
            		Thread.currentThread().interrupt();
            		break;
				}
			}
		}

		private void think() throws InterruptedException {
            Thread.sleep((int) (Math.random() * 10));
        }

        private void eat() throws InterruptedException {
			count++;
			System.out.println("Philosopher " + id + " ate " + count + " times.");
            Thread.sleep((int) (Math.random() * 10));
        }
	}

}

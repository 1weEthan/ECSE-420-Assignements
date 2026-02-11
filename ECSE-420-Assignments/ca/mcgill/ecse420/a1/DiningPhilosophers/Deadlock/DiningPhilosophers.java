package ca.mcgill.ecse420.a1;

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
			philosophers[i] = new Philosopher(chopsticks[i], chopsticks[(i + 1) % numberOfPhilosophers]);
		}

		ExecutorService executor = Executors.newFixedThreadPool(numberOfPhilosophers);

        for (int i = 0; i < numberOfPhilosophers; i++) {
            executor.execute(philosophers[i]);
        }

	}

	public static class Philosopher implements Runnable {

		private Object leftChopstick;
		private Object rightChopstick;

		public Philosopher(Object left, Object right) {
			this.leftChopstick = left;
			this.rightChopstick = right;
		}

		@Override
		public void run() {
			while (true) {
				try {
					think();
					synchronized (leftChopstick) {
						synchronized (rightChopstick) {
							eat()
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
            Thread.sleep((int) (Math.random() * 10));
        }
	}

}

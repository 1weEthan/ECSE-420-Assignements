package ca.mcgill.ecse420.a1.diningPhilosophers.noStarvation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophers {
	
	public static void main(String[] args) {

		int numberOfPhilosophers = 5;
		Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
		ReentrantLock[] chopsticks = new ReentrantLock[numberOfPhilosophers];

		for (int i = 0; i < numberOfPhilosophers; i++) {
			chopsticks[i] = new ReentrantLock(true);
		}

		for (int i = 0; i < numberOfPhilosophers; i++) {
			if (i % 2 == 0) {
				philosophers[i] = new Philosopher(i, chopsticks[i], chopsticks[(i + 1) % numberOfPhilosophers]);
			} else {
				philosophers[i] = new Philosopher(i, chopsticks[(i + 1) % numberOfPhilosophers], chopsticks[i]);
			}
		}

		ExecutorService executor = Executors.newFixedThreadPool(numberOfPhilosophers);

        for (int i = 0; i < numberOfPhilosophers; i++) {
            executor.execute(philosophers[i]);
        }

	}

	public static class Philosopher implements Runnable {

		private ReentrantLock firstChopstick;
		private ReentrantLock secondChopstick;
		private int count;
		private int id;

		public Philosopher(int id, ReentrantLock first, ReentrantLock second) {
			this.id = id;
			this.firstChopstick = first;
			this.secondChopstick = second;
			this.count = 0;
		}

		@Override
		public void run() {
			while (true) {
				try {
					think();
					firstChopstick.lock();
					try {
						secondChopstick.lock();
						try {
							eat();
						} finally {
							secondChopstick.unlock();
						}
					} finally {
						firstChopstick.unlock();
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

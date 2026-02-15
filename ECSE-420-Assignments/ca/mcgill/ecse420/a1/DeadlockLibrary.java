package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.*;

public class DeadlockLibrary {

    public static void main(String[] args) {
        Library lib = new Library();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(() -> {
            Thread.currentThread().setName("Borrower1");
            lib.borrowBookandManga();
        });

        // grab in opposite order
        executor.execute(() -> {
            Thread.currentThread().setName("Borrower2");
            lib.borrowMangaandBook();
        });

        executor.shutdown();

        // Wait until all tasks are finished
        while (!executor.isTerminated()) {
        }

    }
    private static class Library {
        //fighting for these 2 ressources (locks)
        private static final Lock bookLock = new ReentrantLock();
        private static final Lock mangaLock = new ReentrantLock();


        public void borrowBookandManga() {
            bookLock.lock(); // Acquire the lock
            System.out.println(Thread.currentThread().getName() + ": Locked Book");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
            mangaLock.lock(); // try to grab mangaLock (DEADLOCK HERE)
            System.out.println(Thread.currentThread().getName() + ": Locked Manga");

            //reading time
            mangaLock.unlock();
            bookLock.unlock(); // return the book

        }
        
        public void borrowMangaandBook() {
            mangaLock.lock(); // Acquire the lock
            System.out.println(Thread.currentThread().getName() + ": Locked Manga");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
            bookLock.lock(); // try to grab bookLock (DEADLOCK HERE)
            System.out.println(Thread.currentThread().getName() + ": Locked Book");

            //reading time
            mangaLock.unlock(); //return the manga
            bookLock.unlock();
        }

    }


}
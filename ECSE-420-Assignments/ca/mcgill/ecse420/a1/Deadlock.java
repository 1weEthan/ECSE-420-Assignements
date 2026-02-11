import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
            mangaLock.lock(); // try to grab mangaLock

            //reading time (DEADLOCK HERE)
            mangaLock.unlock();
            bookLock.unlock();

        }
        public void borrowMangaandBook() {
            mangaLock.lock(); // Acquire the lock
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
            bookLock.lock(); // try to grab bookLock

            //reading time (DEADLOCK HERE)
            mangaLock.unlock();
            bookLock.unlock();
        }

    }


}
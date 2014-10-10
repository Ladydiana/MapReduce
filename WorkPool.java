
import java.util.LinkedList;

public class WorkPool { //Clasa oferita in resursele de la lab 5

    int nThreads;
    int nWaiting = 0;
    public boolean ready = false; 
    public String name;
    LinkedList<Task> tasks = new LinkedList<>();

    public WorkPool(int nThreads, String name) {
        this.nThreads = nThreads;
        this.name = name;
    }

    public synchronized Task getWork() throws InterruptedException {
        if (tasks.size() == 0) {
            nWaiting++;
            if (nWaiting == nThreads) {
                ready = true;
                notifyAll();
                return null;
            } else {
                while (!ready && tasks.size() == 0) {
                    this.wait();
                }
                if (ready) {
                    return null;
                }
                nWaiting--;
            }
        }
        return tasks.remove();
    }

    synchronized void putWork(Task sp) {
        tasks.add(sp);
        this.notify();
    }
}

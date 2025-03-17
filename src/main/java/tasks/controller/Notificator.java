package tasks.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import org.apache.log4j.Logger;
import org.controlsfx.control.Notifications;
import tasks.model.Task;

import java.util.Date;

public class Notificator extends Thread {

    private static final int millisecondsInSec = 1000;
    private static final int secondsInMin = 60;

    private static final Logger log = Logger.getLogger(Notificator.class.getName());

    private ObservableList<Task> tasksList;

    public Notificator(ObservableList<Task> tasksList){
        this.tasksList=tasksList;
    }
    private volatile boolean running = true; // Variabilă de control

    @Override
    public void run() {
        Date currentDate = new Date();
        while (running) {
            for (Task t : tasksList) {
                if (t.isActive()) {
                    Date next = t.nextTimeAfter(currentDate);
                    if (next != null && getTimeInMinutes(currentDate) == getTimeInMinutes(next)) {
                        showNotification(t);
                    }
                }
            }
            try {
                Thread.sleep(millisecondsInSec * secondsInMin);
            } catch (InterruptedException e) {
                log.error("Thread interrupted", e);
                running = false; // Corecție: Setează `running` pe `false` pentru oprire
            }
            currentDate = new Date();
        }
    }

    // Metodă pentru oprirea thread-ului
    public void stopNotifier() {
        running = false;
    }

    public static void showNotification(Task task){
        log.info("push notification showing");
        Platform.runLater(() -> {
            Notifications.create().title("Task reminder").text("It's time for " + task.getTitle()).showInformation();
        });
    }
    private static long getTimeInMinutes(Date date){
        return date.getTime()/millisecondsInSec/secondsInMin;
    }
}

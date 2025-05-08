package tasks.integration;

import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tasks.model.ArrayTaskList;
import tasks.model.Task;
import tasks.model.TaskList;
import tasks.services.TasksService;
import java.util.Date;
import java.util.Iterator;
import java.util.Calendar;
import static org.junit.jupiter.api.Assertions.*;

public class TaskIntegrationTest {

    private Task task1;
    private Task task2;
    private TasksService tasksService;
    private TaskList taskList;
    private Date currentDate;

    @BeforeEach
    public void setUp() {
        taskList = new ArrayTaskList();
        currentDate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        task1 = new Task("Task 1", currentDate);
        task1.setActive(true);

        calendar.add(Calendar.HOUR, 2);
        Date startDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date endDate = calendar.getTime();
        task2 = new Task("Task 2", startDate, endDate, 3600);
        task2.setActive(true);

        taskList.add(task1);
        taskList.add(task2);

        tasksService = new TasksService(taskList);
    }

    @Test
    @DisplayName("TC1: Observable list test")
    public void observableListTest() {
        ObservableList<Task> result = tasksService.getObservableList();
        assertEquals(2, result.size());

        assertEquals("Task 1", result.get(0).getTitle());
        assertEquals(currentDate, result.get(0).getTime());

        assertEquals("Task 2", result.get(1).getTitle());
        assertTrue(result.get(1).isRepeated());
        assertEquals(3600, result.get(1).getRepeatInterval());
    }

    @Test
    @DisplayName("TC2: Interval hours feature test")
    public void intervalHoursTest() {
        String interval1 = tasksService.getIntervalInHours(task1);
        String interval2 = tasksService.getIntervalInHours(task2);

        assertEquals("00:00", interval1); // taskul nu se repeta => 0
        assertEquals("01:00", interval2); // task-ul se repeta la o ora
    }

    @Test
    @DisplayName("TC3: Filter tasks test")
    public void filterTaskTest() {
        Date end = new Date(currentDate.getTime() + 1000 * 3600 * 24); // 24 hours later

        Iterable<Task> filteredTasks = tasksService.filterTasks(currentDate, end);
        assertNotNull(filteredTasks);
        assertTrue(filteredTasks.iterator().hasNext());
    }

    @Test
    @DisplayName("TC4: Parse string test")
    public void parseStringTest() {
        int seconds1 = tasksService.parseFromStringToSeconds("01:30");
        int seconds2 = tasksService.parseFromStringToSeconds("00:45");

        assertEquals(5400, seconds1);
        assertEquals(2700, seconds2);

        task2.setTime(task2.getStartTime(), task2.getEndTime(), seconds1);
        assertEquals(5400, task2.getRepeatInterval());
    }

    @Test
    @DisplayName("TC5: Task modification test")
    public void modificationsTest() {
        String newTimeString = "02:15"; // 2h 15m
        int newIntervalSeconds = tasksService.parseFromStringToSeconds(newTimeString);

        task2.setTime(task2.getStartTime(), task2.getEndTime(), newIntervalSeconds);
        ObservableList<Task> updatedTasks = tasksService.getObservableList();
        Task updatedTask = updatedTasks.get(1);

        assertEquals(8100, updatedTask.getRepeatInterval()); // 2h 15m = 135m = 8100s
        assertEquals("02:15", tasksService.getIntervalInHours(updatedTask));
        assertTrue(updatedTask.isRepeated());
    }
}
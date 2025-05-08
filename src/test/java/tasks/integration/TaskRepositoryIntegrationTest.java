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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskRepositoryIntegrationTest {

    private Task task1;
    private Task task2;
    private TasksService tasksService;
    private TaskList taskList;

    @BeforeEach
    public void setUp() {
        taskList = new ArrayTaskList();

        // Configure mock objects
        task1 = mock(Task.class);
        task2 = mock(Task.class);

        when(task1.getTitle()).thenReturn("Task 1");
        when(task1.getRepeatInterval()).thenReturn(3600);
        when(task2.getTitle()).thenReturn("Task 2");
        when(task2.getRepeatInterval()).thenReturn(7200);

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
        assertEquals("Task 2", result.get(1).getTitle());
    }

    @Test
    @DisplayName("TC2: Interval hours feature test")
    public void intervalHoursTest() {
        String result = tasksService.getIntervalInHours(task1);
        assertEquals("01:00", result);

        result = tasksService.getIntervalInHours(task2);
        assertEquals("02:00", result);
    }

    @Test
    @DisplayName("TC3: Filter tasks test")
    void filterTasksTest() {
        when(task1.nextTimeAfter(any())).thenReturn(new Date());
        Date start = new Date();
        Date end = new Date(start.getTime() + 1000 * 3600 * 24); // 24 hours later

        Iterable<Task> filteredTasks = tasksService.filterTasks(start, end);
        assertNotNull(filteredTasks);
        assertTrue(filteredTasks.iterator().hasNext());
        assertEquals(task1, filteredTasks.iterator().next());

        verify(task1, atLeastOnce()).nextTimeAfter(any(Date.class));
    }

    @Test
    @DisplayName("TC4: Parse from string test")
    public void parseFromStringTest() {
        assertEquals(3600, tasksService.parseFromStringToSeconds("01:00")); // 1 hour
        assertEquals(5400, tasksService.parseFromStringToSeconds("01:30")); // 1.5 hours
    }

    @Test
    @DisplayName("TC5: Form time unit test")
    public void formTimeUnitTest() {
        assertEquals("05", tasksService.formTimeUnit(5));
        assertEquals("15", tasksService.formTimeUnit(15));
        assertEquals("00", tasksService.formTimeUnit(0));
    }
}
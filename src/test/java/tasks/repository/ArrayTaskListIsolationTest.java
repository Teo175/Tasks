package tasks.repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import tasks.model.ArrayTaskList;
import tasks.model.Task;
import java.util.Iterator;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;

public class ArrayTaskListIsolationTest {

    private ArrayTaskList taskList;

    @Mock
    private Task mockTask1;

    @Mock
    private Task mockTask2;

    @Mock
    private Task mockTask3;

    @BeforeEach
    public void setUp() {
        initMocks(this);
        taskList = new ArrayTaskList();
    }


    @Test
    @DisplayName("TC1: Basic add operations")
    public void listOperationsTest() {
        assertEquals(0, taskList.size());

        taskList.add(mockTask1);
        taskList.add(mockTask2);

        assertEquals(2, taskList.size());

        assertSame(mockTask1, taskList.getTask(0));
        assertSame(mockTask2, taskList.getTask(1));
    }

    @Test
    @DisplayName("TC2: Remove operations")
    public void removeOperationsTest() {
        taskList.add(mockTask1);
        taskList.add(mockTask2);
        taskList.add(mockTask3);

        boolean result = taskList.remove(mockTask2);
        assertTrue(result);
        assertEquals(2, taskList.size());

        assertSame(mockTask1, taskList.getTask(0));
        assertSame(mockTask3, taskList.getTask(1));

        Task nonExistentTask = mock(Task.class);
        result = taskList.remove(nonExistentTask);
        assertFalse(result);
        assertEquals(2, taskList.size());
    }

    @Test
    @DisplayName("TC3: Testing array expansion")
    public void testArrayExpansion() {
        Task[] mockTasks = new Task[15];
        for (int i = 0; i < mockTasks.length; i++) {
            mockTasks[i] = mock(Task.class);
        }

        for (Task task : mockTasks) {
            taskList.add(task);
        }

        assertEquals(mockTasks.length, taskList.size());
        for (int i = 0; i < mockTasks.length; i++) {
            assertSame(mockTasks[i], taskList.getTask(i));
        }
    }
}
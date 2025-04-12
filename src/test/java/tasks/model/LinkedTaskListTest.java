package tasks.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.text.ParseException;
import static org.junit.jupiter.api.Assertions.*;


class LinkedTaskListTest {
    private LinkedTaskList taskList;
    private Task taskA;
    private Task taskB;
    private Task taskC;

    @BeforeEach
    void setUp() {
        taskList = new LinkedTaskList();
        try{
            taskA = new Task("A",Task.getDateFormat().parse("2020-01-01 10:10"));
            taskB = new Task("B",Task.getDateFormat().parse("2020-01-01 10:10"));
            taskC = new Task("C",Task.getDateFormat().parse("2020-01-01 10:10"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("TC1: Null task")
    void removeNullTask() {
        taskList.add(taskA);

        assertThrows(NullPointerException.class, () -> {
            taskList.remove(null);
        }, "Null exception expected");
        assert(taskList.size() == 1);
    }

    @Test
    @DisplayName("TC2: Empty list")
    void removeFromEmptyList() {
        boolean result = taskList.remove(taskA);
        assert(!result);
        assert(taskList.size() == 0);
    }

    @Test
    @DisplayName("TC3: Remove task from single element list")
    void removeTaskFromSingleElementList() {
        taskList.add(taskA);
        boolean result = taskList.remove(taskA);

        assert(result);
        assert(taskList.size() == 0);
    }

    @Test
    @DisplayName("TC4: Remove single element, task not in list")
    void removeTaskNotInList() {
        taskList.add(taskA);
        boolean result = taskList.remove(taskB);

        assert(!result);
        assert(taskList.size() == 1);
    }

    @Test
    @DisplayName("TC5: Remove tail")
    void removeTail() {
        taskList.add(taskA);
        taskList.add(taskB);
        taskList.add(taskC);

        boolean result = taskList.remove(taskC);
        assert(result);
        assert(taskList.size() == 2);
        assert(taskList.getTask(0) == taskA);
        assert(taskList.getTask(1) == taskB);
    }

    @Test
    @DisplayName("TC6: Remove head")
    void removeHead() {
        taskList.add(taskA);
        taskList.add(taskB);
        taskList.add(taskC);

        boolean result = taskList.remove(taskA);
        assert(result);
        assert(taskList.size() == 2);
        assert(taskList.getTask(0) == taskB);
        assert(taskList.getTask(1) == taskC);
    }

    @Test
    @DisplayName("TC7: Remove element")
    void removeElement() {
        taskList.add(taskA);
        taskList.add(taskB);
        taskList.add(taskC);

        boolean result = taskList.remove(taskB);
        assert(result);
        assert(taskList.size() == 2);
        assert(taskList.getTask(0) == taskA);
        assert(taskList.getTask(1) == taskC);
    }

    @Test
    @DisplayName("TC8: Remove element that appears twice")
    void removeElementThatAppearsTwice() {
        taskList.add(taskB);
        taskList.add(taskA);
        taskList.add(taskA);

        boolean result = taskList.remove(taskA);
        assert(result);
        assert(taskList.size() == 2);
        assert(taskList.getTask(0) == taskB);
        assert(taskList.getTask(1) == taskA);
    }
}
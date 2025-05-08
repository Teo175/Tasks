package tasks.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import tasks.services.TaskIO;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

public class TaskIsolationTest {

    private SimpleDateFormat dateFormat;
    private Date startDate;
    private Date endDate;
    private Task task1;
    private Task task2;

    @BeforeEach
    public void setUp() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.MAY, 1, 12, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        startDate = cal.getTime();

        cal.add(Calendar.DAY_OF_MONTH, 7);
        endDate = cal.getTime();

        task1 = new Task("Simple Task", startDate);
        task2 = new Task("Repeated Task", startDate, endDate, 60);
    }

    @Test
    @DisplayName("TC1: Constructor and getters test")
    public void constructorTest() {
        assert("Simple Task".equals(task1.getTitle()));
        assert(task1.getTime() == startDate);
        assert(!task1.isActive());
        assert(!task1.isRepeated());
        assert(task1.getRepeatInterval() == 0);

        // task2
        assert(startDate == task2.getStartTime());
        assert(endDate == task2.getEndTime());
        assert(task2.getRepeatInterval() == 60);
        assert(task2.isRepeated());

        // invalid
        assertThrows(IllegalArgumentException.class, () -> new Task("Invalid Task", new Date(-1)));
        assertThrows(IllegalArgumentException.class, () -> new Task("Invalid Task", startDate, endDate, 0));
    }

    @Test
    @DisplayName("TC2: Setters test")
    public void settersTest() {
        task1.setTitle("New Title");
        assert("New Title".equals(task1.getTitle()));

        assert(!task1.isActive());
        task1.setActive(true);
        assert(task1.isActive());

        Date newTime = new Date(startDate.getTime() + 100000);
        task1.setTime(newTime);
        assert(task1.getTime() == newTime);
        assert(!task1.isRepeated());
    }

    @Test
    @DisplayName("TC3: Testing nextTimeAfter function")
    public void nextTimeAfterTest() {
        task1.setActive(true);
        task2.setActive(true);

        // task1
        Date beforeStart = new Date(startDate.getTime() - 1000);

        assert(task1.nextTimeAfter(beforeStart) == startDate);
        assert(task1.nextTimeAfter(startDate) == null);
        assert(task1.nextTimeAfter(new Date(startDate.getTime() + 1000)) == null);

        task1.setActive(false);
        assert(task1.nextTimeAfter(beforeStart) == null);

        // task2
        assert(task2.nextTimeAfter(beforeStart) == startDate);

        Date expectedNext = new Date(startDate.getTime() + 60 * 1000);
        assert(task2.nextTimeAfter(startDate).equals(expectedNext));

        assert(task2.nextTimeAfter(endDate) == null);
        assert(task2.nextTimeAfter(new Date(endDate.getTime() + 1000)) == null);
    }

    @Test
    @DisplayName("TC4: Formatted output functionality")
    public void formattedOutputTest() {
        assert(dateFormat.format(startDate).equals(task1.getFormattedDateStart()));
        assert(dateFormat.format(startDate).equals(task1.getFormattedDateEnd()));
        assert(dateFormat.format(endDate).equals(task2.getFormattedDateEnd()));
        assert("No".equals(task1.getFormattedRepeated()));
    }
}
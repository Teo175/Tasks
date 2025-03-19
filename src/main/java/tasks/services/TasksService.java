package tasks.services;
import org.apache.log4j.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tasks.model.ArrayTaskList;
import tasks.model.Task;
import tasks.validators.TaskValidator;

import java.util.Date;

public class TasksService {

    private ArrayTaskList tasks;

    private static final Logger log = Logger.getLogger(TasksService.class.getName());
    private ObservableList<Task> observableList;
    private TaskValidator validator;

    public TasksService(ArrayTaskList tasks){
        this.tasks = tasks;
        this.validator = new TaskValidator();
    }

    public ObservableList<Task> getObservableList(){
        //return FXCollections.observableArrayList(tasks.getAll());
        if (observableList == null) {
            observableList = FXCollections.observableArrayList(tasks.getAll());
        }
        return observableList;
    }
    public String getIntervalInHours(Task task){
        int seconds = task.getRepeatInterval();
        int minutes = seconds / DateService.SECONDS_IN_MINUTE;
        int hours = minutes / DateService.MINUTES_IN_HOUR;
        minutes = minutes % DateService.MINUTES_IN_HOUR;
        return formTimeUnit(hours) + ":" + formTimeUnit(minutes);//hh:MM
    }
    public String formTimeUnit(int timeUnit){
        StringBuilder sb = new StringBuilder();
        if (timeUnit < 10) sb.append("0");
        if (timeUnit == 0) sb.append("0");
        else {
            sb.append(timeUnit);
        }
        return sb.toString();
    }

    public int parseFromStringToSeconds(String stringTime){//hh:MM
        String[] units = stringTime.split(":");
        int hours = Integer.parseInt(units[0]);
        int minutes = Integer.parseInt(units[1]);
        int result = (hours * DateService.MINUTES_IN_HOUR + minutes) * DateService.SECONDS_IN_MINUTE;
        return result;
    }

    public Iterable<Task> filterTasks(Date start, Date end){
        TasksOperations tasksOps = new TasksOperations(getObservableList());
        Iterable<Task> filtered = tasksOps.incoming(start,end);
        //Iterable<Task> filtered = tasks.incoming(start, end);

        return filtered;
    }

    public void deleteTask(Task task) {
        try {
            if (task == null) {
                throw new IllegalArgumentException("Task cannot be null");
            }

            boolean removed = tasks.remove(task);
            if (!removed) {
                throw new IllegalArgumentException("Task not found in the list");
            }

            getObservableList().remove(task);
            TaskIO.rewriteFile(getObservableList());
        } catch (Exception e) {
            log.error("Error deleting task: " + e.getMessage(), e);
            throw new RuntimeException("Failed to delete task: " + e.getMessage(), e);
        }
    }

    public void updateTask(Task oldTask, Task newTask)  {
        try {
            validator.validateTask(newTask);

            boolean found = false;
            for (int i = 0; i < tasks.size(); i++) {
                if (oldTask.equals(tasks.getTask(i))) {
                    tasks.getAll().set(i, newTask);
                    found = true;
                    break;
                }
            }

            if (!found) {
                throw new IllegalArgumentException("Original task not found");
            }

            int obsIndex = getObservableList().indexOf(oldTask);
            if (obsIndex >= 0) {
                getObservableList().set(obsIndex, newTask);
            }

            TaskIO.rewriteFile(getObservableList());
        } catch (TaskValidator.ValidationException e) {
            log.error("Validation error when updating task: " + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error updating task: " + e.getMessage(), e);
            throw new RuntimeException("Failed to update task: " + e.getMessage(), e);
        }
    }

    public void addTask(Task task) {
        try {
            validator.validateTask(task);

            tasks.add(task);
            getObservableList().add(task);
            TaskIO.rewriteFile(getObservableList());
        } catch (TaskValidator.ValidationException e) {
            log.error("Validation error when adding task: " + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error adding task: " + e.getMessage(), e);
            throw new RuntimeException("Failed to add task: " + e.getMessage(), e);
        }
    }
}

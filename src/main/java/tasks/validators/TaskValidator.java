package tasks.validators;
import tasks.model.Task;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskValidator {
    public static class ValidationException extends Exception {
        private List<String> errors;

        public ValidationException(List<String> errors) {
            super("Validation failed: " + String.join(", ", errors));
            this.errors = errors;
        }

        public List<String> getErrors() {
            return errors;
        }
    }

    public void validateTask(Task task) throws ValidationException {
        List<String> errors = new ArrayList<>();

        // Valideaza titlul
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            errors.add("Title cannot be empty");
        }

        // Valideaza data de început
        if (task.getStartTime() == null) {
            errors.add("Start time cannot be null");
        }

        // Valideaza task-urile repetitive
        if (task.isRepeated()) {
            // Valideaza data de sfarșit
            if (task.getEndTime() == null) {
                errors.add("End time cannot be null for repeated tasks");
            } else if (task.getStartTime().after(task.getEndTime())) {
                errors.add("Start time cannot be after end time");
            }

            // Valideaza intervalul
            if (task.getRepeatInterval() <= 0) {
                errors.add("Interval must be positive for repeated tasks");
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
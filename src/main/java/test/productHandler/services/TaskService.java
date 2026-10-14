package test.productHandler.services;

import org.springframework.stereotype.Service;
import test.productHandler.entity.Task;
import test.productHandler.entity.TaskStatus;
import test.productHandler.exceptions.TaskNotFoundException;
import test.productHandler.repository.TaskRepository;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public UUID createNewTask(Task task) {
        if(task.getTitle() == null || task.getTitle().isBlank()){
            throw new IllegalArgumentException("Task title must not be empty");
        }
        taskRepository.save(task);
        return task.getId();
    }

    public void deleteTaskById(UUID taskId) {
        if(!taskRepository.existById(taskId)){
            throw  new TaskNotFoundException("Task with id: " + taskId + " not found");
        }

        taskRepository.deleteById(taskId);
    }

    public List<Task> getAllTasks() {
        return taskRepository.getAllTasks();
    }

    public Task getById(UUID taskId) {
        return taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Task with id " + taskId + " not found"));
    }

    public Void update(UUID taskId, TaskDataToUpdate taskDataToUpdate) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Task with id " + taskId + " not found"));
        task.setTitle(taskDataToUpdate.title);
        task.setStatus(taskDataToUpdate.status);
        taskRepository.save(task);

        return  null;
    }

    public record TaskDataToUpdate(String title, TaskStatus status){}
}

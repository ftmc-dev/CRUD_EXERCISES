package test.productHandler.repository;

import org.springframework.stereotype.Repository;
import test.productHandler.entity.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TaskRepository {

    private final Map<UUID, Task> tasks = new HashMap<>();

    public Task save(Task task) {
        tasks.put(task.getId(), task);
        return task;
    }

    public Optional<Task> findById(UUID taskId) {
        return Optional.ofNullable(tasks.get(taskId));
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteById(UUID taskId) {
        tasks.remove(taskId);
    }

    public boolean existById(UUID taskId) {
        return tasks.containsKey(taskId);
    }
}

package test.productHandler.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import test.productHandler.api.TaskApi;
import test.productHandler.dto.TaskCreateDTO;
import test.productHandler.dto.TaskCreationResponseDTO;
import test.productHandler.dto.TaskDTO;
import test.productHandler.dto.UpdateTaskRequestDTO;
import test.productHandler.entity.Task;
import test.productHandler.mappers.TaskMapper;
import test.productHandler.services.TaskService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class TaskController implements TaskApi {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @Override
    public ResponseEntity<TaskCreationResponseDTO> createNewTask(@Valid TaskCreateDTO taskCreateDTO) {
        UUID uuid = taskService.createNewTask(taskMapper.fromTaskCreateDTOToTask(taskCreateDTO));
        return ResponseEntity.status(201).body(new TaskCreationResponseDTO(uuid));
    }

    @Override
    public ResponseEntity<Void> deleteTaskById(UUID taskId) {
        taskService.deleteTaskById(taskId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        List<Task> result = taskService.getAllTasks();
        List<TaskDTO> taskDTO = result.stream()
                .map(taskMapper::fromTaskToTaskDTO)
                .collect(Collectors.toList());

        return ResponseEntity.status(200).body(taskDTO);
    }

    @Override
    public ResponseEntity<TaskDTO> getTaskById(UUID taskId) {
        Task task = taskService.getById(taskId);
        TaskDTO taskDTO = taskMapper.fromTaskToTaskDTO(task);
        return ResponseEntity.status(200).body(taskDTO);
    }

    @Override
    public ResponseEntity<Void> updateTask(UUID taskId, @NotNull UpdateTaskRequestDTO updateTaskRequestDTO) {
        var taskDataToUpdate = taskMapper.fromUpdateTaskRequestDTOToTaskData(updateTaskRequestDTO);
        taskService.update(taskId, taskDataToUpdate);
        return ResponseEntity.noContent().build();
    }
}

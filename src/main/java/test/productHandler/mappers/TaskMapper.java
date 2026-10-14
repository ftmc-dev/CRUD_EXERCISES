package test.productHandler.mappers;

import org.mapstruct.Mapper;
import test.productHandler.dto.TaskCreateDTO;
import test.productHandler.dto.TaskDTO;
import test.productHandler.dto.TaskStatusDTO;
import test.productHandler.dto.UpdateTaskRequestDTO;
import test.productHandler.entity.Task;
import test.productHandler.entity.TaskStatus;
import test.productHandler.services.TaskService;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    default Task fromTaskCreateDTOToTask(TaskCreateDTO taskCreateDTO) {
        return new Task(taskCreateDTO.getTitle());
    }

    default TaskService.TaskDataToUpdate fromUpdateTaskRequestDTOToTaskData(UpdateTaskRequestDTO updateTaskRequestDTO) {
        return new TaskService.TaskDataToUpdate(updateTaskRequestDTO.getTitle(), TaskStatus.valueOf(updateTaskRequestDTO.getStatus().name()));
    }

    default TaskDTO fromTaskToTaskDTO(Task task) {

        return new TaskDTO(task.getId(), task.getTitle(), TaskStatusDTO.valueOf(task.getStatus().name()));
    }

}

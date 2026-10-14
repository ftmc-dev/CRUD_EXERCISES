package test.productHandler.entity;

import lombok.Getter;
import lombok.Setter;
import test.productHandler.dto.TaskStatusDTO;

import java.util.UUID;

@Getter
@Setter
public class Task {
    private static UUID idGenerated = UUID.randomUUID();
    private final UUID id;
    private String title;
    private TaskStatus status;

    public Task(String title) {
        this.id = idGenerated;
        this.title = title;
        this.status = TaskStatus.IN_PROGRESS;
    }
}

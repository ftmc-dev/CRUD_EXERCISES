package test.productHandler.entity;

public record Status(TaskStatus taskStatus) {
    public static Status createStatus(TaskStatus taskStatus) {
        return new Status(taskStatus);
    }

    public static Status updateStatus(TaskStatus taskStatus) {
        return new Status(taskStatus);
    }
}

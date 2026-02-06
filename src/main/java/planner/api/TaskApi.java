package planner.api;

import planner.api.dto.TaskDto;

public class TaskApi {
    private final ApiClient client;

    public TaskApi(ApiClient client) {
        this.client = client;
    }

    public TaskDto[] getTasks(String token) throws Exception {
        return client.getJson("/api/tasks", TaskDto[].class, token);
    }   
}

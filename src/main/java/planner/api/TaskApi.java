package planner.api;

import planner.api.dto.CreateTaskRequest;
import planner.api.dto.TaskDto;
import java.util.Map;
import java.util.List;

public class TaskApi {
    private final ApiClient client;

    public TaskApi(ApiClient client) {
        this.client = client;
    }

    public TaskDto[] getTasks(String token) throws Exception {
        return client.getJson("/api/tasks", TaskDto[].class, token);
    }
    
    public TaskDto createTask(String token, String title) throws Exception {
        return client.postJson("/api/tasks", new CreateTaskRequest(title), TaskDto.class, token);
    }

    public void updateProgress(String token, Long taskId, int progress) throws Exception {
        String path = "/api/tasks/" + taskId + "/progress";
        var body = Map.of("progress", progress);
        client.putJson(path, body,  Void.class, token);
    }

    public void renameTask(String token, Long taskId, String newTitle) throws Exception{
        String path = "/api/tasks/" + taskId + "/rename";
        var body = Map.of("title", newTitle);
        client.putJson(path, body, Void.class, token);
    }

    public void completeTask(String token, Long taskId) throws Exception {
        String path = "/api/tasks/" + taskId + "/complete";
        var body = Map.of();
        client.putJson(path, body, Void.class, token);
    }

    public void reorder(String token, List<Long> orderedTaskIds) throws Exception {
    String path = "/api/tasks/reorder";
    client.putJson(path, orderedTaskIds, Void.class, token);  // ✓ Direct list
}

    
}

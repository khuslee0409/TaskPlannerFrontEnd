package planner.api.dto;

public class CreateTaskRequest {
    public String title;
    public String deadline;
    
    public CreateTaskRequest(String title, String deadline){
        this.title = title;
        this.deadline = deadline;
    }
}
    


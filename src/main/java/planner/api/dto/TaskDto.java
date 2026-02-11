package planner.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskDto {
    public Long id;
    public String title;
    public int progress;
    public boolean completed;
    public int sortOrder;
    public String createdAt;


    @Override
    public String toString() {
        return title + " (" + progress + "%)";
    }
}

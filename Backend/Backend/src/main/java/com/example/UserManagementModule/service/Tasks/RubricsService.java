package com.example.UserManagementModule.service.Tasks;

import com.example.UserManagementModule.Exceptions.ResourceNotFoundException;
import com.example.UserManagementModule.entity.Task.Rubrics;
import com.example.UserManagementModule.entity.Task.Task;
import com.example.UserManagementModule.repository.Task.RubricsRepository;
import com.example.UserManagementModule.repository.Task.TaskRepository;
import jakarta.ws.rs.NotFoundException;
import org.springframework.stereotype.Service;

@Service
public class RubricsService {

    private final RubricsRepository rubricsRepository;
    private final TaskService taskService;
    private final TaskRepository taskRepository;

    public RubricsService(RubricsRepository rubricsRepository, TaskService taskService, TaskRepository taskRepository) {
        this.rubricsRepository = rubricsRepository;
        this.taskService = taskService;
        this.taskRepository = taskRepository;
    }

    public Rubrics addRubrics(String name, Integer score, String taskId) {
        Task task = taskRepository.findById(taskId).get();
        Rubrics rubrics = new Rubrics();
        rubrics.setRubricName(name);
        rubrics.setRubricScore(score);

        // Set both sides of the relationship
        rubrics.setTask(task);
        task.getRubrics().add(rubrics);  // Modify the existing collection

        // Save the rubric
        return rubricsRepository.save(rubrics);
    }

    public Rubrics getRubricsById(String id){
        return rubricsRepository.findById(id).orElseThrow(() -> new NotFoundException("Rubrics not found with id " + id));
    }

    public void deleteRubrics(String rubricId){
        Rubrics rubric = rubricsRepository.findById(rubricId)
                .orElseThrow(() -> new ResourceNotFoundException("Rubric not found with id: " + rubricId));

        // Get the associated task
        Task task = rubric.getTask();
        if (task != null) {
            // Remove the rubric from task's collection
            task.getRubrics().remove(rubric);
        }

        // Delete the rubric
        rubricsRepository.delete(rubric);
    }
}

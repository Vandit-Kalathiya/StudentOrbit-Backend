package com.example.UserManagementModule.controller.Batch;


import com.example.UserManagementModule.dto.Batch.BatchRequest;
import com.example.UserManagementModule.entity.Batches.Batch;
import com.example.UserManagementModule.entity.Faculty.Faculty;
import com.example.UserManagementModule.entity.Groups.Group;
import com.example.UserManagementModule.repository.Batch.BatchRepository;
import com.example.UserManagementModule.service.Batch.BatchService;
import com.example.UserManagementModule.service.Faculty.FacultyService;
import jakarta.ws.rs.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/faculty/batches")
public class FacultyBatchController {

    @Autowired
    private BatchService batchService;
    @Autowired
    private BatchRepository batchRepository;
    @Autowired
    private FacultyService facultyService;

    @PostMapping("/add")
    public ResponseEntity<Batch> createBatch(@RequestBody BatchRequest batchRequest) {

        if(batchRepository.getBatchByBatchNameAndSemester(batchRequest.getBatchName(), batchRequest.getSemester()).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Batch already exists with this batchName and semester : " + batchRequest.getBatchName() + " and semester : " + batchRequest.getSemester());
        }
        Batch batch = new Batch();
        System.out.println("Batch Created");
        batch.setBatchName(batchRequest.getBatchName());
        batch.setSemester(batchRequest.getSemester());
        batch.setYear(batchRequest.getSemester()%2==0?(Integer) (batchRequest.getSemester()/2):(Integer) (batchRequest.getSemester()/2)+1);
        batch.setStartId(batchRequest.getStartId().toUpperCase());
        batch.setEndId(batchRequest.getEndId().toUpperCase());
        batch.setCreatedAt(LocalDateTime.now());

        Faculty faculty = facultyService.findFacultyByUserName(batchRequest.getAssignedFacultyUsername());
        if(faculty == null){
            throw new NotFoundException("Faculty not found for faculty username : "+ batchRequest.getAssignedFacultyUsername());
        }
        batch.setAssignedFaculty(faculty);
        Batch savedBatch = batchService.saveBatch(batch);


        faculty.addBatch(savedBatch);
        return new ResponseEntity<>(batchService.saveBatch(batch), HttpStatus.CREATED);
    }

    @GetMapping("/allBatches")
    public ResponseEntity<List<Batch>> getAllBatches() {
        List<Batch> batches = batchService.getAllBatches();
        return ResponseEntity.ok(batches);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Batch getBatchById(@PathVariable String id) {
        Batch batch = batchService.getBatchById(id);

        if (batch == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Batch not found with id " + id);
        }

        return batch;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBatch(@PathVariable String id) {
//        try {
            batchService.deleteBatch(id);
            return ResponseEntity.ok("Batch deleted successfully");
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body(e.getMessage());
//        }
    }

    @GetMapping("/b/{username}")
    public ResponseEntity<List<Batch>> getBatchesByUsername(@PathVariable String username) {
//        System.out.println(facultyService.findFacultyByUserName(username).getBatches()+"------------------------------------------");
        return ResponseEntity.ok(facultyService.findFacultyByUserName(username).getBatches());
    }

    @GetMapping("/g/{username}")
    public ResponseEntity<List<Group>> getGroupsByUsername(@PathVariable String username) {
        return ResponseEntity.ok(facultyService.findFacultyByUserName(username).getGroups());
    }

    @GetMapping("/allGroups/{sem}/{batchName}")
    public ResponseEntity<List<Group>> getAllGroupsOfBatchByBatchName(@PathVariable Integer sem,@PathVariable String batchName){
        return ResponseEntity.ok(batchService.getAllGroupsOfBatch(sem,batchName));
    }

    @GetMapping("/{sem}/{batchName}")
    public ResponseEntity<Batch> getBatchesByBatchName(@PathVariable Integer sem,@PathVariable String batchName){
        return ResponseEntity.ok(batchService.getBatchByBatchNameAndSemester(batchName,sem).get());
    }
}

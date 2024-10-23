package com.example.UserManagementModule.controller.Batch;


import com.example.UserManagementModule.dto.Batch.BatchRequest;
import com.example.UserManagementModule.entity.Batches.Batch;
import com.example.UserManagementModule.repository.Batch.BatchRepository;
import com.example.UserManagementModule.service.Batch.BatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5174")
@RequestMapping("/faculty/batches")
public class FacultyBatchController {

    @Autowired
    private BatchService batchService;
    @Autowired
    private BatchRepository batchRepository;

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
        batch.setStartId(batchRequest.getStartId());
        batch.setEndId(batchRequest.getEndId());
        batch.setCreatedAt(LocalDateTime.now());

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
    public ResponseEntity<Void> deleteBatch(@PathVariable String id) {
        batchService.deleteBatch(id);
        return ResponseEntity.noContent().build();
    }
}

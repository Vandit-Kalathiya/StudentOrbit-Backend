package com.example.UserManagementModule.dto.Batch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BatchRequest {

    private String batchName;
    private Integer semester;
    private String startId;
    private String endId;
}

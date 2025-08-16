package com.example.authservice.model;

import com.example.authservice.entities.UserInfo;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;
@Data

@EqualsAndHashCode(callSuper = false)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserDetailsDto extends UserInfo {
    // No need to redeclare fields - they're inherited from UserInfo
    // This DTO can be used directly as it inherits all UserInfo fields
}

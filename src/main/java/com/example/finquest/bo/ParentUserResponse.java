package com.example.finquest.bo;
import com.example.finquest.entity.ChildUserEntity;
import com.example.finquest.entity.ParentUserEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParentUserResponse {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("username")
    private String username;
    private String roles = "ROLE_PARENT";

    // Optional: Include children if you want to return them
    private List<ChildUserResponse> children;

    public ParentUserResponse(ParentUserEntity parentUserEntity) {
        this.id = parentUserEntity.getId();
        this.username = parentUserEntity.getUsername();
        this.roles = parentUserEntity.getRoles();

        // If you want to include children, map them to ChildUserResponse (DTO)
        if (parentUserEntity.getChildren() != null) {
            this.children = parentUserEntity.getChildren().stream()
                    .map(child -> new ChildUserResponse(child))  // Assuming you have a ChildUserResponse DTO
                    .collect(Collectors.toList());
        }
    }
}

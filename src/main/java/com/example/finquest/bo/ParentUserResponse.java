package com.example.finquest.bo;
import com.example.finquest.entity.ChildUserEntity;
import com.example.finquest.entity.ParentUserEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;

import javax.persistence.JoinColumn;
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
    @JsonProperty("roles")
    private String roles = "ROLE_PARENT";
    @JsonProperty("balance")
    private Double balance;
    @JsonProperty("children")
    private List<ChildUserResponse> children;

    public ParentUserResponse(ParentUserEntity parentUserEntity) {
        this.id = parentUserEntity.getId();
        this.username = parentUserEntity.getUsername();
        this.roles = parentUserEntity.getRoles();

        if (parentUserEntity.getChildren() != null) {
            this.children = parentUserEntity.getChildren().stream()
                    .map(child -> new ChildUserResponse(child))
                    .collect(Collectors.toList());
        }
    }

    public ParentUserResponse(Long id, String username) {
    }
}

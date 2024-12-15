package com.example.finquest.bo;
import com.example.finquest.entity.ChildUserEntity;
import com.example.finquest.entity.ParentUserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParentUserResponse {
    private Long id;
    private String username;
    private String password;
    private String roles = "ROLE_PARENT";
//    private List<ChildUserEntity> children;


    public ParentUserResponse(ParentUserEntity parentUserEntity) {
        this.id = parentUserEntity.getId();
        this.username = parentUserEntity.getUsername();
        this.roles = parentUserEntity.getRoles();
//        this.children = parentUserEntity.getChildren();
    }
}

package com.example.finquest.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChildUserRequest {
    private String username;
    private String password;
    private Long avatarId;
}

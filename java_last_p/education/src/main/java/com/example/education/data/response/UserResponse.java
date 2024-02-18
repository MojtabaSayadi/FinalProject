package com.example.education.data.response;


import com.example.education.model.oauth.Role;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserResponse {
    private Boolean status;
    private Long id;
    private String username;
    private String password;
    private String startTime;
    private String endTime;
    private String email;
    private Set<Role> authorities;
    private Integer active;
}

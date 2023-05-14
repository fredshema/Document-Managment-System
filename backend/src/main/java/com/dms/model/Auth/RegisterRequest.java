package com.dms.model.Auth;

import com.dms.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String names;
    private String username;
    private String email;
    private String password;

    public User toUser() {
        return User.builder()
                .names(names)
                .username(username)
                .email(email)
                .password(password)
                .build();
    }
}

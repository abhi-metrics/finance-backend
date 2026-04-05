package com.finance.dto.request;

import com.finance.model.Role;
import com.finance.model.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Email(message = "Please provide a valid email address")
    private String email;

    private Role role;

    private UserStatus status;

    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;
}

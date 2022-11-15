package Onol.onol.DTO.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginReqDTO {
    @NotBlank
    private String email;

    @NotBlank
    private String password;

}

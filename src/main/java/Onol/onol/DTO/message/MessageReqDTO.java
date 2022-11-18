package Onol.onol.DTO.message;

import Onol.onol.Domain.auth.Authority;
import Onol.onol.Domain.auth.Member;
import Onol.onol.Domain.main.Message;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
@AllArgsConstructor
public class MessageReqDTO {

    private String identifier;

    private String content;

}

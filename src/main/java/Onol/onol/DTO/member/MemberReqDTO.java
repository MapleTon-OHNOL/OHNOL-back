package Onol.onol.DTO.member;

import Onol.onol.Domain.auth.Authority;
import Onol.onol.Domain.auth.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberReqDTO {
    private String email;
    private String password;
    private String username;

    public Member toMember(PasswordEncoder passwordEncoder, Set<Authority> authorities) {
        return Member.builder()
                .email(email)
                .username(username)
                .password(passwordEncoder.encode(password))
                .activated(false)
                .authorities(authorities)
                .build();
    }

}
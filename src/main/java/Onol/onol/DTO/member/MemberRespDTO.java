package Onol.onol.DTO.member;

import Onol.onol.Domain.auth.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberRespDTO {
    private String email;
    private String username;

    public static MemberRespDTO of(Member member) {
        return new MemberRespDTO(member.getEmail(), member.getUsername());
    }
}
package Onol.onol.DTO.message;

import Onol.onol.Domain.auth.Member;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageCreateRespDTO {

    private int status;

    private String username;

    private int count;

    public MessageCreateRespDTO(int status) {
        this.status = status;
    }
    public static MessageCreateRespDTO of(Member member) {
        return new MessageCreateRespDTO(2,member.getUsername(), member.getMessageCount());
    }

}

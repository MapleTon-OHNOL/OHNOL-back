package Onol.onol.DTO.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenReqDTO {
    private String accessToken;
    private String refreshToken;
}
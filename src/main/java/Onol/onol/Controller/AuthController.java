package Onol.onol.Controller;

import Onol.onol.DTO.jwt.TokenDTO;
import Onol.onol.DTO.jwt.TokenReqDTO;
import Onol.onol.DTO.login.LoginReqDTO;
import Onol.onol.DTO.member.MemberReqDTO;
import Onol.onol.DTO.member.MemberRespDTO;
import Onol.onol.Service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public MemberRespDTO signup(@RequestBody MemberReqDTO memberRequestDto) {
        log.debug("memberRequestDto = {}",memberRequestDto);
        return authService.signup(memberRequestDto);
    }
    @PostMapping("/login")
    public TokenDTO login(@RequestBody LoginReqDTO loginReqDTO) {
        return authService.login(loginReqDTO);
    }

    @PostMapping("/reissue")
    public TokenDTO reissue(@RequestHeader("Authorization") String accessToken, @RequestHeader("refresh-token") String refreshToken) {
        return authService.reissue(new TokenReqDTO(accessToken.substring(7), refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity logout(@RequestHeader("Authorization") String accessToken){
        return authService.logout(accessToken.substring(7));
    }

    @PostMapping("/info")
    public MemberRespDTO info(@RequestHeader("Authorization") String accessToken){
        return authService.getInfo(accessToken.substring(7));
    }
}

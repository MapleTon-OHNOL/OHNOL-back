package Onol.onol.Service;

import Onol.onol.DTO.jwt.TokenDTO;
import Onol.onol.DTO.jwt.TokenReqDTO;
import Onol.onol.DTO.login.LoginReqDTO;
import Onol.onol.DTO.member.MemberIdentifierReqDTO;
import Onol.onol.DTO.member.MemberReqDTO;
import Onol.onol.DTO.member.MemberRespDTO;
import Onol.onol.Domain.auth.Authority;
import Onol.onol.Domain.auth.Member;
import Onol.onol.Domain.auth.MemberAuth;
import Onol.onol.Domain.jwt.RefreshToken;
import Onol.onol.ExceptionHandler.AuthorityExceptionType;
import Onol.onol.ExceptionHandler.BizException;
import Onol.onol.ExceptionHandler.JwtExceptionType;
import Onol.onol.ExceptionHandler.MemberExceptionType;
import Onol.onol.Jwt.CustomEmailPasswordAuthToken;
import Onol.onol.Jwt.TokenProvider;
import Onol.onol.Repository.AuthorityRepository;
import Onol.onol.Repository.MemberRepository;
import Onol.onol.Repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomUserDetailsService customUserDetailsService;;
    public static final String BEARER_PREFIX = "Bearer ";

    private String resolveToken(String bearerToken) {
        // bearer : 123123123123123 -> return 123123123123123123
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 랜덤한 문자열을 원하는 길이만큼 반환합니다.
     *
     * @param length 문자열 길이
     * @return 랜덤문자열
     */
    private static String getRandomString(int length)
    {
        StringBuffer buffer = new StringBuffer();
        Random random = new Random();

        String chars[] =
    "a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,0,1,2,3,4,5,6,7,8,9".split(",");

        for (int i=0 ; i<length ; i++)
        {
            buffer.append(chars[random.nextInt(chars.length)]);
        }
        return buffer.toString();
    }

    @Transactional
    public MemberRespDTO signup(MemberReqDTO memberRequestDto) {
        if (memberRepository.existsByEmail(memberRequestDto.getEmail())) {
            throw new BizException(MemberExceptionType.DUPLICATE_USER);
        }

        // DB 에서 ROLE_USER를 찾아서 권한으로 추가한다.
        Authority authority = authorityRepository
                .findByAuthorityName(MemberAuth.ROLE_USER).orElseThrow(()->new BizException(AuthorityExceptionType.NOT_FOUND_AUTHORITY));

        Set<Authority> set = new HashSet<>();
        set.add(authority);

        String identifier = getRandomString(8);

        Member member = memberRequestDto.toMember(passwordEncoder, set);
        member.setIdentifier(identifier);
        log.debug("member = {}",member);
        return MemberRespDTO.of(memberRepository.saveMember(member));
    }

    @Transactional
    public TokenDTO login(LoginReqDTO loginReqDTO) {
        CustomEmailPasswordAuthToken customEmailPasswordAuthToken = new CustomEmailPasswordAuthToken(loginReqDTO.getEmail(), loginReqDTO.getPassword());
        Authentication authenticate = authenticationManager.authenticate(customEmailPasswordAuthToken);
        String email = authenticate.getName();
        Member member = customUserDetailsService.getMember(email);

        String accessToken = tokenProvider.createAccessToken(email, member.getAuthorities());
        // 만약 해당 유저의 refreshToken이 이미 있다면 삭제하고 재생성
        if (refreshTokenRepository.existsByKey(email)){
            refreshTokenRepository.deleteRefreshToken(refreshTokenRepository.findByKey(email).orElseThrow(()->new BizException(MemberExceptionType.NOT_FOUND_USER)));
        }
        String newRefreshToken = tokenProvider.createRefreshToken(email, member.getAuthorities());

        //refresh Token 저장
        refreshTokenRepository.saveRefreshToken(
                RefreshToken.builder()
                        .key(email)
                        .value(newRefreshToken)
                        .build()
        );

        return tokenProvider.createTokenDTO(accessToken, newRefreshToken, member.getIdentifier());

    }

    @Transactional
    public TokenDTO reissue(TokenReqDTO tokenRequestDto) {
        /*
         *  accessToken 은 JWT Filter 에서 검증되고 옴
         * */
        String originAccessToken = resolveToken(tokenRequestDto.getAccessToken());
        String originRefreshToken = tokenRequestDto.getRefreshToken();

        // refreshToken 검증
        int refreshTokenFlag = tokenProvider.validateToken(originRefreshToken);

        log.debug("refreshTokenFlag = {}", refreshTokenFlag);

        //refreshToken 검증하고 상황에 맞는 오류를 내보낸다.
        if (refreshTokenFlag == -1) {
            throw new BizException(JwtExceptionType.BAD_TOKEN); // 잘못된 리프레시 토큰
        } else if (refreshTokenFlag == 2) {
            throw new BizException(JwtExceptionType.REFRESH_TOKEN_EXPIRED); // 유효기간 끝난 토큰
        }

        // 2. Access Token 에서 Member Email 가져오기
        Authentication authentication = tokenProvider.getAuthentication(originAccessToken);

        log.debug("Authentication = {}",authentication);

        // 3. 저장소에서 Member Email 를 기반으로 Refresh Token 값 가져옴
        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
                .orElseThrow(() -> new BizException(MemberExceptionType.LOGOUT_MEMBER)); // 로그 아웃된 사용자


        // 4. Refresh Token 일치하는지 검사
        if (!refreshToken.getValue().equals(originRefreshToken)) {
            throw new BizException(JwtExceptionType.BAD_TOKEN); // 토큰이 일치하지 않습니다.
        }

        // 5. 새로운 토큰 생성
        String email = tokenProvider.getMemberEmailByToken(originAccessToken);
        Member member = customUserDetailsService.getMember(email);

        String newAccessToken = tokenProvider.createAccessToken(email, member.getAuthorities());
        String newRefreshToken = tokenProvider.createRefreshToken(email, member.getAuthorities());
        TokenDTO tokenDto = tokenProvider.createTokenDTO(newAccessToken, newRefreshToken, member.getIdentifier());

        log.debug("refresh Origin = {}",originRefreshToken);
        log.debug("refresh New = {} ",newRefreshToken);
        // 6. 저장소 정보 업데이트 (dirtyChecking으로 업데이트)
        refreshToken.updateValue(newRefreshToken);

        // 토큰 발급
        return tokenDto;
    }

    public ResponseEntity logout(String bearerToken){
        String accessToken = resolveToken(bearerToken);

        String email = tokenProvider.getMemberEmailByToken(accessToken);
        RefreshToken refreshToken = refreshTokenRepository.findByKey(email)
                .orElseThrow(() -> new BizException(MemberExceptionType.LOGOUT_MEMBER));

        refreshTokenRepository.deleteRefreshToken(refreshToken);
        return new ResponseEntity(HttpStatus.OK);
    }

    public MemberRespDTO getInfo(String bearerToken){
        String accessToken = resolveToken(bearerToken);

        String email = tokenProvider.getMemberEmailByToken(accessToken);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER));

        return MemberRespDTO.of(member);
    }

    public MemberRespDTO infoByIdentifier(MemberIdentifierReqDTO memberIdentifierReqDTO) {
        String identifier = memberIdentifierReqDTO.getIdentifier();

        Member member = memberRepository.findByIdentifier(identifier)
                .orElseThrow(()-> new BizException(MemberExceptionType.NOT_FOUND_USER));

        return MemberRespDTO.of(member);

    }
}

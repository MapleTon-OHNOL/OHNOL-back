package Onol.onol.Service;

import Onol.onol.Domain.auth.Authority;
import Onol.onol.Domain.auth.Member;
import Onol.onol.ExceptionHandler.BizException;
import Onol.onol.ExceptionHandler.MemberExceptionType;
import Onol.onol.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws BizException{
        log.debug("CustomUserDetailsService -> email = {}",email);
        return memberRepository.findByEmail(email)
                .map(this::createUserDetails)
                .orElseThrow(() -> new BizException(MemberExceptionType.NOT_FOUND_USER));
    }

    @Transactional(readOnly = true)
    public Member getMember(String email) throws BizException {
        return memberRepository.findByEmail(email)
                .orElseThrow(()->new BizException(MemberExceptionType.NOT_FOUND_USER));
    }

    // DB 에 User 값이 존재한다면 UserDetails 객체로 만들어서 리턴
    private UserDetails createUserDetails(Member member) {

        // Collections<? extends GrantedAuthority>
        List<SimpleGrantedAuthority> authList = member.getAuthorities()
                .stream()
                .map(Authority::getAuthorityName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        authList.forEach(o-> log.debug("authList -> {}",o.getAuthority()));

        return new User(
                member.getEmail(),
                member.getPassword(),
                authList
        );
    }
}


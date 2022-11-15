package Onol.onol.Domain.auth;

import Onol.onol.DTO.member.MemberUpdateDTO;
import Onol.onol.Domain.main.Message;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
public class Member {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id")
    private Long id;

    @Column(name="username", length = 50, nullable = false)
    private String username;

    @Column(name="email", unique = true, nullable = false)
    private String email;

    @Column(name="password", nullable = false)
    private String password;

    @JsonIgnore
    @Column(name="activated")
    private boolean activated;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name="member_authority",
            joinColumns = {@JoinColumn(name="member_id", referencedColumnName = "member_id")},
            inverseJoinColumns = {@JoinColumn(name="authority_name", referencedColumnName = "authority_name")})
    private Set<Authority> authorities = new HashSet<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();

    @Builder
    public Member(String username, String email, String password, boolean activated, Set<Authority> authorities) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.activated = activated;
        this.authorities = authorities;
    }

    public void addAuthority(Authority authority){
        this.getAuthorities().add(authority);
    }

    public void removeAuthority(Authority authority){
        this.getAuthorities().remove(authority);
    }

    public void activate(boolean flag){
        this.activated = flag;
    }

    public String getAuthoritiesToString(){
        return this.authorities.stream()
                .map(Authority::getAuthorityName)
                .collect(Collectors.joining(","));
    }

    public void updateMember(MemberUpdateDTO dto, PasswordEncoder passwordEncoder){
        if(dto.getPassword() != null) this.password = passwordEncoder.encode(dto.getPassword());
        if(dto.getUsername() != null) this.username = dto.getUsername();
        if(dto.getAuthorities().size() > 0 ){
            this.authorities = dto.getAuthorities().stream()
                    .filter(MemberAuth::containsKey)
                    .map(MemberAuth::get)
                    .map(Authority::new)
                    .collect(Collectors.toSet());
        }
    }
}
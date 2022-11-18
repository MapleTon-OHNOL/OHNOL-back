package Onol.onol.Repository;

import Onol.onol.Domain.auth.Member;
import Onol.onol.ExceptionHandler.BizException;
import Onol.onol.ExceptionHandler.MemberExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepository{
    private final EntityManager em;

    public Member saveMember(Member member){
        em.persist(member);
        return em.find(Member.class, member.getId());
    }



    public Optional<Member> findByUsername(String username){
        try {
            Member member = em.
                    createQuery("select m from Member m where m.username = :username", Member.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return Optional.of(member);
        }catch(NoResultException e){
            throw new BizException(MemberExceptionType.NOT_FOUND_USER);
        }
    }

    public Optional<Member> findByEmail(String email){
        try {
            Member member = em.
                    createQuery("select m from Member m where m.email = :email", Member.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.of(member);
        }catch(NoResultException e){
            throw new BizException(MemberExceptionType.NOT_FOUND_USER);
        }
    }

    public boolean existsByEmail(String email){
        try {
            em
                    .createQuery("select m from Member m where m.email = :email", Member.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return true;
        }catch(NoResultException e){
            return false;
        }
    }

    public Optional<Member> findByIdentifier(String identifier){
        try{
            Member member = em
                    .createQuery("select m from Member m where m.identifier = :identifier", Member.class)
                    .setParameter("identifier", identifier)
                    .getSingleResult();
            return Optional.of(member);
        }catch (NoResultException e){
            throw new BizException(MemberExceptionType.NOT_FOUND_USER);
        }
    }
}

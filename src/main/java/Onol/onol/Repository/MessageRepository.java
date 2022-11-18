package Onol.onol.Repository;

import Onol.onol.Domain.auth.Member;
import Onol.onol.Domain.main.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Message findFirstByMemberReceiverAndAndMemberSender(Member memberReceiver, Member memberSender);
}

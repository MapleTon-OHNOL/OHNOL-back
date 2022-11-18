package Onol.onol.Domain.main;

import Onol.onol.Domain.auth.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
public class Message {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_receiver_id")
    private Member memberReceiver;

    @ManyToOne
    @JoinColumn(name = "member_sender_id")
    private Member memberSender;

    private String content;

    public Message(Member memberReceiver, Member memberSender, String content) {
        this.memberReceiver = memberReceiver;
        this.memberSender = memberSender;
        this.content = content;
    }
}

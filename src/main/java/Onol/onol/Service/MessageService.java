package Onol.onol.Service;

import Onol.onol.DTO.message.MessageCreateRespDTO;
import Onol.onol.DTO.message.MessageListDTO;
import Onol.onol.DTO.message.MessageListRespDTO;
import Onol.onol.DTO.message.MessageReqDTO;
import Onol.onol.Domain.auth.Member;
import Onol.onol.Domain.main.Message;
import Onol.onol.Jwt.TokenProvider;
import Onol.onol.Repository.MemberRepository;
import Onol.onol.Repository.MessageRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

    public static final String BEARER_PREFIX = "Bearer ";
    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    private String resolveToken(String bearerToken) {
        // bearer : 123123123123123 -> return 123123123123123123
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Transactional
    public MessageCreateRespDTO createMessage(String bearerToken, String identifier, MessageReqDTO messageReqDTO) {
        String accessToken = resolveToken(bearerToken);
        if (StringUtils.hasText(accessToken)) {
            String memberEmail = tokenProvider.getMemberEmailByToken(accessToken);

            Member memberSender = memberRepository.findByEmail(memberEmail).get();
            Member memberFindByIdentifier = memberRepository.findByIdentifier(identifier).get();
//            자기 자신에게 보내는 경우
            if (memberFindByIdentifier.getId() == memberSender.getId()) {
                return new MessageCreateRespDTO(0);
            }

            Message firstByMemberReceiverAndAndMemberSender = messageRepository.findFirstByMemberReceiverAndAndMemberSender(
                    memberFindByIdentifier, memberSender);
//            이미 편지 쓴 이력이 있는 경우
            if (firstByMemberReceiverAndAndMemberSender != null) {
                return new MessageCreateRespDTO(1, memberSender.getUsername());
            }

            Message message = new Message(memberFindByIdentifier, memberSender, messageReqDTO.getContent());
            messageRepository.save(message);

            memberFindByIdentifier.addMessageCount();

            return MessageCreateRespDTO.of(memberFindByIdentifier);

        }

        return null;
    }

    public MessageListRespDTO messages(String bearerToken, String identifier) {
        String accessToken = resolveToken(bearerToken);
        //            방문한 페이지
        Member memberFindByIdentifier = memberRepository.findByIdentifier(identifier).get();
        if (StringUtils.hasText(accessToken)) {
            String memberEmail = tokenProvider.getMemberEmailByToken(accessToken);
//            로그인한사람
            Member memberFindByToken = memberRepository.findByEmail(memberEmail).get();
            System.out.println("memberFindByToken = " + memberFindByToken);
            System.out.println("memberFindByIdentifier = " + memberFindByIdentifier);
//            자기 페이지라면
            if (memberFindByIdentifier.getId() == memberFindByToken.getId()) {
//                25일이후라면
                //if (LocalDateTime.now().isAfter(LocalDateTime.now().plus(4, ChronoUnit.MINUTES))){//of(2022, 12, 25, 0, 0))) {
                if (LocalDateTime.now().isAfter(LocalDateTime.of(2022,11,19,18,57))){
                    List<Message> receiveMessages = memberFindByToken.getReceiveMessages();
                    Set<Member> sendMessageToThisMember = receiveMessages.stream()
                            .map(Message::getMemberSender)
                            .collect(Collectors.toSet());

                    List<Message> sendMessages = memberFindByToken.getSendMessages();
                    Set<Member> thisMemberSend = sendMessages.stream()
                            .map(Message::getMemberReceiver)
                            .collect(Collectors.toSet());
                    sendMessageToThisMember.retainAll(thisMemberSend);
                    List<MessageListDTO> messages = new ArrayList<>();
                    for (Member member : sendMessageToThisMember) {
                        Message sendingMessage = messageRepository.findFirstByMemberReceiverAndAndMemberSender(
                                member, memberFindByToken);
                        Message receivingMessage = messageRepository.findFirstByMemberReceiverAndAndMemberSender(
                                memberFindByToken, member);
                        MessageListDTO messageListDTO = new MessageListDTO(member.getUsername(),
                                receivingMessage.getContent(),
                                sendingMessage.getContent());
                        messages.add(messageListDTO);
                    }

                    return new MessageListRespDTO(memberFindByToken.getUsername(), identifier,
                            memberFindByToken.getMessageCount(), messages);


//                25일 이전이면
                } else {
                    String username = memberFindByToken.getUsername();
                    Integer messageCount = memberFindByToken.getMessageCount();
                    return new MessageListRespDTO(username, identifier, messageCount);
                }
            } else { //자기 페이지가 아니라면
                String username = memberFindByToken.getUsername();
                String visitName = memberFindByIdentifier.getUsername();
                Integer messageCount = memberFindByIdentifier.getMessageCount();
                return new MessageListRespDTO(username, visitName, identifier, messageCount);
            }

        } else {//로그인 안했으면
            String username = memberFindByIdentifier.getUsername();
            Integer messageCount = memberFindByIdentifier.getMessageCount();
            return new MessageListRespDTO(username, messageCount);

        }

    }
}

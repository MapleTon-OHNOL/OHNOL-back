package Onol.onol.DTO.message;

import java.util.List;
import lombok.Data;

@Data
public class MessageListRespDTO {

    private int status;
    private String username;
    private String visitName;
    private int messageCount;
    private String identifier;
    private List<MessageListDTO> messageList;

    public MessageListRespDTO(String visitName, int messageCount) {
        this.status = 0;
        this.visitName = visitName;
        this.messageCount = messageCount;
    }

    public MessageListRespDTO(String username, String visitName, String identifier, int messageCount) {
        this.status = 1;
        this.username = username;
        this.visitName = visitName;
        this.identifier = identifier;
        this.messageCount = messageCount;
    }

    public MessageListRespDTO(String username, String identifier, int messageCount) {
        this.status = 2;
        this.username = username;
        this.identifier = identifier;
        this.messageCount = messageCount;
    }

    public MessageListRespDTO(String username, String identifier, int messageCount, List<MessageListDTO> messageList) {
        this.status = 3;
        this.username = username;
        this.identifier = identifier;
        this.messageCount = messageCount;
        this.messageList = messageList;
    }
}

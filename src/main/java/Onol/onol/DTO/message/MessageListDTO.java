package Onol.onol.DTO.message;

import Onol.onol.Domain.main.Message;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageListDTO {

    private Map<String, String> message;

    public MessageListDTO (String visitName, String toMeMessage, String fromMeMessage) {
        Map<String, String> messages = new HashMap<>();
        messages.put("visit", visitName);
        messages.put("toMe", toMeMessage);
        messages.put("fromMe", fromMeMessage);
        this.message = messages;
    }
}

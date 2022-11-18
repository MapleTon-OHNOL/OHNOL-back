package Onol.onol.Controller;

import Onol.onol.DTO.message.MessageCreateRespDTO;
import Onol.onol.DTO.message.MessageListRespDTO;
import Onol.onol.DTO.message.MessageReqDTO;
import Onol.onol.Service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/u/{identifier}")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public MessageCreateRespDTO MessageCreate(@PathVariable("identifier") String identifier, @RequestHeader("Authorization") String bearerToken, @RequestBody MessageReqDTO messageReqDTO) {
        return messageService.createMessage(bearerToken, identifier, messageReqDTO);
    }

    @GetMapping
    public MessageListRespDTO Home(@PathVariable("identifier") String identifier, @RequestHeader("Authorization") String bearerToken) {
        return messageService.messages(bearerToken, identifier);
    }
}


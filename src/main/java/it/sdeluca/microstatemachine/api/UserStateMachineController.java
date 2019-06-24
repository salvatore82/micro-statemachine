package it.sdeluca.microstatemachine.api;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/v1/statemachine")
@Api(value = "Controller to state machine")
public class UserStateMachineController {

	@Autowired
	private StateMachineService<String, String> service;
    @Autowired
    private SimpMessagingTemplate template;

	@ApiOperation("Start state machine")
	@PostMapping("/{userId}/start")
	public UUID start(@PathVariable(value = "userId") String userId) throws Exception {
		StateMachine<String, String> stateMachine = service.acquireStateMachine(userId);
		template.convertAndSend("/topic/hello", stateMachine.getState().getId());
		return stateMachine.getUuid();
	}

	@ApiOperation("Validate step for state machine")
	@PutMapping("/{userId}/validate")
	public void validate(@PathVariable(value = "userId") String userId, @RequestParam("isFileValid") Boolean isFileValid) throws Exception {
		StateMachine<String, String> stateMachine = service.acquireStateMachine(userId);
		Message<String> message = MessageBuilder.withPayload("VALIDATE").setHeader("isFileValid", isFileValid).build();
		stateMachine.sendEvent(message);
		template.convertAndSend("/topic/hello", stateMachine.getState().getId());
	}

}
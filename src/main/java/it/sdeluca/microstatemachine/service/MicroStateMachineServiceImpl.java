/**
 * 
 */
package it.sdeluca.microstatemachine.service;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.sdeluca.microstatemachine.dto.MicroContentRepository;
import it.sdeluca.microstatemachine.dto.MicroUser;

/**
 * @author S.DeLuca
 *
 */
@Service
public class MicroStateMachineServiceImpl implements MicroStateMachineService {

	private static Logger log = LoggerFactory.getLogger(MicroStateMachineServiceImpl.class.getName());
	
	@Autowired
	private StateMachineService<String, String> service;
	@Autowired
	private ObjectMapper objectMapper;
    @Autowired
    private SimpMessagingTemplate template;
	
	@JmsListener(destination = "microuser.topic")
	public void listenForCreatedUser(final Message message) throws JMSException, JsonParseException, JsonMappingException, IOException {
		if (message instanceof ActiveMQBytesMessage) {
			ActiveMQBytesMessage bytesMessage = (ActiveMQBytesMessage) message;
			MicroUser microUser = objectMapper.readValue(new String(bytesMessage.getContent().data), MicroUser.class);
			StateMachine<String, String> stateMachine = service.acquireStateMachine(microUser.getId().toString());
			log.debug("State machine for user with id ".concat(microUser.getId().toString()).concat(" created with UUID ").concat(stateMachine.getUuid().toString()));
			template.convertAndSend("/topic/hello", stateMachine.getState().getId());
		} else {
			throw new JMSException("Failed reading message from topic");
		}
	}

	@JmsListener(destination = "microcontentrepository.topic")
	public void listenForValidatedFile(final Message message) throws JMSException, JsonParseException, JsonMappingException, IOException {
		if (message instanceof ActiveMQTextMessage) {
			ActiveMQTextMessage textMessage = (ActiveMQTextMessage) message;
			MicroContentRepository microContentRepository = objectMapper.readValue(textMessage.getText(), MicroContentRepository.class);
			StateMachine<String, String> stateMachine = service.acquireStateMachine(microContentRepository.getId().toString());
			org.springframework.messaging.Message<String> messageSM = MessageBuilder.withPayload("VALIDATE").setHeader("isFileValid", microContentRepository.getValid()).build();
			stateMachine.sendEvent(messageSM);
			template.convertAndSend("/topic/hello", stateMachine.getState().getId());
		} else {
			throw new JMSException("Failed reading message from topic");
		}
	}
}

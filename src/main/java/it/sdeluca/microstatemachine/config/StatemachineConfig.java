package it.sdeluca.microstatemachine.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineModelConfigurer;
import org.springframework.statemachine.config.model.StateMachineModelFactory;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.statemachine.uml.UmlStateMachineModelFactory;

@Configuration
@EnableStateMachineFactory
public class StatemachineConfig extends StateMachineConfigurerAdapter<String, String> {

	private static Logger log = LoggerFactory.getLogger(StatemachineConfig.class);

	@Autowired
	private StateMachineRuntimePersister<String, String, String> stateMachineRuntimePersister;

	@Override
	public void configure(StateMachineConfigurationConfigurer<String, String> config) throws Exception {
		config.withPersistence().runtimePersister(stateMachineRuntimePersister);
	}

	@Override
	public void configure(StateMachineModelConfigurer<String, String> model) throws Exception {
		model.withModel().factory(modelFactory());
	}

	@Bean
	public StateMachineModelFactory<String, String> modelFactory() {
		return new UmlStateMachineModelFactory("classpath:uml/userstatemachine.uml");
	}

	@Bean(name = "validateGuard")
	public Guard<String, String> validatedGuard() {
		return new Guard<String, String>() {
			@Override
			public boolean evaluate(StateContext<String, String> context) {
				Boolean isFileValid = (Boolean) context.getMessageHeader("isFileValid");
				log.debug("isFileValid from context: " + isFileValid);
				return isFileValid;
			}
		};
	}

	@Bean
	public StateMachineService<String, String> stateMachineService(
			StateMachineFactory<String, String> stateMachineFactory,
			StateMachineRuntimePersister<String, String, String> stateMachineRuntimePersister) {
		return new DefaultStateMachineService<String, String>(stateMachineFactory, stateMachineRuntimePersister);
	}
}
package it.sdeluca.microstatemachine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.data.mongodb.MongoDbPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.mongodb.MongoDbStateMachineRepository;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;

@Configuration
public class MongoPersisterConfig {

	@Bean
	public StateMachineRuntimePersister<String, String, String> stateMachineRuntimePersister(
			MongoDbStateMachineRepository jpaStateMachineRepository) {
		return new MongoDbPersistingStateMachineInterceptor<>(jpaStateMachineRepository);
	}
}
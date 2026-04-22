package iusjc_planning.planning_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // =====================================================
    // EXCHANGES
    // =====================================================
    
    public static final String PLANNING_EXCHANGE = "planning.exchange";
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";

    @Bean
    public TopicExchange planningExchange() {
        return new TopicExchange(PLANNING_EXCHANGE);
    }

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE);
    }

    // =====================================================
    // QUEUES
    // =====================================================
    
    public static final String CONFLIT_DETECTE_QUEUE = "planning.conflit.detecte";
    public static final String COURS_SUSPENDU_QUEUE = "planning.cours.suspendu";
    public static final String RESOLUTION_AUTO_QUEUE = "planning.resolution.auto";
    public static final String SURCHARGE_ENSEIGNANT_QUEUE = "planning.surcharge.enseignant";

    @Bean
    public Queue conflitDetecteQueue() {
        return QueueBuilder.durable(CONFLIT_DETECTE_QUEUE).build();
    }

    @Bean
    public Queue coursSuspenduQueue() {
        return QueueBuilder.durable(COURS_SUSPENDU_QUEUE).build();
    }

    @Bean
    public Queue resolutionAutoQueue() {
        return QueueBuilder.durable(RESOLUTION_AUTO_QUEUE).build();
    }

    @Bean
    public Queue surchargeEnseignantQueue() {
        return QueueBuilder.durable(SURCHARGE_ENSEIGNANT_QUEUE).build();
    }

    // =====================================================
    // ROUTING KEYS
    // =====================================================
    
    public static final String CONFLIT_DETECTE_ROUTING_KEY = "planning.conflit.detecte";
    public static final String COURS_SUSPENDU_ROUTING_KEY = "planning.cours.suspendu";
    public static final String RESOLUTION_AUTO_ROUTING_KEY = "planning.resolution.auto";
    public static final String SURCHARGE_ENSEIGNANT_ROUTING_KEY = "planning.surcharge.enseignant";

    // =====================================================
    // BINDINGS
    // =====================================================
    
    @Bean
    public Binding conflitDetecteBinding() {
        return BindingBuilder
                .bind(conflitDetecteQueue())
                .to(planningExchange())
                .with(CONFLIT_DETECTE_ROUTING_KEY);
    }

    @Bean
    public Binding coursSuspenduBinding() {
        return BindingBuilder
                .bind(coursSuspenduQueue())
                .to(planningExchange())
                .with(COURS_SUSPENDU_ROUTING_KEY);
    }

    @Bean
    public Binding resolutionAutoBinding() {
        return BindingBuilder
                .bind(resolutionAutoQueue())
                .to(planningExchange())
                .with(RESOLUTION_AUTO_ROUTING_KEY);
    }

    @Bean
    public Binding surchargeEnseignantBinding() {
        return BindingBuilder
                .bind(surchargeEnseignantQueue())
                .to(planningExchange())
                .with(SURCHARGE_ENSEIGNANT_ROUTING_KEY);
    }

    // =====================================================
    // MESSAGE CONVERTER
    // =====================================================
    
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
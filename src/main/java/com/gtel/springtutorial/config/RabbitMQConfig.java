package com.gtel.springtutorial.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String OTP_QUEUE = "otpQueue";

    public static final String OTP_EXCHANGE = "otpExchange";
    public static final String OTP_ROUTING_KEY = "otpRoutingKey";

    @Bean
    public Queue queue() {
        return new Queue(OTP_QUEUE, true);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter()); // Đặt converter
        return rabbitTemplate;
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(OTP_EXCHANGE);
    }

    // Binding: Nối queue với exchange theo routing key
    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(OTP_ROUTING_KEY);
    }

}

package com.thelak.core.services;

import com.google.gson.*;
import com.rabbitmq.client.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.thelak.route.message.IMessageService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

@Service
public class MessageService implements IMessageService {

    private final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) -> {
                return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'[HH][:mm][[:ss][.SSS]]").withResolverStyle(ResolverStyle.LENIENT));
            })
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (dt, type, jsonDeserializationContext) -> {
                return new JsonPrimitive(dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'[HH][:mm][[:ss][.SSS]]").withResolverStyle(ResolverStyle.LENIENT)));
            })
            .create();

    ConnectionFactory factory;
    private Channel channel;

    @Value("${spring.rabbitmq.host:#{null}}")
    private String queueHost;

    @Value("${spring.rabbitmq.port:#{null}}")
    private Integer queuePort;

    @Value("${spring.rabbitmq.username:#{null}}")
    private String queueUsername;

    @Value("${spring.rabbitmq.password:#{null}}")
    private String queuePassword;

    @PostConstruct
    private void init() {

        try {
            factory = new ConnectionFactory();
            factory.setHost(queueHost);
            factory.setPort(queuePort);
            factory.setUsername(queueUsername);
            factory.setPassword(queuePassword);

            Connection connection = factory.newConnection();
            channel = connection.createChannel();
        } catch (Exception ex) {

        }
    }

    @Override
    public <TMessageType> boolean publish(String queueName, TMessageType messageObject) {
        try {
            if (!channel.isOpen()) {
                reconnect();
            }
            channel.basicPublish("", queueName,
                    new AMQP.BasicProperties.Builder()
                            .contentEncoding(StandardCharsets.UTF_8.displayName())
                            .timestamp(java.sql.Timestamp.from(Instant.now()))
                            .contentType("application/json")
                            .type(messageObject.getClass().getSimpleName())
                            .build()
                    , gson.toJson(messageObject).getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private void reconnect() {

        init();
    }

    static class PingMessage {

    }

    static class Worker<TMessageType> extends DefaultConsumer {
        Function<TMessageType, Boolean> callback;
        final ExecutorService executorService;
        private final Class<TMessageType> messageTypeClass;

        private final Gson gson = new Gson();

        public Worker(Channel c, int threadCount, Class<TMessageType> messageTypeClass, Function<TMessageType, Boolean> callback) throws Exception {
            super(c);
            this.callback = callback;
            this.messageTypeClass = messageTypeClass;
            executorService = Executors.newFixedThreadPool(threadCount);
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

            Runnable task = () -> {
                try {
                    TMessageType messageObject = Optional.ofNullable(body)
                            .map(item -> new String(item, StandardCharsets.UTF_8))
                            .map(message -> gson.fromJson(message, messageTypeClass))
                            .orElse(null);

                } catch (Exception ex) {

                }

                try {
                    getChannel().basicAck(envelope.getDeliveryTag(), false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            executorService.submit(task);
        }
    }

    public <TMessageType> Closeable subscribe(String queueName, int threadCount, Class<TMessageType> messageClass, Function<TMessageType, Boolean> callback) {
        try {
            Channel consumeChannel = factory.newConnection().createChannel();
            consumeChannel.basicQos(50, true);
            String consumerId = consumeChannel.basicConsume(queueName, false, new Worker<>(consumeChannel, threadCount, messageClass, callback));
            return () -> consumeChannel.basicCancel(consumerId);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;

    }

    @Override
    public <TMessageType> Closeable subscribeBatch(String queueName, int batchSize, Class<TMessageType> messageClass, Function<List<TMessageType>, Boolean> callback) {
        try {
            Channel consumeChannel = factory.newConnection().createChannel();
            consumeChannel.basicQos(batchSize);
            final LinkedList<TMessageType> batch = new LinkedList<>();
            final Instant[] startTime = {Instant.now()};

            final String consumerId = consumeChannel.basicConsume(queueName, false, new DefaultConsumer(consumeChannel) {

                javax.swing.Timer timer = new javax.swing.Timer(2000, $ -> {
                    try {
                        publish(queueName, new PingMessage());
                    } catch (Exception e) {
                    }
                });

                @PreDestroy
                private void destroy() {
                    timer.stop();
                }

                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                    final String messageTypeName = properties.getType() == null ? "null" : messageClass.getSimpleName();
                    final String encoding = properties.getContentEncoding() == null ? StandardCharsets.UTF_8.displayName() : properties.getContentEncoding();
                    Charset charset = Charset.forName(encoding);


                    if (messageTypeName.equals(properties.getType())) {

                        try {

                            TMessageType messageObject = Optional.ofNullable(body)
                                    .map(item -> new String(item, charset))
                                    .map(message -> gson.fromJson(message, messageClass))
                                    .orElse(null);

                            if (messageObject != null) {

                                pushMessage(messageObject);
                            } else {
                                System.err.println(String.format("unsupported message format \"%s\"", body == null ? "null" : new String(body, StandardCharsets.UTF_8)));
                                getChannel().basicAck(envelope.getDeliveryTag(), false);
                            }

                            processBatchMessages(envelope);

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else if (PingMessage.class.getSimpleName().equals(properties.getType())) {
                        processBatchMessages(envelope);
                    } else {
                        System.err.println(String.format("unsupported message type \"%s\"", messageTypeName));
                        getChannel().basicAck(envelope.getDeliveryTag(), false);
                    }
                }

                private void pushMessage(TMessageType messageObject) {
                    batch.add(messageObject);
                    if (timer.isRepeats())
                        timer.setRepeats(false); // Only execute once
                    timer.restart();
                }

                private void processBatchMessages(Envelope envelope) {
                    if ((batch.size() >= batchSize) || (batch.size() > 0 && Duration.between(startTime[0], Instant.now()).getSeconds() > 1)) {
                        try {
                            if (callback.apply(batch)) {
                                batch.clear();
                                timer.stop();
                                getChannel().basicAck(envelope.getDeliveryTag(), true);
                                startTime[0] = Instant.now();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            });

            return () -> consumeChannel.basicCancel(consumerId);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}

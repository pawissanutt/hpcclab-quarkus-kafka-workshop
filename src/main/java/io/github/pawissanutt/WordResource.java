package io.github.pawissanutt;

import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import org.eclipse.microprofile.reactive.messaging.*;
import org.jboss.resteasy.reactive.RestQuery;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@Path("word")
public class WordResource {

    @Channel("words-out")
    Emitter<String> emitter;

    @PUT
    public void submit(@RestQuery String word) {
        if (word==null) word = "hello world";
        emitter.send(KafkaRecord.of(word, word));
    }

    @Channel("uppercase-in")
    Multi<String> publisher;

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Multi<String> subscribe() {
        return publisher;
    }

    @Incoming("words-in")
    @Outgoing("uppercase-out")
    public Message<String> toUpperCase(Message<String> message) {
        return message.withPayload(message.getPayload().toUpperCase());
    }

}

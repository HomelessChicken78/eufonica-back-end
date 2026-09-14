package it.eufonica.catalogcommandservice.outbox;

public interface OutboxEventPublisherService {
    /**
     * Publishes an event by persisting it to the outbox.
     *
     * @param aggregateId the identifier of the aggregate associated with the event
     * @param eventType the type of the event (e.g. {@code ArtistCreatedEvent})
     * @param topic the destination topic to which the event will be published
     * @param event the event payload
     *
     * @throws it.eufonica.catalogcommandservice.exception.server.InternalServerErrorException
     * if the event's payload cannot be serialized into a JSON
     */
    public void publish(String aggregateId, String eventType, String topic, Object event);
}

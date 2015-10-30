package global.zombieinvasion.pubsub

import akka.actor.ActorRef
import akka.contrib.pattern.DistributedPubSubExtension
import akka.contrib.pattern.DistributedPubSubMediator
import global.zombieinvasion.BaseActor
import global.zombieinvasion.pubsub.event.Event
import global.zombieinvasion.pubsub.event.SendToAll
import global.zombieinvasion.pubsub.event.SendToLocal
import global.zombieinvasion.pubsub.event.SendToOne
import global.zombieinvasion.pubsub.event.Subscribe
import global.zombieinvasion.pubsub.event.UnSubscribe
import grails.util.Holders
import groovy.util.logging.Log4j

@Log4j
class Subscription extends BaseActor {

    String topic

    def grailsEvents = Holders.applicationContext.getBean("grailsEvents")

    ActorRef mediator = DistributedPubSubExtension.get(getContext().system()).mediator()

    public Subscription(Event message) {
        this.topic = message.topic // set topic for this actor instance (1 instance per unique topic)
    }

    @Override
    void onReceive(Object message) {

        if (message instanceof Subscribe) {

            log.debug("Subscribe -> topic: ${message.topic} | payload: ${message.payload.toString()}")
            mediator.tell(new DistributedPubSubMediator.Put(getSelf()), self)

        } else if (message instanceof SendToAll) {

            log.debug("SendToAll -> topic: ${message.topic} | payload:  ${message.payload.toString()}")
            mediator.tell(new DistributedPubSubMediator.SendToAll("/user/SubscriberManager/${message.serverTopic}", new SendToLocal(message), false), self)

        } else if (message instanceof SendToOne) {

            log.debug("SendToOne ->  topic: ${message.topic} | payload: ${message.payload.toString()}")
            mediator.tell(new DistributedPubSubMediator.Send("/user/SubscriberManager/${message.serverTopic}", new SendToLocal(message), true), self)

        } else if (message instanceof SendToLocal){

            log.debug("SendToLocal ->  topic: ${message.topic} | payload: ${message.payload.toString()}")
            grailsEvents.event(null, message.serverTopic, message, null, null)

        } else if (message instanceof UnSubscribe) {

            mediator.tell(new DistributedPubSubMediator.Remove("/user/SubscriberManager/${message.serverTopic}"), self)
            context.unwatch(self)
            context.stop(self)

        } else {

            unhandled(message)

        }

    }

}
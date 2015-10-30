package global.zombieinvasion.pubsub

import global.zombieinvasion.BaseActor
import akka.actor.Props
import global.zombieinvasion.pubsub.event.Event

class SubscriptionManager extends BaseActor {

    public SubscriptionManager(){
        log.info("SubscriberManagerActor() Initialized")
    }

    @Override
    void onReceive(Object message) throws Exception {

        // case where all subscribers handle the message
        if (message instanceof Event) {

            // check if subscriber is already created
            def subscription = getContext().getChild("${message.serverTopic}")

            if (subscription){ // tell message to existing subscriber
                subscription.tell(message, self)
            }
            else {  // create subscription for topic and tell message
                context.actorOf(Props.create(Subscription.class, message), "${message.serverTopic}").tell(message, self)
            }

        }
        else {

            unhandled(message)

        }
    }
}



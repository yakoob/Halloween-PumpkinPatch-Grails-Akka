package global.zombieinvasion.akka

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.cluster.Cluster
import akka.cluster.ClusterEvent
import global.zombieinvasion.halloween.DeviceUriDispatcher
import global.zombieinvasion.halloween.HalloweenManager
import global.zombieinvasion.pubsub.SubscriptionManager
import global.zombieinvasion.pubsub.event.SendToAll
import global.zombieinvasion.pubsub.event.SendToLocal
import global.zombieinvasion.pubsub.event.SendToOne
import global.zombieinvasion.pubsub.event.Subscribe
import global.zombieinvasion.pubsub.event.UnSubscribe
import groovy.util.logging.Log4j
import javax.annotation.PreDestroy

@Log4j
class AkkaService {

    static transactional = false

    private static ActorSystem system
    private static final ActorRef ACTOR_NO_SENDER = ActorRef.noSender()

    ActorRef clusterListener
    ActorRef halloweenManager
    ActorRef subscriberManager
    ActorRef deviceUriDispatcher

    void init() {
        system = ActorSystem.create("halloween")
        def cluster = Cluster.get(system)
        actorSetup()
        subscribeToGlobalSubscriptions()
        cluster.subscribe(clusterListener, ClusterEvent.MemberEvent.class, ClusterEvent.UnreachableMember.class, ClusterEvent.LeaderChanged.class)
    }

    ActorSystem getSystem() {
        return system
    }

    @PreDestroy
    void destroy() {
        system?.shutdown()
        system = null
        log.error("destroying Akka ActorSystem: done.")
    }

    void actorSetup(){
        clusterListener = actorOf(ClusterListener, "ClusterListener")
        halloweenManager = actorOf(HalloweenManager, "HalloweenManger")
        subscriberManager = actorOf(SubscriptionManager, "SubscriberManager")
        deviceUriDispatcher = actorOf(DeviceUriDispatcher, "DeviceUriDispatcher")
    }

    // subscribe to a topic in our distributed application
    void subscribe(String topic){
        def message = new Subscribe(topic)
        subscriberManager.tell(message, actorNoSender())
    }

    // un-subscribe to a topic in our distributed application
    void unsubscribe(String topic){
        def message = new UnSubscribe(topic)
        subscriberManager?.tell(message, actorNoSender())
    }

    // send to a random server in the cluster subscribed to this topic
    void sendToOne(String topic, payload){
        def message = new SendToOne(topic,payload)
        subscriberManager?.tell(message, actorNoSender())
    }

    // send a message to all servers listening to this topic
    void sendToAll(String topic, payload){
        def message = new SendToAll(topic,payload)
        subscriberManager?.tell(message, actorNoSender())
    }

    // send a message to all methods listening to the argument topic within the jvm of this method call
    void sendToLocal(String topic, payload){
        def message = new SendToLocal(topic,payload)
        subscriberManager?.tell(message, actorNoSender())
    }

    // automate subscriptions upon start up configured for this type of server
    def subscribeToGlobalSubscriptions(){
        // subscribe to global topics
        globalSubscriptionTopics.each { topic ->
            println "subscribe to topic: ${topic}"
            subscribe(topic)
        }
    }

    def getGlobalSubscriptionTopics(){
        def subscriptions = []
        subscriptions << "SEND_DEVICE_URI_COMMAND"
        return subscriptions
    }


    ActorRef actorNoSender() {
        return ACTOR_NO_SENDER
    }

    Props props(Class clazz) {
        assert clazz != null
        Props props = Props.create(clazz)
        return props
    }

    ActorRef actorOf(Props props) {
        assert props != null
        assert system != null

        ActorRef actor = system.actorOf(props)
        return actor
    }

    ActorRef actorOf(Props props, String name) {
        assert props != null
        assert name != null

        assert system != null

        ActorRef actor = system.actorOf(props, name)
        return actor
    }

    ActorRef actorOf(Class clazz) {
        assert clazz != null

        Props props = props(clazz)
        assert props != null
        assert system != null

        ActorRef actor = system.actorOf(props)
        return actor
    }

    ActorRef actorOf(Class clazz, String name) {
        assert clazz != null
        assert name != null

        Props props = props(clazz)
        assert props != null
        assert system != null

        ActorRef actor = system.actorOf(props, name)
        return actor
    }

}

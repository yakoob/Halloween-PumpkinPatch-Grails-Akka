package global.zombieinvasion.halloween

import global.zombieinvasion.halloween.event.Event
import grails.util.Holders
import groovy.util.logging.Log4j
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

@Log4j
class MqttClientService implements MqttCallback {

    static MqttClient CLIENT

    def messageSerializerService
    def akkaService

    void init() {
        CLIENT = new MqttClient("tcp://${Holders.config.app.automation.mqtt.host}:${Holders.config.app.automation.mqtt.port}", "halloweenApp", mqttPersistence)
        CLIENT.connect();
        CLIENT.setCallback(this);
        CLIENT.subscribe("#") // subscript to all topics
        log.info "mqtt client connected"
    }

    private MemoryPersistence getMqttPersistence(){
        return new MemoryPersistence()
    }

    @Override
    public void connectionLost(Throwable cause) {
        log.error "connectionLost: ${cause.message} ${cause.stackTrace}"
        sleep(5000)
        CLIENT = null
        init()
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        try {
            Event event = messageSerializerService.serialize(topic, message?.toString())
            if (event)
                akkaService.getHalloweenManager().tell(event, akkaService.actorNoSender())
        } catch (e) {
            log.error e.stackTrace
        }

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        println "deliveryComplete"
        println token.toString()

    }

    /**
     * TODO: use send here instead of posting commands to rest api's to improve performance
     * @return
     */
    def send(){
        MqttMessage message = new MqttMessage();
        message.setPayload("TODO: Test".getBytes());
        CLIENT.publish("topic/todo", message);
    }

}

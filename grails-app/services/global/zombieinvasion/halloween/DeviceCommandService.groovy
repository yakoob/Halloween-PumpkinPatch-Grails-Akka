package global.zombieinvasion.halloween

import global.zombieinvasion.pubsub.event.Event
import grails.events.Listener
import groovy.util.logging.Log4j

@Log4j
class DeviceCommandService {

    static transactional = false

    @Listener(topic = "SEND_DEVICE_URI_COMMAND")
    def send(Event event) {
        def commandReply = new URL( event.payload ).text
        log.debug (commandReply)
    }
}

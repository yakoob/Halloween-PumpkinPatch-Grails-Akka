package global.zombieinvasion.halloween

import global.zombieinvasion.BaseActor
import grails.plugins.rest.client.RestBuilder
import groovy.util.logging.Log4j
import static grails.async.Promises.task

@Log4j
class DeviceUriDispatcher extends BaseActor {

    /**
     * Notifies Rest Api's of commands to be executed
     * Note: if you wish to distribute this job across multiple servers use: akkaService.sendToOne("SEND_DEVICE_URI_COMMAND", uri)
     * @param message
     * @throws Exception
     */
    @Override
    void onReceive(Object message) throws Exception{
        try {
            String uri = message?.uri
            log.debug uri
            if (!uri) {
                log.error("no where to run to")
                return
            }

            if (uri) {
                // akkaService.sendToOne("SEND_DEVICE_URI_COMMAND", uri)
                new URL( uri ).text
            }

        } catch (e) {
            e.printStackTrace()
        }
    }

}


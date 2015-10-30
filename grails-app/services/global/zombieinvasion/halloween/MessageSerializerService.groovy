package global.zombieinvasion.halloween

import global.zombieinvasion.halloween.event.AudioDetection
import global.zombieinvasion.halloween.event.Event
import global.zombieinvasion.halloween.event.Eyes
import global.zombieinvasion.halloween.event.Motion2
import global.zombieinvasion.halloween.event.Pumpkins
import global.zombieinvasion.halloween.event.Motion
import global.zombieinvasion.halloween.event.Music
import global.zombieinvasion.halloween.event.Projector
import global.zombieinvasion.halloween.event.Smoke
import global.zombieinvasion.halloween.event.Strobe
import global.zombieinvasion.halloween.event.VibrationDetection
import groovy.util.logging.Log4j
import grails.converters.JSON

@Log4j
class MessageSerializerService {

   Map nodeMapping = [
           "2":Motion.newInstance(),
           "4":Pumpkins.newInstance(),
           "6":Eyes.newInstance(),
           "5":Music.newInstance(),
           "8":Motion2.newInstance(),
           "10":Strobe.newInstance(),
           "100":Projector.newInstance(),
           "101":Smoke.newInstance(),
           "102":AudioDetection.newInstance(),
           "103":VibrationDetection.newInstance()
   ]

   public Event serialize(String topic, String message){

      if (topic == "arduionoClient_reconnected"){
         log.debug "arduionoClient_reconnected"
         return
      }


      if (topic == "HomeGenie/MQTT.Listeners/Aurduino/command") {
        // do not look at the Module.Describe messages...
        return
      }

      String node = topic.replaceAll("HomeGenie/HomeAutomation.ZWave/","")
      node = node.replaceAll("Aurduino/HomeAutomation.Servo/", "")
      node = node.replaceAll("Aurduino/HomeAutomation.Audio/", "")
      node = node.replaceAll("Aurduino/HomeAutomation.Vibration/", "")
      node = node.replaceAll("/event","")

      if (!node.isNumber()) {
         log.error "log.error Can not derive node from topic... $topic"
         return
      }

      def event = nodeMapping.get(node).clone()

      def JsonObject = JSON.parse(message)

      event.description = JsonObject.Name
      event.value = JsonObject.Value

      if (event instanceof AudioDetection) {

         event.status = Status.ON

      } else if (event instanceof Motion && event.description == "Sensor.Tamper") {

         event.status = Status.ON
         println event

      } else if (event instanceof Motion && event.description != "Sensor.Tamper") {

         return

      } else {

         switch (event.value) {

            case "0":

               event.status = Status.OFF
               break

            case "1":

               event.status = Status.ON
               break

            case {it == "7" && event instanceof Motion} :

               event.status = Status.ON
               break

            case {it.toInteger() > 0 && event instanceof Motion} :

               event.status = Status.ON
               break
         }
      }

      return event

   }

}

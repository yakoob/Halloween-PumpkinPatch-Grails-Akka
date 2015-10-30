package global.zombieinvasion

import global.zombieinvasion.halloween.Status
import global.zombieinvasion.halloween.command.ProjectorOff
import global.zombieinvasion.halloween.command.ProjectorOn
import global.zombieinvasion.halloween.command.SmokeOff
import global.zombieinvasion.halloween.command.SmokeOn
import grails.converters.JSON
import grails.util.Holders

class TestController {

    def akkaService

    def index(){
        render view: "/test/index", model: ['microphoneSensitivity':Holders.config.app.automation.arduino.sound.detectionAvgDeviation.none, 'projector':Holders.config.app.automation.arduino.projector.status, 'smoke':Holders.config.app.automation.arduino.smoke.status]
    }

    def setDetectionAvgDeviation() {

        BigInteger none = params.startingVolume.toInteger()
        BigInteger lite = none * 2
        BigInteger mid = lite * 8
        BigInteger upper = mid * 3

        Holders.config.app.automation.arduino.sound.detectionAvgDeviation.none = none
        Holders.config.app.automation.arduino.sound.detectionAvgDeviation.lite = lite
        Holders.config.app.automation.arduino.sound.detectionAvgDeviation.mid = mid
        Holders.config.app.automation.arduino.sound.detectionAvgDeviation.upper = upper

        render Holders.config.app.automation.arduino.sound.detectionAvgDeviation as JSON
    }

    def off() {
        akkaService.halloweenManager.tell("MOTION_DETECTION_DISABLED", akkaService.actorNoSender())
        akkaService.halloweenManager.tell("STOP", akkaService.actorNoSender())
        render Status.OFF.name()
    }

    def on(){
        akkaService.halloweenManager.tell("MOTION_DETECTION_ENABLED", akkaService.actorNoSender())
        akkaService.halloweenManager.tell("START", akkaService.actorNoSender())
        render Status.ON.name()
    }

    def smokeOn(){
        akkaService.deviceUriDispatcher.tell(SmokeOn.newInstance(), akkaService.actorNoSender())
        render Status.ON.name()
    }

    def smokeOff(){
        akkaService.deviceUriDispatcher.tell(SmokeOff.newInstance(), akkaService.actorNoSender())
        render Status.OFF.name()
    }

    def projectorOn(){
        akkaService.deviceUriDispatcher.tell(ProjectorOn.newInstance(), akkaService.actorNoSender())
        render Status.ON.name()
    }

    def projectorOff(){
        akkaService.deviceUriDispatcher.tell(ProjectorOff.newInstance(), akkaService.actorNoSender())
        render Status.OFF.name()
    }
}

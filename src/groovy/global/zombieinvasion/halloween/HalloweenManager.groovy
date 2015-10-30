package global.zombieinvasion.halloween

import akka.actor.ActorRef
import akka.actor.Props
import global.zombieinvasion.BaseActor
import akka.actor.Cancellable

import global.zombieinvasion.halloween.command.MusicOff
import global.zombieinvasion.halloween.command.MusicOn
import global.zombieinvasion.halloween.command.ProjectorOff
import global.zombieinvasion.halloween.command.ProjectorOn
import global.zombieinvasion.halloween.command.PumpkinsOff
import global.zombieinvasion.halloween.command.PumpkinsOn
import global.zombieinvasion.halloween.command.SmokeOff
import global.zombieinvasion.halloween.command.SmokeOn
import global.zombieinvasion.halloween.command.StrobeOff
import global.zombieinvasion.halloween.command.StrobeOn
import global.zombieinvasion.halloween.event.AudioDetection
import global.zombieinvasion.halloween.event.Event
import global.zombieinvasion.halloween.event.Motion
import global.zombieinvasion.halloween.event.Music
import global.zombieinvasion.halloween.event.Projector
import global.zombieinvasion.halloween.event.Pumpkins
import global.zombieinvasion.halloween.event.Smoke
import global.zombieinvasion.halloween.event.VibrationDetection
import grails.util.Holders
import groovy.util.logging.Log4j
import scala.concurrent.duration.Duration

import java.util.concurrent.TimeUnit

@Log4j
class HalloweenManager extends BaseActor {

    private Status automation = Status.ON
    private Status motion = Status.OFF
    private Status lights = Status.OFF
    private Status music = Status.OFF
    private Status smoke = Status.OFF
    private Status projector = Status.OFF

    private Cancellable motionNotDetectedTimer = null
    private Cancellable smokeOnTimer = null
    private Cancellable smokeOffTimer = null
    private Cancellable musicOnTimer = null
    private Cancellable projectorTimer = null

    boolean smokeIsBlowing = false

    def akkaService = Holders.applicationContext.getBean("akkaService")

    private ActorRef soundDetectionManager = null

    public HalloweenManager(){
        soundDetectionManager = context.system().actorOf(Props.create(SoundDetectionManager.class), "SoundDetectionManager")
        stopAllDevices()
    }

    @Override
    void onReceive(Object message) throws Exception{

        if (message instanceof Event) {

            if (message instanceof Motion) {

                if (automation == Status.OFF) {
                    log.debug "motion can not run while automation is off"
                    return
                }


                log.debug("MOTION DETECTED : $message")

                if (motion == Status.OFF) {
                    cancelMotionTimeoutTimer()

                    if (message.status == Status.ON) {
                        startSmokeOnTimer()
                    }

                }

                motion = message.status

                if (motion == Status.ON){

                    cancelMotionTimeoutTimer()
                    startMotionTimeoutTimer()

                    if (lights == Status.OFF)
                        akkaService.deviceUriDispatcher.tell(PumpkinsOn.newInstance(), self)


                }

                loggingProxy(message)

            } else if (message instanceof Pumpkins) {

                if (automation == Status.OFF)
                    return

                lights = message.status

                if (motion == Status.ON && lights == Status.ON) {
                    startMusicOnTimer()
                }

                loggingProxy(message)

            } else if (message instanceof Music) {

                if (automation == Status.OFF)
                    return

                music = message.status

                if (message.status == Status.ON)
                    startProjectorOnTimer()

                loggingProxy(message)

            } else if (message instanceof Projector) {

                if (automation == Status.OFF)
                    return

                projector = message.status

                if (projector == Status.ON)
                    Holders.config.app.automation.arduino.projector.status = true
                else
                    Holders.config.app.automation.arduino.projector.status = false

                loggingProxy(message)

            } else if (message instanceof Smoke) {

                if (automation == Status.OFF)
                    return

                smoke = message.status

                if (smoke == Status.ON)
                    Holders.config.app.automation.arduino.smoke.status = true
                else
                    Holders.config.app.automation.arduino.smoke.status = false

                loggingProxy(message)

            } else if (message instanceof AudioDetection) {

                soundDetectionManager?.tell(message, self)

            }

        } else if (message instanceof VibrationDetection) {

            println "vibration: ${message.value}"

        } else if (message instanceof String) {

            if (message == "START") {
                log.debug "automation:$automation"
                Motion motion = Motion.newInstance(['value':8,'status':Status.ON])
                motion.status = Status.ON
                self.tell(motion, akkaService.actorNoSender())
            }

            else if (message == "STOP") {
                stopAllDevices()
            }

            else if (message == "START_SMOKE_TIMER"){
                if (!smokeIsBlowing )
                    startSmokeOnTimer(0)
            }

            else if (message == "MOTION_DETECTION_ENABLED") {
                automation = Status.ON
            }


            else if (message == "MOTION_DETECTION_DISABLED") {
                automation = Status.OFF
            }


        }

        else {

            unhandled(message)

        }

    }

    public void startAllDevices() {
        loggingProxy("START ALL DEVICES")
        akkaService.deviceUriDispatcher.tell(SmokeOn.newInstance(), self)
        akkaService.deviceUriDispatcher.tell(PumpkinsOn.newInstance(), self)
        akkaService.deviceUriDispatcher.tell(MusicOn.newInstance(), self)
        akkaService.deviceUriDispatcher.tell(ProjectorOn.newInstance(), self)
        // akkaService.deviceUriDispatcher.tell(EyesOn.newInstance(), self)
    }

    public void stopAllDevices() {

        loggingProxy("STOP ALL DEVICES")

        smokeOnTimer?.cancel()
        smokeOnTimer = null
        smokeOffTimer?.cancel()
        smokeOffTimer = null

        akkaService.deviceUriDispatcher.tell(SmokeOff.newInstance(), self)
        akkaService.deviceUriDispatcher.tell(PumpkinsOff.newInstance(), self)
        akkaService.deviceUriDispatcher.tell(MusicOff.newInstance(), self)
        akkaService.deviceUriDispatcher.tell(ProjectorOff.newInstance(), self)
        
        motion = Status.OFF
        lights = Status.OFF
        music = Status.OFF
        projector = Status.OFF

    }

    public void cancelMotionTimeoutTimer() {
        if (motionNotDetectedTimer) {
            loggingProxy("CANCEL MOTION TIMEOUT TIMER")
            motionNotDetectedTimer?.cancel()
            motionNotDetectedTimer = null
        }

    }

    public void startMotionTimeoutTimer(){

        loggingProxy("TIMER: (START) - MOTION TIMEOUT TIMER")

        motionNotDetectedTimer = akkaService.system.scheduler().scheduleOnce(Duration.create(10, TimeUnit.MINUTES),
                new Runnable() {
                    @Override
                    public void run() {
                        loggingProxy("TIMER: (TRIGGER) - MOTION NOT DETECTED SHUTTING DOWN...")
                        motion = Status.OFF
                        stopAllDevices()
                    }
                }, akkaService.system.dispatcher()
        )
    }

    public void startSmokeOnTimer(Integer howLong=30){
        loggingProxy("TIMER: (START) - SMOKE ON TIMER")

        smokeOnTimer = akkaService.system.scheduler().scheduleOnce(Duration.create(howLong, TimeUnit.SECONDS),
                new Runnable() {
                    @Override
                    public void run() {
                        if (automation == Status.OFF)
                            return
                        loggingProxy("TIMER: (TRIGGER) - SMOKE ON")
                        smokeIsBlowing = true
                        akkaService.deviceUriDispatcher.tell(SmokeOn.newInstance(), self)
                        if(howLong==30)
                            startSmokeOffTimer(2)
                        else
                            startSmokeOffTimer(3)

                    }
                }, akkaService.system.dispatcher()
        )
    }

    public void startSmokeOffTimer(Integer howLong=2){
        loggingProxy("TIMER: (START) - SMOKE OFF TIMER")
        smokeOffTimer = akkaService.system.scheduler().scheduleOnce(Duration.create(howLong, TimeUnit.SECONDS),
                new Runnable() {
                    @Override
                    public void run() {
                        if (automation == Status.OFF)
                            return

                        loggingProxy("TIMER: (TRIGGER) - SMOKE OFF")

                        smokeOffTimer?.cancel()
                        smokeOffTimer = null
                        smokeOnTimer?.cancel()
                        smokeOnTimer = null

                        akkaService.deviceUriDispatcher.tell(SmokeOff.newInstance(), self)
                        akkaService.deviceUriDispatcher.tell(StrobeOff.newInstance(), self)

                        smokeIsBlowing = false

                        startSmokeOnTimer()

                    }
                }, akkaService.system.dispatcher()
        )
    }

    public void startMusicOnTimer(){
        loggingProxy("TIMER: (START) - MUSIC ON TIMER")
        musicOnTimer = akkaService.system.scheduler().scheduleOnce(Duration.create(10, TimeUnit.SECONDS),
                new Runnable() {
                    @Override
                    public void run() {
                        if (automation == Status.OFF)
                            return
                        loggingProxy("TIMER: (TRIGGER) - MUSIC ON")
                        akkaService.deviceUriDispatcher.tell(MusicOn.newInstance(), self)
                    }
                }, akkaService.system.dispatcher()
        )
    }

    public void startProjectorOnTimer(){

        loggingProxy("TIMER: (START) - PROJECTOR ON TIMER")

        projectorTimer = akkaService.system.scheduler().scheduleOnce(Duration.create(2, TimeUnit.SECONDS),
                new Runnable() {
                    @Override
                    public void run() {
                        if (automation == Status.OFF)
                            return
                        loggingProxy("TIMER: (TRIGGER) - PROJECTOR ON")
                        akkaService.deviceUriDispatcher.tell(ProjectorOn.newInstance(), self)
                    }
                }, akkaService.system.dispatcher()
        )

    }


}


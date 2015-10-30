package global.zombieinvasion.halloween

import groovy.util.logging.Log4j
import akka.actor.Cancellable
import global.zombieinvasion.BaseActor
import global.zombieinvasion.halloween.command.*
import global.zombieinvasion.halloween.event.*
import grails.util.Holders
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit
import static grails.async.Promises.task

@Log4j
class SoundDetectionManager extends BaseActor {

    private Cancellable soundNotDetectedTimer = null
    boolean soundNotDetectedTimerRunning = false

    private Status lightOne
    private Status lightTwo
    private Status lightThree

    private soundDetections = []
    Integer soundDetectionsLastSum = 0

    def akkaService = Holders.applicationContext.getBean("akkaService")

    private Integer detectionBufferSize = Holders.config.app.automation.arduino.sound.detectionBuffer.size

    public SoundDetectionManager(){

    }

    @Override
    void onReceive(Object message) throws Exception{

        if (message instanceof AudioDetection) {

            // turn lights off if no sound detected
            if (!soundNotDetectedTimerRunning)
                startSoundNotDetectedTimer()

            Integer val = message?.value?.toInteger()

            /**
             * map audio sensor values to phillips hue bridge values
             */
            if (val<=Holders.config.app.automation.arduino.sound.detectionAvgDeviation.none)         // no sound... 3 or less accounts for the sound of the projector fan: mic mounted here...
                val = 0
            else if(val<=Holders.config.app.automation.arduino.sound.detectionAvgDeviation.lite)     // very light sound
                val = 1
            else if (val>1 && val<100)
                    val = val
            else                // lots of sound
                val = 100

            soundDetections.add(val)

            def sum
            def avg

            if (soundDetections?.size()>=detectionBufferSize+1) {
                sum = soundDetections?.sum()
                avg = (soundDetections?.sum()/detectionBufferSize)
                log.debug "==================================================="
                log.debug "Sound(sum: $sum | avg: $avg)"
                log.debug "==================================================="
                soundDetections.clear()
                soundDetectionsLastSum = sum

                // set the usable value to the average of last 10 (250 milliseconds) of sound detection
                val = avg

            } else {

                log.debug "${soundDetections.toListString()}"
                // return // do not bother continuing this sub-routine if sound detection buffer isn't full

            }

            if (avg<1) { // sound is not detected so turn off the lights if they are on

                // no audio detected turn off the lights 1 & 2 & 3
                if (lightOne == Status.ON || lightOne == null) {
                    lightOne = Status.OFF
                    akkaService.deviceUriDispatcher.tell(Light1Off.newInstance(), self)
                }

                if (lightTwo == Status.ON || lightTwo == null) {
                    lightTwo = Status.OFF
                    akkaService.deviceUriDispatcher.tell(Light2Off.newInstance(), self)

                }

                if (lightThree == Status.ON || lightThree == null) {
                    lightThree = Status.OFF
                    akkaService.deviceUriDispatcher.tell(Light3Off.newInstance(), self)

                }

            } else { // sound is detected cancel the sound not detected timer and turn the lights on if they ar on

                if (sum && avg) {

                    cancelSoundNotDetectedTimer()

                    // if the lights are off turn them on
                    if (lightOne == Status.OFF) {
                        lightOne = Status.ON
                        akkaService.deviceUriDispatcher.tell(Light1On.newInstance(), self)
                        akkaService.deviceUriDispatcher.tell(Light1Level.newInstance([value:val]), self)
                    }

                    if (lightTwo == Status.OFF) {
                        lightTwo = Status.ON
                        akkaService.deviceUriDispatcher.tell(Light2On.newInstance(), self)
                        akkaService.deviceUriDispatcher.tell(Light2Level.newInstance([value:val]), self)
                    }

                    if (lightThree == Status.OFF) {
                        lightThree = Status.ON
                        akkaService.deviceUriDispatcher.tell(Light3On.newInstance(), self)
                        akkaService.deviceUriDispatcher.tell(Light3Level.newInstance([value:val]), self)
                    }

                    getColorCommands(val,sum).each{
                        akkaService.deviceUriDispatcher.tell(it, self)
                    }

                    def shouldSmokeVal = Holders.config.app.automation.arduino.sound.detectionAvgDeviation.upper*2
                    if (sum>=shouldSmokeVal){
                        context?.sender()?.tell("START_SMOKE_TIMER", self)
                        akkaService.deviceUriDispatcher.tell(StrobeOn.newInstance(), self)
                    }

                }

            }

        }

    }

    /**
     * Maps a average of last 10 sound values to a color for lights 1 & 2
     * @param val
     * @return Command
     */
    def getColorCommands(Integer val, Integer sum = 0){

        def res = []

        if (val == 0) {
            // do nothing
        }
        else if (val <= 5) {

            if (lightOne == Status.ON)
                res.add(Light1ColorGreen.newInstance())
            if (lightTwo == Status.ON)
                res.add(Light2ColorGreen.newInstance())
            if (lightThree == Status.ON)
                res.add(Light3ColorGreen.newInstance())

        }
        else if (val <= 10) {

            if (lightOne == Status.ON)
                res.add(Light1ColorBlue.newInstance())
            if (lightTwo == Status.ON)
                res.add(Light2ColorBlue.newInstance())
            if (lightThree == Status.ON)
                res.add(Light3ColorBlue.newInstance())

        }
        else if (val <= 25) {

            if (lightOne == Status.ON)
                res.add(Light1ColorPurple.newInstance())
            if (lightTwo == Status.ON)
                res.add(Light2ColorPurple.newInstance())
            if (lightThree == Status.ON)
                res.add(Light3ColorPurple.newInstance())

        }
        else if (val <= 50) {

            if (lightOne == Status.ON)
                res.add(Light1ColorOrange.newInstance())
            if (lightTwo == Status.ON)
                res.add(Light2ColorPurple.newInstance())
            if (lightThree == Status.ON) {
                res.add(Light3ColorPurple.newInstance())
            }

        }
        else if (val <= 75) {

            if (lightOne == Status.ON)
                res.add(Light1ColorPurple.newInstance())
            if (lightTwo == Status.ON)
                res.add(Light2ColorOrange.newInstance())
            if (lightThree == Status.ON) {
                res.add(Light3ColorPink.newInstance())
            }
        }
        else {

            if (lightOne == Status.ON)
                res.add(Light1ColorPurple.newInstance())
            if (lightTwo == Status.ON)
                res.add(Light2ColorPurple.newInstance())
            if (lightThree == Status.ON)
                res.add(Light3ColorPurple.newInstance())


            if (lightOne == Status.ON)
                res.add(Light1ColorOrange.newInstance())
            if (lightTwo == Status.ON)
                res.add(Light2ColorOrange.newInstance())
            if (lightThree == Status.ON)
                res.add(Light3ColorOrange.newInstance())

            if (lightOne == Status.ON)
                res.add(Light1ColorRed.newInstance())
            if (lightTwo == Status.ON)
                res.add(Light2ColorRed.newInstance())
            if (lightThree == Status.ON)
                res.add(Light3ColorRed.newInstance())


        }



        return res

    }

    public void cancelSoundNotDetectedTimer(){
        soundNotDetectedTimer?.cancel()
        soundNotDetectedTimerRunning = false
    }

    public void startSoundNotDetectedTimer(){
        soundNotDetectedTimerRunning = true
        soundNotDetectedTimer = akkaService.system.scheduler().scheduleOnce(Duration.create(3, TimeUnit.SECONDS),
                new Runnable() {
                    @Override
                    public void run() {

                        if (lightOne == Status.ON)
                            akkaService.deviceUriDispatcher.tell(Light1Off.newInstance(), self)
                        if (lightTwo == Status.ON)
                            akkaService.deviceUriDispatcher.tell(Light2Off.newInstance(), self)
                        if (lightThree == Status.ON)
                            akkaService.deviceUriDispatcher.tell(Light3Off.newInstance(), self)

                        soundNotDetectedTimerRunning = false
                    }
                }, akkaService.system.dispatcher()
        )

    }

}


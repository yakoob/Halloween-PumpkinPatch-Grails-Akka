package global.zombieinvasion.halloween.command

import global.zombieinvasion.halloween.event.Eyes
import global.zombieinvasion.halloween.event.Music
import global.zombieinvasion.halloween.event.Pumpkins
import global.zombieinvasion.halloween.event.Strobe
import grails.util.Holders
import groovy.transform.TupleConstructor

@TupleConstructor
class Command implements Serializable {
    public String node
    public String value
}

class PumpkinsOn extends Command {

    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.ZWave/${Pumpkins.newInstance().node}/Control.On"
    }

}

class PumpkinsOff extends Command {

    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.ZWave/${Pumpkins.newInstance().node}/Control.Off"
    }

}

class MusicOn extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.ZWave/${Music.newInstance().node}/Control.On"
    }
}

class MusicOff extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.ZWave/${Music.newInstance().node}/Control.Off"
    }
}


class StrobeOn extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.ZWave/${Strobe.newInstance().node}/Control.On"
    }
}

class StrobeOff extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.ZWave/${Strobe.newInstance().node}/Control.Off"
    }
}

class ProjectorOn extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.arduino.projector.host}/arduino/servo/6/0"
    }
}

class ProjectorOff extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.arduino.projector.host}/arduino/servo/6/180"
    }
}

class SmokeOn extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.arduino.smoke.host}/arduino/servo/5/25"
    }
}

class SmokeOff extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.arduino.smoke.host}/arduino/servo/5/60"
    }
}

class Light1Level extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/1/Control.Level/$value"
    }
}

class Light2Level extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/2/Control.Level/$value"
    }
}

class Light3Level extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/3/Control.Level/$value"
    }
}


class Light1On extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/1/Control.On"
    }
}

class Light1Off extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/1/Control.Off"
    }
}

class Light2On extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/2/Control.On"
    }
}

class Light2Off extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/2/Control.Off"
    }
}

class Light3On extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/3/Control.On"
    }
}

class Light3Off extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/3/Control.Off"
    }
}

class Light1ColorPurple extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/1/Control.ColorHsb/${Color.PURPLE}/"
    }
}

class Light1ColorBlue extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/1/Control.ColorHsb/${Color.BLUE}/"
    }
}

class Light1ColorGreen extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/1/Control.ColorHsb/${Color.GREEN}/"
    }
}

class Light1ColorPink extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/1/Control.ColorHsb/${Color.PINK}/"
    }
}

class Light1ColorOrange extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/1/Control.ColorHsb/${Color.ORANGE}/"
    }
}

class Light1ColorRed extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/1/Control.ColorHsb/${Color.RED}/"
    }
}

class Light1ColorBlack extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/1/Control.ColorHsb/${Color.BLACK}/"
    }
}

class Light1ColorWhite extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/1/Control.ColorHsb/${Color.WHITE}/"
    }
}

class Light2ColorPurple extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/2/Control.ColorHsb/${Color.PURPLE}/"
    }
}

class Light2ColorBlue extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/2/Control.ColorHsb/${Color.BLUE}/"
    }
}

class Light2ColorGreen extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/2/Control.ColorHsb/${Color.GREEN}/"
    }
}

class Light2ColorPink extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/2/Control.ColorHsb/${Color.PINK}/"
    }
}

class Light2ColorOrange extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/2/Control.ColorHsb/${Color.ORANGE}/"
    }
}

class Light2ColorRed extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/2/Control.ColorHsb/${Color.RED}/"
    }
}

class Light2ColorBlack extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/2/Control.ColorHsb/${Color.BLACK}/"
    }
}

class Light2ColorWhite extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/2/Control.ColorHsb/${Color.WHITE}/"
    }
}

class Light3ColorPurple extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/3/Control.ColorHsb/${Color.PURPLE}/"
    }
}

class Light3ColorBlue extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/3/Control.ColorHsb/${Color.BLUE}/"
    }
}

class Light3ColorGreen extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/3/Control.ColorHsb/${Color.GREEN}/"
    }
}

class Light3ColorPink extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/3/Control.ColorHsb/${Color.PINK}/"
    }
}

class Light3ColorOrange extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/3/Control.ColorHsb/${Color.ORANGE}/"
    }
}

class Light3ColorRed extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/3/Control.ColorHsb/${Color.RED}/"
    }
}

class Light3ColorBlack extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/3/Control.ColorHsb/${Color.BLACK}/"
    }
}

class Light3ColorWhite extends Command {
    public getUri(){
        return "http://${Holders.config.app.automation.homeGenie.host}/api/HomeAutomation.PhilipsHue/3/Control.ColorHsb/${Color.WHITE}/"
    }
}

class Color {
    static String PURPLE = "0.7117647058823499,0.9724025974025973,0.9042207792207793"
    static String BLUE = "0.6562091503267974,0.9529220779220778,0.9334415584415585"
    static String GREEN = "0.27320261437908283,0.9724025974025973,1"
    static String PINK = "0.8215686274509816,1,1"
    static String ORANGE = "0.09542483660130567,1,1"
    static String RED = "0.9797385620915028,1,1"
    static String BLACK = "0,0,0"
    static String WHITE = "0,0,1"
}
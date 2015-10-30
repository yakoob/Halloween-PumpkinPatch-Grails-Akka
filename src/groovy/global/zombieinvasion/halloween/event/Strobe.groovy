package global.zombieinvasion.halloween.event

import global.zombieinvasion.halloween.Device
import global.zombieinvasion.halloween.Type

class Strobe extends Event{

    Type type = Type.POWER_SWITCH
    Device device = Device.LIGHTS
    String node = "10"

}

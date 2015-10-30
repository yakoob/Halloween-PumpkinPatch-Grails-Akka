package global.zombieinvasion.halloween.event

import global.zombieinvasion.halloween.Device
import global.zombieinvasion.halloween.Type
import groovy.transform.ToString

class Smoke extends Event{

    Type type = Type.SERVO
    Device device = Device.SMOKE
    String node = "101"

}

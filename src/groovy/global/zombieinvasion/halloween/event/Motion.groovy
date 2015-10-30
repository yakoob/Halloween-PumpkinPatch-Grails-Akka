package global.zombieinvasion.halloween.event

import global.zombieinvasion.halloween.Device
import global.zombieinvasion.halloween.Type

class Motion extends Event{
    Type type = Type.SENSOR
    Device device = Device.MOTION
    String node = "2"
}

class Motion2 extends Motion {
    String node = "8"
}
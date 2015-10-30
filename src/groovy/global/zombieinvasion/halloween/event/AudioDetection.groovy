package global.zombieinvasion.halloween.event

import global.zombieinvasion.halloween.Device
import global.zombieinvasion.halloween.Type

class AudioDetection extends Event{

    Type type = Type.SENSOR
    Device device = Device.AUDIO
    String node = "102"

}

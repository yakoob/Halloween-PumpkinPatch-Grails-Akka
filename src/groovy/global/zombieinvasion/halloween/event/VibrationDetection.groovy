package global.zombieinvasion.halloween.event

import global.zombieinvasion.halloween.Device
import global.zombieinvasion.halloween.Type

class VibrationDetection extends Event{

    Type type = Type.SENSOR
    Device device = Device.VIBRATION
    String node = "103"

}

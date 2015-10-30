package global.zombieinvasion.halloween.event

import global.zombieinvasion.halloween.Device
import global.zombieinvasion.halloween.Status
import global.zombieinvasion.halloween.Type
import groovy.transform.AutoClone
import groovy.transform.ToString

@AutoClone
@ToString(includeNames = true, includeSuperProperties = true)
abstract class Event implements Serializable {
    String name = this.class.simpleName
    Type type
    Status status
    String node
    Device device
    String description
    String value

}

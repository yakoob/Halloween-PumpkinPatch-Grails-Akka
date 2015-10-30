class BootStrap {

    def akkaService
    def mqttClientService

    def init = { servletContext ->
        akkaService.init()
        mqttClientService.init()
    }

    def destroy = {
    }

}

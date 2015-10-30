package global.zombieinvasion.pubsub.event

import groovy.transform.TupleConstructor

@TupleConstructor
class Event implements Serializable {

    private static final long serialVersionUID = 2345672023264757610L;

    // messaging topic
    String topic

    // messaging message
    def payload

    public String getTopic(){
        if (this.topic)
            return this.topic
        else
            return "SYSTEM"
    }

    public getWebTopic(){
        return formatWebTopic(getTopic())
    }

    public getServerTopic(){
        return formatServerTopic(getTopic())
    }

    // convert topic to a valid akka actor path
    def formatServerTopic(String t){
        if (t){
            def res = t.replace("/topic/", "")
            return res.replaceAll("/", "-")
        }
        return null
    }

    def formatWebTopic(String t) {
        return "/topic/${t.replaceAll("-", "/")}"
    }

    @Override
    public String toString(){
        return "Topic: ${topic} | Payload: ${payload.toString()}"
    }

}

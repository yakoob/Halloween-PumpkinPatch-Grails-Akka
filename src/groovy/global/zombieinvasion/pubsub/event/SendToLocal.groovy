package global.zombieinvasion.pubsub.event

class SendToLocal extends Event {

    private static final long serialVersionUID = 2345672023264757612L;

    public SendToLocal(Event e){
        this.topic = e.topic
        this.payload = e.payload
    }

    public SendToLocal(String t, p){
        this.topic = t
        this.payload = p
    }


}

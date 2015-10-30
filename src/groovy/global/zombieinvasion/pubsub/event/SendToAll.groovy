package global.zombieinvasion.pubsub.event

class SendToAll extends Event {

    private static final long serialVersionUID = 2345672023264757614L;

    public SendToAll(Event e){
        this.topic = e.topic
        this.payload = e.payload
    }
    public SendToAll(String t, p){
        this.topic = t
        this.payload = p
    }
}

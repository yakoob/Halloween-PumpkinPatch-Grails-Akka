package global.zombieinvasion.pubsub.event

class SendToAck extends Event {

    private static final long serialVersionUID = 2345672023264757615L;

    public SendToAck(Event e){
        this.topic = e.topic
        this.payload = e.payload
    }
    public SendToAck(String t, p){
        this.topic = t
        this.payload = p
    }
}

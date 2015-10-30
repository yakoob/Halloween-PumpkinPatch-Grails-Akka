package global.zombieinvasion.pubsub.event

class SendToOne extends Event {

    private static final long serialVersionUID = 2345672023264757613L;

    public SendToOne(Event e){
        this.topic = e.topic
        this.payload = e.payload
    }
    public SendToOne(String t, p){
        this.topic = t
        this.payload = p
    }

}

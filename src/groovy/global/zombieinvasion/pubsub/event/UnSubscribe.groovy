package global.zombieinvasion.pubsub.event

class UnSubscribe extends Event {

    private static final long serialVersionUID = 2345672023264757616L;

    public UnSubscribe(String t){
        this.topic = t
    }

}

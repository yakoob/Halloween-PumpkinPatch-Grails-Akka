package global.zombieinvasion.pubsub.event

class Subscribe extends Event {

    private static final long serialVersionUID = 2345672023264757617L;

    public Subscribe(String t){
        this.topic = t
    }

}

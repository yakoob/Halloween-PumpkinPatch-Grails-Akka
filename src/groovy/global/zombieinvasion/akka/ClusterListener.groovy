package global.zombieinvasion.akka

import akka.cluster.Cluster
import akka.cluster.ClusterEvent
import akka.cluster.ClusterEvent.MemberEvent
import akka.cluster.ClusterEvent.MemberUp
import akka.cluster.ClusterEvent.MemberRemoved
import akka.cluster.ClusterEvent.UnreachableMember
import akka.cluster.ClusterEvent.LeaderChanged
import global.zombieinvasion.BaseActor
import groovy.util.logging.Log4j

@Log4j
class ClusterListener extends BaseActor {
    Cluster cluster = Cluster.get(getContext().system())

    //subscribe to cluster changes
    @Override
    public void preStart() {
        //#subscribe
        cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(),
                MemberEvent.class, UnreachableMember.class)
        //#subscribe
    }

    //re-subscribe when restart
    @Override
    public void postStop() {
        cluster.unsubscribe(getSelf())
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof MemberUp) {
            MemberUp mUp = (MemberUp) message
            log.debug "Member is Up: ${mUp.member().toString()}"
            log.debug "Members: ${cluster.readView().members().toString()}"

        } else if (message instanceof UnreachableMember) {
            UnreachableMember mUnreachable = (UnreachableMember) message
            log.debug "Member detected as unreachable: ${mUnreachable.member().toString()}"

        } else if (message instanceof MemberRemoved) {
            MemberRemoved mRemoved = (MemberRemoved) message
            log.debug "Member is Removed: ${mRemoved.member()}"
            log.debug "Members: ${cluster.readView().members().toString()}"

        } else if (message instanceof MemberEvent) {
            // ignore
            log.debug "memberEvent: ${message.toString()}"

        } else if (message instanceof LeaderChanged) {
            LeaderChanged mLeaderChanged = (LeaderChanged) message
            log.debug "Leader Changed: ${message?.toString()}"
            akkaService.sendToOne("AKKA_CLUSTER_LEADER_CHANGED", mLeaderChanged?.leader?.toString())
            log.debug "Members: ${cluster.readView().members().toString()}"

        } else {
            unhandled(message)
        }

    }


}

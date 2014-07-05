package org.neo4j.extension.ha.events;

import org.neo4j.cluster.InstanceId;
import org.neo4j.cluster.member.ClusterMemberListener;
import org.neo4j.kernel.ha.cluster.HighAvailabilityMemberChangeEvent;
import org.neo4j.kernel.ha.cluster.HighAvailabilityMemberListener;

import java.net.URI;

/**
 * Created by stefan on 05.07.14.
 */
public class HighAvailabilityAndClusterMemberListener implements HighAvailabilityMemberListener, ClusterMemberListener {

    @Override
    public void coordinatorIsElected(InstanceId coordinatorId) {
        System.out.println("coordinatorIsElected " + coordinatorId);
    }

    @Override
    public void memberIsAvailable(String role, InstanceId availableId, URI atUri) {
        System.out.println("memberIsAvailable " + role + " " + availableId + " " + atUri);
    }

    @Override
    public void memberIsUnavailable(String role, InstanceId unavailableId) {
        System.out.println("memberIsUnavailable " + role + " " + unavailableId);
    }

    @Override
    public void memberIsFailed(InstanceId instanceId) {
        System.out.println("memberIsFailed " + instanceId);
    }

    @Override
    public void memberIsAlive(InstanceId instanceId) {
        System.out.println("memberIsAlive " + instanceId);
    }

    @Override
    public void masterIsElected(HighAvailabilityMemberChangeEvent event) {
        System.out.println("masterIsElected " + event);
    }

    @Override
    public void masterIsAvailable(HighAvailabilityMemberChangeEvent event) {
        System.out.println("masterIsAvailable" + event);
    }

    @Override
    public void slaveIsAvailable(HighAvailabilityMemberChangeEvent event) {
        System.out.println("slaveIsAvailable " + event);
    }

    @Override
    public void instanceStops(HighAvailabilityMemberChangeEvent event) {
        System.out.println("instanceStops " + event);
    }
}

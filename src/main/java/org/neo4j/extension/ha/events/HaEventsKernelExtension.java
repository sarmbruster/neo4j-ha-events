package org.neo4j.extension.ha.events;

import org.neo4j.cluster.member.ClusterMemberEvents;
import org.neo4j.kernel.ha.cluster.HighAvailability;
import org.neo4j.kernel.ha.cluster.HighAvailabilityMemberChangeEvent;
import org.neo4j.kernel.ha.cluster.HighAvailabilityMemberListener;
import org.neo4j.kernel.lifecycle.Lifecycle;
import org.neo4j.kernel.lifecycle.LifecycleAdapter;

/**
 * Created by stefan on 04.07.14.
 */
public class HaEventsKernelExtension extends LifecycleAdapter {

    final private HighAvailability highAvailability;
    final private ClusterMemberEvents clusterMemberEvents;
    final private HighAvailabilityAndClusterMemberListener listener = new HighAvailabilityAndClusterMemberListener();

    public HaEventsKernelExtension(HighAvailability highAvailability, ClusterMemberEvents clusterMemberEvents) {
        this.highAvailability = highAvailability;
        this.clusterMemberEvents = clusterMemberEvents;
    }

    @Override
    public void start() throws Throwable {
        highAvailability.addHighAvailabilityMemberListener(listener);
        clusterMemberEvents.addClusterMemberListener(listener);
    }

    @Override
    public void stop() throws Throwable {
        highAvailability.removeHighAvailabilityMemberListener(listener);
        clusterMemberEvents.removeClusterMemberListener(listener);
    }

}

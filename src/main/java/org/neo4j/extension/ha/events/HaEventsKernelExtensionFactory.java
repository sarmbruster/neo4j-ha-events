package org.neo4j.extension.ha.events;

import org.neo4j.cluster.member.ClusterMemberEvents;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.extension.KernelExtensionFactory;
import org.neo4j.kernel.ha.HighlyAvailableGraphDatabase;
import org.neo4j.kernel.ha.cluster.HighAvailability;
import org.neo4j.kernel.lifecycle.Lifecycle;

import java.lang.reflect.Field;

/**
 * Created by stefan on 04.07.14.
 */
public class HaEventsKernelExtensionFactory extends KernelExtensionFactory<HaEventsKernelExtensionFactory.Dependencies> {

    public interface Dependencies {
        GraphDatabaseService getGraphDatabaseService();
        ClusterMemberEvents getClusterMemberEvents();
        HighAvailability getHighAvailability(); // don't use this right now, we need to use the workaround
    }

    public HaEventsKernelExtensionFactory() {
        super("haevents");
    }

    @Override
    public Lifecycle newKernelExtension(Dependencies dependencies) throws Throwable {

        if (dependencies.getGraphDatabaseService() instanceof HighlyAvailableGraphDatabase) {
            HighAvailability highAvailability = workaroundToAccessHighAvailability(dependencies);
            return new HaEventsKernelExtension(highAvailability, dependencies.getClusterMemberEvents());
        }
        return null;
    }

    /**
     * since {@link org.neo4j.kernel.ha.HighlyAvailableGraphDatabase} does not expose the {@link org.neo4j.kernel.ha.cluster.HighAvailability} via getDependencies()
     * we access it via a private field per reflection.
     * In future this should no longer be necessary if the upstream issue is fixed.
     * @param dependencies
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private HighAvailability workaroundToAccessHighAvailability(Dependencies dependencies) throws NoSuchFieldException, IllegalAccessException {
        HighlyAvailableGraphDatabase ha = (HighlyAvailableGraphDatabase)dependencies.getGraphDatabaseService();
        Field memberStateMachineField = ha.getClass().getDeclaredField("memberStateMachine");
        memberStateMachineField.setAccessible(true);
        return (HighAvailability) memberStateMachineField.get(ha);
    }

}

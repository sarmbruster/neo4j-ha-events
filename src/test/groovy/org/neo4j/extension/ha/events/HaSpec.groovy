package org.neo4j.extension.ha.events

import org.junit.Rule
import org.neo4j.extension.spock.WithNeo4jTransaction
import spock.lang.Specification

/**
 * Created by stefan on 04.07.14.
 */
class HaSpec extends Specification {

    @Delegate
    @Rule
    Neo4jHAResource neo4j = new Neo4jHAResource(
            config: [
                        [
                                graphDbDir: "data/graph.db.1",
                                'ha.server_id' : '1',
                                'ha.initial_hosts': ':5000,:5001:,:5002',
                                'ha.cluster_server': ':5000',
                        ],
                        [
                                graphDbDir: "data/graph.db.2",
                                'ha.server_id' : '2',
                                'ha.initial_hosts': ':5000,:5001:,:5002',
                                'ha.cluster_server': ':5001',
                        ],
                        [
                                graphDbDir: "data/graph.db.3",
                                'ha.server_id' : '3',
                                'ha.initial_hosts': ':5000,:5001:,:5002',
                                'ha.cluster_server': ':5002',
                        ],
            ]
    )

    @WithNeo4jTransaction
    def "should start up fine with extension enabled"() {
        expect:
        neo4j.graphDatabaseService.createNode()
    }

}

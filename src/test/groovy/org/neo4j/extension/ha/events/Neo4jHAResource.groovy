package org.neo4j.extension.ha.events

import groovyx.gpars.GParsPool
import org.junit.rules.ExternalResource
import org.neo4j.cypher.javacompat.ExecutionEngine
import org.neo4j.extension.spock.GraphDatabaseServiceProvider
import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.graphdb.factory.HighlyAvailableGraphDatabaseFactory

/**
 * Created by stefan on 04.07.14.
 */
class Neo4jHAResource extends ExternalResource implements GraphDatabaseServiceProvider {

    def config = []
    String graphDbDir
    def graphDatabaseServices

    @Lazy
    ExecutionEngine executionEngine = new ExecutionEngine(graphDatabaseService)

    @Override
    protected void before() throws Throwable {

        GParsPool.withPool {
            graphDatabaseServices = config.collectParallel {
                def builder = new HighlyAvailableGraphDatabaseFactory().newHighlyAvailableDatabaseBuilder(it.graphDbDir)
                builder.setConfig(it).newGraphDatabase()
            }
        }


        String.metaClass.cypher = { -> executionEngine.execute(delegate) }
        String.metaClass.cypher = { Map params -> executionEngine.execute(delegate, params) }
    }

    @Override
    protected void after() {
        GParsPool.withPool {
            graphDatabaseServices.eachParallel { it.shutdown()}
        }
    }

    @Override
    GraphDatabaseService getGraphDatabaseService() {
        graphDatabaseServices[0]
    }
}

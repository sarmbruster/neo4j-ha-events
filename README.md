neo4j-ha-events
===============

Purpose
-------

The purpose of this small project is to show how to subscribe to internal cluster events in a Neo4j HA cluster setup.
By default there is just a way to poll cluster status, either by [JMX](http://docs.neo4j.org/chunked/stable/jmx-mxbeans.html#jmx-high-availability) or by using [`HighAvailableGraphDatabase.isMaster()`](https://github.com/neo4j/neo4j/blob/master/enterprise/ha/src/main/java/org/neo4j/kernel/ha/HighlyAvailableGraphDatabase.java#L635) or [`HighAvailableGraphDatabase.role()`](https://github.com/neo4j/neo4j/blob/master/enterprise/ha/src/main/java/org/neo4j/kernel/ha/HighlyAvailableGraphDatabase.java#L630).

Couple of people mentioned they want to have a non-polling approach to get notified in case something changes in the cluster.

Fortunately Neo4j has such a facility as a internal non-public component. This small project shows how we can get access to it. 



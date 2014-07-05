neo4j-ha-events
===============

Purpose
-------

The purpose of this small project is to show how to subscribe to internal cluster events in a Neo4j HA cluster setup.
By default there is just a way to poll cluster status, either by [JMX](http://docs.neo4j.org/chunked/stable/jmx-mxbeans.html#jmx-high-availability) or by using [`HighAvailableGraphDatabase.isMaster()`](https://github.com/neo4j/neo4j/blob/master/enterprise/ha/src/main/java/org/neo4j/kernel/ha/HighlyAvailableGraphDatabase.java#L635) or [`HighAvailableGraphDatabase.role()`](https://github.com/neo4j/neo4j/blob/master/enterprise/ha/src/main/java/org/neo4j/kernel/ha/HighlyAvailableGraphDatabase.java#L630).

Couple of people mentioned they want to have a non-polling approach to get notified in case something changes in the cluster.

Fortunately Neo4j has such a facility as a internal non-public component. This small project shows how we can get access to it. 

Installation
------------

Clone the github code and run `./gradlew assemble`. Copy the resulting jar file from `build/libs` to your Neo4j's `plugin` folder. The catched events are written to `$NEO4J/data/log/console.log` with a date prefix.

Description
-----------

A Neo4j kernel extension is a class subclassing [KernelExtensionFactory](https://github.com/neo4j/neo4j/blob/master/community/kernel/src/main/java/org/neo4j/kernel/extension/KernelExtensionFactory.java) and being registered via JVM's service loader capability. The dependencies are injected by the superclass.

Neo4j's internal dependency mechanism already gives access to an interface called [ClusterMemberEvents](https://github.com/neo4j/neo4j/blob/master/enterprise/cluster/src/main/java/org/neo4j/cluster/member/ClusterMemberEvents.java) enabling to register customer listeners. 
Additionally Neo4j HA contains a interface called [HighAvailability](https://github.com/neo4j/neo4j/blob/master/enterprise/ha/src/main/java/org/neo4j/kernel/ha/cluster/HighAvailability.java) allowing to register another listener. Unfortunately the latter is not exposed via the dependency mechanism. As a workaround we access a private field on HighAvailableGraphDatabase to get access.

[HaEventsKernelExtension](https://github.com/sarmbruster/neo4j-ha-events/blob/master/src/main/java/org/neo4j/extension/ha/events/HaEventsKernelExtension.java) is reponsible for registering a kind of dummy event listener [HighAvailabilityAndClusterMemberListener](https://github.com/sarmbruster/neo4j-ha-events/blob/master/src/main/java/org/neo4j/extension/ha/events/HighAvailabilityAndClusterMemberListener.java) implementing both kind of event listeners. As of now it just traces the received events to stdout.

WARNING: do not add complex and time consuming code into the listeners as they might influence the cluster.

For the included trivial test, the output looks like this

```
masterIsElected HA Member State Event[ old state: PENDING, new state: TO_MASTER, server cluster URI: 1, server HA URI: null]
coordinatorIsElected 1
masterIsElected HA Member State Event[ old state: TO_MASTER, new state: TO_MASTER, server cluster URI: 1, server HA URI: null]
coordinatorIsElected 1
masterIsElected HA Member State Event[ old state: TO_MASTER, new state: TO_MASTER, server cluster URI: 1, server HA URI: null]
coordinatorIsElected 1
masterIsAvailableHA Member State Event[ old state: TO_MASTER, new state: MASTER, server cluster URI: 1, server HA URI: ha://127.0.1.1:6001?serverId=1]
memberIsAvailable master 1 ha://127.0.1.1:6001?serverId=1
masterIsElected HA Member State Event[ old state: PENDING, new state: PENDING, server cluster URI: 1, server HA URI: null]
coordinatorIsElected 1
masterIsElected HA Member State Event[ old state: PENDING, new state: PENDING, server cluster URI: 1, server HA URI: null]
coordinatorIsElected 1
masterIsAvailableHA Member State Event[ old state: PENDING, new state: TO_SLAVE, server cluster URI: 1, server HA URI: ha://127.0.1.1:6001?serverId=1]
memberIsAvailable master 1 ha://127.0.1.1:6001?serverId=1
memberIsAvailable backup 1 backup://127.0.1.1:6362
memberIsAvailable backup 1 backup://127.0.1.1:6362
slaveIsAvailable HA Member State Event[ old state: MASTER, new state: MASTER, server cluster URI: 3, server HA URI: ha://127.0.1.1:6002?serverId=3]
memberIsAvailable slave 3 ha://127.0.1.1:6002?serverId=3
slaveIsAvailable HA Member State Event[ old state: TO_SLAVE, new state: SLAVE, server cluster URI: 3, server HA URI: ha://127.0.1.1:6002?serverId=3]
memberIsAvailable slave 3 ha://127.0.1.1:6002?serverId=3
masterIsAvailableHA Member State Event[ old state: SLAVE, new state: SLAVE, server cluster URI: 1, server HA URI: ha://127.0.1.1:6001?serverId=1]
memberIsAvailable master 1 ha://127.0.1.1:6001?serverId=1
masterIsAvailableHA Member State Event[ old state: MASTER, new state: MASTER, server cluster URI: 1, server HA URI: ha://127.0.1.1:6001?serverId=1]
memberIsAvailable master 1 ha://127.0.1.1:6001?serverId=1
slaveIsAvailable HA Member State Event[ old state: SLAVE, new state: SLAVE, server cluster URI: 3, server HA URI: ha://127.0.1.1:6002?serverId=3]
memberIsAvailable slave 3 ha://127.0.1.1:6002?serverId=3
memberIsAvailable backup 1 backup://127.0.1.1:6362
slaveIsAvailable HA Member State Event[ old state: MASTER, new state: MASTER, server cluster URI: 3, server HA URI: ha://127.0.1.1:6002?serverId=3]
memberIsAvailable slave 3 ha://127.0.1.1:6002?serverId=3
memberIsAvailable backup 1 backup://127.0.1.1:6362
masterIsElected HA Member State Event[ old state: PENDING, new state: PENDING, server cluster URI: 1, server HA URI: null]
coordinatorIsElected 1
masterIsElected HA Member State Event[ old state: PENDING, new state: PENDING, server cluster URI: 1, server HA URI: null]
coordinatorIsElected 1
masterIsAvailableHA Member State Event[ old state: PENDING, new state: TO_SLAVE, server cluster URI: 1, server HA URI: ha://127.0.1.1:6001?serverId=1]
memberIsAvailable master 1 ha://127.0.1.1:6001?serverId=1
memberIsAvailable backup 1 backup://127.0.1.1:6362
slaveIsAvailable HA Member State Event[ old state: TO_SLAVE, new state: TO_SLAVE, server cluster URI: 3, server HA URI: ha://127.0.1.1:6002?serverId=3]
memberIsAvailable slave 3 ha://127.0.1.1:6002?serverId=3
masterIsAvailableHA Member State Event[ old state: TO_SLAVE, new state: TO_SLAVE, server cluster URI: 1, server HA URI: ha://127.0.1.1:6001?serverId=1]
memberIsAvailable master 1 ha://127.0.1.1:6001?serverId=1
slaveIsAvailable HA Member State Event[ old state: TO_SLAVE, new state: TO_SLAVE, server cluster URI: 3, server HA URI: ha://127.0.1.1:6002?serverId=3]
memberIsAvailable slave 3 ha://127.0.1.1:6002?serverId=3
memberIsAvailable backup 1 backup://127.0.1.1:6362
masterIsElected HA Member State Event[ old state: TO_SLAVE, new state: TO_SLAVE, server cluster URI: 1, server HA URI: null]
coordinatorIsElected 1
masterIsElected HA Member State Event[ old state: SLAVE, new state: SLAVE, server cluster URI: 1, server HA URI: null]
masterIsElected HA Member State Event[ old state: MASTER, new state: MASTER, server cluster URI: 1, server HA URI: null]
coordinatorIsElected 1
coordinatorIsElected 1
masterIsAvailableHA Member State Event[ old state: TO_SLAVE, new state: TO_SLAVE, server cluster URI: 1, server HA URI: ha://127.0.1.1:6001?serverId=1]
masterIsAvailableHA Member State Event[ old state: SLAVE, new state: SLAVE, server cluster URI: 1, server HA URI: ha://127.0.1.1:6001?serverId=1]
memberIsAvailable master 1 ha://127.0.1.1:6001?serverId=1
memberIsAvailable master 1 ha://127.0.1.1:6001?serverId=1
masterIsAvailableHA Member State Event[ old state: MASTER, new state: MASTER, server cluster URI: 1, server HA URI: ha://127.0.1.1:6001?serverId=1]
memberIsAvailable master 1 ha://127.0.1.1:6001?serverId=1
slaveIsAvailable HA Member State Event[ old state: TO_SLAVE, new state: TO_SLAVE, server cluster URI: 3, server HA URI: ha://127.0.1.1:6002?serverId=3]
memberIsAvailable slave 3 ha://127.0.1.1:6002?serverId=3
slaveIsAvailable HA Member State Event[ old state: SLAVE, new state: SLAVE, server cluster URI: 3, server HA URI: ha://127.0.1.1:6002?serverId=3]
memberIsAvailable slave 3 ha://127.0.1.1:6002?serverId=3
memberIsAvailable backup 1 backup://127.0.1.1:6362
memberIsAvailable backup 1 backup://127.0.1.1:6362
slaveIsAvailable HA Member State Event[ old state: MASTER, new state: MASTER, server cluster URI: 3, server HA URI: ha://127.0.1.1:6002?serverId=3]
memberIsAvailable slave 3 ha://127.0.1.1:6002?serverId=3
memberIsAvailable backup 1 backup://127.0.1.1:6362
slaveIsAvailable HA Member State Event[ old state: SLAVE, new state: SLAVE, server cluster URI: 2, server HA URI: ha://127.0.1.1:6003?serverId=2]
memberIsAvailable slave 2 ha://127.0.1.1:6003?serverId=2
slaveIsAvailable HA Member State Event[ old state: MASTER, new state: MASTER, server cluster URI: 2, server HA URI: ha://127.0.1.1:6003?serverId=2]
memberIsAvailable slave 2 ha://127.0.1.1:6003?serverId=2
slaveIsAvailable HA Member State Event[ old state: TO_SLAVE, new state: SLAVE, server cluster URI: 2, server HA URI: ha://127.0.1.1:6003?serverId=2]
memberIsAvailable slave 2 ha://127.0.1.1:6003?serverId=2
instanceStops HA Member State Event[ old state: MASTER, new state: PENDING, server cluster URI: null, server HA URI: null]
instanceStops HA Member State Event[ old state: SLAVE, new state: PENDING, server cluster URI: null, server HA URI: null]
memberIsUnavailable master 1
memberIsUnavailable master 1
memberIsUnavailable master 1
instanceStops HA Member State Event[ old state: SLAVE, new state: PENDING, server cluster URI: null, server HA URI: null]
memberIsUnavailable slave 3
memberIsUnavailable slave 3
memberIsUnavailable slave 3
memberIsUnavailable backup 1
memberIsUnavailable backup 1
memberIsUnavailable slave 2
coordinatorIsElected 2
```

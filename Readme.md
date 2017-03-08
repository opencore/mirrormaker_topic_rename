# Message Handler For MirrorMaker to change topic names

MirrorMaker is the de-facto standard tool for mirroring Kafka topics between different clusters. By default it will always keep the topic name the same between clusters, but there can be cases in which it is necessary to change a topic name during the mirroring process.
This repository shows how this can easily be achieved by implementing a custom MessageHandler and using this with MirrorMaker.

This work is based on [code](https://github.com/gwenshap/kafka-examples/blob/master/MirrorMakerHandler/README.md) from Gwen Shapiras example repository, so most of the credit goes to her.

Setup
=====
To use this code with MirrorMaker simply build the jar from this repository and put it on the machine that you will be running MirrorMaker from.

Usage
=====
Before starting MirrorMaker you need to include the jar file in your classpath:

``` bash
export CLASSPATH=/home/sliebau/mmchangetopic-1.0-SNAPSHOT.jar
```

Having done this, you can start MirrorMaker with your usual configuration and just add the handler classname as well as a configuration string to tell the handler, which topics to rename and which to leave as is.

The two parameters you will need to add are:

**--message.handler**:

This takes the classname of the handler class to use, if you have not changed anything in the code the value from the example below should work.

**--message.handler.args**:

This is used to configure which topics to change, it should have the following format: sourcetopic1,targettopic1;sourcetopic2,targettopic2;...

It is a semicolon separated list of string pairs, which are itself separated by a comma. In the above example any message from the topic *sourcetopic1* would be mirrored to *targettopic1* (and the same with *2*) on the target cluster. Any other topics that MirrorMaker is following will not be changed and written to a topic of the same name on the target cluster.

``` bash
kafka-mirror-maker --consumer.config consumer.properties --producer.config producer.properties --whitelist test_.* --message.handler com.opencore.RenameTopicHandler --message.handler.args `test_source,test_target;test_source2,test_target2`
```


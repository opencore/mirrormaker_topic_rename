package com.opencore;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import kafka.consumer.BaseConsumerRecord;
import kafka.tools.MirrorMaker;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * An example implementation of MirrorMakerMessageHandler that allows to define a list of topics
 * that should be mirrored into topics with a different name.
 */
public class RenameTopicHandler implements MirrorMaker.MirrorMakerMessageHandler {
  private final HashMap<String, String> topicMap = new HashMap<String, String>();

  /**
   * Constructor that will be passed the args passed on the command line.
   *
   * @param topicList A list of topics that should be mapped to a different topic name on the target cluster.
   *                  Needs to be in the format "oldname,newname;oldname,newname;..."
   */
  public RenameTopicHandler(String topicList) {
    String[] topicAssignments = topicList.split(";");
    for (String topicAssignment : topicAssignments) {
      String[] topicsArray = topicAssignment.split(",");
      if (topicsArray.length == 2) {
        // any other array lenth means malformed arguments were passed in
        topicMap.put(topicsArray[0], topicsArray[1]);
      }
    }
  }

  public List<ProducerRecord<byte[], byte[]>> handle(BaseConsumerRecord record) {
    String targetTopic = null;
    if (topicMap.containsKey(record.topic())) {
      // for this topic the name should be substituted by something else, so return a new record with the changed name
      targetTopic = topicMap.get(record.topic());
    } else {
      // no substitution necessary, return the record with unchanged properties
      targetTopic = record.topic();
    }

    Long timestamp = record.timestamp() == ConsumerRecord.NO_TIMESTAMP ? null : record.timestamp();
    // topic is set correctly at this point, return a list containing the new record with updated parameters
    return Collections.singletonList(new ProducerRecord<byte[], byte[]>(targetTopic, null, timestamp, record.key(), record.value(), record.headers()));
  }
}

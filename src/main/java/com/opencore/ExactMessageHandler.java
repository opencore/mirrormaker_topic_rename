package com.opencore;

import java.util.Collections;
import java.util.List;
import kafka.consumer.BaseConsumerRecord;
import kafka.tools.MirrorMaker;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.record.RecordBatch;

/**
 * An implementation of MirrorMakerMessageHandler that can be used when a topic should be recreated as close
 * to identical as possible.
 * This will preserve topic, partition, timestamp, headers, key and value.
 *
 * Note: by preserving the exact partition this handler bypasses the partitioning algorithm, which means that the
 * target topic has to be set up with at least as many partitions as the source topic, as otherwise messages will
 * be written to non-existent partitions which will fail.
 */
public class ExactMessageHandler implements MirrorMaker.MirrorMakerMessageHandler {
  public List<ProducerRecord<byte[], byte[]>> handle(BaseConsumerRecord record) {
    Long timestamp = record.timestamp() == RecordBatch.NO_TIMESTAMP ? null : record.timestamp();
    return Collections.singletonList(new ProducerRecord<byte[], byte[]>(record.topic(), record.partition(), timestamp, record.key(), record.value(), record.headers()));
  }
}

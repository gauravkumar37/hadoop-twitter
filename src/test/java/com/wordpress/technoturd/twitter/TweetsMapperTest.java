package com.wordpress.technoturd.twitter;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.wordpress.technoturd.twitter.TweetsMapper.Tweet;

/**
 * MRUnit class for testing MR classes
 * 
 * @author GauravKumar
 */
public class TweetsMapperTest {

	MapDriver<LongWritable, Text, LongWritable, IntWritable> mapDriver;
	ReduceDriver<LongWritable, IntWritable, IntWritable, LongWritable> reduceDriver;
	MapReduceDriver<LongWritable, Text, LongWritable, IntWritable, IntWritable, LongWritable> mapReduceDriver;

	@Before
	public void setUp() {
		TweetsMapper mapper = new TweetsMapper();
		TweetsReducer reducer = new TweetsReducer();
		mapDriver = MapDriver.newMapDriver(mapper);
		reduceDriver = ReduceDriver.newReduceDriver(reducer);
		mapReduceDriver = MapReduceDriver.newMapReduceDriver(mapper, reducer);
	}

	@Test
	public void testMapperNormal() {
		mapDriver
			.withInput(
				new LongWritable(1),
				new Text(
					"\"11139539214\",\"\",\"\",\"11130842432\",\"16507391\",\"2010-03-27 11:22:20 +0000\",\"web\",\"RT @MTVIndia: RT: @vinay_vasisht India needs no earth hour, there are enough power-cuts already to take care of it #ifm\""));
		mapDriver.withOutput(new LongWritable(16507391), new IntWritable(1));
		mapDriver.runTest();
	}

	@Test
	public void testMapperEmptyUserId() {
		mapDriver
			.withInput(
				new LongWritable(1),
				new Text(
					"\"70520202441392128\",\"70471797828616195\",\"16452299\",\"\",\"\",\"2011-05-17 16:05:01 +0000\",\"web\",\"@pocketnowtweets lo karlo baat ho gaya kaam mere mobile OS ka :-\\\""));
		mapDriver.runTest();
	}

	@Test
	public void testMapperFirstLine() {
		mapDriver
			.withInput(
				new LongWritable(0),
				new Text(
					"\"tweet_id\",\"in_reply_to_status_id\",\"in_reply_to_user_id\",\"retweeted_status_id\",\"retweeted_status_user_id\",\"timestamp\",\"source\",\"text\",\"expanded_urls\""));
		mapDriver.runTest();
	}

	@Test
	public void testMapperMalformedInput() {
		mapDriver.withInput(new LongWritable(1), new Text("malformed text"));
		Counters counters = new Counters();
		mapDriver.withCounters(counters);
		mapDriver.runTest();
		Counter counter = counters.findCounter(Tweet.CORRUPT_RECORDS);
		Assert.assertEquals("Malformed Input", counter.getValue(), 1L);
	}

	@Test
	public void testReducer() {
		List<IntWritable> values = new ArrayList<IntWritable>();
		values.add(new IntWritable(1));
		values.add(new IntWritable(1));
		values.add(new IntWritable(1));
		reduceDriver.withInput(new LongWritable(16507391), values);
		reduceDriver.withOutput(new IntWritable(3), new LongWritable(16507391));
		reduceDriver.runTest();
	}
}
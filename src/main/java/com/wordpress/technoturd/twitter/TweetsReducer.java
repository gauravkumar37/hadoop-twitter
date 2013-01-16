package com.wordpress.technoturd.twitter;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Reducer class for aggregating counts received from Mapper class.<br>
 * Takes inputs as {@link LongWritable} for user id of the user whose status was retweeted and list of {@link IntWritable}
 * for counts.<br>
 * Outputs {@link IntWritable} for total count and {@link LongWritable} for user id of the user whose status was retweeted.
 * 
 * @author GauravKumar
 */
public class TweetsReducer extends Reducer<LongWritable, IntWritable, IntWritable, LongWritable> {

	@Override
	public void reduce(LongWritable key, Iterable<IntWritable> values, Context context) throws IOException,
		InterruptedException {
		int count = 0;
		for (IntWritable value : values) {
			count += value.get();
		}
		context.write(new IntWritable(count), key);
	}
}
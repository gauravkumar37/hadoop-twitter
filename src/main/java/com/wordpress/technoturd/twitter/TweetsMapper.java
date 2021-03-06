package com.wordpress.technoturd.twitter;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVParser;

/**
 * Mapper class for extracting retweeted userid out of the csv line of tweets.<br>
 * Takes inputs as {@link LongWritable} for byte offset and {@link Text} for line of csv read.<br>
 * Outputs {@link LongWritable} for user id of the user whose status was retweeted and {@link IntWritable} for count 1
 * 
 * @author GauravKumar
 */
public class TweetsMapper extends Mapper<LongWritable, Text, LongWritable, IntWritable> {
	static final Logger LOGGER = Logger.getLogger(TweetsMapper.class);

	enum Tweet {
		CORRUPT_RECORDS
	}

	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		if (key.get() == 0)
			return;
		CSVParser csvParser = new CSVParser();
		String retweetedUserId = null;
		try {
			retweetedUserId = csvParser.parseLine(value.toString())[4];
		} catch (Exception e) {
			try {
				retweetedUserId = csvParser.parseLine(value.toString() + "\"")[4];
			} catch (Exception ex) {
				LOGGER.error("Even after appending an extra \", error still persists. Skipping record with key:" + key.get()
					+ " and value:" + value.toString());
				context.setStatus("Detected corrupt csv record. See logs for more details.");
				context.getCounter(Tweet.CORRUPT_RECORDS).increment(1);
			}
		}
		try {
			if (retweetedUserId != null && !retweetedUserId.equals(""))
				context.write(new LongWritable(Long.parseLong(retweetedUserId)), new IntWritable(1));
		} catch (Exception e) {
			LOGGER.error("Error in converting:\n" + retweetedUserId, e);
		}
	}
}
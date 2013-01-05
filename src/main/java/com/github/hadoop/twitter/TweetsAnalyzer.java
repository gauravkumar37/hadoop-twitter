package com.github.hadoop.twitter;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * Main Job launching class
 * 
 * @author GauravKumar
 */
public class TweetsAnalyzer {

	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.err.println("Usage: " + TweetsAnalyzer.class.toString() + " <input path> <output path>");
			System.exit(-1);
		}

		Job job = new Job();
		job.setJarByClass(TweetsAnalyzer.class);
		job.setJobName("Tweets Analyzer");

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.setMapperClass(TweetsMapper.class);
		job.setReducerClass(TweetsReducer.class);

		job.setMapOutputKeyClass(LongWritable.class);
		job.setMapOutputValueClass(IntWritable.class);
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(LongWritable.class);

		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
package com.wordpress.technoturd.twitter;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

public class TweetsAnalyzerDriver extends Configured implements Tool {
	static final Logger LOGGER = Logger.getLogger(TweetsAnalyzerDriver.class);

	@Override
	public int run(String[] args) throws Exception {
		Configuration conf = getConf();
		Job job = new Job(conf, "Hadoop Twitter Analytics");
		job.setJarByClass(getClass());

		Path input = new Path("data/csv");
		Path output = new Path("target/output");
		LOGGER.debug("Using input path: " + input);
		LOGGER.debug("Using output path: " + output);

		FileSystem fs = FileSystem.getLocal(conf);
		if (fs.exists(output))
			if (fs.delete(output, true))
				LOGGER.info("Output directory: " + output + " deleted.");
			else {
				LOGGER.fatal("Unable to delete output directory: " + output);
				return 1;
			}

		FileInputFormat.addInputPath(job, input);
		FileOutputFormat.setOutputPath(job, output);

		job.setMapperClass(TweetsMapper.class);
		job.setReducerClass(TweetsReducer.class);

		job.setMapOutputKeyClass(LongWritable.class);
		job.setMapOutputValueClass(IntWritable.class);
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(LongWritable.class);

		return job.waitForCompletion(true) ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		System.exit(ToolRunner.run(new TweetsAnalyzerDriver(), args));
	}
}
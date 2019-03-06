package org.myorg;

import java.io.IOException;
import java.util.regex.Pattern;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
//imported in order to get filename
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

//calculates and stores the count of all words in the input files
public class DocWordCount extends Configured implements Tool {

   private static final Logger LOG = Logger .getLogger( DocWordCount.class);
   //main - executes run, which runs the hadoop job
   public static void main( String[] args) throws  Exception {
      int res  = ToolRunner .run( new DocWordCount(), args);
      System .exit(res);
   }

   public int run( String[] args) throws  Exception {
      Job job  = Job .getInstance(getConf(), " docwordcount ");
      job.setJarByClass( this .getClass());
      //sets input path
      FileInputFormat.addInputPaths(job,  args[0]);
      //sets output path
      FileOutputFormat.setOutputPath(job,  new Path(args[ 1]));
      job.setMapperClass( Map .class);
      job.setReducerClass( Reduce .class);
      job.setOutputKeyClass( Text .class);
      job.setOutputValueClass( IntWritable .class);

      return job.waitForCompletion( true)  ? 0 : 1;
   }
   //Map function
   public static class Map extends Mapper<LongWritable ,  Text ,  Text ,  IntWritable > {
      //each word has a sum value of one
      private final static IntWritable one  = new IntWritable( 1);
      private Text word  = new Text();
      //regex to detect indvidual words
      private static final Pattern WORD_BOUNDARY = Pattern .compile("\\s*\\b\\s*");

      public void map( LongWritable offset,  Text lineText,  Context context)
        throws  IOException,  InterruptedException {
          //gets file name, concatenated to word after delimiter
         String filename = ((FileSplit) context.getInputSplit()).getPath().getName();
         //current line
         String line  = lineText.toString();
         Text currentWord  = new Text();
         //temporary string to combine word, delimiter, and filename
         String term;
         //interates each indvidual word
         for ( String word  : WORD_BOUNDARY .split(line)) {
            if (word.isEmpty()) {
               continue;
            }
            //concatenates word with delimiter and filename
            term = word+"#####"+filename;
            currentWord  = new Text(term);
            //writes the term
            context.write(currentWord,one);
         }
      }
   }
   //Reduce function
   public static class Reduce extends Reducer<Text ,  IntWritable ,  Text ,  IntWritable > {
      @Override
      public void reduce( Text word,  Iterable<IntWritable > counts,  Context context)
         throws IOException,  InterruptedException {
         //total of given word is calculated
         int sum  = 0;
         for ( IntWritable count  : counts) {
            sum  += count.get();
         }
         //writes to output
         context.write(word,  new IntWritable(sum));
      }
   }
}

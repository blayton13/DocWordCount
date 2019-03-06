README FOR DocWordCount

1. If you are connected to UNCC internet, continue to step 2
  If not use the following page to connect to the UNCC VPN:
   https://spaces.uncc.edu/pages/viewpage.action?pageId=6653379

2. Connect to the hadoop cluster with the following command:
'''
  $ ssh dsba-hadoop.uncc.edu -l <username>
'''
  <username> should be replaced with your uncc username
  If you experience any errors, make sure you have DUO verification enabled

3. From a separate terminal on your local system, move the DocWordCount.java and
  Canterbury.zip to your home directory on the cluster
'''
  $ scp DocWordCount.java <username>@dsba-hadoop.uncc.edu:/users/<username>
  $ scp canterbury.zip <username>@dsba-hadoop.uncc.edu:/users/<username>
'''
4. From the cluster terminal issue the command '$ ls' to ensure your files have
  been copied to the cluster. Next, unzip the canterbury.zip file:
'''
  $ unzip canterbury.zip
'''
5. Next we will transfer the input files to the Hadoop filesystem
  '''
  $ hadoop fs -put canterbury/* /user/<username>/input
'''
  Check that the input files were copied with the following:
  '''
  $ hadoop fs -ls input
'''
  This should print a list of all eight files that canterbury.zip contains.

6. Create a directory called build in your working directory of the cluster
'''
  $ mkdir build
'''
  Now we will compile and build the DocWordCount.java program.
  '''
  $ javac -cp /opt/cloudera/parcels/CDH/lib/hadoop/*:/opt/cloudera/parcels/CDH/lib/hadoop-mapreduce/* DocWordCount.java -d build -Xlint
'''
  Next we will create a jar file for the program
  '''
  $ jar -cvf docwordcount.jar -C build/ .
  '''

  Check with the ls command to make sure docwordcount.jar was created

7. Run the hadoop job with the following command
'''
  $ hadoop jar docwordcount.jar org.myorg.DocWordCount /user/<username>/input/ /user/<username>/output
'''
  You can run the following to print the output
  '''
  $ hadoop fs -cat output/*
  '''

8. To copy to output to your system, first you must transfer it to the cluster
  Make a directory in the working directory called result and cd to to it
  '''
  $ mkdir result
  $ cd result
'''
  Next, execute the following command to transfer the files to /result
  '''
  $ hadoop fs -get /user/<username>/output/*
'''
  On your local terminal, create a new folder for the output
  '''
  $ mkdir output
'''
  Execute the following to copy the files from the cluster to your system.
  '''
  $ scp <username>@dsba-hadoop.uncc.edu:/users/<username>/result/* output/
'''

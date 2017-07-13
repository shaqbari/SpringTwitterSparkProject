package com.sist.spark;

import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import com.sun.tools.javac.code.Attribute.Array;

import scala.Tuple2;

public class SparkMain {

	public static void main(String[] args) {
		try {
			//하둡연결
			Configuration hadoopConf=new Configuration();
			hadoopConf.set("fs.default.name", "hdfs://NameNode:9000");//하둡주소  
			JobConf jconf=new JobConf(hadoopConf);			        
			
			//spark 연결
			SparkConf conf=new SparkConf().setAppName("wordcount").setMaster("local");//서버연동설정
			JavaSparkContext sc=new JavaSparkContext(conf);
			
			//1. 파일 읽기, 실시간
			JavaRDD<String> input=sc.textFile("./spark.txt");
			
			//2. 한줄씩 읽어서 데이터를 분리//mapper에 해당
											  		// 읽은한줄(아래s에 해당), 자른단어
			JavaRDD<String> words=input.flatMap(new FlatMapFunction<String, String>() {
												//단어가 들어오면 자른다.
				@Override
				public Iterable<String> call(String s) throws Exception {
					System.out.println(s);
					return Arrays.asList(s.split(" "));
				}
			});
			
			//3. 단어별 1씩 부여, reduce에 해당
			JavaPairRDD<String, Integer> counts=words.mapToPair(new PairFunction<String, String, Integer>() {
				public Tuple2<String, Integer> call(String s) throws Exception {
					//값을 합쳐주는 클래스가 tuple이다
					return new Tuple2<String, Integer>(s, 1);//자른단어당 1씩 부여
				
				}
				
				//aaa[1, 1, 1]가 자동으로 만들어진다.
				
									//	sum누적되는 sum전단계의 i더하는	
			}).reduceByKey(new Function2<Integer, Integer, Integer>() {
				//통계내는 메소드
				//sum=sum+i;
				public Integer call(Integer sum, Integer i) throws Exception {
					//x가 누적된 값, y는 누적할 값
					return sum+i;
				}
			
			});
			
			/*JavaPairRDD<String, Integer> res=counts.reduceByKey(new Function2<Integer, Integer, Integer>() {
				//통계내는 메소드
				//sum=sum+i;
				public Integer call(Integer sum, Integer i) throws Exception {
					//x가 누적된 값, y는 누적할 값
					return sum+i;
				}
			
			});*/
			
			//4. 통계	
			//counts.saveAsTextFile("./output");
			//res.savaAsTextFile("./output");
			//결과값에서 ()를 없애야 csv로 사용할 수 있다.
			//자체내 join이 있다. sparkSql
			
			counts.saveAsNewAPIHadoopFile("/spark_ns1/", Text.class, IntWritable.class, TextOutputFormat.class, jconf);
			//실시간으로 하둡에 저장
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			
		}
		
	}

}

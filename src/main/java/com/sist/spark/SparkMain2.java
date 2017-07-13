package com.sist.spark;

import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.Tuple2;

/*
 * 	커피스미스 임성은 삼계탕 전지윤 안희정 류석춘 
	커피스미스 임성은 삼계탕 전지윤 안희정       
	커피스미스 임성은 삼계탕 전지윤
	커피스미스 임성은 삼계탕 전지윤
 * 
 *  	파일 읽기
 *  1.   JavaRDD<String> files=sc.textFile("./spark.txt");
 *  	JavaRDD.flatMap(); //한줄씩 읽기
 *  						
 *  	    	//자른걸 모아주는 	                     			전체글	한줄
 *  	JavaRDD<String> words=input.flatMap(new FlatMapFunction<String, String>() {
							//어떻게 자를지결정 여기서는 공백으로 잘랐다. twitter의 경우 Pattern과 Matcher이용				
			@Override
			public Iterable<String> call(String s) throws Exception {
				System.out.println(s);
				return Arrays.asList(s.split(" "));
			}
		}); 	
 *  
 *  2. 단어 자르기
 *    커피스미스
 *    임성은
  *   삼계탕
 *    전지윤 
 *    안희정
 *    류석춘
 *    커피스미스
 *    임성은
  *   삼계탕
 *    전지윤 
 *    안희정
 *    커피스미스
 *    임성은
  *   삼계탕
 *    전지윤
 *    커피스미스
 *    임성은
  *   삼계탕
 *    전지윤
 *    
 *   3.자른거당 1 부여
 *   																//자른전체단어, 단어당(s), 숫자부여
 *   JavaPairRDD<String, Integer> counts=words.mapToPair(new PairFunction<String, String, Integer>() {
				public Tuple2<String, Integer> call(String s) throws Exception {
					//값을 합쳐주는 클래스가 tuple이다
					return new Tuple2<String, Integer>(s, 1);//자른단어당 1씩 부여
				
				}
				
				//이후aaa[1, 1, 1]가 자동으로 만들어진다.
				
									//	sum누적되는 sum전단계의 i더하는	
			})
 *   
 *    커피스미스 1
 *    임성은 1
  *   삼계탕 1
 *    전지윤 1
 *    안희정 1
 *    류석춘 1
 *    ...
 *      
 *    4. 셔플(자동처리) *    
 *    커피스미스 [1, 1, 1, 1]
 *    	임성은 [1, 1, 1, 1]
  *     삼계탕 [1, 1, 1, 1]
 *      전지윤 [1, 1, 1, 1]
 *      안희정 [1, 1, 1,]
 *      류석춘 [1]
 *    
 *    
 *    /*JavaPairRDD<String, Integer> res=counts.reduceByKey(new Function2<Integer, Integer, Integer>() {
				                                //통계내는 메소드
				//sum=sum+i;
				public Integer call(Integer sum, Integer i) throws Exception {
					//x가 누적된 값, y는 누적할 값
					return sum+i;
				}
			
 *    
 *    5. reduce
 *    커피스미스 4
 *    삼계탕 4
 *    전지윤 4
 *    안희정 3
 *    류석춘 1
 * */
public class SparkMain2 {

	public static void main(String[] args) {
		Configuration hadoopConf=new Configuration();
		hadoopConf.set("fs.default.name", "hdfs://NameNode:9000");//하둡주소  
		JobConf jconf=new JobConf(hadoopConf);			
		
		try {
			//netcat(연습용 서버) 시작 콘솔창에서 nc -lk 9999
			//netcat(값을 보내는 소켓프로그램)이용할때는 local이 2이상이어야 한다. 문자채팅도 바로바로 분석가능
			SparkConf conf=new SparkConf().setAppName("netcat").setMaster("local[2]");//서버연동설정
			//JavaSparkContext sc=new JavaSparkContext(conf);//local에 존재하는거 가져올때 쓴다.
			
			JavaStreamingContext sc=new JavaStreamingContext(conf, new Duration(1000));//몇초에 한번 분석하는지 설정
			
			//Socket s=new Socket(ip, port);
			JavaReceiverInputDStream<String> lines=sc.socketTextStream("localhost", 9999);//소켓연결
			JavaDStream<String> words=lines.flatMap(new FlatMapFunction<String, String>() {

				public Iterable<String> call(String s) throws Exception {
					
					return Arrays.asList(s.split(" "));
				}
			});
			
			JavaPairDStream<String, Integer> counts=words.mapToPair(new PairFunction<String, String, Integer>() {

				public Tuple2<String, Integer> call(String s) throws Exception {
					
					return new Tuple2<String, Integer>(s, 1);
				}
			});
			
			JavaPairDStream<String, Integer> res=counts.reduceByKey(new Function2<Integer, Integer, Integer>() {
				
				@Override
				public Integer call(Integer sum, Integer i) throws Exception {
					return sum+i;
				}
			});
											    //공백이면 date가 붙는다.
			res.saveAsHadoopFiles("/stream_ns1-", "", Text.class, IntWritable.class, TextOutputFormat.class, jconf);
			//여기서는 TextOutputFormat이 경로에 lib안붙은것을 가져온다.
			
			res.print();
			
			sc.start();//소켓 스타트
			sc.awaitTermination();//들어올때까지 기다린다.
			
			//time이 저장명이 될것이다.
			
			//mapreduce는 plum을 이용해야 실시간으로 분석 가능하다.
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
	}

}










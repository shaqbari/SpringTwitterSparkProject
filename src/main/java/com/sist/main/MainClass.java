package com.sist.main;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.hadoop.mapreduce.JobRunner;
import org.springframework.stereotype.Component;

import com.sun.research.ws.wadl.Application;

@Component
public class MainClass {
	@Autowired
	private Configuration conf;
	
	@Autowired
	private JobRunner jr;
	
	public static void main(String[] args) {
		ApplicationContext app=new ClassPathXmlApplicationContext("app.xml");
		MainClass mc=(MainClass)app.getBean("mainClass");
		
		mc.hadoopMapReduce();//aop적용시켜놓았다.
		
		
	}
	
	//0.기존 데이터 지우기
	public void hadoopFileDelete(){
		try {
			FileSystem fs=FileSystem.get(conf);
			if (fs.exists(new Path("/tweet_output"))) {
				fs.delete(new Path("/tweet_output"), true);//true옵션이면 rmr(remove)
			}
			if (fs.exists(new Path("/tweet_input/app_ns1.log"))) {
				fs.delete(new Path("/tweet_input/app_ns1.log"), true);//true옵션이면 rmr(remove)
			}
			fs.close();
			System.out.println("하둡 폴더 삭제 완료");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			
		}
		
	}
	
	//1. hadoop => 수집데이터 보내기 before
	public void hadoopFileInput(){
		try {
			FileSystem fs=FileSystem.get(conf);
			fs.copyFromLocalFile(new Path("./app.log"), new Path("/tweet_input/app_ns1.log"));
			fs.close();
			System.out.println("수집 데이터 하둡에 전송");
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	//2. hadoop => 분석
	public void hadoopMapReduce(){
		try {
			jr.call();
			
			System.out.println("하둡 데이터 분석 완료");
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();

		}
		
	}
	
	//3. 분석 ==> hive 
	public void hadoopResultSend(){
		try {
			FileSystem fs=FileSystem.get(conf);
			fs.copyToLocalFile(new Path("/tweet_output/part-r-00000"), new Path("/home/sist/r-data/twitter_result"));
			fs.close();
			System.out.println("hive에 하둡분석결과 가져오기");
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	//4. hive ==> R

}

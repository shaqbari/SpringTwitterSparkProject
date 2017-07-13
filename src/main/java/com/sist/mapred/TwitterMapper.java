package com.sist.mapred;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.sist.html.NaverRealTimeRanking;

import java.util.*;

public class TwitterMapper extends Mapper<LongWritable, Text, Text, IntWritable>{
	private static final IntWritable one=new IntWritable(1);
	private Text res=new Text();
/*	private String[] data={"전소민", "류석춘", "추자현", "우효광", "원펀치","강연재","넉살","학교2017","수소차","스타크래프트"};
*/	
	
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, IntWritable>.Context context)
			throws IOException, InterruptedException {
		List<String> list=NaverRealTimeRanking.naverRank();
		
		Pattern[] p=new Pattern[list.size()];
		for (int a = 0; a < p.length; a++) {
			p[a]=Pattern.compile(list.get(a));
			
		}
		
		Matcher[] m=new Matcher[list.size()];
		for (int a = 0; a < m.length; a++) {
			m[a]=p[a].matcher(value.toString());
			while (m[a].find()) {
				res.set(m[a].group());//((aaa)(bbb)(ccc)(ddd)) 모두 잡는게 group //여기서 data[a]를 줘도 된다.
				context.write(res, one);
			}
			
		}
	
		
	}
	

	
	
}

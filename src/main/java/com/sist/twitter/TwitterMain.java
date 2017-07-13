package com.sist.twitter;

import twitter4j.FilterQuery;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import java.util.*;
/*
 * 
 * 
 * */

import com.sist.html.NaverRealTimeRanking;


public class TwitterMain {
	public static void main(String[] args) {
		try {
/*			String[] data={"전소민", "류석춘", "추자현", "우효광", "원펀치","강연재","넉살","학교2017","수소차","스타크래프트"};
*/			List<String> nList=NaverRealTimeRanking.naverRank();
			String[] data=new String[nList.size()];
			
			int i=0;
			for (String s : nList) {
				data[i]=s;
				System.out.println("data["+i+"]="+s);
				i++;
			}

			//짜[가-힣] : 짜다 + 짜장면 중국집은 짜다가 많이 나올수 밖에 없다 ==> 짜[가-힣]+^[짜장면]			
			TwitterStream ts=new TwitterStreamFactory().getInstance();
			TwitterListener list=new TwitterListener();
			ts.addListener(list);
			FilterQuery fq=new FilterQuery();
			fq.track(data);
			ts.filter(fq);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		
		}
	}
}

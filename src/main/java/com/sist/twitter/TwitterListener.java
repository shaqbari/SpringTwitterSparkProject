package com.sist.twitter;

import java.io.IOException;
import java.io.*;

import org.apache.log4j.Logger;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;


public class TwitterListener implements StatusListener{
	
	Logger logger=Logger.getLogger(TwitterListener.class);//반복을 없애준다.
	
	
	@Override
	public void onException(Exception e) {
		System.out.println(e.getMessage());
	}

	@Override
	public void onDeletionNotice(StatusDeletionNotice arg0) {
		
	}

	@Override
	public void onScrubGeo(long arg0, long arg1) {
		
	}

	@Override
	public void onStallWarning(StallWarning arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatus(Status status) {
		String data="@"+status.getUser().getScreenName()+":"+status.getText()+"("+status.getCreatedAt()+")\n";
						//쓴사람 id가져오기					
		System.out.println(data);
		//logger.info(data);
		try {
			FileWriter fw=new FileWriter("./app.log", true); //true 옵션을 주면 append모드가 된다.
			fw.write(data);
			fw.close();
			
		} catch (Exception e) {
			System.out.println(e.getMessage()); 
		}
		
	}

	@Override
	public void onTrackLimitationNotice(int arg0) {
		// TODO Auto-generated method stub
		
	}


}

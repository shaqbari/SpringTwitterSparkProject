package com.sist.html;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

public class NaverRealTimeRanking {
 
   public static void main(String[] args) throws Exception {
         
        Document document = Jsoup.connect("http://www.naver.com").get();
         
        if (null != document) {
            // id가 realrank 인 ol 태그 아래 id가 lastrank인 li 태그를 제외한 모든 li 안에 존재하는
            // a 태그의 내용을 가져옵니다.
            Elements elements = document.select("div.ah_roll_area span.ah_k");
             
            for (int i = 0; i < 10; i++) {
                System.out.println("------------------------------------------");
                System.out.println("검색어 : " + elements.get(i).text());
     
            }
        }
    }
    
	public static List<String> naverRank() {
        List<String> list=new ArrayList<String>();
		try {
			Document document = Jsoup.connect("http://www.naver.com").get();
         
	        if (null != document) {
	            Elements elements = document.select("div.ah_roll_area span.ah_k");
	             
	            for (int i = 0; i < 10; i++) {
	                list.add(elements.get(i).text());
	            }
	        }
		        
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        return list;
    }
}
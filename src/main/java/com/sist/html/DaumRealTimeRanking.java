package com.sist.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class DaumRealTimeRanking {
 
    public static void main(String[] args) throws Exception {
         
        Document document = Jsoup.connect("http://www.daum.net").get();
         
        if (null != document) {
            // id가 realrank 인 ol 태그 아래 id가 lastrank인 li 태그를 제외한 모든 li 안에 존재하는
            // a 태그의 내용을 가져옵니다.
            Elements elements = document.select("div.roll_txt span.txt_issue");
             
           int j=0;
            for (int i = 0; i < 10; i++) {
                System.out.println("------------------------------------------");
                System.out.println("검색어 : " + elements.get(j).text());
                j+=2;
            }
        }
    }
}
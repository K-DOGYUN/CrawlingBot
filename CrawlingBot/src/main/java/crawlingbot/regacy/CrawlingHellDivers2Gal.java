//package crawlingbot.regacy;
//
//import org.apache.commons.lang3.StringUtils;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import MangHoBot.MangHoBot.CrawlingValues;
//
//import java.io.IOException;
//
//public class CrawlingHellDivers2Gal {
//    public String crawling() {
//    	
//    	String message = "";
//    	
//        try {
//            // 크롤링할 URL
//            String url = "https://gall.dcinside.com/mgallery/board/lists/?id=helldiversseries&sort_type=N&search_head=60&page=1"; // 크롤링할 사이트의 URL
//
//            // HTML 페이지 로드 및 파싱
//            Document doc = Jsoup.connect(url).get();
//
//            // 원하는 데이터 추출
//            Elements elems = doc.select("tr.ub-content.us-post > td.gall_tit.ub-word"); // 예: h2 태그의 클래스가 "article-title"
//
//            // 추출된 데이터 출력
//            url = "https://gall.dcinside.com" + elems.select("a").get(0).attr("href");
//            
//            if (StringUtils.equals(CrawlingValues.hellDivers2Url, url)) {
//            	System.out.println("중복 url");
//            	return "";
//            } else {
//            	CrawlingValues.hellDivers2Url = url;
//            }
//            
//            doc = Jsoup.connect(url).get();
//            
//            Element title = doc.selectFirst("h3.title.ub-word");
//            message += "[" + title.text() + "]" + "(" + CrawlingValues.hellDivers2Url + ")" + "\n";
//            
//            elems = doc.selectFirst("div.write_div").getAllElements();
//            
//            for (Element element : elems) {
//				if (!StringUtils.isEmpty(element.ownText()))
//					message += element.ownText() + "\n";
//			}
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        
//        return message;
//    }
//}

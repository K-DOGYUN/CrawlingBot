package crawlingbot.crawling.dcinside;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import crawlingbot.domain.WebpageConfig;
import crawlingbot.domain.WebpageConfigs;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class CrawlingGallery {
	public static String crawling(WebpageConfig config) {

		String message = "";

		try {
			// HTML 페이지 로드 및 파싱
			Document doc = Jsoup.connect(config.getWebpageUrl()).get();
			
			// 원하는 데이터 추출
			Elements elems = doc.select("tr.ub-content.us-post");
			
			String url = "";
			
			for (Element element : elems) {
				if (StringUtils.equals("icon_notice", element.attr("data-type")))
					continue;
				
				url = "https://gall.dcinside.com" + element.select("td.gall_tit.ub-word").select("a").get(0).attr("href");
				
				break;
			}
			
			if (StringUtils.equals(config.getLatestCrawledUrl(), url)) {
				return null;
			} else {
				config.setLatestCrawledUrl(url);
				WebpageConfigs.getInstance().editConfig(config);
			}

			doc = Jsoup.connect(url).get();

			Element title = doc.selectFirst("h3.title.ub-word > span.title_subject");
			message += "[" + title.text() + "]" + "(" + url + ")" + "\n";

			elems = doc.selectFirst("div.write_div").getAllElements();

			for (Element element : elems) {
				if (!StringUtils.isEmpty(element.ownText()))
					message += element.ownText() + "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (message.length() > 2000)
			message = StringUtils.substring(message, 0, 2000);
		
		return message;
	}
}

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
			Elements elems = doc.select("tr.ub-content.us-post > td.gall_tit.ub-word"); // 예: h2 태그의 클래스가
																						// "article-title"

			// 추출된 데이터 출력
			String url = "https://gall.dcinside.com" + elems.select("a").get(0).attr("href");

			if (StringUtils.equals(config.getLatestCrawledUrl(), url)) {
				log.info("중복 url");
				return null;
			} else {
				config.setLatestCrawledUrl(url);
				WebpageConfigs.getInstance().editConfig(config);
			}

			doc = Jsoup.connect(url).get();

			Element title = doc.selectFirst("h3.title.ub-word");
			message += "[" + title.text() + "]" + "(" + url + ")" + "\n";

			elems = doc.selectFirst("div.write_div").getAllElements();

			for (Element element : elems) {
				if (!StringUtils.isEmpty(element.ownText()))
					message += element.ownText() + "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return message;
	}
}

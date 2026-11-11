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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
				WebpageConfigs.getInstance().saveConfigs();
			}

			doc = Jsoup.connect(url).get();

			Element title = doc.selectFirst("h3.title.ub-word > span.title_subject");
			message += "[제목 : " + title.text() + "]" + "(" + url + ")" + "\n";

			Element body = doc.selectFirst("div.write_div");

			//스팀 링크가 있을경우 게임참가용 링크 생성
			Matcher steamLinkFinder = Pattern.compile("steam://joinlobby/\\d+/\\d+/\\d+").matcher(body.text());

			if (steamLinkFinder.find()) {
				String steamLinkEncodedUrl = URLEncoder.encode(steamLinkFinder.group(), StandardCharsets.UTF_8);
				String steamLinkPageUrl = "https://k-dogyun.github.io/TestWeb/?url=" + steamLinkEncodedUrl;
				message += "[스팀게임참가](" + steamLinkPageUrl + ")\n";
			}

			//본문
			elems = body.getAllElements();
			for (Element element : elems) {
				if (!StringUtils.isEmpty(element.ownText()))
					message += element.ownText() + "\n";
			}
		} catch (IOException e) {
			log.error(e.toString());
		}
		
		//디스코드용 텍스트 크기 제한
		if (message.length() > 2000)
			message = StringUtils.substring(message, 0, 2000);

		return message;
	}
}

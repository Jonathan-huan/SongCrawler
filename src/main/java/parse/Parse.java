package parse;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.MusicChartsModel;
import model.SongModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.HTTPUtils;

public class Parse {

	/**
	 * 获取排行榜数据
	 * @param html 网页源码
	 * @param charts 排行榜列表
	 */
	public static void getChartData(String html, List<MusicChartsModel> charts){
		Document document = Jsoup.parse(html);
		Elements results = document.select("div[class=pc_temp_side]").select("li");
		for (Element result : results) {
			String name = result.select("a").text();
			String url = result.select("a").attr("href");
			//正则表达式提取id
			Pattern pattern = Pattern.compile("/home/(\\d+-\\d+)\\.html");
			Matcher matcher = pattern.matcher(url);

			MusicChartsModel model = new MusicChartsModel();
			model.setId(matcher.find()?matcher.group(1):"");
			model.setName(name);
			model.setUrl(url);
			charts.add(model);
		}
	}

	/**
	 * 获取歌曲数据
	 * @param html 网页源码
	 * @param songs 歌曲列表
	 */
	public static void getSongData(String html, List<SongModel> songs){
		Document document = Jsoup.parse(html);
		Elements results = document.select("div[class=pc_temp_container]").select("li");
		for (Element result : results) {
			String title = result.selectFirst("a").ownText();
			String url=result.selectFirst("a").attr("href");
			//正则表达式提取id
			String pattern = "/mixsong/(\\w+)\\.html";
			Pattern regexPattern = Pattern.compile(pattern);
			Matcher matcher = regexPattern.matcher(url);

			SongModel model = new SongModel();
			model.setId(matcher.find()?matcher.group(1):"");
			model.setTitle(title);
			model.setUrl(url);
			songs.add(model);
		}
		for (SongModel song : songs) {
			String url = song.getUrl();
			try {
				//爬取动态响应数据
				String json = HTTPUtils.getKgJson("https://wwwapi.kugou.com/yy/index.php?r=play/getdata&encode_album_audio_id="+song.getId());
				JsonObject data = new JsonParser().parse(json).getAsJsonObject().get("data").getAsJsonObject();
				StringBuilder artistUrl = new StringBuilder();
				JsonArray authors = data.get("authors").getAsJsonArray();
				for (JsonElement author : authors) {
					String authorId = author.getAsJsonObject().get("e_author_id").getAsString();
					artistUrl.append("https://m.kugou.com/singer/info/").append(authorId).append("、");
				}
				artistUrl.deleteCharAt(artistUrl.length()-1);
				String lyrics = data.get("lyrics").getAsString();
				String albumUrl="https://m.kugou.com/album/info/"+data.get("encode_album_id").getAsString();

				String detailHtml = HTTPUtils.getRawHtml(url);
				Document detailDocument = Jsoup.parse(detailHtml);
				Elements detailResult = detailDocument.select("div[class=songContent fl]");
				String album = detailResult.select("p[class=albumName fl]").select("a").text();
				String artist = detailResult.select("p[class=singerName fl]").select("a").text();
				song.setAlbum(album);
				song.setAlbumUrl(albumUrl);
				song.setArtist(artist);
				song.setArtistUrl(artistUrl.toString());
				song.setLyrics(lyrics);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}

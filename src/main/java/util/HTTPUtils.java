package util;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;

public abstract class HTTPUtils {
	public static String getRawHtml(String personalUrl) throws ParseException, IOException {
		HttpClient httpClient = HttpClients.custom().build(); 
		//获取响应文件，即html，采用get方法获取响应数据
		HttpGet getMethod = new HttpGet(personalUrl);
        //设置请求头信息，鉴别身份，防止爬虫被禁止
		getMethod.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
				+ "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");
		HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1,
				HttpStatus.SC_OK, "OK");
		try {
			//执行get方法
			response = httpClient.execute(getMethod);
		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			// getMethod.abort();
		}
		//获取响应状态码
		int StatusCode = response.getStatusLine().getStatusCode();
		//如果状态响应码为200，则获取html实体内容或者json文件
		String entity = "";
		if(StatusCode == 200){
			entity = EntityUtils.toString (response.getEntity(),"utf-8");
			EntityUtils.consume(response.getEntity());
		}else {
			//否则，消耗掉实体
			EntityUtils.consume(response.getEntity());
		}
		return entity;
	}

	public static String getKgJson(String url) throws IOException {
		HttpClient httpClient = HttpClients.custom().build();
		//获取响应文件，即html，采用get方法获取响应数据
		HttpGet getMethod = new HttpGet(url);
		//设置请求头信息，鉴别身份，防止爬虫被禁止
		getMethod.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
				+ "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");
		getMethod.setHeader("Cookie","kg_mid=17c6325387d31553b41d18927211120a");
		HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1,
				HttpStatus.SC_OK, "OK");
		try {
			//执行get方法
			response = httpClient.execute(getMethod);
		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			// getMethod.abort();
		}
		//获取响应状态码
		int StatusCode = response.getStatusLine().getStatusCode();
		//如果状态响应码为200，则获取html实体内容或者json文件
		String entity = "";
		if(StatusCode == 200){
			try {
				entity = EntityUtils.toString (response.getEntity(),"utf-8");
			} catch (IOException e) {
				e.printStackTrace();
			}
			EntityUtils.consume(response.getEntity());
		}else {
			//否则，消耗掉实体
			EntityUtils.consume(response.getEntity());
		}
		return entity;
	}
}
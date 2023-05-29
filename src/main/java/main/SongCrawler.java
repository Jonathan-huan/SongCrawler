package main;
import db.MYSQLControl;
import model.MusicChartsModel;
import model.SongModel;
import parse.Parse;
import util.HTTPUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SongCrawler {
    private static final String BASE_URL = "https://www.kugou.com/yy/html/rank.html";
    public static void main(String[] args) throws IOException {
        //清空表数据
        MYSQLControl.truncateTable("chart_song_relation");
        MYSQLControl.truncateTable("music_chart");
        MYSQLControl.truncateTable("song");

        List<MusicChartsModel> charts = new ArrayList<MusicChartsModel>();
        String html = HTTPUtils.getRawHtml(BASE_URL);
        Parse.getChartData(html,charts);
        MYSQLControl.insertMusicCharts(charts);
        System.out.println("爬取完成,共爬取排行榜：" + charts.size() + "个\n");
        for (MusicChartsModel chart : charts) {
            List<SongModel> songs = new ArrayList<SongModel>();
            System.out.println("正在爬取排行榜：" + chart.getName());
            String chartUrl = chart.getUrl();
            String chartHtml = HTTPUtils.getRawHtml(chartUrl);
            Parse.getSongData(chartHtml,songs);
            System.out.println("爬取完成,共爬取歌曲：" + songs.size() + "首");
            MYSQLControl.insertSongs(songs);
            //建立联系
            for (SongModel song : songs) {
                MYSQLControl.insertChartSongRelation(chart.getId(),song.getId());
            }
        }
    }
}

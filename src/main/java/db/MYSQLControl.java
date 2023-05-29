package db;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;

import model.MusicChartsModel;
import model.SongModel;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ColumnListHandler;

public class MYSQLControl {
	//根据自己的数据库地址修改
	static DataSource ds = MyDataSource.getDataSource("jdbc:mysql://127.0.0.1:3306/experiment01?useUnicode=true&characterEncoding=UTF8");
	static QueryRunner qr = new QueryRunner(ds);
	//查询一列
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<Object> getListOneBySQL (String sql,String id){
		List<Object> list=null;
		try {
			list = (List<Object>) qr.query(sql, new ColumnListHandler(id));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	//插入音乐榜单数据
	public static void insertMusicCharts(List<MusicChartsModel> datalist){
		Object[][] params = new Object[datalist.size()][3];
		for ( int i=0; i<params.length; i++ ){
			params[i][0] = datalist.get(i).getId();
			params[i][1] = datalist.get(i).getName();
			params[i][2] = datalist.get(i).getUrl();
		}
		try{
			qr.batch("insert into music_chart (id, name, url) values (?,?,?)", params);
			System.out.println("执行数据库完毕！"+"成功插入数据：" + datalist.size() + "条");
		}catch (SQLException e){
			e.printStackTrace();
		}
	}

	//插入音乐数据
	public static void insertSongs(List<SongModel> datalist){
		Object[][] params = new Object[datalist.size()][8];
		for ( int i=0; i<params.length; i++ ){
			params[i][0] = datalist.get(i).getId();
			params[i][1] = datalist.get(i).getTitle();
			params[i][2] = datalist.get(i).getUrl();
			params[i][3] = datalist.get(i).getAlbum();
			params[i][4] = datalist.get(i).getAlbumUrl();
			params[i][5] = datalist.get(i).getArtist();
			params[i][6] = datalist.get(i).getArtistUrl();
			params[i][7] = datalist.get(i).getLyrics();
		}
		try {
			qr.batch("insert ignore into song (id,title,url,album,album_url,artist,artistUrl,lyrics) values (?,?,?,?,?,?,?,?)", params);
			System.out.println("执行数据库完毕！"+"成功插入数据：" + datalist.size() + "条");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void insertChartSongRelation(String chartId, String songId){
		Object[][] params = new Object[1][3];
		for ( int i=0; i<params.length; i++ ){
			params[i][0] = chartId+songId;
			params[i][1] = chartId;
			params[i][2] = songId;
		}
		try {
			qr.batch("insert into chart_song_relation (id,chart_id,song_id) values (?,?,?)", params);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void truncateTable(String tableName){
		try {
			qr.update("truncate table " + tableName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
import java.io.*;
import java.net.*;
import java.util.*;
import com.google.gson.*;
import java.util.regex.*;

public class Main
{
	private static Gson g=new Gson();
	private static Pattern p=Pattern.compile("\"(http.*?)\"");
	
	
	
	
	public static void main(String[] args)
	{
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					;
					try
					{
						startDown(null);//获取首页
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					// TODO: Implement this method
				}
			}).start();
		//获取首页
		
	}
	
	public static void startDown(LoliBean bean) throws IOException{
		if(bean==null){
			bean=getMain("0");//获取首页
		}
		for(LoliBean.Bean lb:bean.posts){
			DownImageFromBean(lb,1,-1);//下载图片
		}
		startDown(getMain(bean.start));//下一页
	}
	//下载图片
	public static void DownImageFromBean(LoliBean.Bean postUser,int page,int max) throws IOException{
		String user=connect("http://floor.huluxia.com/post/detail/ANDROID/2.2?platform=2&gkey=460000&app_version=3.5.0.81&versioncode=20141377&market_id=floor_tencent&_key=C0EC1E482DC1D4B9BF4B0835C26123C86D1FF478A6EC6C49CD04CF54CAF274230094A5B0CEA92A6B807506C75F7AE0F04BE1C8D39CD32349&device_code=%5Bw%5D02%3A00%3A00%3A00%3A00%3A00-%5Bi%5D862561033391226-%5Bs%5D89860116850001487636&post_id="+postUser.postID+"&page_no="+page+"&page_size=20&doc=1");
		JsonElement je=new JsonParser().parse(user).getAsJsonObject().get("totalPage");
		max=je.getAsNumber().intValue();
		JsonElement title=new JsonParser().parse(user).getAsJsonObject().get("post").getAsJsonObject().get("title");
		File f=new File("/storage/emulated/0/360/Loli_Spider/HLX/"+title);
		if(!f.exists()){
			f.mkdirs();
		}
		Matcher m=p.matcher(user);
		
		while(m.find()){
			downLoadFromUrl(m.group(1),f.getAbsolutePath());
			
		}
		if(page!=max){
		
			DownImageFromBean(postUser,page+1,max);//下一页
		
		}
		
	}
	//获取页数
	public static LoliBean getMain(String start){
		 String str=connect("http://floor.huluxia.com/post/list/ANDROID/2.1?platform=2&gkey=460000&app_version=3.5.0.81&versioncode=20141377&market_id=floor_tencent&_key=C0EC1E482DC1D4B9BF4B0835C26123C86D1FF478A6EC6C49CD04CF54CAF274230094A5B0CEA92A6B807506C75F7AE0F04BE1C8D39CD32349&device_code=%5Bw%5D02%3A00%3A00%3A00%3A00%3A00-%5Bi%5D862561033391226-%5Bs%5D89860116850001487636&start="+start+"&count=20&cat_id=56&tag_id=0&sort_by=0");
		 LoliBean bean=g.fromJson(str,LoliBean.class);
		 return bean;
	}
	//链接
	public static String connect(String urls){
		StringBuffer sb = new StringBuffer();
		try {
			//构建一URL对象
			URL url = new URL(urls);
			//使用openStream得到一输入流并由此构造一个BufferedReader对象
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			//读取www资源
			while ((line = in.readLine()) != null)
			{
				sb.append(line);
			}
			in.close();
		}
		catch (Exception ex)
		{
			System.err.println(ex);
		}
		return sb.toString();
	
	}
	
	public static void  downLoadFromUrl(String urlStr,String savePath) throws IOException{
		URL url = new URL(urlStr);  
		String fileName=urlStr.substring(urlStr.lastIndexOf("/")+1,urlStr.length()).replace("ht","jpg");
		
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
		//设置超时间为3秒
		conn.setConnectTimeout(3*1000);
		//防止屏蔽程序抓取而返回403错误
		conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

		//得到输入流
		InputStream inputStream = conn.getInputStream();  
		//获取自己数组
		byte[] getData = readInputStream(inputStream);    

		//文件保存位置
		File saveDir = new File(savePath);
		if(!saveDir.exists()){
			saveDir.mkdir();
		}
		File file = new File(saveDir+File.separator+fileName);    
		FileOutputStream fos = new FileOutputStream(file);     
		fos.write(getData); 
		if(fos!=null){
			fos.close();  
		}
		if(inputStream!=null){
			inputStream.close();
		}


		System.out.println("info:"+url+" download success"); 
		

	}



	
	public static  byte[] readInputStream(InputStream inputStream) throws IOException {  
		byte[] buffer = new byte[1024];  
		int len = 0;  
		ByteArrayOutputStream bos = new ByteArrayOutputStream();  
		while((len = inputStream.read(buffer)) != -1) {  
			bos.write(buffer, 0, len);  
		}  
		bos.close();  
		return bos.toByteArray();  
	}  

	
	
}

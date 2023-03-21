package cn.zvo.fileupload.storage.qiniuKodo;


import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cn.zvo.fileupload.FileUpload;
import cn.zvo.fileupload.bean.SubFileBean;


public class Demo {
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		String accessKey = "qpwgTxSc30He1mj9BP2tbblpWxvICaxxxxxxxxxxx";
		String secretKey = "sYCOoTd-YqYRVq4bwOGhjyu1TJ32xxxxxxxxxxxx";
		String bucketName = "test1-user";
		String domain = "http://test.weiwenhao.cn";
		
		
		Map<String, String> map = new  HashMap<String, String>();
		map.put("accessKey", accessKey);
		map.put("secretKey", secretKey);
		map.put("bucketName", bucketName);
		map.put("domain", domain);
		QiniuKodoStorage kodoStorage = new QiniuKodoStorage(map);
		
		FileUpload fileUpload = new FileUpload();
		fileUpload.setStorage(kodoStorage);
		//上传，这里上传一个文本文件，内容是 abcd ，将他保存到 桶 a/b 目录下的 1.txt 文件 并打印结果
		System.out.println(fileUpload.uploadString("test.txt", "abcd"));
//		System.out.println(kodoStorage.get("a/b/1.txt"));//得到文件数据
//		System.out.println(fileUpload.getText("a/b/1.txt"));//打印文本内容
//		List<SubFileBean> list = fileUpload.getSubFileList("a/b/c/");//获取文件列表
//		for(int i = 0;i < list.size();i++) {//遍历列表
//			System.out.print(list.get(i)+"\n");
//		}
	}
}

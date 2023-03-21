package cn.zvo.fileupload.storage.qiniuKodo;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.management.StringValueExp;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import com.xnx3.BaseVO;
import cn.zvo.fileupload.StorageInterface;
import cn.zvo.fileupload.bean.SubFileBean;
import cn.zvo.fileupload.vo.UploadFileVO;

/**
 * 文件上传之 七牛云 Kodo
 * @author 魏文浩
 *
 */
public class QiniuKodoStorage implements StorageInterface {
	public String accessKey ; //七牛云的 Access Key Id, 获取方式可通过网址获取 https://portal.qiniu.com/user/key
	public String secretKey ; //七牛云的 Access Key Secret, 获取方式可通过网址获取 https://portal.qiniu.com/user/key
	public String bucketName ; //桶的名称
	public String domain;	 //桶绑定的自定义域名，格式如 http://123.zvo.cn 设置时要注意格式 没域名可暂用测试域名来测试
	
	public Configuration cfg;//构造一个带指定 Region 对象的配置类
	public Auth auth; //构造密钥

	/**
	 * 文件上传-七牛云OBS
	 * @param map 传入一个 Map<String, String> 其中map要定义这么几个参数：
	 * 			<ul>
	 * 				<li>map.put("accessKeyId", "七牛云的 Access Key Id");</li>
	 * 				<li>map.put("accessKeySecret", "七牛云的 Access Key Secret");</li>
	 * 				<li>map.put("bucketName", "桶的名称")</li>
	 * 				<li>map.put("domain", "桶绑定的自定义域名")</li>
	 * 			</ul>
	 */
	public QiniuKodoStorage(Map<String, String> map) {
		accessKey = map.get("accessKey");
		secretKey = map.get("secretKey");
		bucketName = map.get("bucketName");
		domain = map.get("domain");
		
		this.cfg = new Configuration();
		this.cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;
		this.auth = Auth.create(accessKey, secretKey);
	}

	@Override
	public UploadFileVO upload(String path, InputStream inputStream) {
		UploadFileVO vo = new UploadFileVO();
		
		UploadManager uploadManager = new UploadManager(this.cfg);
		String token = this.auth.uploadToken(this.bucketName);//获取上传凭证
		try {
			//输入流上传到指定路径
			Response r = uploadManager.put(inputStream, path, token, null, null);
			if(r.isOK()) {//响应判断
				vo.setBaseVO(UploadFileVO.SUCCESS, "SUCCESS");
			}else {
				vo.setBaseVO(UploadFileVO.FAILURE, r.error);
			}
			vo.setPath(path);
		} catch (QiniuException e) {
			e.printStackTrace();
			vo.setBaseVO(UploadFileVO.FAILURE, "上传失败! e:"+e.getMessage());
		}
		return vo;
	}


	@Override
	public InputStream get(String path) {
		//第一步，生成资源获取的url
//		String domainOfBucket = "http://rqtif7ehv.hb-bkt.clouddn.com";
		String encodedFileName = null;
		try {
			encodedFileName = URLEncoder.encode(path, "utf-8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		String publicUrl = String.format("%s/%s", this.domain, encodedFileName);
		long expireInSeconds = 3600;//1小时，可以自定义链接过期时间
		String finalUrl = this.auth.privateDownloadUrl(publicUrl, expireInSeconds);
		
		//第二步，网络获取资源
		try {
			URL url = new URL(finalUrl);
			// 打开连接
			HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection.setConnectTimeout(3000);	//超时30秒
			httpConnection.setReadTimeout(3000);
			httpConnection.setRequestProperty("User-Agent", "Internet Explorer");
			InputStream input = httpConnection.getInputStream();
			return input;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	@Override
	public BaseVO delete(String path) {
		BucketManager bucketManager = new BucketManager(this.auth, this.cfg);
		try {
			//删除成功
		    bucketManager.delete(this.bucketName, path);
		    return BaseVO.success();
		} catch (QiniuException e) {
		    //如果遇到异常，说明删除失败
			e.printStackTrace();
			return BaseVO.failure("删除失败" + e.getMessage());
		}
	}


	@Override
	public void copyFile(String originalFilePath, String newFilePath) {
		String fromKey = originalFilePath;//初始路径
		String toKey = newFilePath;//新路径
		BucketManager bucketManager = new BucketManager(this.auth, this.cfg);
		try {
			//同一个桶内文件复制
		    bucketManager.copy(this.bucketName, fromKey, this.bucketName, toKey);
		} catch (QiniuException e) {
		    //如果遇到异常，说明复制失败
			e.printStackTrace();
		    System.err.println("复制失败" + e.getMessage());
		}
	}


	@Override
	public List<SubFileBean> getSubFileList(String path) {
		BucketManager bucketManager = new BucketManager(this.auth, this.cfg);
		
		List<SubFileBean> list = new ArrayList<SubFileBean>();
		//列举空间文件列表
		BucketManager.FileListIterator fileListIterator = bucketManager.createFileListIterator(this.bucketName, path);
		while (fileListIterator.hasNext()) {
		    //处理获取的file list结果
		    FileInfo[] items = fileListIterator.next();
		    for (FileInfo item : items) {
		    	if(item.key.indexOf(path) != 0) {
		    		continue;
		    	}
		    	SubFileBean bean = new SubFileBean();
		    	bean.setPath(item.key);
		    	bean.setSize(item.fsize);
		    	bean.setLastModified(item.putTime);
		    	list.add(bean);
		    }
		}
		return list;
	}

	@Override
	public long getSize(String path) {
		BucketManager bucketManager = new BucketManager(this.auth, this.cfg);
		long size = 0;
		try {
			//获取文件大小
		    FileInfo fileInfo = bucketManager.stat(this.bucketName, path);
		    size = fileInfo.fsize;
		} catch (QiniuException e) {
		    System.err.println(e.response.toString());
		}
		 return size;
	}
	
	@Override
	public BaseVO createFolder(String path) {
		return BaseVO.success();
	}
}
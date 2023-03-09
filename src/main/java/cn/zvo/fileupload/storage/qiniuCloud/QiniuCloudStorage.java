package cn.zvo.fileupload.storage.qiniuCloud;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

public class QiniuCloudStorage {

	public static void main(String[] args) {
		String accessKey = "xxxxx";
		String secretKey = "xxxxx";
		String bucketName = "user-image-ceshi";
		Configuration cfg = new Configuration();
		cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;
		UploadManager uploadManager = new UploadManager(cfg);
		Auth auth = Auth.create(accessKey, secretKey);
		String token = auth.uploadToken(bucketName);
		String key = "ceshi";
		try {
			Response r = uploadManager.put("hello world".getBytes(), key, token);
			System.err.println(r.bodyString());
		} catch (QiniuException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

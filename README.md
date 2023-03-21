# fileupload-storage-qiniukodo
# 介绍
一个介于七牛云Kodo跟普通开发人员之间的一个文件上传框架，将常用能力进行了封装。  
目的是让未曾接触过Kodo的开发人员，无脑拖来就用，五分钟对接好，开发者无踩坑、也不会给团队里其他同事造坑。  
从此无论本地存储、分布式存储、对象存储……都是完全一样的代码调用！它赋予你各种存储随便切换随便用的能力，而无需动项目代码。  

# 场景
* 任何需要图片、附件上传的系统，需要做文件上传功能，需要拿来即用，不想自己踩坑的。
* 对系统要做分布式部署，要将图片、附件等自行上传产生的文件进行独立存放的。
* 传统方式存放在服务器上，使服务器带宽、磁盘产生不小的费用，需要大幅降低上云成本，将附件独立出来存储及提供高速访问的。
* 初创时业务量小，第一年选择将上传附件放在服务器本身，但考虑到后面可能会做大，做大时要将附件分离出去独立存储，但又不想去改系统代码的，可通过配置两个参数无缝切换。


# 优势
* **无门槛** 开发者无需花精力去查阅详细的文档及SDK，拿来即用，5分钟跑起来。给开发者继续深入探索的机会。
* **bug修复** 如果使用它时出现了bug，开发人员也无需去理会、跟踪，你只需要反馈上来，我们来统一修复。
* **高安全** 数据持久性高达99.9999999999%、千万级并发、高可靠、不怕上传漏洞导致上传可执行文件、多区域自动备份保障数据安全。
* **低成本** 大幅降低带宽、磁盘的费用。比如原本10M带宽，可能降到2M就够用了。原本200G数据盘，你都可以去掉数据盘，只用系统盘运行项目就能使用。

# 使用

### 1. 在普通Java项目中使用
##### 1.1 pom.xml 中加入：

````
<!-- 文件上传相关的核心支持，是必须有的  -->
<dependency> 
    <groupId>cn.zvo.fileupload</groupId>
    <artifactId>fileupload-core</artifactId>
    <version>1.1</version>
</dependency>
<!-- 七牛云依赖 -->
<dependency> 
	<groupId>cn.zvo.fileupload</groupId>
	<artifactId>fileupload-storage-qiniuKodo</artifactId>
	<version>1.0</version>
</dependency>
````
##### 1.2 代码中使用

````
/**** 定义存储位置，存储到七牛云Kodo中 ****/
String accessKey = "qpwgTxSc30He1mj9BP2tbblpXXXXXXXXXX";//七牛云的 Access Key Id, 获取方式可通过网址获取 https://portal.qiniu.com/user/key
String secretKey = "sYCOoTd-YqYRVq4bwOGhjyu1TXXXXXXXXXXXX";////七牛云的 Access Key Secret, 获取方式可通过网址获取 https://portal.qiniu.com/user/key
String bucketName = "test1-user";//桶的名称
String domain = "http://test.weiwenhao.cn";//桶绑定的自定义域名，格式如 http://123.zvo.cn 设置时要注意格式 没域名可暂用测试域名来测试
		
		
Map<String, String> map = new  HashMap<String, String>();
map.put("accessKey", accessKey);
map.put("secretKey", secretKey);
map.put("bucketName", bucketName);
map.put("domain", domain);

QiniuKodoStorage kodoStorage = new QiniuKodoStorage(map);
/**** 创建文件上传工具对象 ****/
FileUpload fileUpload = new FileUpload();
fileUpload.setStorage(kodoStorage);	//设置使用Kodo存储

/**** 上传，这里上传一个文本文件，内容是 123456 ，将他保存到 桶 abc 目录下的 1.txt 文件 并打印结果 ****/
System.out.println(fileUpload.uploadString("a/b/1.txt", "abcd"));
//System.out.println(kodoStorage.get("a/b/1.txt"));//得到文件数据
//System.out.println(fileUpload.getText("a/b/1.txt"));//打印文本内容
//List<SubFileBean> list = fileUpload.getSubFileList("a/b/");//获取文件列表
//for(int i = 0;i < list.size();i++) {//遍历列表
//System.out.print(list.get(i)+"\n");
//}
````

[点此查看 cn.zvo.fileupload.storage.qiniuCloud.Demo.java 文件](src/main/java/cn/zvo/fileupload/storage/qiniuCloud/Demo.java)

### 2. 在SpringBoot项目中使用

##### 2.1 pom.xml 中加入：

````
<!-- 文件上传相关的核心支持，是必须有的  -->
<dependency> 
	<groupId>cn.zvo.fileupload</groupId>
	<artifactId>fileupload-core</artifactId>
	<version>1.1</version>
</dependency>
<!-- 加入七牛云Kodo存储相关实现 -->
<dependency> 
	<groupId>cn.zvo.fileupload</groupId>
	<artifactId>fileupload-storage-qiniuKodo</artifactId>
	<version>1.0</version>
</dependency>
<!-- 在 SpringBoot 框架中的快速使用。 （在不同的框架中使用，这里引入的framework.xxx也不同） -->
<dependency> 
	<groupId>cn.zvo.fileupload</groupId>
	<artifactId>fileupload-framework-springboot</artifactId>
	<version>1.1</version>
</dependency> 
````

##### 2.2 参数配置

配置 application.properties (或yml)，加入：  

````
# 文件上传 https://github.com/xnx3/FileUpload
#
# 设置允许上传的文件最大是多大，比如10MB 单位为 KB、MB ， 如果此项不设置，这里默认是3MB
fileupload.maxSize=10MB
# 设置允许上传的后缀名,传入格式如 png|jpg|gif|zip 多个用英文|分割。如果不设置，默认允许像是pdf、word、图片、音频、视频、zip等常用的且安全的文件后缀都可上传
fileupload.allowUploadSuffix=jpg|png|txt|zip
# 设fileupload域名，传入如： http://xxxx.com/  注意格式，后面以 / 结尾。这里结合CDN加速一起使用效果更佳
fileupload.domain=http://test.weiwenhao.cn/
#
# 设置当前使用的是哪种存储方式
# 如果此不设置，默认使用的是本地存储的方式。如果设置了，pom.xml 文件中，记得将此存储方式引入进来，不然会报错找不到这个class文件
# 下面便是具体针对七牛云Kodo这种存储方式的配置了
# 七牛云的 Access Key Id
fileupload.storage.qiniuKodo.accessKey=qpwgTxSc30He1mj9BP2tbblpXXXXXXXXXX
# 七牛云的 Access Key Secret
fileupload.storage.qiniuKodo.secretKey=sYCOoTd-YqYRVq4bwOGhjyu1TXXXXXXXXXXXX
# 桶的名称
fileupload.storage.qiniuKodo.bucketName=test1-user
#桶绑定的自定义域名，格式如 http://123.zvo.cn  设置时要注意格式
fileupload.storage.qiniuKodo.domain=http://test.weiwenhao.cn
````

##### 2.3 Java代码

建立一个Controller，其中加入：

````
/**
 * 文件上传
 */
@RequestMapping(value="upload.json", method= {RequestMethod.POST})
@ResponseBody
public UploadFileVO uploadImage(@RequestParam("file") MultipartFile multipartFile){
	//将文件上传到 upload/file/ 文件夹中
	return fileUpload.uploadImage("upload/file/", multipartFile);
}

/**
 * 文件下载
 * @param path 要下载的文件，传入如 upload/file/123.zip
 */
@RequestMapping(value="download")
public void download(String path, HttpServletResponse response){
	FileUploadUtil.download(path, response);
}
````
##### 2.4 前端html代码
src/main/resources/static/ 下增加一个 upload.html 的文件，目的是能使用 localhost:8080/upload.html 就能访问到。 其内容为：

````
选择要上传的文件（可以传个图片试试）：<br/>
<input type="file" name="file"/><button onclick="upload();">上传</button>

<script src="http://res.zvo.cn/request/request.js"></script><!-- 文件上传，开源地址 https://github.com/xnx3/request -->
<script>
function upload(){
	var file = document.getElementsByName('file')[0].files[0];	 //要上传的文件
	request.upload('/upload.json', {}, file, function(data){  //执行上传操作
		console.log(data);
		if(data.result == '1'){
			// 上传成功
		}else{
			// 上传出错，可弹出失败提示 ： data.info
		}
	});
}
</script>
````
##### 2.5 运行起来，测试一下
访问 [http://localhost:8080/upload.html](http://localhost:8080/upload.html) 即可进行测试体验了。   QiniuKodoStorage


# 交流
如果您在使用过程中遇到任何异常情况，可在此提 Issues， 请详细说一下您遇到的问题。如果可以，请尽可能描述的详细一些，以便我们可以更全面地测试，以便快速找到问题所在 

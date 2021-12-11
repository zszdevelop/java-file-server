# java-file-server
## 1. Java 文件服务

java常见文件服务实现，封装统一常用 *RESTful* *API*，针对各文件服务特性提供特性service。

### 1.1 文件 *RESTful* API

- 文件上传

  ```
  POST http://localhost:9310/file/upload
  ```

- 文件下载

  ```
  GET http://localhost:9310/file/download
  ```

- 预览文件（仅支持图片和Pdf）

  ```
  GET http://localhost:9310/file/preview
  ```

- 获取预览地址

  ```
  GET http://localhost:9310/file/getPreviewUrl
  ```

  - Minio 实现可支持设置过期时间

- 删除文件

  ```
  DELETE http://localhost:9310/file/delete
  ```

### 1.2 支持文件服务类型

- 本地存储
- Minio
- FastDFS（开发中）
- 阿里云存储（开发中）

## 2. 实现思路

1. 文件controller

   提供统一的controller层，通过service接口调用不同的文件服务实现

2. 文件service

   文件服务实现有很多种（本地存储、Minio、云存储等），每种类型除了支持正常上传、下载、预览、删除外。还提供了一些其他特性，并不希望我们的文件服务变得只支持通用API，对于平台特性的也能够支持。

   ```
   ├── service            // service层
   │       └── FileService                    // 通用文件服务接口
   |				└── LocalFileService               // 本地存储扩展接口-继承通用文件服务
   |				└── MinioFileService               // minio扩展接口-继承通用文件服务
   |				└──	impl
   |						└── LocalFileServiceImpl               // 本地存储实现
   |						└── MinioFileServiceImpl               // minio实现
   ```

3. 返回结果封装

  - 各平台文件服务返回统一的结果，结果带有泛型支持，方便获取数据。

    ```java
    public class FileResult<T> {
        // 错误码
        private Integer code;
        // 消息
        private String msg;
        // 返回结果
        private T data;
    }
    ```

  - 各平台的有特性返回，则通过FileInfo 继承 HashMap 进行扩展

    ```java
    public class FileInfo extends HashMap<String, Object> {
    		...
    }
    ```

    

​	

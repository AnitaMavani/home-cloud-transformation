<template>
    <div class="uploader-panel">
      <div class="uploader-title">
        <span>Upload Task</span>
        <span class="tips"></span>
      </div>
      <div class="file-list slideDown">
        <div v-for="(item, index) in fileList" :key="index" class="file-item">
          <div class="upload-panel">
            <div class="file-name">
              {{ item.fileName }}
            </div>
            <div class="progress">
              <!--上传-->
              <el-progress
                :percentage="item.uploadProgress"
                v-if="
                  item.status == STATUS.uploading.value ||
                  item.status == STATUS.upload_seconds.value ||
                  item.status == STATUS.upload_finish.value
                "
              />
            </div>
            <div class="upload-status">
              <!--图标-->
              <span
                :class="['iconfont', 'icon-' + STATUS[item.status].icon]"
                :style="{ color: STATUS[item.status].color }"
              ></span>
              <!--状态描述-->
              <span
                class="status"
                :style="{ color: STATUS[item.status].color }"
                >{{
                  item.status == "fail" ? item.errorMsg : STATUS[item.status].desc
                }}</span
              >
              <!--上传中-->
              <span
                class="upload-info"
                v-if="item.status == STATUS.uploading.value"
              >
                {{ size2Str(item.uploadSize) }}/{{
                  size2Str(item.totalSize)
                }}
              </span>
            </div>
          </div>
          <div class="op">
            <!--MD5-->
            <el-progress
              type="circle"
              :width="50"
              :percentage="item.md5Progress"
              v-if="item.status == STATUS.init.value"
            />
            <div class="op-btn">
              <span v-if="item.status === STATUS.uploading.value">
    <!-- 上传图标 -->
<span v-if="item.pause" @click="startUpload(item.uid)" class="btn-item">
  <img :src="getImage('upload')" :style="{ 'object-fit': 'contain', width: '28px', height: '28px' }" />
</span>

<!-- 暂停图标 -->
<span v-else @click="pauseUpload(item.uid)" class="btn-item">
  <img :src="getImage('pause')" :style="{ 'object-fit': 'contain', width: '28px', height: '28px' }" />
</span>
  </span>

  <!-- 删除图标 -->
  <span v-if="item.status != STATUS.init.value && item.status != STATUS.upload_finish.value && item.status != STATUS.upload_seconds.value"
        @click="delUpload(item.uid, index)" class="btn-item">
    <img :src="getImage('del')" :style="{ 'object-fit': 'contain',width: '28px', height: '28px' }" />
  </span>

  <!-- 清理图标 -->
  <span v-if="item.status == STATUS.upload_finish.value || item.status == STATUS.upload_seconds.value"
        @click="delUpload(item.uid, index)" class="btn-item">
    <img :src="getImage('clean')" :style="{ 'object-fit': 'contain',width: '28px', height: '28px' }" />
  </span>
            </div>
          </div>
        </div>
        <div v-if="fileList.length == 0" class="no-data">
        <div class="iconfont icon-empty"></div>
        <div class="msg">{{ noDataMessage }}</div>
      </div>
      </div>
    </div>
  </template>
  
  <script setup>
 /* eslint-disable */
  import SparkMD5 from "spark-md5";
  import { getCurrentInstance, ref, reactive} from "vue";
  const { proxy } = getCurrentInstance();
  
const fit = 'cover';  // 适配图片
const getImage = (iconName) => {
  return new URL(`/src/assets/${iconName}.png`, import.meta.url).href;
};
  const api = {
    upload: "/file/uploadFile",
  };
  const noDataMessage = 'There are currently no upload tasks available';
  const STATUS = {
    emptyfile: {
      value: "emptyfile",
      desc: "File is Empty",
      color: "#F75000",
      icon: "close",
    },
    fail: {
      value: "fail",
      desc: "Upload Failed",
      color: "#F75000",
      icon: "close",
    },
    init: {
      value: "init",
      desc: "Analyzing",
      color: "#e6a23c",
      icon: "clock",
    },
    uploading: {
      value: "uploading",
      desc: "Uploading",
      color: "#409eff",
      icon: "upload",
    },
    upload_finish: {
      value: "upload_finish",
      desc: "Upload Finished",
      color: "#67c23a",
      icon: "ok",
    },
    upload_seconds: {
      value: "upload_seconds",
      desc: "Second Finished",
      color: "#67c23a",
      icon: "ok",
    },
  };
  const size2Str = (limit) => {
    var size = "";
    if (limit < 0.1 * 1024) { //小于0.1KB，则转化成B
        size = limit.toFixed(2) + "B"
    } else if (limit < 0.1 * 1024 * 1024) { //小于0.1MB，则转化成KB
        size = (limit / 1024).toFixed(2) + "KB"
    } else if (limit < 0.1 * 1024 * 1024 * 1024) { //小于0.1GB，则转化成MB
        size = (limit / (1024 * 1024)).toFixed(2) + "MB"
    } else { //其他转化成GB
        size = (limit / (1024 * 1024 * 1024)).toFixed(2) + "GB"
    }
    var sizeStr = size + ""; //转成字符串
    var index = sizeStr.indexOf("."); //获取小数点处的索引
    var dou = sizeStr.substr(index + 1, 2) //获取小数点后两位的值
    if (dou == "00") { //判断后两位是否为00，如果是则删除00
        return sizeStr.substring(0, index) + sizeStr.substr(index + 3, 2)
    }
    return size;
};

  const chunkSize = 1024 * 512;
  const fileList = ref([]);
  const delList = ref([]);
  
  const addFile = async (file, filePid) => {
    const fileItem = {
      file: file,
      //文件UID
      uid: file.uid,
      //md5进度
      md5Progress: 0,
      //md5值
      md5: null,
      //文件名
      fileName: file.name,
      //上传状态
      status: STATUS.init.value,
      //已上传大小
      uploadSize: 0,
      //文件总大小
      totalSize: file.size,
      //进度
      uploadProgress: 0,
      //暂停
      pause: false,
      //当前分片
      chunkIndex: 0,
      //父级ID
      filePid: filePid,
      //错误信息
      errorMsg: null,
    };
    //加入文件
    fileList.value.unshift(fileItem);
    if (fileItem.totalSize == 0) {
      fileItem.status = STATUS.emptyfile.value;
      return;
    }
    //文件MD5
    let md5FileUid = await computeMD5(fileItem);
    if (md5FileUid == null) {
      return;
    }
    uploadFile(md5FileUid);
  };
  defineExpose({ addFile });
  
  //开始上传
  const startUpload = (uid) => {
    let currentFile = getFileByUid(uid);
    currentFile.pause = false;
    uploadFile(uid, currentFile.chunkIndex);
  };
  //暂停上传
  const pauseUpload = (uid) => {
    let currentFile = getFileByUid(uid);
    currentFile.pause = true;
  };
  //删除文件
  const delUpload = (uid, index) => {
    delList.value.push(uid);
    fileList.value.splice(index, 1);
  };
  
  const emit = defineEmits(["uploadCallback"]);
  const uploadFile = async (uid, chunkIndex) => {
    chunkIndex = chunkIndex ? chunkIndex : 0;
    //分片上传
    let currentFile = getFileByUid(uid);
    const file = currentFile.file;
    const fileSize = currentFile.totalSize;
    const chunks = Math.ceil(fileSize / chunkSize);
    for (let i = chunkIndex; i < chunks; i++) {
      let delIndex = delList.value.indexOf(uid);
      if (delIndex != -1) {
        delList.value.splice(delIndex, 1);
        // console.log(delList.value);
        break;
      }
      currentFile = getFileByUid(uid);
      if (currentFile.pause) {
        break;
      }
      let start = i * chunkSize;
      let end = start + chunkSize >= fileSize ? fileSize : start + chunkSize;
      let chunkFile = file.slice(start, end);
      let uploadResult = await proxy.Request({
        url: api.upload,
        showLoading: false,
        dataType: "file",
        params: {
          file: chunkFile,
          fileName: file.name,
          fileMd5: currentFile.md5,
          chunkIndex: i,
          chunks: chunks,
          fileId: currentFile.fileId,
          filePid: currentFile.filePid,
        },
        showError: false,
        errorCallback: (errorMsg) => {
          currentFile.status = STATUS.fail.value;
          currentFile.errorMsg = errorMsg;
        },
        uploadProgressCallback: (event) => {
          let loaded = event.loaded;
          if (loaded > fileSize) {
            loaded = fileSize;
          }
          currentFile.uploadSize = i * chunkSize + loaded;
          currentFile.uploadProgress = Math.floor(
            (currentFile.uploadSize / fileSize) * 100
          );
        },
      });
      if (uploadResult == null) {
        break;
      }
      currentFile.fileId = uploadResult.data.fileId;
      currentFile.status = STATUS[uploadResult.data.status].value;
      currentFile.chunkIndex = i;
      if (
        uploadResult.data.status == STATUS.upload_seconds.value ||
        uploadResult.data.status == STATUS.upload_finish.value
      ) {
        currentFile.uploadProgress = 100;
        emit("uploadCallback");
        break;
      }
    }
  };
  
  const computeMD5 = (fileItem) => {
    let file = fileItem.file;
    let blobSlice =
      File.prototype.slice ||
      File.prototype.mozSlice ||
      File.prototype.webkitSlice;
    let chunks = Math.ceil(file.size / chunkSize);
    let currentChunk = 0;
    let spark = new SparkMD5.ArrayBuffer();
    let fileReader = new FileReader();
    let time = new Date().getTime();
    //file.cmd5 = true;
  
    let loadNext = () => {
      let start = currentChunk * chunkSize;
      let end = start + chunkSize >= file.size ? file.size : start + chunkSize;
      fileReader.readAsArrayBuffer(blobSlice.call(file, start, end));
    };
  
    loadNext();
    return new Promise((resolve, reject) => {
      let resultFile = getFileByUid(file.uid);
      fileReader.onload = (e) => {
        spark.append(e.target.result); // Append array buffer
        currentChunk++;
        if (currentChunk < chunks) {
          /*  console.log(
            `第${file.name},${currentChunk}分片解析完成, 开始第${
              currentChunk + 1
            } / ${chunks}分片解析`
          ); */
          let percent = Math.floor((currentChunk / chunks) * 100);
          resultFile.md5Progress = percent;
          loadNext();
        } else {
          let md5 = spark.end();
          /*  console.log(
            `MD5计算完成：${file.name} \nMD5：${md5} \n分片：${chunks} 大小:${
              file.size
            } 用时：${new Date().getTime() - time} ms`
          ); */
          spark.destroy(); //释放缓存
          resultFile.md5Progress = 100;
          resultFile.status = STATUS.uploading.value;
          resultFile.md5 = md5;
          resolve(fileItem.uid);
        }
      };
      fileReader.onerror = () => {
        resultFile.md5Progress = -1;
        resultFile.status = STATUS.fail.value;
        resolve(fileItem.uid);
      };
    }).catch((error) => {
      return null;
    });
  };
  
  //获取文件
  const getFileByUid = (uid) => {
    let file = fileList.value.find((item) => {
      return item.file.uid === uid;
    });
    return file;
  };
  </script>
  
  <style lang="scss" scoped>
  .btn-item {
  cursor: pointer;
  display: inline-flex; // 添加 flex 布局以居中图片
  align-items: center;
  justify-content: center;
}
  .icon {
  text-align: center;
  display: inline-block;
  border-radius: 3px;
  overflow: hidden;
}

.no-data {
  text-align: center;
  padding: 10px 0px;
  .icon-empty {
    font-size: 50px;
    color: #bbb;
  }
  .msg {
    margin-top: 10px;
    color: #909399;
    font-size: 14px;
  }
}
  .uploader-panel {
    .uploader-title {
      border-bottom: 1px solid #ddd;
      line-height: 40px;
      padding: 0px 10px;
      font-size: 15px;
      .tips {
        font-size: 13px;
        color: rgb(169, 169, 169);
      }
    }
    .file-list {
      overflow: auto;
      padding: 10px 0px;
      min-height: calc(100vh / 2);
      max-height: calc(100vh - 120px);
      .file-item {
        position: relative;
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 3px 10px;
        background-color: #fff;
        border-bottom: 1px solid #ddd;
      }
      .file-item:nth-child(even) {
        background-color: #fcf8f4;
      }
      .upload-panel {
        flex: 1;
        .file-name {
          color: rgb(64, 62, 62);
        }
        .upload-status {
          display: flex;
          align-items: center;
          margin-top: 5px;
          .iconfont {
            margin-right: 3px;
          }
          .status {
            color: red;
            font-size: 13px;
          }
          .upload-info {
            margin-left: 5px;
            font-size: 12px;
            color: rgb(112, 111, 111);
          }
        }
        .progress {
          height: 10px;
        }
      }
      .op {
        width: 100px;
        display: flex;
        align-items: center;
        justify-content: flex-end;
        .op-btn {
          .btn-item {
            cursor: pointer;
          }
          .del,
          .clean {
            margin-left: 5px;
          }
        }
      }
    }
  }
  </style>
  
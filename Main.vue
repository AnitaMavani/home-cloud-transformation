<template>
  <el-container>
    <!-- Header -->
    <el-header class="header">
      <img src="@/assets/logo.png" alt="HOMECLOUD Logo">
      <span class="header-text">HOMECLOUD</span>
      
 
      <!-- 独立的图标和其关联的 popover -->
      <el-popover
        :width="800"
        trigger="click"
        v-model:visible="showUploader"
        :offset="20"
        transition="none"
        :hide-after="0"
        :popper-style="{ padding: '0px' }"
      >
        <template #reference>
          <span class="fa fa-exchange fa-rotate-90 icons-upload"></span>
        </template>
        <template #default>
          <Uploader
            ref="uploaderRef"
            @uploadCallback="uploadCallbackHandler"
          ></Uploader>
        </template>
      </el-popover>


    </el-header>
     <!-- Sidebar and Main Content -->
    <el-container>
      <!-- Sidebar -->
      <el-aside :style="{ width: '200px' , display: 'flex', flexDirection: 'column' }">
  <!-- Toggle Sidebar Button -->
  <el-menu default-active="1"  style="flex: 1;">
          <el-submenu index="1">
            <template v-slot:title>
                <i class="el-icon-menu"></i>
                <span>NavMenu</span>
            </template>
            <el-menu-item-group>
              <template v-slot:title>
    <span style="font-size: 18px;">Category</span></template>

<!---->
<el-menu-item index="1-1">
  <span class="menu-item-content">
    <el-tooltip content="All Files" placement="bottom" effect="light">
      <i class="fa fa-th-list"></i>
    </el-tooltip>
    <el-tooltip content="All Files" placement="right" effect="light">
      All
    </el-tooltip>
  </span>
</el-menu-item>

<el-menu-item index="1-2">
  <span class="menu-item-content">
    <el-tooltip content="All Video Files" placement="bottom" effect="light">
      <i class="fa fa-youtube-play"></i>
    </el-tooltip>
    <el-tooltip content="All Video Files" placement="right" effect="light">
      Video
    </el-tooltip>
  </span>
</el-menu-item>

<el-menu-item index="1-3">
  <span class="menu-item-content">
    <el-tooltip content="All Music Files" placement="bottom" effect="light">
      <i class="fa fa-music"></i>
    </el-tooltip>
    <el-tooltip content="All Music Files" placement="right" effect="light">
      Music
    </el-tooltip>
  </span>
</el-menu-item>

<el-menu-item index="1-4">
  <span class="menu-item-content">
    <el-tooltip content="All Picture Files" placement="bottom" effect="light">
      <i class="fa fa-picture-o"></i>
    </el-tooltip>
    <el-tooltip content="All Picture Files" placement="right" effect="light">
      Picture
    </el-tooltip>
  </span>
</el-menu-item>

<el-menu-item index="1-5">
  <span class="menu-item-content">
    <el-tooltip content="All Document Files" placement="bottom" effect="light">
      <i class="fa fa-file-text-o"></i>
    </el-tooltip>
    <el-tooltip content="All Document Files" placement="right" effect="light">
      Document
    </el-tooltip>
  </span>
</el-menu-item>

<el-menu-item index="1-6">
  <span class="menu-item-content">
    <el-tooltip content="All Other Files" placement="bottom" effect="light">
      <i class="fa fa-server"></i>
    </el-tooltip>
    <el-tooltip content="All Other Files" placement="right" effect="light">
      Other
    </el-tooltip>
  </span>
</el-menu-item>
            </el-menu-item-group>

            <el-menu-item>
        <span class="menu-item-content">
          <el-tooltip content="Recycle Files" placement="bottom" effect="light">
            <i class="fa fa-recycle"></i>
          </el-tooltip>
          <el-tooltip content="Recycle Files" placement="right" effect="light">
            Recycle
          </el-tooltip>
        </span>
      </el-menu-item>
          </el-submenu>
        </el-menu>
        
      </el-aside>

      <!-- Main Content -->
<el-main style="padding: 10px;">

  <!-- Button and Search Group -->
  <div class="button-group">
    <el-upload
            :show-file-list="false"
            :with-credentials="true"
            :multiple="true"
            :http-request="addFile"
                      >
            <el-button type="primary">
              <span class="fa fa-upload"></span>
              Upload
            </el-button>
          </el-upload>

          <el-button type="success">
          <span class="iconfont icon-folder-add"></span>
          Creat New Folder
        </el-button>
        <el-button
       
          type="danger"
          :disabled="selectFileIdList.length == 0"
        >
          <span class="iconfont icon-del"></span>
          Batch Delete
        </el-button>
        <el-button
          type="warning"
          :disabled="selectFileIdList.length == 0"
        >
          <span class="iconfont icon-move"></span>
          Batch Move
        </el-button>

        <div class="iconfont icon-refresh"></div>
      </div>

       <!-- Table -->
       <div class="file-list" >
      <Table
        ref="dataTableRef"
        :columns="columns"
        :showPagination="true"
        :dataSource="tableData"
        :fetch="loadDataList"
        :initFetch="false"
        :options="tableOptions"
        @rowSelected="rowSelected"
      >
      <template #fileName="{ row }">
  <div class="file-name">{{ row.fileName }}</div>
</template>
<template #DownLoad="{ row }">
  <el-button size="small" type="primary" @click="download(row)">Download</el-button>
</template>

        <template #fileSize="{ index, row }">
  <span v-if="row.fileSize">{{ size2Str(row.fileSize) }}</span>
</template>
      </Table>
    </div>
    
      </el-main>
          </el-container>
  </el-container>
</template>

<script setup>

import axios from "axios";

import Uploader from "./Uploader.vue";
import { useStore } from "vuex";
import { ref, watch, getCurrentInstance, nextTick, onMounted, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessageBox } from 'element-plus'


const confirm = (message, okfun) => {
    ElMessageBox.confirm(message, 'Message', {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'info',
    }).then(() => {
        okfun();
    }).catch(() => { })
};
const showLoading = ref(false);
const editing = ref(false);
const { proxy } = getCurrentInstance();
const loadDataList = async () => {
  let params = {
    pageNo: tableData.value.pageNo,
    pageSize: tableData.value.pageSize,
  };
 
  let result = await proxy.Request({
    url: api.loadDataList,
    showLoading: showLoading,
    params,
  });
  if (!result) {
    return;
  }
  tableData.value = result.data;
  editing.value = false;
};

const size2Str = (limit) => {
  var size = "";
  if (limit < 0.1 * 1024) {
    size = limit.toFixed(2) + "B";
  } else if (limit < 0.1 * 1024 * 1024) {
    size = (limit / 1024).toFixed(2) + "KB";
  } else if (limit < 0.1 * 1024 * 1024 * 1024) {
    size = (limit / (1024 * 1024)).toFixed(2) + "MB";
  } else {
    size = (limit / (1024 * 1024 * 1024)).toFixed(2) + "GB";
  }
  var sizeStr = size + ""; // 转成字符串
  var index = sizeStr.indexOf("."); // 获取小数点处的索引
  var dou = sizeStr.substr(index + 1, 2); // 获取小数点后两位的值
  if (dou === "00") { // 判断后两位是否为00，如果是则删除00
    return sizeStr.substring(0, index) + sizeStr.substr(index + 3, 2);
  }
  return size;
};
onMounted(() => {
  loadDataList();
});
const showUploader = ref(false);
const emit = defineEmits(["addFile"]);
//添加文件
const uploaderRef = ref();
const addFile = async (fileData) => {
 
  const fileEvent = { file: fileData.file, filePid: 0 };
  const { file, filePid } = fileEvent;  
  showUploader.value = true;
  uploaderRef.value.addFile(file, filePid);
};

const uploadCallbackHandler = () => {loadDataList();};

const api = {
  loadDataList: "/file/loadDataList",
  download: "/api/file/download",
};


//列表
const columns = [

  {
    label: "FileName",
    prop: "fileName",
    scopedSlots: "fileName",
    
  },
  { label: "DownLoad",
    prop: "DownLoad",
    scopedSlots: "DownLoad",
     },

  {
    label: "LastUpdateTime",
    prop: "lastUpdateTime",
    width: 200,
  },
  {
    label: "Size",
    prop: "fileSize",
    scopedSlots: "fileSize",
    width: 150,
  },
];

//列表
const tableData = ref({});
const tableOptions = {
  extHeight: 50,
  selectType: "checkbox",
};
const download = (row) => {
  if (!row || !row.fileId) {
    console.error("Missing file information.");
    return;
  }
  window.location.href = api.download + "/" + row.fileId;
};


//多选 批量选择
const selectFileIdList = ref([]);
const selectFileList = ref([]);
const rowSelected = (rows) => {
  selectFileList.value = rows;
  selectFileIdList.value = [];
  rows.forEach((item) => {
    selectFileIdList.value.push(item.fileId);
  });
};
</script>


<style scoped>
.icon {
  text-align: center;
  display: inline-block;
  border-radius: 3px;
  overflow: hidden;
  align-items: center;
  iimg {
  width: 32px !important;
  height: 32px !important;
  object-fit: cover !important;
}
}
.top {
    margin-top: 20px;

    .top-op {
        display: flex;
        align-items: center;

        .btn {
            margin-right: 10px;
        }

        .search-panel {
            margin-left: 10px;
            width: 300px;
        }

        .icon-refresh {
            cursor: pointer;
            margin-left: 10px;
        }

        .not-allow {
            background: #d2d2d2 !important;
            cursor: not-allowed;
        }
    }
}

.file-list {
    .file-item {
        display: flex;
        align-items: center;
        padding: 0px 0px;

        .file-name {
            margin-left: 8px;
            flex: 1;
            width: 0;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;

            >span {
                cursor: pointer;

                &:hover {
                    color: #06a7ff;
                }
            }

            .transfer-status {
                font-size: 13px;
                margin-left: 10px;
                color: #e6a23c;
            }

            .transfer-fail {
                color: #f75000;
            }
        }       
    }
}


.menu-item-content {
  display: flex;
  align-items: center;
  gap: 5px;  
}
/* 增加菜单项之间的间距 */
.el-menu-item {
    margin-top: 10px;
    margin-bottom: 10px;
}
.el-menu-item:hover {
    background-color: #ffffff; /* 示例：改变背景色 */
    cursor: pointer; /* 将鼠标指针改为手形 */
}

/* 定义悬停在菜单项内容上时的样式 */
.menu-item-content:hover {
    color: #409EFF; /* 示例：改变文字颜色 */
}
.el-menu {
    margin-top: 0px; /* 调整这个值以满足您的需求 */
    background-color: rgb(235, 243, 255);
}
.header{ background-color: rgb(75, 151, 255);}
.header-text {
  display: fixed;
  color: white; /* 文字颜色为白色 */
  font-size: 35px; /* 文字大小为18号 */
  margin-left: 10px; /* 在图片和文字之间添加一些空间 */
  align-items: center;
  /* 可以添加其他样式以匹配设计 */
}
.ellipsis-text {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 180px;  /* 或其他适当的宽度 */
}
img {
    height: 38px; /* Adjust as needed */
    margin: 0px; /* Adjust as needed */
}
/* 为按钮添加自定义颜色 */
.el-button--primary {
  background-color: #409eff;
  border-color: #409eff;
}

.el-button--success {
  background-color: #67c23a;
  border-color: #67c23a;
}

.el-button--danger {
  background-color: #f56c6c;
  border-color: #f56c6c;
}

.el-button--warning {
  background-color: #e6a23c;
  border-color: #e6a23c;
}

.el-aside {
    transition: width 0.3s ease;
    overflow-y: auto; /* Allow vertical scrolling */
    overflow-x: hidden; /* Hide horizontal scrollbar */
    
}

.icons-upload {
    font-size: 20px;
    position: absolute;
    right: 395px;  /* 1140 (from user-info) + 20 (some gap) */
    top: 20px;
    color: white;
    cursor: pointer;
}

.button-group {
    display: flex;
    align-items: center;
    gap: 10px; /* spacing between items */
    margin-bottom: 0px; /* spacing before the table */
}

.btn-create-folder {
    background-color: #4CAF50; /* green */
    color: white;
    /* other styles as required */
}
.btn-batch-delete {
    background-color: #f44336; /* red */
    color: white;
    /* other styles as required */
}

.btn-batch-move {
    background-color: #2196F3; /* blue */
    color: white;
    /* other styles as required */
}
.btn-refresh {
    background-color: #FFEB3B; /* yellow */
    color: black;
    /* other styles as required */
}
.search-input {
    flex-grow: 1;
    padding: 5px;
    border: 1px solid #ccc;
    /* other styles as required */
}
.button-group .el-button {
    align-items: center;
    justify-content: center;
}
.fa {
    margin-right: 9px;  /* or any desired spacing */
    margin-LEFT: -6px;
}
</style>
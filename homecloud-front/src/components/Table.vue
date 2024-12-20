<template>
  <div>
    <!-- DataTable Component -->
    <el-table
      ref="dataTable"
      :data="dataSource.list || []"
      :height="tableHeight"
      :stripe="options.stripe"
      :border="options.border"
      header-row-class-name="table-header-row"
      highlight-current-row
      @row-click="handleRowClick"
      @selection-change="handleSelectionChange"
    >
     <!-- Selection Checkbox -->
      <el-table-column
        v-if="options.selectType && options.selectType == 'checkbox'"
        type="selection"
        width="50"
        align="center"
      ></el-table-column>
      <!-- Index Column -->
      <el-table-column
        v-if="options.showIndex"
        label="序号"
        type="index"
        width="60"
        align="center"
      ></el-table-column>
      <!-- Data Columns -->
      <template v-for="(column, index) in columns">
        <template v-if="column.scopedSlots">
          <el-table-column
            :key="index"
            :prop="column.prop"
            :label="column.label"
            :align="column.align || 'left'"
            :width="column.width"
          >
            <template #default="scope">
              <slot
                :name="column.scopedSlots"
                :index="scope.$index"
                :row="scope.row"
              >
              </slot>
            </template>
          </el-table-column>
        </template>
        <template v-else>
          <!-- Regular Data Columns -->
          <el-table-column
            :key="index"
            :prop="column.prop"
            :label="column.label"
            :align="column.align || 'left'"
            :width="column.width"
            :fixed="column.fixed"
          >
          </el-table-column>
        </template>
      </template>
    </el-table>
     <!-- Pagination -->
    <div class="pagination" v-if="showPagination">
      <el-pagination
        v-if="dataSource.totalCount"
        background
        :total="dataSource.totalCount"
        :page-sizes="[15, 30, 50, 100]"
        :page-size="dataSource.pageSize"
        :current-page.sync="dataSource.pageNo"
        :layout="layout"
        @size-change="handlePageSizeChange"
        @current-change="handlePageNoChange"
        style="text-align: right"
      ></el-pagination>
    </div>
  </div>
</template>
<script setup>
import { ref, computed } from "vue";

const emit = defineEmits(["rowSelected", "rowClick"]);
const props = defineProps({
  dataSource: Object,
  showPagination: {
    type: Boolean,
    default: true,
  },
  showPageSize: {
    type: Boolean,
    default: true,
  },
  options: {
    type: Object,
    default: {
      extHeight: 0,
      showIndex: false,
    },
  },
  columns: Array,
  fetch: Function, // Function to fetch data
  initFetch: {
    type: Boolean,
    default: true,
  },
});

const layout = computed(() => {
  return `total, ${
    props.showPageSize ? "sizes" : ""
  }, prev, pager, next, jumper`;
});
//Top: 60, Distance from the top to the content area: 20, Content top and bottom padding: 15*2, Pagination area height: 46
const topHeight = 60 + 20 + 30 + 46;

// Table height initialization
const tableHeight = ref(
  props.options.tableHeight
    ? props.options.tableHeight
    : window.innerHeight - topHeight - props.options.extHeight
);

// Initialization
const init = () => {
  if (props.initFetch && props.fetch) {
    props.fetch();
  }
};
init();

// DataTable reference
const dataTable = ref();

// Clear selection
const clearSelection = () => {
  dataTable.value.clearSelection();
};

// Set current row selection
const setCurrentRow = (rowKey, rowValue) => {
  let row = props.dataSource.list.find((item) => {
    return item[rowKey] === rowValue;
  });
  dataTable.value.setCurrentRow(row);
};
// Expose child components
defineExpose({ setCurrentRow, clearSelection });

// Row click handler
const handleRowClick = (row) => {
  emit("rowClick", row);
};

// Selection change handler
const handleSelectionChange = (row) => {
  emit("rowSelected", row);
};

// Page size change handler
const handlePageSizeChange = (size) => {
  props.dataSource.pageSize = size;
  props.dataSource.pageNo = 1;
  props.fetch();
};
// Page number change handler
const handlePageNoChange = (pageNo) => {
  props.dataSource.pageNo = pageNo;
  props.fetch();

  const columns = ref([
  { prop: 'columnName1', label: 'Column 1', width: 150 }, // 第一列宽度为 150px
  { prop: 'columnName2', label: 'Column 2', width: 200 }, // 第二列宽度为 200px
  // ... Other column configurations
]);
};
</script>
<style lang="scss" scoped>
.pagination {
  padding-top: 10px;
  padding-right: 10px;
}
.el-pagination {
  justify-content: right;
}

:deep .el-table__cell {
  padding: 4px 0px;
}
:deep .el-table__cell {
  align-items: center; // Align content vertically center
}
:deep .el-table__row {
  height: 55.2px; // Adjust this value to change row height
}
</style>

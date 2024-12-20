import axios from 'axios'
import { ElLoading, ElMessage } from 'element-plus'
import router from '@/router'

const contentTypeForm = 'application/x-www-form-urlencoded;charset=UTF-8'
const contentTypeJson = 'application/json'
const responseTypeJson = "json"

let loading = null;
const instance = axios.create({
    baseURL: '/api',
    timeout: 30 * 1000,
});

//Request pre-interceptor
instance.interceptors.request.use(
    (config) => {
        if (config.showLoading) {
            loading = ElLoading.service({
                lock: true,
                text: 'Loading......',
                background: 'rgba(0, 0, 0, 0.7)',
            });
        }
        return config;
    },
    (error) => {
        if (error.config.showLoading && loading) {
            loading.close();
        }
        message.error("Request failed to send");
        return Promise.reject("Request failed to send");
    }
);

//Request post-interceptor
instance.interceptors.response.use(
    (response) => {
        const { showLoading, errorCallback, showError = true, responseType } = response.config;
        if (showLoading && loading) {
            loading.close()
        }
        const responseData = response.data;
        if (responseType == "arraybuffer" || responseType == "blob") {
            return responseData;
        }
        // Normal request
        if (responseData.code == 200) {
            return responseData;
        } else if (responseData.code == 901) {
            // Login timeout
            router.push("/login?redirectUrl=" + encodeURI(router.currentRoute.value.path));
            return Promise.reject({ showError: false, msg: "Login timed out" });
        } else {
            //  Other errors
            if (errorCallback) {
                errorCallback(responseData.info);
            }
            return Promise.reject({ showError: showError, msg: responseData.info });
        }
    },
    (error) => {
        if (error.config.showLoading && loading) {
            loading.close();
        }
        return Promise.reject({ showError: true, msg: "Network error" })
    }
);

const request = (config) => {
    const { url, params, dataType, showLoading = true, responseType = responseTypeJson } = config;
    let contentType = contentTypeForm;
    let formData = new FormData();// Create a form object
    for (let key in params) {
        formData.append(key, params[key] == undefined ? "" : params[key]);
    }
    if (dataType != null && dataType == 'json') {
        contentType = contentTypeJson;
    }
    let headers = {
        'Content-Type': contentType,
        'X-Requested-With': 'XMLHttpRequest',
    }

    return instance.post(url, formData, {
        onUploadProgress: (event) => {
            if (config.uploadProgressCallback) {
                config.uploadProgressCallback(event);
            }
        },
        responseType: responseType,
        headers: headers,
        showLoading: showLoading,
        errorCallback: config.errorCallback,
        showError: config.showError
    }).catch(error => {
        console.log(error);
        if (error.showError) {
            message.error(error.msg);
        }
        return null;
    });
};

export default request;

const showMessage = (msg, callback, type) => {
    ElMessage({
        type: type,
        message: msg,
        duration: 2000,
        onClose: () => {
            if (callback) {
                callback();
            }
        }
    })
}

export const message = {
    error: (msg, callback) => {
        showMessage(msg, callback, "error");
    },
    success: (msg, callback) => {
        showMessage(msg, callback, "success");
    },
    warning: (msg, callback) => {
        showMessage(msg, callback, "warning");
    },
}

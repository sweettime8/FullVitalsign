'use strict';

// Địa chỉ backEnd
const loginUrl = 'http://103.21.151.185:8415';
const uploadUrl = 'http://103.21.151.185:8413';

var singleUploadForm = document.querySelector('#singleUploadForm');
var singleFileUploadInput = document.querySelector('#singleFileUploadInput');
var singleFileUploadError = document.querySelector('#singleFileUploadError');
var singleFileUploadSuccess = document.querySelector('#singleFileUploadSuccess');

var multipleUploadForm = document.querySelector('#multipleUploadForm');
var multipleFileUploadInput = document.querySelector('#multipleFileUploadInput');
var multipleFileUploadError = document.querySelector('#multipleFileUploadError');
var multipleFileUploadSuccess = document.querySelector('#multipleFileUploadSuccess');

function getData() {
    let myKeyJwt = sessionStorage.getItem("myKeyJwt");
    if(myKeyJwt) {
        axios({
            method: 'get',
            responseType: 'json',
            url: loginUrl + '/v1.0/user?currentPage=1&rowsPerPage=20&sort=uuid',
            headers: {'Authorization': 'Bearer ' + myKeyJwt}
        })
        .then(response => {
            if(response.data) {
                console.log(response.data);
                alert('Get data OK!');
            }else
                alert('Loi ko xac dinh');
        })
        .catch(error => {
            console.error(error.response.data);
            alert('Get data that bai hoac chua dang nhap');
        });
    } else {
        alert('Chua dang nhap!');
    }
}

function postData() {
    axios({
      method: 'post',
      responseType: 'json',
      url: loginUrl + '/v1.0/user/login',
      data: {
        email: $('#email').val(),
        password: $('#password').val()
      }
    })
     .then(response => {
       if(response.data) {
           
            sessionStorage.setItem("myKeyJwt", response.data.accessToken);
            alert('Login thanh cong');
       }else
            alert('Loi ko xac dinh');   
     })
     .catch(error => {
       //console.error(error.response.data);
       if( sessionStorage.getItem("myKeyJwt") )
            sessionStorage.setItem("myKeyJwt", undefined);
       alert('Login that bai ['+error.response.data.status+']: ' + error.response.data.message);
     });
}

function uploadSingleFile(file) {
    //let myKeyJwt = sessionStorage.getItem("myKeyJwt");
    let myKeyJwt = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkZDYwOWE0ZC0zYWZmLTRlZWYtYTUzYy05ZmE3NmQ2NjlhMDMiLCJpYXQiOjE1OTkyNzYxMTksImV4cCI6MTU5OTg4MDkxOX0.nK9M6oGcdDucg18MNObQac3ibYYrgCKDlXDNKJByj2iK71ayBTdHo1WsROE7Pkr3LVg1jCp0-qyVSm_4prkgVA';
    if(myKeyJwt) {
        var formData = new FormData();
        formData.append("file", file);

        var xhr = new XMLHttpRequest();
        xhr.open("POST", uploadUrl + '/v1.0/upload/qa/image');
        xhr.setRequestHeader('Authorization', 'Bearer ' + myKeyJwt);
        xhr.onload = function() {
            console.log(xhr.responseText);
            var response = JSON.parse(xhr.responseText);
            if(xhr.status === 200) {
                singleFileUploadError.style.display = "none";
                singleFileUploadSuccess.innerHTML = "<p>Upload thành công.</p><p>Địa chỉ tải file : <a href='" + response.fileDownloadUri + "' target='_blank'>" + response.fileDownloadUri + "</a></p>";
                singleFileUploadSuccess.style.display = "block";
            } else {
                singleFileUploadSuccess.style.display = "none";
                singleFileUploadError.innerHTML = (response && response.message) || "Lỗi không xác định";
            }
        }
        xhr.send(formData);
    }
    else
        alert('Chua dang nhap!');
}

function uploadMultipleFiles(files) {
    let myKeyJwt = sessionStorage.getItem("myKeyJwt");
    if(myKeyJwt) {
        var formData = new FormData();
        for(var index = 0; index < files.length; index++) {
            formData.append("files", files[index]);
        }

        var xhr = new XMLHttpRequest();
        xhr.open("POST", uploadUrl + '/v1.0/upload/qa/image');
        xhr.setRequestHeader('Authorization', 'Bearer ' + myKeyJwt);

        xhr.onload = function() {
            console.log(xhr.responseText);
            var response = JSON.parse(xhr.responseText);
            if(xhr.status == 200) {
                multipleFileUploadError.style.display = "none";
                var content = "<p>Upload thành công</p>";
                for(var i = 0; i < response.length; i++) {
                        content += "<p>Địa chỉ tải file : <a href='" + response[i].fileDownloadUri + "' target='_blank'>" + response[i].fileDownloadUri + "</a></p>";
                }
                multipleFileUploadSuccess.innerHTML = content;
                multipleFileUploadSuccess.style.display = "block";
            } else {
                multipleFileUploadSuccess.style.display = "none";
                multipleFileUploadError.innerHTML = (response && response.message) || "Lỗi không xác định";
            }
        }
        xhr.send(formData);
    }
    else
        alert('Chua dang nhap!');
}


singleUploadForm.addEventListener('submit', function(event){
    var files = singleFileUploadInput.files;
    if(files.length === 0) {
        singleFileUploadError.innerHTML = "Chưa chọn file";
        singleFileUploadError.style.display = "block";
    }
    uploadSingleFile(files[0]);
    event.preventDefault();
}, true);


multipleUploadForm.addEventListener('submit', function(event){
    var files = multipleFileUploadInput.files;
    if(files.length === 0) {
        multipleFileUploadError.innerHTML = "Phải chọn ít nhất 1 file";
        multipleFileUploadError.style.display = "block";
    }
    uploadMultipleFiles(files);
    event.preventDefault();
}, true);

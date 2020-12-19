var crypto = require('crypto');

exports.hash = function (data) {
    return crypto.createHash('md5').update(data).digest("hex");
};

exports.isEmpty = function (obj) {
    return (Object.keys(obj).length === 0 && obj.constructor === Object);
};

exports.formatTimeForSchedules = function (times) {
    let arrNgayThangNam = times.split(' ')[0];
    let arrGioPhut = times.split(' ')[1];

    let ngay = Number(arrNgayThangNam.split('-')[2]);
    let thang = Number(arrNgayThangNam.split('-')[1]) - 1;
    let nam = arrNgayThangNam.split('-')[0];

    let gio = Number(arrGioPhut.split(':')[0]);
    let phut = Number(arrGioPhut.split(':')[1]);
    let giay = Number(arrGioPhut.split(':')[2]);

    //return new Date(nam, thang, ngay, gio, phut, 0);
    return new Date(nam, thang, ngay, gio, phut, giay);
}

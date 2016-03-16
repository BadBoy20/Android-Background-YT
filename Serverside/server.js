var http = require('http');
var fs = require('fs');
var port = 2000;

/* THIS PASSWORD IS WHAT CHECKS AGAINST THE PHONE TO ENSURE ITS YOUR PHONE */
var UApassword = 'afbqi';
/*                                                                         */
var filePath = "/home/GGbaby/msDL/"; //Area in your server to keep the music files temporarily
var maxFiles = 2; // total number of files you want to keep on the server

var spawn = require('child_process').spawn;
var fileArray = [];

function getDateTime() {
  var date = new Date();
  var hour = date.getHours();
  hour = (hour < 10 ? "0" : "") + hour;
  var min = date.getMinutes();
  min = (min < 10 ? "0" : "") + min;
  var sec = date.getSeconds();
  sec = (sec < 10 ? "0" : "") + sec;
  var year = date.getFullYear();
  var month = date.getMonth() + 1;
  month = (month < 10 ? "0" : "") + month;
  var day = date.getDate();
  day = (day < 10 ? "0" : "") + day;
  return year + ":" + month + ":" + day + ":" + hour + ":" + min + ":" + sec;
}
http.createServer(function(request, response) {
      var ip = request.connection.remoteAddress;
      if (request.method == 'POST') {
        console.log(getDateTime() + " " + ip + " " + "Incoming POST request");
        if (request.headers['user-agent'] == UApassword) {

          var strBody;
          request.on("data", function(chunk) {
            strBody += chunk;
          });
          request.on("end", function() {
            var songID = strBody.split('uuid=')[1];
            var link = "https://www.youtube.com/watch?v=" + strBody.replace("undefined&ytlink=", "").split('uuid=')[0];
            var child = spawn('youtube-dl', ['-q', '-f', '141,140', '-o', filePath + songID + '.m4a', link, '--prefer-ffmpeg']);
            console.log('Child thread for ' + link + " started. Pid: " + child.pid);
            child.unref();
            child.on('close', function(exitCode) {
              if (exitCode !== 0) {
                console.error(getDateTime() + " " + ip + " " + 'Download Cancelled of pid:');
                response.end();
              } else {
                console.log(getDateTime() + " " + ip + " " + 'File Downloaded.');
                fileArray.push(songID);
                response.end();
              }
            });
          });
        }
      } else if (request.url.indexOf('/song') > -1) {
        if (request.headers['user-agent'] == UApassword) {
          if (fileArray.length > maxFiles) {
            console.log(getDateTime() + " " + ip + " " + "Deleting Older File")
            fs.unlinkSync(filePath + fileArray[0] + '.m4a');
            fileArray.shift();
          }
          var songId = request.url.replace("/song/", "");
          console.log(songId);
          console.log(getDateTime() + " " + ip + " " + "Music playback");
          var stat = fs.statSync(filePath + songId + ".m4a");
          response.writeHead(200, {
            'Content-Type': 'audio/m4a',
            'Content-Length': stat.size
          });
          var readStream = fs.createReadStream(filePath + songId + ".m4a");
          readStream.pipe(response);
        }
      }
    }).listen(port);
  host = '127.0.0.1';
  console.log('Listening at http://' + host + ':' + port);

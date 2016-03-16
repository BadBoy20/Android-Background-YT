# Android-Background-YT

Hi. This piece of software is made to run music from youtube in the background of an Android Phone.

The idea is that there would be a server that would be accepting incoming POST requests from your phone. Then use that information to get the m4a from youtube using youtube-dl. Then put that file up for grabs so that your phone can get it and play it in the background from the app's media player as a service (enabling it to run in the background).

Installation:

Use Android Studio to load up that project. Then make the apk file and upload it to your phone and install it in your phone. Make sure of the parameter of UserAgent called UApassword. That password has to be consistent with the one in the server.

On the server side. Put the server.js file on your server, and run it. Again, make sure the UApassword at the top of the server.js file is the same as the one on phone. 

The UApassword is a kind of check in the user agent to make sure only those YOU allow to use the service can use it.

Make sure your firewall allows incoming NEW requests and outgoing RELATED requests for this to work.

Usage:

Just open the app after you compiled it. Put in search query. Select a song, click "get song" and the phone will automatically download and play it.

Other options:

In the server side code, there is a variable for file Path, to temporarily put your music files in. 
A maxFiles variable that tells the computer how many files it can keep. And the server.js file is programmed so that an array is kept of how many files are in there currently. when the number of files you have on the server exceed the MaxFiles value, the server deletes the first file, and so on..

<b>Collaboration:</b>

Pull Requests, suggestions and contributions are allowed and in fact, encouraged.

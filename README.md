# androidClientMp3Convertor
This android app is client for my application which download youtube video and convert it to mp3. 
User copy video url to text field and then application can send url to server which is listening on port 4467.
After server done all the work application receive mp3 through the SOCKET connection.
This is the less data demanding solution for end user. All traffic and computing power is handled by server.
Server: https://github.com/hejsek/serversideMp3Convertor

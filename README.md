# Impression-App-for-android

(Commented out code is for future development to be linked with https://github.com/MaxymWojnowskyj/Impression-App-socket-server)

Implemented Firebase google authentication along with a temporary create and join friend mode functionality using Firebase Realtime database. With the purpose of familiariazing my self with android app development using android studio and Firebase as the backend database server. 


The Firebase database uses the google authentication to store the users data then generates random gameRoom codes when a player creates a game.

Seen here with the following example formatting:
```
gameRooms: {
  123a: {
    {"uid1": true},
    {"uid2", true},
    etc..
  },
  d2d2: {},
  etc..
}
users: {
  "uid1": {
      {userName, iconURL}
   },
   "uid2": {},
   etc..
}

```

Here is the basic login page:
(Clicking on the google login button redirects the user to the google standard login page.)
<img width="173" alt="Screen Shot 2022-08-22 at 1 34 59 AM" src="https://user-images.githubusercontent.com/97153442/185865270-e8e0b84a-a1a8-4831-a2d3-85cdb23ffa54.png">


Here is the basic home page:
(The logged in user data is being displayed under the Impression App title)
<img width="1094" alt="Screen Shot 2022-08-21 at 4 12 03 PM" src="https://user-images.githubusercontent.com/97153442/185865136-950f0415-ceda-4ae8-b90a-876401f5c9fc.png">


Here is a video demo of a user creating and leaving a game with the gameData being created in the database:
(Please note that I am running an emulator on my old laptop so the reason for the app running slowly is due to my laptops low processing power.) 


https://user-images.githubusercontent.com/97153442/185864957-3a7257a2-a200-4706-bf3f-e6b49e254ca4.mov




(Image slideshow incase the .mov video doesnt load)

<img width="1078" alt="Screen Shot 2022-08-22 at 1 37 43 AM" src="https://user-images.githubusercontent.com/97153442/185865983-66baa3ea-0006-49f5-b09b-7c8a86b84a37.png">
<img width="1086" alt="Screen Shot 2022-08-22 at 1 38 13 AM" src="https://user-images.githubusercontent.com/97153442/185866038-e21f5436-78c8-4a1d-8f2d-837ff0f1266b.png">
<img width="1104" alt="Screen Shot 2022-08-22 at 1 38 26 AM" src="https://user-images.githubusercontent.com/97153442/185866866-a6e450a2-7b9a-448f-8581-c7a819e66501.png">
<img width="1078" alt="Screen Shot 2022-08-22 at 1 38 46 AM" src="https://user-images.githubusercontent.com/97153442/185866133-ee7ce6d5-5517-49ea-9d82-19d2e83d8ea7.png">

# Yet Another Chat App

This repo contains example code for a workshop that introduces the Firebase
Realtime Database.

This is not an official Google product.

## Workshop Prereqs

1. If you don't already have a Google account (such as a GMail account), create
   one: https://accounts.google.com/SignUp. This will be used to create and
   manage the Firebase Project.
1. Install Android Studio.
    * https://developer.android.com/studio/index.html
1. Ensure the latest Android SDK is installed. (Android 8.0 "O", API Level 26,
   Revision 2, as of the time of this writing.) Note that we'll actually target
   a lower API level.
    * Open Android Studio
    * Tools -> Android -> SDK Manager
    * Check the box next to the latest version
    * Click OK
1. Ensure an Android Virtual Device is available, preferably running the latest
   system image ("O", API Level 26 as of the time of this writing.) But feel
   free to create a few different ones running different API levels.
    * Open Android Studio
    * Tools -> Android -> AVD Manager
    * Click Create Virtual Device
    * Pick a hardware device, and click Next. (I'm using 'Pixel'.)
    * Choose latest recommended system image, and click Next. (You may need to
      click the 'Download' link to download it first.)
    * On the Verify Configuration screen, click Finish.
    * Click the 'play' button on the newly created AVD to launch it.
1. Checkout this project, and build it under the `start` directory.
    * `git clone https://github.com/google/YetAnotherChatApp`
    * In Android Studio, open YetAnotherChatApp/start.
    * Click the 'Run app' button. (^R)
    * It should look like this:

![Screenshot of the example app in
action](https://github.com/google/YetAnotherChatApp/YetAnotherChatAppScreenShot.png)


## Step 1: Optional: s/example.com/yourdomainhere.com/

The app has android package name of `com.example.yetanotherchatapp`. You should
strongly consider changing this to a domain that you own. However
`com.example.yetanotherchatapp` will work for this workshop.

There's many ways to do this, but possibly the easiest is to right click on the
package name from within Android Studio and select 'Refactor -> Rename'.


## Step 2: Setup the Firebase Project and Download `google-services.json`

We'll use the Android Studio assitant to create the app in the Firebase Console
and download google-services.json (which is used by your app to connect to
Firebase.) Alternatively, this step can be done via the Firebase Console itself,
but you'd need to download google-services.json and integrate it with your
project manually.

1. In Android Studio, open Tools -> Firebase. This will open the assistant, with
   a long list of available Firebase tools.
1. Open up the 'Realtime Database' item and click 'Save and retrieve data'.
1. Click 'Connect to Firebase'. (This may redirect you to a browser window to
   sign in with your Google account.)
1. Select 'Create new Firebase project'. Give it a name and choose a
   Country/Region. (I'm using 'YetAnotherChatApp', and 'Canada' respectively.)
1. Click 'Connect to Firebase'

Now we need to enable the RTDB:

1. You should still be in the Firebase Realtime Database assistant.
1. Click 'Add the Realtime Database to your app'
1. Click 'Accept Changes'

You can now close the assitant.


## Step 3: Setup RTDB Rules for Public Access

1. Go to the Firebase Console: https://console.firebase.google.com/
1. If necessary, click the project you just created. (If you only have one
   project, this won't be necessary.)
1. Click the 'Database' item from the menu on the left.
1. Click 'Get Started' in the Realtime Database card.
1. Click the 'Rules' tab
1. The current rules look like this:
    ```javascript
    {
      "rules": {
        ".read": "auth != null",
        ".write": "auth != null"
      }
    }
    ```
    For now, we'll change this to allow public read/writes. (This might be ok
    for development. Once you start using Authentication, you won't want to do
    this.)
    ```javascript
    {
      "rules": {
        ".read": true,
        ".write": true
      }
    }
    ```
1. Click 'Publish'


## Step 4: Coding (Finally!); Send Chat Messages

The app currently has a text field to enter a chat message, but that message
doesn't go anywhere. Lets cause any messages entered here to be put into the
database.

But before we can do that, we need our app to connect to Firebase:

1. In Android Studio, click View -> Tool Windows -> TODO. (This will bring up a
   list of locations in the code marked with 'TODO'.)
1. Find TODO #1, and connect to Firebase. We'll store all messages within the
   'messages' node, so store a reference to that. (Name it `messagesRef` for
   consistency with the rest of the workshop.)
1. Hints:
   1. Open up the Assistant window again to see some sample code for connecting
      (and writing data) to the database.

Now store entered messages in Firebase:

1. Find TODO #2, and store the chat message in the database. For each message,
   assign it a random id and store it under the 'messages' node.
1. Hints:
   1. Call `push()` on a `DatabaseReference` to generate a random id
   1. `chatMessageEntry.getText()` doesn't return a `string`; it returns an
      `Editable`. Firebase doesn't know what to do with that. Call `toString()`
      on the resulting value.

Note that most Firebase calls are asynchronous. The call to `setValue()` will
return before the value has actually been written to the database. `setValue()`
returns a `Task` which you can use to schedule further actions to be taken after
the task completes (either successfully or unsuccessfully) but we won't cover
that here.

Now build and run your app. Enter a message and click the send button. (Note:
don't type the 'Enter' button on your physical keyboard; we haven't setup a
listener for that.) If everything worked correctly, we should be able to see our
message in the Firebase console.  (https://console.firebase.google.com; select
your app; select 'Database' from the menu on the left; select the 'Data' tab.)


## Step 5: More Coding; Receive Chat Messages

So now we can send messages, but the area for receiving messages is still one
big placeholder. Lets fix that.

1. In Android Studio, open the TODO window again.
1. Find TODO #3. Create a List of messages within the adapter. We'll use a List,
   with the newest messages at the beginning, and the oldest messages at the
   end. (Since we'll only be adding new messages at the beginning, and we'll
   expire old messages at the end, a queue might work nicely, though we do need
   to be able to index messages.)
1. Find TODO #4(a-b). Rather than passing in some placeholder strings, pass the
   `messagesRef` so that our adapter can query Firebase.
1. Find TODO #5. Setup a query such that Firebase retrieves the most recent 100
   messages, and places them in the newly create messages List.
1. Hints:
   1. Register a ChildEventListener on the messages node reference. We only care
      about the onChildAdded event, so you can leave the other required methods
      as noops. (For a real app, you'd want to add something reasonable for
      these other methods.)
   1. Don't forget to call `notifyDataSetChanged()` to let the
      `RecyclerView.Adapter` know that changes have been made.


## Step 6: Restrict access

We don't authenticate users, and maybe that's ok. But we shouldn't allow users
to delete the entire chat history or edit previous messages, so lets add a few
restrictions to the database. (Even if we don't add delete functionality to our
app, a malicious user could extract the API keys and write something custom that
executes against our database.)


1. Goto the Firebase console, select 'Database' from the menu, and select the
   'Rules' tab. (Currently, the rules are set to public.)
1. Click the 'Simulator' button.
1. Click the 'Write' radio button.
1. In the Location text box, fill in '/messages/some-msg-id'
1. In the Data(JSON) text box, just enter 'null'. (In the Firebase database,
   writing 'null' to a location causes that location to be deleted.)
1. Click Run.

Since we haven't defined any restrictions yet, the simulation should allow this.

Change the rules as follows:
```javascript
{
  "rules": {
    ".read": true,

    "messages": {
      "$id": {
        ".write": "!data.exists() && newData.exists()"
      }
    }
  }
}
```

Click 'Run' in the simulator again. The simulator should now reject the request.

Don't forget to click 'Publish' to save your rule changes.


## Extra Credit

There's a number of obvious extensions to this app that might be interesting to
investigate. Here's a few ideas:

### Throw out our RecyclerView Adapter.

Was implementing the RecyclerView adapter too much work? (Keep in mind, we
didn't implement a full production-ready version.) Use this instead:

https://github.com/firebase/FirebaseUI-Android/blob/master/database/README.md

### Add Authentication

We'd expect most chat apps to authenticate users. Firebase supports
authenticating users via various providers, such as simple email/password, a
Google account, or even simply anonymous. (Anonymous users can later be
"upgraded" to one of the other providers. This allows the user to start using
the app immediately without having to worry about authentication and then later
upgrade.) Login to the Firebase console and click the 'Authentication' menu item
on the left to get started.

Firebase handles all the backend logic for you. But what about the frontend UI
code? You can do it all yourself, or just use this:
https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md

### Add Notifications

Chat apps often notify the user when a message has been received. Check out the
'Notifications' item within the Firebase console to get started. But avoid
notification spam.

### Not quite ready to go server-less?

Firebase often allows you to eliminate your server altogether. But what if
there's just that little something extra that requires you to run some custom
script on a server somewhere. Should you spin up your own server?  Probably not.

Instead, check out Firebase Functions. (Once again, click the 'Functions' item
within the Firebase console.) This allows you to run javascript functions in the
cloud that will run either based off of an http trigger, or (more likely in this
case) a Realtime Database change. For instance, you could run a sanitization
function over each chat message.

For our app, if we keep the 100 message restriction, then another possible use
of a function would be to eliminate all messages that aren't within the latest
100. (If this is done, then you could additionally eliminate the limit
restriction within the app, though you would have to implement the
onChildRemoved callback.)

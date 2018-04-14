# Yet Another Chat App (JavaScript - Firestore)

This directory contains example code for a workshop that introduces the
Firebase Firestore Database. For simplicity's sake and to minimize
dependencies, this is done in raw html without using any web framework.

## Workshop Prereqs

1. If you don't already have a Google account (such as a GMail account), create
   one: https://accounts.google.com/SignUp. This will be used to create and
   manage the Firebase Project.
1. Checkout this project, and open the start html file in your web browser
    * `git clone https://github.com/google/YetAnotherChatApp`
    * In your web browser, open web/start/index.html from within this
      repository.


## Step 1: Setup the Firebase Project and enable Firestore

1. Go to https://console.firebase.google.com and create a project
1. On the resulting 'Get started here' page, click 'Add Firebase to your web
   app'
1. That should being up a code snippet that looks like this:
   ```javascript
   var config = {
     apiKey: "...",
     authDomain: "...",
     databaseURL: "...",
     projectId: "...",
     storageBucket: "...",
     messagingSenderId: "..."
   };
   ```
   Copy + paste the config portion of that code into index.html near `TODO #1`
   (replacing the placeholder code)

Now we need to enable Firestore:

1. On the Firebase console, click 'Database' (on the right, under the 'Develop' section.)
1. Under 'Cloud Firestore Beta', click 'Get Started'.
1. Choose 'Start in test mode' and click 'Enable'.


## Step 2: Connect to the Firestore database, and retrieve a reference to the messages.

The chat messages collection doesn't exist yet, but that's ok!

1. Find `TODO #2` in `index.html` and add code to connect to Firestore.
1. Find `TODO #3` and add code to retrieve a reference to the 'messages'
   collection.

Hints:
* https://firebase.google.com/docs/reference/js/firebase.firestore.Firestore
* https://firebase.google.com/docs/reference/js/firebase.firestore.Firestore#collection


## Step 3: Send messages to Firestore.

1. Find `TODO #4` and remove the call to `appendMessageToChatArea()`.
1. Add code to create a message within Firestore.
1. Now refresh your web browser tab and enter a chat message. It should no
   longer show up on the web page, but should now be visible within the
   firebase console.

Note that most Firebase calls are asynchronous. The call to `add()` will return
before the value has actually been written to the database. `add()` returns a
`Promise` which you can use to schedule further actions to be taken after the
promise completes (either successfully or unsuccessfully) but we won't cover
that here.

Hints:
* https://firebase.google.com/docs/reference/js/firebase.firestore.CollectionReference#add

### Troubleshooting:

CORS may prevent the requests to firebase from working. Check your web browser's console for an error like this:

```
The 'Access-Control-Allow-Origin' header has a value 'null' that is not equal to the supplied origin. Origin 'null' is therefore not allowed access.
```

If you see that, you can try one of these workarounds:
* If you're using Google Chrome, try passing `--allow-file-access-from-files` when starting Chrome.
* Serve the file from a web server.
  * python 2.x:
    ```shell
    $ python -m SimpleHTTPServer 8000
    ```
  * python 3.x:
    ```shell
    $ python -m http.server
    ```
  * npm (https://firebase.google.com/docs/web/setup):
    ```
    $ npm install -g firebase-tools
    $ firebase init    # Generate a firebase.json (REQUIRED)
    $ firebase serve   # Start development server
    ```


## Step 4: Receive messages from Firestore

1. Find `TODO #5` and add code to do the following:
   1. Order the `messagesCollection` by the `timestamp` field, and then:
   1. Register a snapshot listener on the resulting query.
   1. The listener should iterate through each `docChange` and
   1. For each `added` document, it should append the message to the chat area.

Notes:
* We add the listener during the `window.onload` function. It'll stick around
  indefinitely, and the `onSnapshot` callback will trigger every time an entry
  is added to the database.
* We only handle the `added` change type. Depending on your use case, you might
  also need to handle `modified` and `removed` change types.

Hints:
* https://firebase.google.com/docs/reference/js/firebase.firestore.CollectionReference#onSnapshot
* https://firebase.google.com/docs/reference/js/firebase.firestore.QuerySnapshot#docChanges
* https://firebase.google.com/docs/reference/js/firebase.firestore.DocumentChange#type
* https://firebase.google.com/docs/reference/js/firebase.firestore.DocumentChange#doc
* https://firebase.google.com/docs/reference/js/firebase.firestore.QueryDocumentSnapshot#data


## Extra Credit

There's a number of obvious extensions to this app that might be interesting to
investigate. Here's a few ideas:

### Restrict Deletes

We don't authenticate users, and maybe that's ok. But we shouldn't allow users
to delete the entire chat history or edit previous messages, so we could add a
few restrictions to the database. (Even if we don't add delete functionality to
our app, a malicious user could extract the API keys and write something custom
that executes against our database.)

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

https://github.com/firebase/firebaseui-web

### Not quite ready to go server-less?

Firebase often allows you to eliminate your server altogether. But what if
there's just that little something extra that requires you to run some custom
script on a server somewhere. Should you spin up your own server?  Probably not.

Instead, check out Firebase Functions. (Once again, click the 'Functions' item
within the Firebase console.) This allows you to run javascript functions in the
cloud that will run either based off of an http trigger, or (more likely in this
case) a Firestore Database change. For instance, you could run a sanitization
function over each chat message.

For our app, we might use a function to delete all messages older than a
certain time, or maybe use a function to ensure we only keep the most recent
100 messages.

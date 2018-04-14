/* Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.yetanotherchatapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO #1: Retrieve and store a reference to the Firebase Database. We'll store/retrieve
        // messages via a "messages" node. (This node does not need to exist prior to using it;
        // Firebase will create it automatically for us in this case.)
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        final DatabaseReference messagesRef = db.getReference("messages");

        final EditText chatMessageEntry = (EditText)findViewById(R.id.chat_message_entry);
        chatMessageEntry.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEND) {
                        // TODO #2: Rather than "sending" the message via a Toast, we want to write
                        // it to the RTDB here instead. (No need to add it to the messagesList;
                        // we'll setup Firebase to do that automatically for us.)
                        DatabaseReference newMessageRef = messagesRef.push();
                        newMessageRef.setValue(chatMessageEntry.getText().toString());
                        chatMessageEntry.setText("");
                        return true;
                    }
                    return false;
                }
            });

        RecyclerView messagesList = (RecyclerView)findViewById(R.id.messages_view);
        messagesList.setLayoutManager(new LinearLayoutManager(this));

        // TODO #4a: Remove the placeholder strings.

        // TODO #4b: Pass in a DatabaseReference pointing to the messages node rather than a list of
        // placeholder strings.
        messagesList.setAdapter(new ChatViewAdapter(messagesRef));
    }

    private static class ChatViewAdapter extends RecyclerView.Adapter<ChatViewAdapter.ViewHolder>
    {
        // TODO #3: Rather than a list of placeholder strings, use a List of messages. (So really,
        // just rename placeholderStrings to messages and initialize it to an empty list.)
        private final List<String> messages = new ArrayList<>();

        // TODO #4b: Pass in a DatabaseReference pointing to the messages node rather than a list of
        // placeholder strings.
        ChatViewAdapter(DatabaseReference messagesRef) {
            // TODO #5: Load up the initial collection of messages from the database. You'll
            // probably want to limit this to something reasonable; perhaps 100.
            messagesRef.limitToLast(100).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    messages.add(0, dataSnapshot.getValue(String.class));
                    while (messages.size() > 100)
                    {
                        messages.remove(messages.size()-1);
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        /**
         * Create new views, as necessary.
         *
         * The layout manager calls this function whenever it needs a new view.
         * These views will be reused. No need to set the content; the layout
         * manager will do that by calling onBindViewHolder().
         */
        @Override
        public ChatViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewGroup layout = (ViewGroup) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
            return new ChatViewAdapter.ViewHolder(layout);
        }

        /**
         * Replace the contents of a view.
         *
         * The layout manager calls this function whenever it wants to display
         * a view with (potentially) different contents.
         */
        @Override
        public void onBindViewHolder(ChatViewAdapter.ViewHolder viewHolder, int position) {
            viewHolder.setTextContents(messages.get(position));
        }

        /**
         * Calculate the size of the dataset.
         *
         * The layout manager calls this function whenever it needs to
         * determine how many items are in the dataset.
         */
        @Override
        public int getItemCount() {
            return messages.size();
        }

        /**
         * Represents a view of each data item in our list.
         */
        static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView listItemTextView;

            ViewHolder(ViewGroup layout) {
                super(layout);
                listItemTextView = layout.findViewById(R.id.list_item_text_view);
            }

            void setTextContents(String text) {
                listItemTextView.setText(text);
            }
        }
    }
}

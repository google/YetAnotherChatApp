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
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText chatMessageEntry = (EditText)findViewById(R.id.chat_message_entry);
        chatMessageEntry.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEND) {
                        Toast.makeText(
                                chatMessageEntry.getContext(),
                                "Message is: " + chatMessageEntry.getText(),
                                Toast.LENGTH_SHORT).show();
                        chatMessageEntry.setText("");
                        return true;
                    }
                    return false;
                }
            });

        RecyclerView messagesList = (RecyclerView)findViewById(R.id.messages_view);
        messagesList.setLayoutManager(new LinearLayoutManager(this));

        List<String> placeholderStrings = new ArrayList<>();
        for (int i=0; i<1000; i++) {
            placeholderStrings.add("item" + i);
        }
        messagesList.setAdapter(new ChatViewAdapter(placeholderStrings));
    }

    private static class ChatViewAdapter extends RecyclerView.Adapter<ChatViewAdapter.ViewHolder>
    {
        private final List<String> placeholderStrings;

        ChatViewAdapter(List<String> placeholderStrings) {
            this.placeholderStrings = placeholderStrings;
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
            viewHolder.setTextContents(placeholderStrings.get(position));
        }

        /**
         * Calculate the size of the dataset.
         *
         * The layout manager calls this function whenever it needs to
         * determine how many items are in the dataset.
         */
        @Override
        public int getItemCount() {
            return placeholderStrings.size();
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

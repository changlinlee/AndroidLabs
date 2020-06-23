package com.example.androidlabs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity {

    private static final String TAG = "ChatRoomActivity";

    private List<ChatRow> list = new ArrayList<>();
    private ListView chatList;
    private Button send, receive;
    private EditText message;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        chatList = (ListView) findViewById(R.id.chat_list);
        send = (Button) findViewById(R.id.btn_send);
        receive = (Button) findViewById(R.id.btn_receive);
        message =(EditText) findViewById(R.id.message);

        chatAdapter = new ChatAdapter(ChatRoomActivity.this, R.layout.chat_row_send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatAdapter.add(new ChatRow(message.getText().toString(), true));
                message.setText("");
            }
        });

        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatAdapter.add(new ChatRow(message.getText().toString(), false));
                message.setText("");
            }
        });

        chatList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chatList.setAdapter(chatAdapter);
    }

    class ChatAdapter extends ArrayAdapter<ChatRow> {

        private TextView chatText;
        private Activity context;
        private List<ChatRow> list = new ArrayList<>();

        public ChatAdapter(Activity context, int resourceId) {
            super(context, resourceId);
            this.context = context;
        }

        @Override
        public void add(@Nullable ChatRow object) {
            list.add(object);
            super.add(object);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ChatRow chatRow = getItem(position);
            View view = convertView;
            LayoutInflater inflater = context.getLayoutInflater();
            if (chatRow.sendOrRec) {
                view = inflater.inflate(R.layout.chat_row_send, parent, false);
                chatText = (TextView) view.findViewById(R.id.message_send);
                chatText.setText(chatRow.message);
            } else {
                view = inflater.inflate(R.layout.chat_row_receive, parent, false);
                chatText = (TextView) view.findViewById(R.id.message_receive);
                chatText.setText(chatRow.message);
            }
            return view;
        }

        @Nullable
        @Override
        public ChatRow getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

    }

    class ChatRow {
        public String message;
        public boolean sendOrRec;

        public ChatRow(String message, boolean sendOrRec) {
            this.message = message;
            this.sendOrRec = sendOrRec;
        }
    }
}
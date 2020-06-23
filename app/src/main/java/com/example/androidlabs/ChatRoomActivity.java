package com.example.androidlabs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity {

    private static final String TAG = "ChatRoomActivity";

    private List<ChatMessage> list = new ArrayList<>();
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
        message = (EditText) findViewById(R.id.message);

        chatAdapter = new ChatAdapter(ChatRoomActivity.this, R.layout.chat_row_send, list);

        send.setOnClickListener(click -> {
            list.add(new ChatMessage(message.getText().toString(), true));
            chatAdapter.notifyDataSetChanged();
            message.setText("");
        });

        receive.setOnClickListener(click -> {
            list.add(new ChatMessage(message.getText().toString(), false));
            chatAdapter.notifyDataSetChanged();
            message.setText("");
        });

        chatList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chatList.setAdapter(chatAdapter);

        chatList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoomActivity.this);
                builder.setTitle("Do you want to delete this?")
                        .setMessage("The selected row is: " + (position + 1) + "\nThe database id id:" + id)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                list.remove(position);
                                chatAdapter.notifyDataSetChanged();
                                Toast.makeText(ChatRoomActivity.this, "Succeed", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(ChatRoomActivity.this, "Canceled", Toast.LENGTH_LONG).show();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
        });
    }

    class ChatAdapter extends ArrayAdapter<ChatMessage> {

        private TextView chatText;
        private Activity context;
        private List<ChatMessage> list;

        public ChatAdapter(Activity context, int resourceId, List<ChatMessage> list) {
            super(context, resourceId, list);
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ChatMessage chatMessage = getItem(position);
            View view;
            LayoutInflater inflater = context.getLayoutInflater();
//            LayoutInflater inflater = LayoutInflater.from(getContext());
            if (chatMessage.sendOrRec) {
                // ensure which layout
                view = inflater.inflate(R.layout.chat_row_send, parent, false);
                // notice the widget should in view
                chatText = (TextView) view.findViewById(R.id.message_send);
                chatText.setText(chatMessage.message);
            } else {
                view = inflater.inflate(R.layout.chat_row_receive, parent, false);
                chatText = (TextView) view.findViewById(R.id.message_receive);
                chatText.setText(chatMessage.message);
            }
            return view;
        }

        @Nullable
        @Override
        public ChatMessage getItem(int position) {
            return this.list.get(position);
        }

        @Override
        public int getCount() {
            return this.list.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    class ChatMessage {
        public String message;
        public boolean sendOrRec;

        public ChatMessage(String message, boolean sendOrRec) {
            this.message = message;
            this.sendOrRec = sendOrRec;
        }
    }
}
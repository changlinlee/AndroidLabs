package com.example.androidlabs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity {

    private static final String TAG = "ChatRoomActivity";
    public static final String DATABASE_ID = "database_id";
    public static final String MESSAGE_CONTENT = "message_content";
    public static final String MESSAGE_IS_SEND = "message_isSend";

    private List<ChatMessage> list = new ArrayList<>();
    private ListView chatList;
    private Button sendBtn, receiveBtn;
    private EditText message;
    private ChatAdapter chatAdapter;
    private FrameLayout mFrameLayout;
    private boolean send = true;
    private boolean receive = false;
    private boolean isTablet = false;
    private MessageDataSource dataSource;
    private DetailsFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        fragment = new DetailsFragment();

        chatList = (ListView) findViewById(R.id.chat_list);
        sendBtn = (Button) findViewById(R.id.btn_send);
        receiveBtn = (Button) findViewById(R.id.btn_receive);
        message = (EditText) findViewById(R.id.message);
        mFrameLayout = (FrameLayout) findViewById(R.id.fragment_position);
        isTablet = mFrameLayout != null;

        dataSource = new MessageDataSource(ChatRoomActivity.this);
        dataSource.open();
        list = dataSource.getAllMessage();

        chatAdapter = new ChatAdapter(ChatRoomActivity.this, R.layout.chat_row_send, list);

        sendBtn.setOnClickListener(click -> {
            list.add(dataSource.createChatMessage(message.getText().toString(), send));
            chatAdapter.notifyDataSetChanged();
            message.setText("");
        });

        receiveBtn.setOnClickListener(click -> {
            list.add(dataSource.createChatMessage(message.getText().toString(), receive));
            chatAdapter.notifyDataSetChanged();
            message.setText("");
        });

        chatList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chatList.setAdapter(chatAdapter);

        chatList.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoomActivity.this);
            builder.setTitle("Do you want to delete this?")
                    .setMessage("The selected row is: " + (position + 1) + "\nThe database id id:" + list.get(position).id)
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dataSource.deleteChatMessage(list.get(position));
                        list.remove(position);
                        chatAdapter.notifyDataSetChanged();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .remove(fragment)
                                .addToBackStack(null)
                                .commit();
                        Toast.makeText(ChatRoomActivity.this, "Succeed", Toast.LENGTH_LONG).show();
                    })
                    .setNegativeButton("No", (dialog, which) ->
                            Toast.makeText(ChatRoomActivity.this, "Canceled", Toast.LENGTH_LONG).show());
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        });

        chatList.setOnItemClickListener((parent, view, position, id) -> {
            Bundle bundle = new Bundle();
            bundle.putString(DATABASE_ID, String.valueOf(list.get(position).id));
            bundle.putString(MESSAGE_CONTENT, list.get(position).message);
            bundle.putBoolean(MESSAGE_IS_SEND, list.get(position).sendOrRec);

            if (isTablet) {

                fragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_position, fragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                Intent intent = new Intent(ChatRoomActivity.this, EmptyActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        dataSource.close();
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
            return list.get(position).id;
        }
    }

    class ChatMessage {
        long id;
        String message;
        boolean sendOrRec;

        public ChatMessage() {
        }

        public void setId(long id) {
            this.id = id;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void setSendOrRec(boolean sendOrRec) {
            this.sendOrRec = sendOrRec;
        }
    }

    class ChatSQLiteHelper extends SQLiteOpenHelper {
        static final String TABLE_NAME = "message";
        static final String ID = "id";
        static final String CONTENT = "content";
        static final String TYPE = "type";
        static final String DATABASE_NAME = "message.db";
        static final int DATABASE_VERSION = 1;
        static final String DATABASE_CREATE = "CREATE TABLE "
                + TABLE_NAME + "( " + ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CONTENT + " TEXT, " + TYPE + " INTEGER NOT NULL);";

        public ChatSQLiteHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    class MessageDataSource {
        SQLiteDatabase database;
        ChatSQLiteHelper dbHelper;
        String[] allColumns = { ChatSQLiteHelper.ID, ChatSQLiteHelper.CONTENT, ChatSQLiteHelper.TYPE};

        public MessageDataSource(Context context) {
            dbHelper = new ChatSQLiteHelper(context);
        }

        public void open() throws SQLException {
            database = dbHelper.getWritableDatabase();
        }

        public void close() {
            dbHelper.close();
        }

        public ChatMessage createChatMessage(String message, boolean type) {
            ContentValues values = new ContentValues();
            values.put(ChatSQLiteHelper.CONTENT, message);
            values.put(ChatSQLiteHelper.TYPE, type);
            long insertId = database.insert(ChatSQLiteHelper.TABLE_NAME, null, values);
            Cursor cursor = database.query(ChatSQLiteHelper.TABLE_NAME, allColumns,
                    ChatSQLiteHelper.ID + "=" + insertId,
                    null, null, null, null);
            cursor.moveToFirst();
            printCursor(cursor, database.getVersion());
            ChatMessage newMessage = cursorToChatMessage(cursor);
            cursor.close();
            return newMessage;
        }

        private ChatMessage cursorToChatMessage(Cursor cursor) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setId(cursor.getLong(0));
            chatMessage.setMessage(cursor.getString(1));
            chatMessage.setSendOrRec(cursor.getString(2).equals("1"));
            return chatMessage;
        }

        public void deleteChatMessage(ChatMessage chatMessage) {
            long id = chatMessage.id;
            database.delete(ChatSQLiteHelper.TABLE_NAME, ChatSQLiteHelper.ID + "=" + id, null);
        }

        public List<ChatMessage> getAllMessage() {
            List<ChatMessage> messages = new ArrayList<>();
            Cursor cursor = database.query(ChatSQLiteHelper.TABLE_NAME, allColumns,
                    null, null, null, null, null);
            cursor.moveToFirst();
            printCursor(cursor, database.getVersion());
            while (!cursor.isAfterLast()) {
                ChatMessage chatMessage = cursorToChatMessage(cursor);
                messages.add(chatMessage);
                cursor.moveToNext();
            }
            cursor.close();
            return messages;
        }

        public void printCursor(Cursor cursor, int version) {
            Log.e(TAG, "version: " + version);
            Log.e(TAG, "The number of columns: " + cursor.getColumnCount());
            Log.e(TAG, "The name of the columns: " + Arrays.toString(cursor.getColumnNames()));
            Log.e(TAG, "The number of rows: " + cursor.getCount());
            while (!cursor.isAfterLast()) {
                String result = cursor.getString(0) + ", "
                        + cursor.getString(1) + ", "
                        + cursor.getString(2);
                Log.e(TAG, "row content: " + result);
                cursor.moveToNext();
            }
            cursor.moveToFirst();
        }
    }
}
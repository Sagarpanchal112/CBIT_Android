package com.tfb.cbit;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.tfb.cbit.activities.BaseAppCompactActivity;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivitySocketBinding;
import com.tfb.cbit.utility.CryptLib;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.tfb.cbit.utility.Utils.SOCKET_URI;

public class SocketActivity extends BaseAppCompactActivity {

    //ws://18.218.35.100:5000/socket.io/?EIO=4&transport=websocket
    //private static final String SOCKET_URI = "http://18.218.35.100:5000";
    //private static final String SOCKET_URI = "http://192.168.0.106:3500"; //live
    //private static final String SOCKET_URI = "http://192.168.0.189:3500"; //local
    // private static final String SOCKET_URI = "http://192.168.0.106:3600"; //local
    private static final String SOCKET_PATH = "/socket.io";
    private static final String[] TRANSPORTS = {
            "websocket"};
    private static Socket mSocket;
    private CryptLib cryptLib = null;

    static {
        try {
            //mSocket = IO.socket("https://socket-io-chat.now.sh/");
            IO.Options options = new IO.Options();
            options.path = SOCKET_PATH;
            options.transports = TRANSPORTS;
            mSocket = IO.socket(SOCKET_URI, options);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private ActivitySocketBinding binding;

    // private Boolean isConnected = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySocketBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        try {
            cryptLib = new CryptLib();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("onCherry", onCherry);
       /* mSocket.on("new message", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("TAG",args[0].toString());
            }
        });*/
        mSocket.connect();
        binding.btnSend.setOnClickListener(view -> {
            btnSendClick();
        });

    }

    protected void btnSendClick() {
        if (!binding.edtMsg.getText().toString().trim().isEmpty()) {
            String encryptData = "";
            try {
                encryptData = cryptLib.encryptPlainTextWithRandomIV(binding.edtMsg.getText().toString().trim(), getString(R.string.crypt_pass));
            } catch (Exception e) {
                e.printStackTrace();
            }
            mSocket.emit("cherry", encryptData);
            binding.edtMsg.setText("");
        }
    }

    private Emitter.Listener onCherry = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("TAG", args[0].toString() + " ");
                    try {
                        JSONObject jsonObject = new JSONObject(args[0].toString());
                        String value = jsonObject.getString("data");
                        String decrypt = "";
                        try {
                            decrypt = cryptLib.decryptCipherTextWithRandomIV(value, getString(R.string.crypt_pass));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.d("TAG", decrypt);
                        Toast.makeText(getApplicationContext(), decrypt, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //if(!mSocket.connected()) {
                    Toast.makeText(getApplicationContext(),
                            "Connected", Toast.LENGTH_LONG).show();
                    Log.d("TAG", "Connected");
                    // isConnected = true;
                    // }
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("TAG", "disconnected");
                    //isConnected = false;
                    Toast.makeText(getApplicationContext(),
                            "Disconnected", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("TAG", "Error connecting " + args[0].toString());
                    Toast.makeText(getApplicationContext(),
                            "Error connecting", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        }
    };


    public void loginEmit(String id) {
        try {
            JSONObject object = new JSONObject();
            object.put("user_id", id);
            mSocket.emit("login", object, new Ack() {
                @Override
                public void call(Object... args) {
                    final String response = args[0].toString();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            //System.out.println("onLogin ");
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                //EventBus.getDefault().post(new UnAuthorizedEvent(jsonObject.getString("message")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        super.onDestroy();
        //mSocket.off("new message", onNewMessage);
    }
}

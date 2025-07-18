package com.example.haminavodayaho;

import static com.example.haminavodayaho.Login.USER;
import static com.example.haminavodayaho.MainActivity.IP;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

import java.io.File;
import java.net.URI;
import java.util.Collections;
import java.util.Locale;

public class AudioCall extends AppCompatActivity {
    private final String url = "ws://"+IP+"/ws/call/"+USER;
    private static final String TAG = "AudioCall";
    private PeerConnectionFactory factory;
    private PeerConnection peerConnection;
    private AudioTrack localAudioTrack;
    private WebSocketClient webSocketClient;
    boolean isCaller;
    private int seconds = 0;
    private boolean running = false;
    private final Handler handler = new Handler();
    String contact;
    ImageView callProfile, call_end;
    TextView callUserName, callStatus;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_audio_call);

        callProfile = findViewById(R.id.callProfile);
        callUserName = findViewById(R.id.callUserName);
        callStatus = findViewById(R.id.callStatus);
        call_end = findViewById(R.id.call_end);

        PeerConnectionFactory.initialize(
                PeerConnectionFactory.InitializationOptions.builder(this).createInitializationOptions()
        );

        Intent intent = getIntent();
        String name = intent.getStringExtra("username");
        String profileUrl = intent.getStringExtra("profile");
        contact = intent.getStringExtra("contact");
        isCaller = getIntent().getBooleanExtra("isCaller", true);

        if (!TextUtils.isEmpty(profileUrl) || profileUrl != null) {
            if (profileUrl.startsWith("/")) {
                File imageFile = new File(profileUrl);
                Glide.with(getApplicationContext())
                        .load(imageFile)
                        .into(callProfile);
            } else {
                Glide.with(getApplicationContext())
                        .load(profileUrl)
                        .into(callProfile);
            }
        }
        assert name != null;
        if (!name.isEmpty()){
            callUserName.setText(name);
        } else {
            callUserName.setText(contact);
        }

        call_end.setOnClickListener(v -> {
            if (peerConnection != null) {
                peerConnection.close();
            }
            if (webSocketClient != null) {
                webSocketClient.close();
            }
            running = false;
            callStatus.setText("Call Ended");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AudioCall.super.onBackPressed();
                    finish();
                }
            },1000);
        });

//        runOnUiThread(() -> {
//            callStatus.setText("Connected - 00:00");
//            startTimer();
//        });
        startWebSocket();
        initializeWebRTC();
    }
    private void initializeWebRTC(){
        factory = PeerConnectionFactory.builder().createPeerConnectionFactory();

        AudioSource audioSource = factory.createAudioSource(new MediaConstraints());
        localAudioTrack = factory.createAudioTrack("101", audioSource);

        PeerConnection.IceServer stunServer = PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer();

        peerConnection = factory.createPeerConnection(
                Collections.singletonList(stunServer),
                new PeerConnection.Observer() {
                    @Override
                    public void onSignalingChange(PeerConnection.SignalingState signalingState) {
                    }
                    @Override
                    public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
                    }
                    @Override
                    public void onIceConnectionReceivingChange(boolean b) {
                    }
                    @Override
                    public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
                    }
                    @Override
                    public void onIceCandidate(IceCandidate iceCandidate) {
                        try{
                            JsonObject json = new JsonObject();
                            json.addProperty("type", "ice");
                            json.addProperty("sdpMid", iceCandidate.sdpMid);
                            json.addProperty("sdpMLineIndex", iceCandidate.sdpMLineIndex);
                            json.addProperty("sdpCandidate", iceCandidate.sdp);
                            if (webSocketClient != null) {
                                webSocketClient.send(json.toString());
                            }
                        } catch (Exception e){
                            Log.e("yogi", "ICE Error: ", e);                        }
                    }
                    @Override
                    public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
                    }
                    @Override
                    public void onAddStream(MediaStream mediaStream) {
                        Log.d("yogi", "onAddStream: Remote Stream added.");
                    }
                    @Override
                    public void onRemoveStream(MediaStream mediaStream) {
                    }
                    @Override
                    public void onDataChannel(DataChannel dataChannel) {
                    }
                    @Override
                    public void onRenegotiationNeeded() {
                    }
                }
        );
        MediaStream stream = factory.createLocalMediaStream("mediaStream");
        stream.addTrack(localAudioTrack);
        //peerConnection.addStream(stream);
        peerConnection.addTrack(localAudioTrack);
    }
    private void startWebSocket(){
        URI uri;
        try{
            uri = new URI(url+"/"+contact);
        } catch (Exception e){
            Log.e(TAG, "URI Error: ", e);
            return;
        }
        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d(TAG, "WebSocket opened.");

                if (isCaller) {
                    MediaConstraints constraints = new MediaConstraints();
                    peerConnection.createOffer(new SdpObserver() {
                        @Override
                        public void onCreateSuccess(SessionDescription sessionDescription) {
                            try {
                                peerConnection.setLocalDescription(new SdpObserverAdapter(), sessionDescription);
                                JsonObject json = new JsonObject();
                                json.addProperty("type", "offer");
                                json.addProperty("sdp", sessionDescription.description);
                                webSocketClient.send(json.toString());
                            } catch (Exception e) {
                                Log.e(TAG, "Offer Error: ", e);
                            }
                        }
                        @Override
                        public void onSetSuccess() {}
                        @Override
                        public void onCreateFailure(String s) {}
                        @Override
                        public void onSetFailure(String s) {}
                    }, constraints);
                }
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void onMessage(String message) {
                try{
                    JsonObject json = new JsonParser().parse(message).getAsJsonObject();
                    String type = json.get("type").getAsString();

                    if (type.equals("offer")) {
                        SessionDescription offer = new SessionDescription(SessionDescription.Type.OFFER, json.get("sdp").getAsString());
                        peerConnection.setRemoteDescription(new SdpObserverAdapter(), offer);
                        peerConnection.createAnswer(new SdpObserver() {
                            @Override
                            public void onCreateSuccess(SessionDescription sessionDescription) {
                                peerConnection.setLocalDescription(new SdpObserverAdapter(), sessionDescription);
                                try {
                                    JsonObject json = new JsonObject();
                                    json.addProperty("type", "answer");
                                    json.addProperty("sdp", sessionDescription.description);
                                    webSocketClient.send(json.toString());
                                } catch (Exception e) {
                                    Log.e(TAG, "Answer Error: ", e);
                                }
                            }
                            @Override
                            public void onSetSuccess() {
                            }
                            @Override
                            public void onCreateFailure(String s) {
                            }
                            @Override
                            public void onSetFailure(String s) {
                            }
                        }, new MediaConstraints());
                    }
                    else if (type.equals("accept")) {
                        runOnUiThread(() -> {
                            callStatus.setText("Call Accepted - Connected");
                            startTimer();
                            SessionDescription answer = new SessionDescription(SessionDescription.Type.ANSWER, json.get("sdp").getAsString());
                            peerConnection.setRemoteDescription(new SdpObserverAdapter(), answer);
                        });
                    }
                    else if (type.equals("reject")) {
                        runOnUiThread(() -> {
                            callStatus.setText("Call Rejected");
                            new Handler().postDelayed(AudioCall.this::finish, 1000);
                        });
                    }
                    else if (type.equals("answer")) {
                        SessionDescription answer = new SessionDescription(SessionDescription.Type.ANSWER, json.get("sdp").getAsString());
                        peerConnection.setRemoteDescription(new SdpObserverAdapter(), answer);
                    }
                    else if (type.equals("ice")) {
                        IceCandidate candidate = new IceCandidate(
                                json.get("sdpMid").getAsString(),
                                json.get("sdpMLineIndex").getAsInt(),
                                json.get("sdpCandidate").getAsString());
                        if (peerConnection == null) return;
                        peerConnection.addIceCandidate(candidate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Message Error: ", e);
                }
            }
            @Override
            public void onClose(int code, String reason, boolean remote) {
            }
            @Override
            public void onError(Exception ex) {
                Log.e(TAG, "WebSocket Error: ", ex);
            }
        };
        webSocketClient.connect();
    }
    private static class SdpObserverAdapter implements SdpObserver {
        @Override public void onCreateSuccess(SessionDescription sessionDescription) {}
        @Override public void onSetSuccess() {}
        @Override public void onCreateFailure(String s) {}
        @Override public void onSetFailure(String s) {}
    }

    private void startTimer() {
        running = true;
        handler.post(new Runnable() {
            @Override
            public void run() {
                int minutes = seconds / 60;
                int secs = seconds % 60;
                String time = String.format(Locale.getDefault(), "Connected - %02d:%02d", minutes, secs);
                runOnUiThread(() -> callStatus.setText(time));
                if (running) {
                    seconds++;
                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.close();
        }
        if (peerConnection != null) {
            peerConnection.close();
        }
        if (localAudioTrack != null) {
            localAudioTrack.dispose();
        }
    }

}
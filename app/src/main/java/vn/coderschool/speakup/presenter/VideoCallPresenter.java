package vn.coderschool.speakup.presenter;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.video.VideoController;

import java.io.IOException;
import java.util.List;

import vn.coderschool.speakup.model.MatchingResult;
import vn.coderschool.speakup.view.VideoCallView;

public class VideoCallPresenter implements Presenter<VideoCallView> {

    private VideoCallView view;

    private final String SINCH_APP_KEY = "420799df-c501-4c8c-bc3b-4e2b8f8e4946";
    private final String SINCH_APP_SECRET = "dKXg2GvUx0SDLqpdzH6r9Q==";
    private final String SINCH_ENV_HOST = "sandbox.sinch.com";

    private SinchClient sinchClient;
    private CallClient callClient;
    private Call call;
    private String userId;

    public MatchingResult matchingResult;

    public AudioController audioController;
    public VideoController videoController;

    public void muteeee(){
        audioController.disableSpeaker();
    }

    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;

    @Override
    public void attachView(VideoCallView view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    public void configSinchService() {

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        sinchClient = Sinch.getSinchClientBuilder()
                .context(view.getContext())
                .applicationKey(SINCH_APP_KEY)
                .applicationSecret(SINCH_APP_SECRET)
                .environmentHost(SINCH_ENV_HOST)
                .userId(userId)
                .build();
//        sinchClient
        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();

        sinchClient.addSinchClientListener(getSinchClientListener());

        sinchClient.start();

        callClient = sinchClient.getCallClient();
        callClient.addCallClientListener(new CallClientListener() {
            @Override
            public void onIncomingCall(CallClient callClient, Call call) {
                configCall(call);
                view.showConnecting();
                call.answer();
                view.callAccepted();
            }
        });
    }

    public void makeCall(String partnerId) {
        call = callClient.callUserVideo(partnerId);
        configCall(call);
    }

    public void terminateCall() {
        call.hangup();
    }

    public void configCall(Call incommingCall) {
        call = incommingCall;
        call.addCallListener(new com.sinch.android.rtc.video.VideoCallListener() {
            @Override
            public void onVideoTrackAdded(Call call) {
                videoController = sinchClient.getVideoController();
                audioController = sinchClient.getAudioController();

                view.showVideoCall(videoController.getRemoteView(), videoController.getLocalView());
            }

            @Override
            public void onCallProgressing(Call call) {
            }

            @Override
            public void onCallEstablished(Call call) {
            }

            @Override
            public void onCallEnded(Call call) {
                sinchClient.stopListeningOnActiveConnection();
                sinchClient.terminate();

                view.showCallFinished();
            }

            @Override
            public void onShouldSendPushNotification(Call call, List<PushPair> list) {
            }
        });
    }

    public void setMicroEnabled(boolean enabled) {
        if (audioController == null)
            return;

        if (enabled)
            audioController.unmute();
        else audioController.mute();
    }

    public void setVideoEnabled(boolean enabled) {
        if (videoController == null)
            return;

//        if (enabled)
//            videoController.getLocalView();
//        else videoController.getRemoteView();
    }

    public void startRecordAudio(String fileName) {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(fileName);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecord() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    public void startPlaying(String fileName) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(fileName);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopPlaying() {
        mediaPlayer.release();
        mediaPlayer = null;
    }


    @NonNull
    private SinchClientListener getSinchClientListener() {
        return new SinchClientListener() {
            @Override
            public void onClientStarted(SinchClient sinchClient) {
                if (matchingResult.isMakeCall()) {
                    makeCall(matchingResult.getPartner().getId());
                }
            }

            @Override
            public void onClientStopped(SinchClient sinchClient) {
            }

            @Override
            public void onClientFailed(SinchClient sinchClient, SinchError sinchError) {
            }

            @Override
            public void onRegistrationCredentialsRequired(SinchClient sinchClient, ClientRegistration clientRegistration) {
            }

            @Override
            public void onLogMessage(int i, String s, String s1) {
            }
        };
    }
}

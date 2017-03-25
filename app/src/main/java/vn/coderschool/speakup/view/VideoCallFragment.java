package vn.coderschool.speakup.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vn.coderschool.speakup.R;
import vn.coderschool.speakup.model.MatchingResult;
import vn.coderschool.speakup.model.User;
import vn.coderschool.speakup.presenter.VideoCallPresenter;

public class VideoCallFragment extends Fragment implements VideoCallView {

    private VideoCallPresenter presenter;

    private MatchingResult matchingResult;

    private User user;
    private User partner;

    public interface VideoCallListener {
        void onCallFinish();
    }

    private VideoCallListener listener;

    @BindView(R.id.layout_partner_video)
    RelativeLayout partnerVideo;

    @BindView(R.id.layout_user_video)
    RelativeLayout userVideo;

    @BindView(R.id.layout_contacting)
    RelativeLayout contacting;

    public static VideoCallFragment getInstance(VideoCallListener listener, MatchingResult matchingResult) {
        VideoCallFragment fragment = new VideoCallFragment();
        fragment.listener = listener;
        fragment.matchingResult = matchingResult;
        fragment.partner = new User();
        fragment.partner.setId(matchingResult.getPartner());

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_call, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        presenter = new VideoCallPresenter();
        presenter.attachView(this);
        presenter.matchingResult = matchingResult;

        presenter.configSinchService();
    }

    @Override
    public void showConnecting() {
        Toast.makeText(getActivity(), "Someone calling", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showVideoCall(View remoteView, View localView) {
        Toast.makeText(getActivity(), "Video shown", Toast.LENGTH_SHORT).show();
        contacting.setVisibility(View.GONE);
        userVideo.addView(localView);
                partnerVideo.addView(remoteView);
    }

    @Override
    public void showCallFinished() {
        listener.onCallFinish();
    }

    @OnClick(R.id.button_end)
    public void endConversation() {
        presenter.terminateCall();
    }

    @Override
    public void callAccepted() {
        Toast.makeText(getActivity(), "call accepted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToast(String xxxxx) {
        Toast.makeText(getActivity(), xxxxx, Toast.LENGTH_SHORT).show();
    }
}

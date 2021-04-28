package com.kokorobot.speechfreeze;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.kokorobot.speechfreeze.KebbiRobot.KebbiEvent;
import com.kokorobot.speechfreeze.KebbiRobot.KebbiEventListener;
import com.kokorobot.speechfreeze.KebbiRobot.KebbiEventListener.EventType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Fragment_2 extends Fragment
    implements OnClickListener {

    private static final String TAG = "FRAGMENT_1";

    private final List<String> mCommandBack = Arrays.asList(
            "トップ", "トップへ", "トップへ戻る", "トップ画面", "戻る", "もどる", "終わる", "終わり"
    );

    MainActivity mActivity;
    KebbiRobot mRobot;
    KebbiEventListener mListener;

    private static List<EventType> subscribedEvents = new ArrayList<>(
            Arrays.asList(EventType.TTS_EVENT, EventType.STT_EVENT));

    public Fragment_2() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_2, container, false);
        view.findViewById(R.id.menu_button).setOnClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        mActivity = (MainActivity)getActivity();
        mRobot = mActivity.getRobot();
        mRobot.prepareListening(mCommandBack);
        mListener = new KebbiEventListener() {
            @Override
            public void onKebbiEvent(EventType eventType, KebbiEvent event) {
                if (eventType == EventType.TTS_EVENT) {
                    mRobot.startListening();
                } else if (eventType == EventType.STT_EVENT) {
                    String resultType = event.get(0);
                    String result = event.get(1);
                    switch (resultType) {
                        case "RESULT_LOCAL":
                            if (mListener != null) {
                                mRobot.unregisterKebbiEventListener(subscribedEvents, TAG);
                                mListener = null;
                                mActivity.backFragment();
                            }
                            break;
                        case "STT_ERROR":
                            break;
                        case "TIMEOUT":
                        case "NOT_UNDERSTOOD":
                        default:
                            mRobot.startListening();
                    }
                }
            }

            @Override
            public String getId() {
                return TAG;
            }
        };
        mRobot.registerKebbiEventListener(subscribedEvents, mListener);
        mRobot.say("どうしましょうか？");
        super.onResume();
    }

    @Override
    public void onPause() {
        if (mListener != null) {
            mRobot.unregisterKebbiEventListener(subscribedEvents, TAG);
            mListener = null;
        }
        mRobot.stopSay();
        mRobot.stopListening();
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        mActivity.backFragment();
    }


}

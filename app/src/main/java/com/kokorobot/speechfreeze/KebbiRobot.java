package com.kokorobot.speechfreeze;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.kokorobot.speechfreeze.KebbiRobot.KebbiEventListener.EventType;
import com.nuwarobotics.service.IClientId;
import com.nuwarobotics.service.agent.NuwaRobotAPI;
import com.nuwarobotics.service.agent.RobotEventListener;
import com.nuwarobotics.service.agent.SimpleGrammarData;
import com.nuwarobotics.service.agent.VoiceEventListener;
import com.nuwarobotics.service.agent.VoiceResultJsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;

public class KebbiRobot {

    private static final String TAG = "KOKOBOTKEBBIROBOT";

    private NuwaRobotAPI api;
    private boolean apiIsReady = false;
    private HashMap<EventType, List<KebbiEventListener>> mEventListeners;

    public KebbiRobot(Context context, String package_id) {
        IClientId id = new IClientId(package_id);
        api = new NuwaRobotAPI(context, id);
        mEventListeners = new HashMap<>();
        registerRobotCallbacks();
    }

    public void release() {
        api.release();
    }

    private void registerRobotCallbacks() {
        api.registerRobotEventListener(new RobotEventListener() {
            @Override
            public void onWikiServiceStart() {
                apiIsReady = true;
            }

            @Override
            public void onWikiServiceStop() {
                apiIsReady = false;
            }

            @Override
            public void onWikiServiceCrash() {
            }

            @Override
            public void onWikiServiceRecovery() {
            }

            @Override
            public void onStartOfMotionPlay(String s) {

            }

            @Override
            public void onPauseOfMotionPlay(String s) {

            }

            @Override
            public void onStopOfMotionPlay(String s) {

            }

            @Override
            public void onCompleteOfMotionPlay(String s) {

            }

            @Override
            public void onPlayBackOfMotionPlay(String s) {

            }

            @Override
            public void onErrorOfMotionPlay(int i) {

            }

            @Override
            public void onPrepareMotion(boolean b, String s, float v) {

            }

            @Override
            public void onCameraOfMotionPlay(String s) {

            }

            @Override
            public void onGetCameraPose(float v, float v1, float v2, float v3, float v4, float v5, float v6, float v7, float v8, float v9, float v10, float v11) {

            }

            @Override
            public void onTouchEvent(int type, int touch) {

            }

            @Override
            public void onPIREvent(int val) {

            }

            @Override
            public void onTap(int type) {
            }

            @Override
            public void onLongPress(int i) {

            }

            @Override
            public void onWindowSurfaceReady() {

            }

            @Override
            public void onWindowSurfaceDestroy() {

            }

            @Override
            public void onTouchEyes(int i, int i1) {

            }

            @Override
            public void onRawTouch(int i, int i1, int i2) {

            }

            @Override
            public void onFaceSpeaker(float v) {

            }

            @Override
            public void onActionEvent(int i, int i1) {

            }

            @Override
            public void onDropSensorEvent(int i) {

            }

            @Override
            public void onMotorErrorEvent(int i, int i1) {

            }
        });

        api.registerVoiceEventListener(new VoiceEventListener() {
            @Override
            public void onWakeup(boolean b, String s, float v) {

            }

            @Override
            public void onTTSComplete(boolean b) {
                Log.d(TAG, "TTS Complete");
                KebbiEvent event = new KebbiEvent();
                event.add("TTS_FINISHED");
                raiseEvent(EventType.TTS_EVENT, event);
            }

            @Override
            public void onSpeechRecognizeComplete(boolean b, ResultType resultType, String s) {
                Log.d(TAG, "onSpeechRecognizeComplete :" + b + ", " + resultType + ", " + s);
            }

            @Override
            public void onSpeech2TextComplete(boolean b, String s) {
                Log.d(TAG, "onSpeech2TextComplete :" + b + ", " + s);
            }

            @Override
            public void onMixUnderstandComplete(boolean b, ResultType resultType, String s) {
                Log.d(TAG, "onMixUnderstandComplete :" + b + ", " + resultType + ", " + s);
                KebbiEvent event = new KebbiEvent();
                if (b && s.equals("_VOICETIMEOUT_")) {
                    Log.d(TAG, "STT timeout");
                    event.add("TIMEOUT");
                    event.add("");

                }  else if (!b && resultType == ResultType.LOCAL_COMMAND && !s.equals("")) {
                    String result = VoiceResultJsonParser.parseVoiceResult(s);
                    Log.d(TAG, "STT result: " + result);
                    event.add("RESULT_LOCAL");
                    event.add(result);
                }else if (!b && resultType == ResultType.LOCAL_COMMAND || s.contains("result")) {
                    Log.d(TAG, "STT not understood");
                    event.add("NOT_UNDERSTOOD");
                    event.add("");
                } else if (b) {
                    Log.w(TAG, "STT ERROR");
                    event.add("STT_ERROR");
                    event.add("");
                } else {
                    Log.w(TAG, "STT unknown event!");
                    event.add("STT_ERROR");
                    event.add("");
                }
                raiseEvent(EventType.STT_EVENT, event);
            }

            @Override
            public void onSpeechState(ListenType listenType, SpeechState speechState) {
//                Log.d(TAG, "onSpeechState :" + listenType + ", " + speechState);
            }

            @Override
            public void onSpeakState(SpeakType speakType, SpeakState speakState) {

            }

            @Override
            public void onGrammarState(boolean b, String s) {
                Log.d(TAG, "onGrammarState: " + b + ", " + s);
            }

            @Override
            public void onListenVolumeChanged(ListenType listenType, int i) {

            }

            @Override
            public void onHotwordChange(HotwordState hotwordState, HotwordType hotwordType, String s) {

            }
        });
    }

    public void say(String text) {
        if (apiIsReady) {
            api.stopListen();
            api.startTTS(text);
        }
    }

    public void animSay(String text, String motion) {
        if (apiIsReady) {
            api.stopListen();
            api.startTTS(text);
            api.motionPlay(motion, false);
        }
    }

    public void stopSay() {
        if (apiIsReady) {
            api.stopTTS();
        }
    }

    public void prepareListening(List<String> vocab) {
        if (!apiIsReady) {
            return;
        }
        if (vocab.size() == 0) {
            throw new IllegalArgumentException("Need at least one word to recognize");
        }
        SimpleGrammarData grammar = new SimpleGrammarData(TAG);
        grammar.addSlot(vocab.toArray(new String[0]));
        grammar.updateBody();
        api.createGrammar(grammar.grammar, grammar.body);
    }

    public void startListening() {
        api.stopTTS();
        api.startLocalCommand();
    }

    public void stopListening() {
        if (apiIsReady)
            api.stopListen();
    }

    public boolean isReady() {
        return apiIsReady;
    }

    public void registerKebbiEventListener(EventType eventType, KebbiEventListener listener) {
        if (mEventListeners.containsKey(eventType)) {
            mEventListeners.get(eventType).add(listener);
        } else {
            mEventListeners.put(eventType, new ArrayList<>(asList(listener)));
        }
    }

    public void registerKebbiEventListener(List<EventType> eventTypes, KebbiEventListener listener) {
        eventTypes.forEach(eventType -> registerKebbiEventListener(eventType, listener));
    }

    public void unregisterKebbiEventListener(EventType eventType, String listenerId) {
        if (mEventListeners.containsKey(eventType)) {
            List<KebbiEventListener> listeners = mEventListeners.get(eventType);
            listeners.removeIf(l -> l.getId().equals(listenerId));
        }
    }

    public void unregisterKebbiEventListener(List<EventType> eventTypes, String listenerId) {
        eventTypes.forEach(eventType -> unregisterKebbiEventListener(eventType, listenerId));
    }

    private void raiseEvent(EventType eventType, KebbiEvent event) {
        for (KebbiEventListener listener : mEventListeners.getOrDefault(eventType, new ArrayList<>())) {
            if (listener != null)
                listener.onKebbiEvent(eventType, event);
        }
    }

    public interface KebbiEventListener {
        enum EventType {
            STT_EVENT,
            TTS_EVENT
        }

        void onKebbiEvent(EventType eventType, KebbiEvent event);

        String getId();

    }

    public class KebbiEvent extends ArrayList<String> {}
}

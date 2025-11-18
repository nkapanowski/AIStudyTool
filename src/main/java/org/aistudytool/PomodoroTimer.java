package org.aistudytool;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class PomodoroTimer {
    private Timeline timeline;
    private int timeRemaining;
    Label timerLabel;
    Label modeLabel;

    public PomodoroTimer(Label timerLabel, Label modeLabel) {
        this.timerLabel = timerLabel;
        this.modeLabel = modeLabel;
        this.timeRemaining = 1500;
    }

    public void startTimer() {
        if (timeline != null) timeline.stop();
        timeRemaining = 1500;
        modeLabel.setText("Study");
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeRemaining--;
            timerLabel.setText(String.format("%02d:%02d", timeRemaining / 60, timeRemaining % 60));
            if (timeRemaining <= 0) {
                timeline.stop();
                startBreak();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void startBreak() {
        if (timeline != null) timeline.stop();
        timeRemaining = 300;
        modeLabel.setText("Break");
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeRemaining--;
            timerLabel.setText(String.format("%02d:%02d", timeRemaining / 60, timeRemaining % 60));
            if (timeRemaining <= 0) {
                timeline.stop();
                timeRemaining = 1500;
                timerLabel.setText("25:00");
                modeLabel.setText("Study");
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void stopTimer() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    public String getCurrentTime() { return timerLabel.getText(); }
    public String getCurrentMode() { return modeLabel.getText(); }
}
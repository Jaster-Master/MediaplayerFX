package com.jastermaster;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class TitleAnimation {
    private final Label songTitle;
    private final Label songInterpreter;

    private Timeline songTitleSliderAnimation;
    private PauseTransition songTitleStartPause;
    private PauseTransition songTitleEndPause;
    private Timeline songInterpreterSliderAnimation;
    private PauseTransition songInterpreterStartPause;
    private PauseTransition songInterpreterEndPause;

    private final Program program;

    public TitleAnimation(Program program, Label songTitle, Label songInterpreter) {
        this.program = program;
        this.songTitle = songTitle;
        this.songInterpreter = songInterpreter;
        setUpScrollPanes();
    }

    public void resetAnimations() {
        try {
            program.mainCon.songTitleScrollPane.setHvalue(0);
            program.mainCon.songInterpreterScrollPane.setHvalue(0);
            songTitleSliderAnimation.stop();
            songTitleStartPause.stop();
            songTitleEndPause.stop();
            songInterpreterSliderAnimation.stop();
            songInterpreterStartPause.stop();
            songInterpreterEndPause.stop();
        } catch (NullPointerException ignored) {
        }
    }

    // TODO: Speed an Länge des Labels anpassen
    private void setUpScrollPanes() {
        songTitle.widthProperty().addListener((observableValue, oldValue, newValue) -> {
            KeyValue hValue = new KeyValue(program.mainCon.songTitleScrollPane.hvalueProperty(), 1.0);
            KeyFrame duration = new KeyFrame(Duration.seconds(0), hValue);
            songTitleSliderAnimation = new Timeline(duration);
            songTitleSliderAnimation.setOnFinished(actionEvent -> {
                songTitleEndPause = new PauseTransition(Duration.seconds(1));
                songTitleEndPause.setOnFinished(actionEvent1 -> {
                    program.mainCon.songTitleScrollPane.setHvalue(0);
                    songTitleStartPause = new PauseTransition(Duration.seconds(1));
                    songTitleStartPause.setOnFinished(actionEvent2 -> songTitleSliderAnimation.play());
                    songTitleStartPause.play();
                });
                songTitleEndPause.play();
            });
            if (newValue.doubleValue() > program.mainCon.songTitleScrollPane.getWidth()) {
                songTitleStartPause = new PauseTransition(Duration.seconds(1));
                songTitleStartPause.setOnFinished(actionEvent -> songTitleSliderAnimation.play());
                songTitleStartPause.play();
            } else {
                songTitleSliderAnimation.stop();
            }
        });
        songInterpreter.widthProperty().addListener((observableValue, oldValue, newValue) -> {
            KeyValue hValue = new KeyValue(program.mainCon.songInterpreterScrollPane.hvalueProperty(), 1.0);
            KeyFrame duration = new KeyFrame(Duration.seconds(0), hValue);
            songInterpreterSliderAnimation = new Timeline(duration);
            songInterpreterSliderAnimation.setOnFinished(actionEvent -> {
                songInterpreterEndPause = new PauseTransition(Duration.seconds(1));
                songInterpreterEndPause.setOnFinished(actionEvent1 -> {
                    program.mainCon.songInterpreterScrollPane.setHvalue(0);
                    songInterpreterStartPause = new PauseTransition(Duration.seconds(1));
                    songInterpreterStartPause.setOnFinished(actionEvent2 -> songInterpreterSliderAnimation.play());
                    songInterpreterStartPause.play();
                });
                songInterpreterEndPause.play();
            });
            if (newValue.doubleValue() > program.mainCon.songInterpreterScrollPane.getWidth()) {
                songInterpreterStartPause = new PauseTransition(Duration.seconds(1));
                songInterpreterStartPause.setOnFinished(actionEvent -> songInterpreterSliderAnimation.play());
                songInterpreterStartPause.play();
            } else {
                songInterpreterSliderAnimation.stop();
            }
        });
    }
}

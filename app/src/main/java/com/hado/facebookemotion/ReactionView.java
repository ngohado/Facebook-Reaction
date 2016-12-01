package com.hado.facebookemotion;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import static com.hado.facebookemotion.CommonDimen.DIVIDE;

/**
 * Created by Hado on 26-Nov-16.
 */

public class ReactionView extends View {

    enum StateDraw {
        BEGIN,
        CHOOSING,
        NORMAL
    }

    public static final long DURATION_ANIMATION = 200;

    public static final long DURATION_BEGINNING_EACH_ITEM = 300;

    public static final long DURATION_BEGINNING_ANIMATION = 900;

    private EaseOutBack easeOutBack;

    private Board board;

    private Emotion[] emotions = new Emotion[6];

    private StateDraw state = StateDraw.BEGIN;

    private int currentPosition = 0;

    public ReactionView(Context context) {
        super(context);
        init();
    }

    public ReactionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReactionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        board = new Board(getContext());
        setLayerType(LAYER_TYPE_SOFTWARE, board.boardPaint);

        emotions[0] = new Emotion(getContext(), "Like", R.drawable.like);
        emotions[1] = new Emotion(getContext(), "Love", R.drawable.love);
        emotions[2] = new Emotion(getContext(), "Haha", R.drawable.haha);
        emotions[3] = new Emotion(getContext(), "Wow", R.drawable.wow);
        emotions[4] = new Emotion(getContext(), "Cry", R.drawable.cry);
        emotions[5] = new Emotion(getContext(), "Angry", R.drawable.angry);

        initElement();
    }

    private void initElement() {
        board.currentY = CommonDimen.HEIGHT_VIEW_REACTION + 10;
        for (Emotion e : emotions) {
            e.currentY = board.currentY + CommonDimen.DIVIDE;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (state != null) {
            board.drawBoard(canvas);
            for (Emotion emotion : emotions) {
                emotion.drawEmotion(canvas);
            }
        }
    }

    private void beforeAnimateBeginning() {
        board.beginHeight = Board.BOARD_HEIGHT_NORMAL;
        board.endHeight = Board.BOARD_HEIGHT_NORMAL;

        board.beginY = Board.BOARD_BOTTOM + 150;
        board.endY = Board.BOARD_Y;

        easeOutBack = EaseOutBack.newInstance(DURATION_BEGINNING_EACH_ITEM, Math.abs(board.beginY - board.endY), 0);

        for (int i = 0; i < emotions.length; i++) {
            emotions[i].endY = Board.BASE_LINE - Emotion.NORMAL_SIZE;
            emotions[i].beginY = Board.BOARD_BOTTOM + 150;
            emotions[i].currentX = i == 0 ? Board.BOARD_X + DIVIDE : emotions[i - 1].currentX + emotions[i - 1].currentSize + DIVIDE;
        }
    }


    private void beforeAnimateChoosing() {
        board.beginHeight = board.getCurrentHeight();
        board.endHeight = Board.BOARD_HEIGHT_MINIMAL;

        for (int i = 0; i < emotions.length; i++) {
            emotions[i].beginSize = emotions[i].currentSize;

            if (i == currentPosition) {
                emotions[i].endSize = Emotion.CHOOSE_SIZE;
            } else {
                emotions[i].endSize = Emotion.MINIMAL_SIZE;
            }
        }
    }

    private void beforeAnimateNormalBack() {
        board.beginHeight = board.getCurrentHeight();
        board.endHeight = Board.BOARD_HEIGHT_NORMAL;

        for (int i = 0; i < emotions.length; i++) {
            emotions[i].beginSize = emotions[i].currentSize;
            emotions[i].endSize = Emotion.NORMAL_SIZE;
        }
    }


    private void calculateInSessionChoosingAndEnding(float interpolatedTime) {
        board.setCurrentHeight(board.beginHeight + (int) (interpolatedTime * (board.endHeight - board.beginHeight)));

        for (int i = 0; i < emotions.length; i++) {
            emotions[i].currentSize = calculateSize(i, interpolatedTime);
            emotions[i].currentY = Board.BASE_LINE - emotions[i].currentSize;
        }
        calculateCoordinateX();
        invalidate();
    }

    private void calculateInSessionBeginning(float interpolatedTime) {
        float currentTime = interpolatedTime * DURATION_BEGINNING_ANIMATION;

        if (currentTime > 0) {
            board.currentY = board.endY + easeOutBack.getCoordinateYFromTime(Math.min(currentTime, DURATION_BEGINNING_EACH_ITEM));
        }

        if (currentTime >= 100) {
            emotions[0].currentY = emotions[0].endY + easeOutBack.getCoordinateYFromTime(Math.min(currentTime - 100, DURATION_BEGINNING_EACH_ITEM));
        }

        if (currentTime >= 200) {
            emotions[1].currentY = emotions[1].endY + easeOutBack.getCoordinateYFromTime(Math.min(currentTime - 200, DURATION_BEGINNING_EACH_ITEM));
        }

        if (currentTime >= 300) {
            emotions[2].currentY = emotions[2].endY + easeOutBack.getCoordinateYFromTime(Math.min(currentTime - 300, DURATION_BEGINNING_EACH_ITEM));
        }

        if (currentTime >= 400) {
            emotions[3].currentY = emotions[3].endY + easeOutBack.getCoordinateYFromTime(Math.min(currentTime - 400, DURATION_BEGINNING_EACH_ITEM));
        }

        if (currentTime >= 500) {
            emotions[4].currentY = emotions[4].endY + easeOutBack.getCoordinateYFromTime(Math.min(currentTime - 500, DURATION_BEGINNING_EACH_ITEM));
        }

        if (currentTime >= 600) {
            emotions[5].currentY = emotions[5].endY + easeOutBack.getCoordinateYFromTime(Math.min(currentTime - 600, DURATION_BEGINNING_EACH_ITEM));
        }

        invalidate();
    }

    private int calculateSize(int position, float interpolatedTime) {
        int changeSize = emotions[position].endSize - emotions[position].beginSize;
        return emotions[position].beginSize + (int) (interpolatedTime * changeSize);
    }

    private void calculateCoordinateX() {
        emotions[0].currentX = Board.BOARD_X + DIVIDE;
        emotions[emotions.length - 1].currentX = Board.BOARD_X + Board.BOARD_WIDTH - DIVIDE - emotions[emotions.length - 1].currentSize;

        for (int i = 1; i < currentPosition; i++) {
            emotions[i].currentX = emotions[i - 1].currentX + emotions[i - 1].currentSize + DIVIDE;
        }

        for (int i = emotions.length - 2; i > currentPosition; i--) {
            emotions[i].currentX = emotions[i + 1].currentX - emotions[i].currentSize - DIVIDE;
        }

        if (currentPosition != 0 && currentPosition != emotions.length - 1) {
            if (currentPosition <= (emotions.length / 2 - 1)) {
                emotions[currentPosition].currentX = emotions[currentPosition - 1].currentX + emotions[currentPosition - 1].currentSize + DIVIDE;
            } else {
                emotions[currentPosition].currentX = emotions[currentPosition + 1].currentX - emotions[currentPosition].currentSize - DIVIDE;
            }
        }
    }

    public void show() {
        state = StateDraw.BEGIN;
        setVisibility(VISIBLE);
        beforeAnimateBeginning();
        startAnimation(new BeginningAnimation());
    }

    private void selected(int position) {
        if (currentPosition == position && state == StateDraw.CHOOSING) return;

        state = StateDraw.CHOOSING;
        currentPosition = position;

        startAnimation(new ChooseEmotionAnimation());
    }

    public void backToNormal() {
        state = StateDraw.NORMAL;
        startAnimation(new ChooseEmotionAnimation());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handled = true;
                break;
            case MotionEvent.ACTION_MOVE:
                for (int i = 0; i < emotions.length; i++) {
                    if (event.getX() > emotions[i].currentX && event.getX() < emotions[i].currentX + emotions[i].currentSize) {
                        selected(i);
                        break;
                    }
                }
                handled = true;
                break;
            case MotionEvent.ACTION_UP:
                backToNormal();
                handled = true;
                break;
        }
        return handled;
    }

    class ChooseEmotionAnimation extends Animation {
        public ChooseEmotionAnimation() {
            if (state == StateDraw.CHOOSING) {
                beforeAnimateChoosing();
            } else if (state == StateDraw.NORMAL) {
                beforeAnimateNormalBack();
            }
            setDuration(DURATION_ANIMATION);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            calculateInSessionChoosingAndEnding(interpolatedTime);
        }
    }

    class BeginningAnimation extends Animation {

        public BeginningAnimation() {
            beforeAnimateBeginning();
            setDuration(DURATION_BEGINNING_ANIMATION);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            calculateInSessionBeginning(interpolatedTime);
        }
    }
}

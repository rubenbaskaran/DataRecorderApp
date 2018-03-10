package rubenbaskaran.com.datarecorderapp.BusinessLogic;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

/**
 * A class, that can be used as a TouchListener on any view (e.g. a Button).
 * It cyclically runs a clickListener, emulating keyboard-like behaviour. First
 * click is fired immediately, next one after the initialInterval, and subsequent
 * ones after the normalInterval.
 * Interval is scheduled after the onClick completes, so it has to run fast.
 * If it runs slow, it does not generate skipped onClicks. Can be rewritten to
 * achieve this.
 */

public class RepeatListener implements View.OnTouchListener
{

    //region Getters
    public Handler getHandler()
    {
        return handler;
    }

    public Runnable getHandlerRunnable()
    {
        return handlerRunnable;
    }
    //endregion

    private Handler handler = new Handler();
    private int initialInterval;
    private final int normalInterval;
    private final View.OnClickListener clickListener;
    private View downView;

    private Runnable handlerRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            handler.postDelayed(this, normalInterval);
            clickListener.onClick(downView);
        }
    };

    /**
     * @param initialInterval The interval after first click event
     * @param normalInterval  The interval after second and subsequent click events
     * @param clickListener   The OnClickListener, that will be called periodically
     */
    public RepeatListener(int initialInterval, int normalInterval, View.OnClickListener clickListener)
    {
        if (clickListener == null)
            throw new IllegalArgumentException("null runnable");

        if (initialInterval < 0 || normalInterval < 0)
            throw new IllegalArgumentException("negative interval");

        this.initialInterval = initialInterval;
        this.normalInterval = normalInterval;
        this.clickListener = clickListener;
    }

    public boolean onTouch(View view, MotionEvent motionEvent)
    {
        switch (motionEvent.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                handler.removeCallbacks(handlerRunnable);
                handler.postDelayed(handlerRunnable, initialInterval);
                downView = view;
                downView.setPressed(true);
                clickListener.onClick(view);
                return true;

            case MotionEvent.ACTION_UP:
                handler.removeCallbacks(handlerRunnable);
                downView.setPressed(false);
                downView = null;
                return true;

            case MotionEvent.ACTION_CANCEL:
                handler.removeCallbacks(handlerRunnable);
                downView.setPressed(false);
                downView = null;
                return true;
        }

        return false;
    }
}
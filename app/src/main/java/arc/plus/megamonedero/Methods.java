package arc.plus.megamonedero;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

public class Methods {
    public static void ContraerBuscador(Context context, RelativeLayout HolderSearchBar, EditText Buscador, ImageView IconoBuscadorComprimido, RecyclerView Sugerencias){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Constant.dpToPx(40), Constant.dpToPx(40));
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            params.topMargin = Constant.dpToPx(10);

            HolderSearchBar.setLayoutParams(params);
            HolderSearchBar.setBackground(context.getResources().getDrawable(R.drawable.buscador_comprimido));

            IconoBuscadorComprimido.setVisibility(View.VISIBLE);
            Buscador.setVisibility(View.GONE);
            Sugerencias.setVisibility(View.GONE);
    }

    public static void ExpandirBuscador(Context context, RelativeLayout HolderSearchBar, EditText Buscador, ImageView IconoBuscadorComprimido, RecyclerView Sugerencias){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.removeRule(RelativeLayout.ALIGN_PARENT_END);
        params.topMargin = 0;

        HolderSearchBar.setLayoutParams(params);
        HolderSearchBar.setBackground(null);
        HolderSearchBar.setBackgroundColor(context.getResources().getColor(R.color.white));
        IconoBuscadorComprimido.setVisibility(View.GONE);
        Buscador.setVisibility(View.VISIBLE);
        Sugerencias.setVisibility(View.VISIBLE);
    }

    public static void MostrarTarjeta(Context context, RelativeLayout Card){
        Animation EnteringCard = AnimationUtils.loadAnimation(context, R.anim.abajo_arriba);
        Card.setVisibility(View.VISIBLE);
        Card.setAnimation(EnteringCard);
        EnteringCard.start();
    }

    public static void EsconderTarjeta(Context context, RelativeLayout Card){
        Animation ExitingCard = AnimationUtils.loadAnimation(context, R.anim.bottom_out_down);
        Card.setVisibility(View.GONE);
        Card.setAnimation(ExitingCard);
        ExitingCard.start();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); // hide
        } else {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY); // show
        }
    }

    public class ShowAnim extends Animation {
        int targetHeight;
        View view;

        public ShowAnim(View view, int targetHeight) {
            this.view = view;
            this.targetHeight = targetHeight;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            view.getLayoutParams().height = (int) (targetHeight * interpolatedTime);
            view.requestLayout();
        }

        @Override
        public void initialize(int width, int height, int parentWidth,
                               int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}

package arc.plus.megamonedero;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
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

}

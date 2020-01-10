package arc.plus.megamonedero;

import android.content.res.Resources;

public class Constant {
    public static Integer SRVICIOS = 2;
    public static Integer TRANSPORTES = 1;
    public static Integer TIPO_BUSQUEDA = 0;

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

}

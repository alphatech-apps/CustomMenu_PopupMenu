``` java

package com.jakir.custommanuandpopupmenu;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.appbar.MaterialToolbar;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
public class MainActivity extends AppCompatActivity {
Button button;
MaterialToolbar toolbar;
Menu menu1;
boolean itemChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = findViewById(R.id.toolbar);
        ActionBar actionBar = getSupportActionBar();
        setSupportActionBar(toolbar);


        button = findViewById(R.id.button);

        button.setOnClickListener(veiw -> showStyledPopup(veiw));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        menu1 = menu;
        if (menu != null) {

            // Overflow icon tint
            Drawable overflow = toolbar.getOverflowIcon();
            if (overflow != null) {
                overflow.setTint(ContextCompat.getColor(this, R.color.redx));
            }

            // Menu item tint for all items of menu
            for (int i = 0; i < menu.size(); i++) {
                Drawable icon = menu.getItem(i).getIcon();
                if (icon != null) {
                    icon.setTint(ContextCompat.getColor(this, R.color.redx));
                }
            }

    // Menu item tint for single menu
    MenuItem menuItem = menu.findItem(R.id.action_screen_saver);
    if (menuItem != null && menuItem.getIcon() != null) {
    menuItem.getIcon().setTint(ContextCompat.getColor(this, R.color.redx));
    }
        }
        return true;
    }

   

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) { // its 3dot overflow menu
        if (menu != null) {
            // Force to show icons on overflow menu
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception ignore) {
                }
            }

            // Force to set padding on overflow menu
            try {
                for (int i = 0; i < menu.size(); i++) {
                    MenuItem item = menu.getItem(i);
                    View actionView = findViewById(item.getItemId());

                    if (actionView == null) { // overflow items normally have no actionView
                        Drawable icon = item.getIcon();

                        if (icon != null && !(icon instanceof InsetDrawable)) { // check before applying
                            int marginPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7, getResources().getDisplayMetrics());
                            item.setIcon(new InsetDrawable(icon, marginPx, 0, marginPx, 0));
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }

        return super.onMenuOpened(featureId, menu);
    }
 

    private void showStyledPopup(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.my_menu);

        // Force show icons on popup menu
        try {
            Field mPopup = popupMenu.getClass().getDeclaredField("mPopup");
            mPopup.setAccessible(true);
            Object menuPopupHelper = mPopup.get(popupMenu);
            Method setForceIcons = Objects.requireNonNull(menuPopupHelper).getClass().getMethod("setForceShowIcon", boolean.class);
            setForceIcons.invoke(menuPopupHelper, true);
        } catch (Exception ignore) {
        }

        // Force to set padding on popup menu
        for (int i = 0; i < popupMenu.getMenu().size(); i++) {
            MenuItem item = popupMenu.getMenu().getItem(i);
            Drawable icon = item.getIcon();
            if (icon != null && !(icon instanceof InsetDrawable)) { // check before applying
                int marginPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7, getResources().getDisplayMetrics());
                item.setIcon(new InsetDrawable(icon, marginPx, 0, marginPx, 0));
            }
        }

        // Popup Menu item tint for all items of menu
        Menu menu = popupMenu.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            Drawable icon = menu.getItem(i).getIcon();
            if (icon != null) {
                icon.setTint(ContextCompat.getColor(this, R.color.redx2));
            }
        }

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int id = menuItem.getItemId();
            return false;
        });

        popupMenu.show();
    }

}
 